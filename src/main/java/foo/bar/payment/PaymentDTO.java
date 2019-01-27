package foo.bar.payment;

import java.math.BigDecimal;
import java.util.Objects;

class PaymentDTO {

    private Long leftAccountId;

    private Long rightAccountId;

    private BigDecimal amount;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PaymentDTO{");
        sb.append("leftAccountId=").append(leftAccountId);
        sb.append(", rightAccountId=").append(rightAccountId);
        sb.append(", amount=").append(amount);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentDTO that = (PaymentDTO) o;
        return Objects.equals(leftAccountId, that.leftAccountId) &&
                Objects.equals(rightAccountId, that.rightAccountId) &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftAccountId, rightAccountId, amount);
    }

    public Long getLeftAccountId() {
        return leftAccountId;
    }

    public void setLeftAccountId(Long leftAccountId) {
        this.leftAccountId = leftAccountId;
    }

    public Long getRightAccountId() {
        return rightAccountId;
    }

    public void setRightAccountId(Long rightAccountId) {
        this.rightAccountId = rightAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
