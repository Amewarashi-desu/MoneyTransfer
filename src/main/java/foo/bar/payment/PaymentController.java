package foo.bar.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

@Path("payment")
@Singleton
public class PaymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    private PaymentService paymentService;

    @Inject
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response findPayment(@PathParam("id") Long paymentId) {

        LOGGER.info("GET request for payment ID: {}", paymentId);

        try {

            Optional<Payment> optionalPayment = paymentService.findPayment(paymentId);

            if (optionalPayment.isPresent()) {
                return Response.ok(optionalPayment.get()).build();
            } else {
                throw new NotFoundException();
            }

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error", e);
            return Response.serverError().entity("Unable to perform operation, please contact us. There was an error during searching Payment with ID " + paymentId).build();
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response processPayment(@HeaderParam("Host") String host, PaymentDTO paymentDTO) {

        try {

            LOGGER.info("Request for processing payment: {}", paymentDTO);

            final Long paymentId = paymentService.processPayment(paymentDTO);

            LOGGER.info("Payment {} successfully processed. Payment ID:{}", paymentDTO, paymentId);

            String template = "%s/payment/%s";

            return Response.created(new URI(String.format(template, host, paymentId))).build();

        } catch (Exception e) {
            LOGGER.error("Error during processing payment", e);
            return Response.serverError().entity("Unable to perform operation, please contact us. There was an error during processing " + paymentDTO).build();
        }

    }


}
