package foo.bar.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

class Account {

    private Long id;

    private BigDecimal balance;

    private LocalDateTime created;

    Account() {
    }

    Account(Long id, BigDecimal balance, LocalDateTime created) {
        this.id = id;
        this.balance = balance;
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
}
