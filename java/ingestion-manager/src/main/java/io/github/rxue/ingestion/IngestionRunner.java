package io.github.rxue.ingestion;

import lombok.Builder;
import net.sf.cglib.proxy.Enhancer;

import java.nio.file.Path;

import static io.github.rxue.ingestion.HttpFileDownloader.MB;

public class IngestionRunner implements Runnable {
    private final String dataSourceURL;
    private final Path downloadDirectoryPath;
    public IngestionRunner(String dataSourceURL, Path downloadDirectoryPath) {
        this.dataSourceURL = dataSourceURL;
        this.downloadDirectoryPath = downloadDirectoryPath;
    }

    @Override
    public void run() {
        System.out.println("Execute the executor");
        StatusLogger statusLogger = new StatusLogger();
        HttpFileDownloader httpFileDownloader = getHttpFileDownloader(statusLogger);
        httpFileDownloader.download(dataSourceURL, MB * 100, downloadDirectoryPath);
        MessageCounter mc = messageCounter(statusLogger);
        System.out.println("going to call test");
        mc.test();
    }

    private static HttpFileDownloader getHttpFileDownloader(StatusLogger statusLogger) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HttpFileDownloader.class);
        enhancer.setCallback(statusLogger);
        HttpFileDownloader httpFileDownloader = (HttpFileDownloader) enhancer.create();
        return httpFileDownloader;
    }

    private static MessageCounter messageCounter(StatusLogger statusLogger) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(MessageCounter.class);
        enhancer.setCallback(statusLogger);
        MessageCounter messageCounter = (MessageCounter) enhancer.create(new Class[]{Path.class}, new Path[]{Path.of("")});
        System.out.println("going to return MessageCounter proxy::" + messageCounter);
        return messageCounter;
    }

}
