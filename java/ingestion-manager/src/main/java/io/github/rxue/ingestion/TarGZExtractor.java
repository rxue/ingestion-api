package io.github.rxue.ingestion;

import io.github.rxue.ingestion.log.Log;
import jakarta.enterprise.context.Dependent;

import java.io.IOException;
import java.nio.file.Path;
@Dependent
public class TarGZExtractor implements StateDescriber {

    @Log
    public void extract(Path sourceTarGZFilePath, Path targetDirectoryPath) {
        System.out.println("source tar.gz is " + sourceTarGZFilePath);
        System.out.println("target dir is " + targetDirectoryPath);
        ProcessBuilder pb = new ProcessBuilder("tar", "-xzf", sourceTarGZFilePath.toString(), "-C", targetDirectoryPath.toString());
        pb.inheritIO();
        Process process = null;
        try {
            process = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int exitCode = 0;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (exitCode != 0) {
            throw new RuntimeException("extraction failed");
        }
    }

    @Override
    public String description() {
        return "EXTRACT data";
    }

    public static void main(String[] args) {
        TarGZExtractor extractor = new TarGZExtractor();
        Path directory = Path.of("/Users/ruixue/Downloads");
        extractor.extract(directory.resolve("archive-name.tar.gz"), directory);
    }
}
