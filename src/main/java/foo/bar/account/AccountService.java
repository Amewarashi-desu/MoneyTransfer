package foo.bar.account;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountService {

    /**
     * Method for finding Account by ID
     *
     * @param accountId account ID
     * @return optional that has {@link Account} if it where found
     */
    Optional<Account> findAccount(Long accountId);

    /**
     * Method for creating account. Only initial balance is required to create new account
     * @param initialBalance initial balance for new account
     * @return created account ID
     */
    Long createAccount(BigDecimal initialBalance);

}
