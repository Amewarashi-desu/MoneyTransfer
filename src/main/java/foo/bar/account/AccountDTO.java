package foo.bar.account;

import java.math.BigDecimal;
import java.util.Objects;

public class AccountDTO {

    private BigDecimal initialBalance;

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AccountDTO{");
        sb.append("initialBalance=").append(initialBalance);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountDTO that = (AccountDTO) o;
        return Objects.equals(initialBalance, that.initialBalance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(initialBalance);
    }
}
