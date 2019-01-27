package foo.bar.account;

import foo.bar.DBCManagerTest;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class AccountServiceImplTest {

    AccountService service;

    public static AccountService getAccountService() {
        return new AccountServiceImpl(DBCManagerTest.getDbcManager());
    }

    @Before
    public void setUp() {
        service = new AccountServiceImpl(DBCManagerTest.getDbcManager());
    }

    @Test
    public void accountTest() {

        BigDecimal expectedBalance = BigDecimal.valueOf(20000);

        final Long accountId = service.createAccount(expectedBalance);

        final Optional<Account> optionalAccount = service.findAccount(accountId);

        final BigDecimal actualBalance = optionalAccount.get().getBalance();

        assertEquals(0, expectedBalance.compareTo(actualBalance));

    }
}