package foo.bar.payment;

import foo.bar.DBCManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Singleton
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private DBCManager dbcManager;

    private static final String SELECT_PAYMENT_BY_ID =
            "select payment_id, create_time, amount, left_account_id, right_account_id from payment where payment_id=?";

    private static final String WITHDRAW_UPDATE = "update account set balance = (balance-?) where account_id=?";

    private static final String TOP_UP_UPDATE = "update account set balance = (balance+?) where account_id=?";

    private static final String INSERT_PAYMENT = "insert into payment(amount, left_account_id, right_account_id) " +
            "values(?, ?, ?)";

    @Inject
    public PaymentServiceImpl(DBCManager dbcManager) {
        this.dbcManager = dbcManager;
    }

    @Override
    public Optional<Payment> findPayment(Long paymentId) {

        try (Connection connection = dbcManager.getConnection()) {

            connection.setReadOnly(true);
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PAYMENT_BY_ID);
            preparedStatement.setLong(1, paymentId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) {

                long payment_id = resultSet.getLong("payment_id");
                LocalDateTime date = resultSet.getTimestamp("create_time").toLocalDateTime();
                BigDecimal amount = resultSet.getBigDecimal("amount");
                long left_account_id = resultSet.getLong("left_account_id");
                long right_account_id = resultSet.getLong("right_account_id");

                return Optional.of(new Payment(payment_id, left_account_id, right_account_id, amount, date));

            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            LOGGER.error("Error", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public Long processPayment(PaymentDTO paymentDTO) {

        try (Connection connection = dbcManager.getConnection()) {

            connection.setReadOnly(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            try {

                PreparedStatement psWithdraw = connection.prepareStatement(WITHDRAW_UPDATE);
                psWithdraw.setBigDecimal(1, paymentDTO.getAmount());
                psWithdraw.setLong(2, paymentDTO.getLeftAccountId());

                PreparedStatement psTopUp = connection.prepareStatement(TOP_UP_UPDATE);
                psTopUp.setBigDecimal(1, paymentDTO.getAmount());
                psTopUp.setLong(2, paymentDTO.getRightAccountId());

                PreparedStatement savePayment = connection.prepareStatement(INSERT_PAYMENT, Statement.RETURN_GENERATED_KEYS);
                savePayment.setBigDecimal(1, paymentDTO.getAmount());
                savePayment.setLong(2, paymentDTO.getLeftAccountId());
                savePayment.setLong(3, paymentDTO.getRightAccountId());

                final int updatedRows = psWithdraw.executeUpdate() + psTopUp.executeUpdate();

                if (updatedRows != 2) {
                    throw new Exception("There must be 2 updated records, but was " + updatedRows);
                }

                savePayment.execute();

                ResultSet generatedKeys = savePayment.getGeneratedKeys();

                if (generatedKeys.next()) {
                    long new_payment_id = generatedKeys.getLong("payment_id");
                    connection.commit();
                    return new_payment_id;
                } else {
                    throw new RuntimeException("There were no generated keys in result set, something went wrong");
                }

            } catch (Exception e) {
                LOGGER.error("Error during account creation. Trying to rollback", e);
                connection.rollback();
                throw e;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
