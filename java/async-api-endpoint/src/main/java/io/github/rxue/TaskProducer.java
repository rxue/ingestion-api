package io.github.rxue;

import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/ingestion")
public class TaskProducer {
    @ConfigProperty(name = "QUEUE_NAME")
    private String queueName;

    @Inject
    ConnectionFactory connectionFactory;

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
}
