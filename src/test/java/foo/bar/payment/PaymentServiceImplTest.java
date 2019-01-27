package foo.bar.payment;

import foo.bar.DBCManagerTest;
import foo.bar.account.AccountService;
import foo.bar.account.AccountServiceImplTest;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class PaymentServiceImplTest {

    PaymentService paymentService;
    AccountService accountService;

    @Before
    public void setUp() {
        paymentService = new PaymentServiceImpl(DBCManagerTest.getDbcManager());
        accountService = AccountServiceImplTest.getAccountService();
    }

    @Test
    public void paymentTest() throws Exception {

        final BigDecimal leftAccInitBal = BigDecimal.valueOf(100_000);
        final BigDecimal rightAccInitBal = BigDecimal.valueOf(30_000);

        final BigDecimal firstPaymentAmount = BigDecimal.valueOf(5555.55);
        final BigDecimal secondPaymentAmount = BigDecimal.valueOf(777.77);

        final BigDecimal leftAccExpectedBalance = leftAccInitBal.subtract(firstPaymentAmount).subtract(secondPaymentAmount);
        final BigDecimal rightAccExpectedBalance = rightAccInitBal.add(firstPaymentAmount).add(secondPaymentAmount);

        final Long leftAccountId = accountService.createAccount(leftAccInitBal);
        final Long rightAccountId = accountService.createAccount(rightAccInitBal);

        PaymentDTO firstPaymentDTO = new PaymentDTO();
        firstPaymentDTO.setAmount(firstPaymentAmount);
        firstPaymentDTO.setLeftAccountId(leftAccountId);
        firstPaymentDTO.setRightAccountId(rightAccountId);

        PaymentDTO secondPaymentDTO = new PaymentDTO();
        secondPaymentDTO.setAmount(secondPaymentAmount);
        secondPaymentDTO.setLeftAccountId(leftAccountId);
        secondPaymentDTO.setRightAccountId(rightAccountId);

        final Long firstPaymentId = paymentService.processPayment(firstPaymentDTO);
        final Long secondPaymentId = paymentService.processPayment(secondPaymentDTO);

        Object leftAccount = accountService.findAccount(leftAccountId).get();

        final Method getBalanceMethod = leftAccount.getClass().getMethod("getBalance");
        getBalanceMethod.setAccessible(true);

        final BigDecimal leftAccCurrBal = (BigDecimal) getBalanceMethod.invoke(leftAccount);

        assertEquals(0, leftAccCurrBal.compareTo(leftAccExpectedBalance));

        Object rightAccount = accountService.findAccount(rightAccountId).get();
        final BigDecimal rightAccCurrBal = (BigDecimal) getBalanceMethod.invoke(rightAccount);

        assertEquals(0, rightAccCurrBal.compareTo(rightAccExpectedBalance));


        final Payment firstPayment = paymentService.findPayment(firstPaymentId).get();
        final Payment secondPayment = paymentService.findPayment(secondPaymentId).get();

        assertEquals(firstPayment.getLeftAccountId(), leftAccountId);
        assertEquals(firstPayment.getRightAccountId(), rightAccountId);
        assertEquals(0, firstPayment.getAmount().compareTo(firstPaymentAmount));

        assertEquals(secondPayment.getLeftAccountId(), leftAccountId);
        assertEquals(secondPayment.getRightAccountId(), rightAccountId);
        assertEquals(0, secondPayment.getAmount().compareTo(secondPaymentAmount));

    }
}