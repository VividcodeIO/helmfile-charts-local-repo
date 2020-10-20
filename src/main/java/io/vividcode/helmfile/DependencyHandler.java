package io.vividcode.helmfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

class DependencyHandler {

    private static final Logger logger = LoggerFactory.getLogger(DependencyHandler.class);
    private final Yaml yaml;

    DependencyHandler() {
        Constructor constructor = new Constructor(DependencyLock.class);
        this.yaml = new Yaml(constructor);
    }

    List<Dependency> extract(Path path) {
        List<Dependency> results = new ArrayList<>();
        this.doExtract(path, results);
        return results;
    }

    private void doExtract(Path path, List<Dependency> results) {
        if (this.isLockfile(path)) {
            results.addAll(this.parseFile(path));
        } else {
            if (path.toFile().isDirectory()) {
                try {
                    Files.newDirectoryStream(path).forEach(p -> this.doExtract(p, results));
                } catch (IOException e) {
                    logger.warn("Failed to list directory {}", path.toAbsolutePath(), e);
                }
            }
        }
    }

    private List<Dependency> parseFile(Path file) {
        try {
            DependencyLock dependencyLock = this.yaml.load(Files.newInputStream(file));
            return dependencyLock.dependencies;
        } catch (IOException e) {
            logger.warn("Failed to parse lock file {}", file.toAbsolutePath(), e);
        }
        return Collections.emptyList();
    }

    private boolean isLockfile(Path path) {
        return path.toFile().isFile() && "helmfile.lock".equalsIgnoreCase(path.toFile().getName());
    }
}
