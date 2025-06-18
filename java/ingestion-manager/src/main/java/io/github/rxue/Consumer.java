package io.github.rxue;

import io.github.rxue.ingestion.IngestionRunner;
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
public class Consumer implements Runnable {
    private final ConnectionFactory connectionFactory;
    private final String queueName;
    private final ExecutorService executor;
    private final IngestionRunner ingestionRunner;

    @Inject
    public Consumer(ConnectionFactory connectionFactory,
                    @ConfigProperty(name = "QUEUE_NAME") String queueName, IngestionRunner ingestionRunner) {
        this.connectionFactory = connectionFactory;
        this.queueName = queueName;
        this.ingestionRunner = ingestionRunner;
        this.executor = Executors.newSingleThreadExecutor();
    }

    void onStart(@Observes StartupEvent ev) {
        executor.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        executor.shutdown();
    }

    @Override
    public void run() {
        System.out.println("Dispatcher with THREAD ID: " + Thread.currentThread().getId());
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            JMSConsumer consumer = context.createConsumer(context.createQueue(queueName));
            while (true) {
                Message message = consumer.receive();
                if (message == null) {
                    return;
                }
                String receivedString = message.getBody(String.class);
                ingestionRunner.run(receivedString);
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }


}
