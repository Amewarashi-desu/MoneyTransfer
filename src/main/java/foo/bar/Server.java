package foo.bar;

import io.undertow.Undertow;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private static final String DEFAULT_HOST = "localhost";

    private static final int DEFAULT_PORT = 8181;

    public static void main(String[] args) {

        int port = DEFAULT_PORT;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        Undertow.Builder serverBuilder = Undertow.builder().addHttpListener(port, DEFAULT_HOST);
        UndertowJaxrsServer server = new UndertowJaxrsServer();
        server.start(serverBuilder);

        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setInjectorFactoryClass("org.jboss.resteasy.cdi.CdiInjectorFactory");
        deployment.setApplicationClass(MoneyTransferApp.class.getName());
        DeploymentInfo deploymentInfo = server.undertowDeployment(deployment);

        deploymentInfo.setClassLoader(Server.class.getClassLoader())
                .setContextPath("/")
                .setDeploymentName("MoneyTransfer")
                .addListeners(Servlets.listener(org.jboss.weld.environment.servlet.Listener.class));

        server.deploy(deploymentInfo);

        LOGGER.info("Application started at localhost:{}", port);

    }

}
