package io.github.rxue.ingestion;

import net.sf.cglib.proxy.Enhancer;

import java.nio.file.Path;

import static io.github.rxue.ingestion.HttpFileDownloader.MB;
public class IngestionRunner implements Runnable {
    private final Path downloadDirectoryPath;
    private final String dataSourceURL;
    public IngestionRunner(Path downloadDirectoryPath, String dataSourceURL) {
        System.out.println("Executor started!!!!!!! DownloadDirectoryPath is " + downloadDirectoryPath + " and source URL is " + dataSourceURL);
        this.downloadDirectoryPath = downloadDirectoryPath;
        this.dataSourceURL = dataSourceURL;
    }

    @Override
    public void run() {
        System.out.println("Execute the executor");
        StateLogger stateLogger = new StateLogger();
        HttpFileDownloader httpFileDownloader = getHttpFileDownloader(stateLogger);
        httpFileDownloader.download(dataSourceURL, MB * 100, downloadDirectoryPath);
        MessageCounter mc = messageCounter(stateLogger);
        System.out.println("going to call test");
        mc.test();
    }

    private static HttpFileDownloader getHttpFileDownloader(StateLogger stateLogger) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HttpFileDownloader.class);
        enhancer.setCallback(stateLogger);
        HttpFileDownloader httpFileDownloader = (HttpFileDownloader) enhancer.create();
        return httpFileDownloader;
    }

    private static MessageCounter messageCounter(StateLogger stateLogger) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(MessageCounter.class);
        enhancer.setCallback(stateLogger);
        MessageCounter messageCounter = (MessageCounter) enhancer.create(new Class[]{Path.class}, new Path[]{Path.of("")});
        System.out.println("going to return MessageCounter proxy::" + messageCounter);
        return messageCounter;
    }

}
