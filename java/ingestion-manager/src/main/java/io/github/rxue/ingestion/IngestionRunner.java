package io.github.rxue.ingestion;

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
        /*
        HttpFileDownloader httpFileDownloader = (HttpFileDownloader) Proxy.newProxyInstance(
                HttpFileDownloader.class.getClassLoader(),
                new Class[]{HttpFileDownloader.class},
                new StateLogger(new HttpFileDownloader()));*/
        HttpFileDownloader httpFileDownloader = new HttpFileDownloader();
        System.out.println("downloader proxy is " + httpFileDownloader);
        httpFileDownloader.download(dataSourceURL, MB * 100, downloadDirectoryPath);
    }
}
