package io.github.rxue;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class Monitor {
    private final Path statusFilePath;

    public Monitor() {
        this.statusFilePath = null;
    }

    public Optional<String> getStatus() throws IOException {
        if (!Files.exists(statusFilePath)) {
            return Optional.empty();
        }
        List<String> statusLines = Files.readAllLines(statusFilePath);
        return Optional.of(statusLines.get(0));
    }
}
