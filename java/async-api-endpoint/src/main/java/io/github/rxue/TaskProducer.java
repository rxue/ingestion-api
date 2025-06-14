package io.github.rxue;

import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/ingestion")
public class TaskProducer {

    @Inject
    ConnectionFactory connectionFactory;

    @Path("start")
    @GET
    public String send() {
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            context.createProducer().send(context.createQueue("task"), "test message");
            System.out.println("task started");
        }
        return "sent";
    }
}
