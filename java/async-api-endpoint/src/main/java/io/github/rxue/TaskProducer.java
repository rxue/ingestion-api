package io.github.rxue;

import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;

@Path("/ingestion")
public class TaskProducer {

    private String queueName;

    ConnectionFactory connectionFactory;

    private Monitor monitor;
    @Inject
    public TaskProducer(@ConfigProperty(name = "QUEUE_NAME") String queueName, ConnectionFactory connectionFactory, Monitor monitor) {
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
            System.out.println("task started");
        }
        return "sent";
    }
    @Path("status")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    public String getStatus() {
        try {
            return monitor.getStatus();
        } catch (IOException e) {
            throw new WebApplicationException();
        }
    }
}
