package io.github.rxue;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class IngestionManager implements Runnable {
    @Inject
    ConnectionFactory connectionFactory;
    @ConfigProperty(name = "QUEUE_NAME")
    private String queueName;

    private final ExecutorService dispatcher = Executors.newSingleThreadExecutor();

    private final ExecutorService executors = Executors.newFixedThreadPool(5);

    private volatile String dataSourceURL;

    void onStart(@Observes StartupEvent ev) {
        dispatcher.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        dispatcher.shutdown();
    }

    @Override
    public void run() {
        System.out.println("THREAD ID: " + Thread.currentThread().getId());
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            JMSConsumer consumer = context.createConsumer(context.createQueue(queueName));
            while (true) {
                Message message = consumer.receive();
                if (message == null) {
                    return;
                }
                dataSourceURL = message.getBody(String.class);
                executors.submit(new Executor(dataSourceURL));
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }


}
