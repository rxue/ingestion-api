package io.github.rxue;

import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.util.Optional;

@Path("/ingestion")
public class IngestionResource {

    private final String queueName;


    private ConnectionFactory connectionFactory;

    private Monitor monitor;
    @Inject
    public IngestionResource(@ConfigProperty(name = "QUEUE_NAME") String queueName,
                             ConnectionFactory connectionFactory, Monitor monitor) {
        this.queueName = queueName;
        this.connectionFactory = connectionFactory;
        this.monitor = monitor;
    }

    @Path("start")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public String send(String dataSourceURL) {
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            context.createProducer().send(context.createQueue(queueName), dataSourceURL);
        }
        return "sent";
    }
    @Path("status")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    public Response getStatus() {
        try {
            return monitor.getStatus()
                    .map(statusString -> Response.ok(statusString).build())
                    .orElse(Response.noContent().build());
        } catch (IOException e) {
            throw new WebApplicationException();
        }
    }
}
