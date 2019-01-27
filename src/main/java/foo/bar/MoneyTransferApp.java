package foo.bar;

import foo.bar.account.AccountController;
import foo.bar.payment.PaymentController;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;

import javax.inject.Inject;
import javax.ws.rs.core.Application;
import java.util.LinkedHashSet;
import java.util.Set;

public class MoneyTransferApp extends Application {

    @Inject
    private AccountController accountController;

    @Inject
    private PaymentController paymentController;

    @Override
    public Set<Object> getSingletons() {
        Set<Object> resources = new LinkedHashSet<Object>();
        resources.add(accountController);
        resources.add(paymentController);

        CorsFilter corsFilter = new CorsFilter();
        corsFilter.getAllowedOrigins().add("*");
        corsFilter.setAllowedMethods("OPTIONS, GET, POST");
        resources.add(corsFilter);

        return resources;
    }
}
