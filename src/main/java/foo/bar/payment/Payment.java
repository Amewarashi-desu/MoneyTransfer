package foo.bar.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

class Payment {

    private Long id;

    private Long leftAccountId;

    private Long rightAccountId;

    private BigDecimal amount;

    private LocalDateTime created;

    Payment() {
    }

    Payment(Long id, Long leftAccountId, Long rightAccountId, BigDecimal amount, LocalDateTime created) {
        this.id = id;
        this.leftAccountId = leftAccountId;
        this.rightAccountId = rightAccountId;
        this.amount = amount;
        this.created = created;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Payment{");
        sb.append("id=").append(id);
        sb.append(", leftAccountId=").append(leftAccountId);
        sb.append(", rightAccountId=").append(rightAccountId);
        sb.append(", amount=").append(amount);
        sb.append(", created=").append(created);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id) &&
                Objects.equals(leftAccountId, payment.leftAccountId) &&
                Objects.equals(rightAccountId, payment.rightAccountId) &&
                Objects.equals(amount, payment.amount) &&
                Objects.equals(created, payment.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, leftAccountId, rightAccountId, amount, created);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
}
