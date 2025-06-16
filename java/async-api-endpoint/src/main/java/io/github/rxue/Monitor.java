package io.github.rxue;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class Monitor {
    private final String dataPath;

    public Monitor(@ConfigProperty(name = "DATA_PATH") String dataPath) {
        this.dataPath = dataPath;
    }

    public String getStatus() throws IOException {
        Path statusFilePath = Path.of(dataPath, "status.txt");
        if (!Files.exists(statusFilePath)) {
            return "not started";
        }
        List<String> statusLines = Files.readAllLines(statusFilePath);
        return statusLines.get(0);
    }
}
