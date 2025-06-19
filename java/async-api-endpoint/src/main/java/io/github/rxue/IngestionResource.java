package io.github.rxue;

import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;

@Path("/")
public class IngestionResource {

    private final String queueName;

    private final ConnectionFactory connectionFactory;

    private Service service;
    @Inject
    public IngestionResource(@ConfigProperty(name = "QUEUE_NAME") String queueName,
                             ConnectionFactory connectionFactory, Service service) {
        this.queueName = queueName;
        this.connectionFactory = connectionFactory;
        this.service = service;
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
        return mapToResponse(service.getStatus());
    }
    @Path("top-senders")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    public Response getTopSenders() {
        return mapToResponse(service.getTopTenSenders());
    }
    private Response mapToResponse(Optional<String> resultOpt) {
        return resultOpt.map(result -> Response.ok(result).build())
                .orElse(Response.noContent().build());
    }
}
