package io.github.rxue;

public class Executor implements Runnable {
    private final String dataSourceURL;
    public Executor(String dataSourceURL) {
        this.dataSourceURL = dataSourceURL;
    }

    @Override
    public void run() {
        System.out.println("Thread " + Thread.currentThread().getId() + ":Received ingestion task download and query the data from source: " + dataSourceURL);
    }
}
