package io.github.rxue.executor;

import java.lang.reflect.Proxy;
import java.nio.file.Path;

import static io.github.rxue.executor.HttpFileDownloader.MB;
public class Executor implements Runnable {
    private final Path downloadDirectoryPath;
    private final String dataSourceURL;
    public Executor(Path downloadDirectoryPath, String dataSourceURL) {
        System.out.println("Executor started!!!!!!! DownloadDirectoryPath is " + downloadDirectoryPath + " and source URL is " + dataSourceURL);
        this.downloadDirectoryPath = downloadDirectoryPath;
        this.dataSourceURL = dataSourceURL;
    }

    @Override
    public void run() {
        System.out.println("Execute");
        HttpFileDownloader httpFileDownloader = (HttpFileDownloader) Proxy.newProxyInstance(
                HttpFileDownloader.class.getClassLoader(),
                new Class[]{HttpFileDownloader.class},
                new StateLogger(new HttpFileDownloader()));
        System.out.println("downloader proxy is " + httpFileDownloader);
        httpFileDownloader.download(dataSourceURL, MB * 100, downloadDirectoryPath);
    }
}
