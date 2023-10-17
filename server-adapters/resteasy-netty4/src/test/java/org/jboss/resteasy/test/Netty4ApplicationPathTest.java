package org.jboss.resteasy.test;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Fetching root path from ApplicationPath annotation related tests.
 *
 * @see https://issues.jboss.org/browse/RESTEASY-1657
 */
public class Netty4ApplicationPathTest {
    private static final String ECHO = "hello";

    @ApplicationPath("/rest-test")
    public static class TestApplication extends Application {
        private final Set<Object> singletons = new HashSet<>();

        public TestApplication() {
            singletons.add(new EchoService());
        }

        @Override
        public Set<Object> getSingletons() {
            return singletons;
        }
    }

    @Path("/")
    public static class EchoService {
        @GET
        @Path("/echo")
        @Produces("text/plain")
        public String echo(@QueryParam("text") final String echo) {
            return echo;
        }
    }

    @Test
    public void testWithClass() throws Exception {
        NettyJaxrsServer server = null;
        Client client = null;
        try {
            ResteasyDeployment deployment = new ResteasyDeploymentImpl();
            deployment.setApplicationClass(TestApplication.class.getName());
            server = new NettyJaxrsServer();
            server.setDeployment(deployment);
            server.setHostname("localhost");
            server.setPort(8080);
            server.start();

            // call resource
            final String path = "/rest-test/echo";
            client = ClientBuilder.newClient();
            String url = String.format("http://%s:%d%s", server.getHostname(), server.getPort(), path);
            Response response = client.target(url).queryParam("text", ECHO).request().get();
            Assertions.assertTrue(response.getStatus() == HttpResponseCodes.SC_OK);
            String msg = response.readEntity(String.class);
            Assertions.assertEquals(ECHO, msg);
        } finally {
            if (client != null) {
                client.close();
            }
            if (server != null) {
                server.stop();
            }
        }
    }

    @Test
    public void testWithApplication() throws Exception {
        NettyJaxrsServer server = null;
        Client client = null;
        try {
            ResteasyDeployment deployment = new ResteasyDeploymentImpl();
            Application app = new TestApplication();
            deployment.setApplication(app);
            server = new NettyJaxrsServer();
            server.setDeployment(deployment);
            server.setHostname("localhost");
            server.setPort(8080);
            server.start();

            // call resource
            final String path = "/rest-test/echo";
            client = ClientBuilder.newClient();
            String url = String.format("http://%s:%d%s", server.getHostname(), server.getPort(), path);
            Response response = client.target(url).queryParam("text", ECHO).request().get();
            Assertions.assertTrue(response.getStatus() == HttpResponseCodes.SC_OK);
            String msg = response.readEntity(String.class);
            Assertions.assertEquals(ECHO, msg);
        } finally {
            if (client != null) {
                client.close();
            }
            if (server != null) {
                server.stop();
            }
        }
    }

    @Test
    public void testWithManualRootPath() throws Exception {
        NettyJaxrsServer server = null;
        Client client = null;
        try {
            ResteasyDeployment deployment = new ResteasyDeploymentImpl();
            deployment.setApplicationClass(TestApplication.class.getName());
            server = new NettyJaxrsServer();
            server.setRootResourcePath("/new-rest-test");
            server.setDeployment(deployment);
            server.setHostname("localhost");
            server.setPort(8080);
            server.start();

            // call resource
            // root resource should be taken from setRootResourcePath method
            final String path = "/new-rest-test/echo";
            client = ClientBuilder.newClient();
            String url = String.format("http://%s:%d%s", server.getHostname(), server.getPort(), path);
            Response response = client.target(url).queryParam("text", ECHO).request().get();
            Assertions.assertTrue(response.getStatus() == HttpResponseCodes.SC_OK);
            String msg = response.readEntity(String.class);
            Assertions.assertEquals(ECHO, msg);
        } finally {
            if (client != null) {
                client.close();
            }
            if (server != null) {
                server.stop();
            }
        }
    }
}
