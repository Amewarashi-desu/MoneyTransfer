package foo.bar.payment;

import java.util.Optional;

public interface PaymentService {

    /**
     * Method for finding payment by ID
     *
     * @param paymentId Payment ID created during payment processing
     * @return optional that has {@link Payment} if it where found
     */
    Optional<Payment> findPayment(Long paymentId);

    /**
     * Method for processing incoming payment. Payment comes as DTO, see @{@link PaymentDTO}
     *
     * @param paymentDTO payment
     * @return created payment ID
     */
    Long processPayment(PaymentDTO paymentDTO);

}
