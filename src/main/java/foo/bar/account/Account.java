package foo.bar.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Account{");
        sb.append("id=").append(id);
        sb.append(", balance=").append(balance);
        sb.append(", created=").append(created);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
                Objects.equals(balance, account.balance) &&
                Objects.equals(created, account.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance, created);
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
