package foo.bar.account;

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
public class AccountServiceImpl implements AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

    private DBCManager dbcManager;

    private static final String SELECT_ACCOUNT_BY_ID = "select account_id, create_time, balance from account where account_id=?";

    private static final String INSERT_ACCOUNT = "insert into account(balance) values(?)";

    @Inject
    AccountServiceImpl(DBCManager dbcManager) {
        this.dbcManager = dbcManager;
    }

    @Override
    public Optional<Account> findAccount(Long accountId) {

        try (Connection connection = dbcManager.getConnection()) {

            connection.setReadOnly(true);

            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACCOUNT_BY_ID);
            preparedStatement.setLong(1, accountId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) {

                long account_id = resultSet.getLong("account_id");
                LocalDateTime date = resultSet.getTimestamp("create_time").toLocalDateTime();
                BigDecimal balance = resultSet.getBigDecimal("balance");

                return Optional.of(new Account(account_id, balance, date));

            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            LOGGER.error("Error", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public Long createAccount(BigDecimal initialBalance) {

        try (Connection connection = dbcManager.getConnection()) {

            connection.setReadOnly(false);

            try {
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ACCOUNT, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setBigDecimal(1, initialBalance);
                preparedStatement.executeUpdate();

                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    long new_account_id = generatedKeys.getLong("account_id");
                    connection.commit();
                    return new_account_id;
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
