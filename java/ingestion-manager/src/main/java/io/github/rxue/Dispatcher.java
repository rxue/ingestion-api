package io.github.rxue;

import io.github.rxue.ingestion.IngestionRunner;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class Dispatcher implements Runnable {
    @Inject
    ConnectionFactory connectionFactory;

    @ConfigProperty(name = "QUEUE_NAME")
    private String queueName;

    @ConfigProperty(name = "DOWNLOAD_DIR")
    private String downloadDirectory;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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
                IngestionRunner ingestionRunner = new IngestionRunner(Path.of(downloadDirectory), message.getBody(String.class));
                ingestionRunner.run();
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }


}
