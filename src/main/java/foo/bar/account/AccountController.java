package foo.bar.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

@Path("account")
@Singleton
public class AccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    private AccountService accountService;

    @Inject
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response findAccount(@PathParam("id") Long accountId) {

        LOGGER.info("GET request for account ID: {}", accountId);

        try {

            Optional<Account> optionalAccount = accountService.findAccount(accountId);

            return optionalAccount.map(ac -> Response.ok(ac).build()).orElseThrow(NotFoundException::new);

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error", e);
            return Response.serverError().entity("Unable to perform operation, please contact us. There was an error during searching Account with ID " + accountId).build();
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAccount(@HeaderParam("Host") String host, AccountDTO accountDTO) {

        try {

            LOGGER.info("Request for creating account: {}", accountDTO);

            final Long accountId = accountService.createAccount(accountDTO.getInitialBalance());

            LOGGER.info("Account created. Account ID: {}", accountId);

            String template = "%s/account/%s";

            return Response.created(new URI(String.format(template, host, accountId))).build();

        } catch (Exception e) {
            LOGGER.error("Error during creating account", e);
            return Response.serverError().entity("Unable to perform operation, please contact us. There was an error during creating account " + accountDTO).build();
        }
    }

}
