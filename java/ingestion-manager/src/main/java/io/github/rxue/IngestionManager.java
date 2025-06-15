package io.github.rxue;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class IngestionManager implements Runnable {
    @Inject
    ConnectionFactory connectionFactory;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private volatile String dataSourceURL;

    public String getDataSourceURL() {
        return dataSourceURL;
    }

    void onStart(@Observes StartupEvent ev) {
        executor.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        executor.shutdown();
    }

    @Override
    public void run() {
        System.out.println("THREAD ID: " + Thread.currentThread().getId());
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            JMSConsumer consumer = context.createConsumer(context.createQueue("task"));
            while (true) {
                Message message = consumer.receive();
                if (message == null) {
                    return;
                }
                dataSourceURL = message.getBody(String.class);
                System.out.println("received:::::" + dataSourceURL);
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }


}
