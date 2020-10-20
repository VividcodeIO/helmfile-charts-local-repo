package io.vividcode.helmfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

class HelmHelper {

    private static final Logger logger = LoggerFactory.getLogger(HelmHelper.class);
    
    void process(Dependency dependency, List<Path> charts) {
        Path archivePath = this.pull(dependency).resolve(dependency.getName() + "-" + dependency.getVersion() + ".tgz");
        charts.add(archivePath);
        Path untarPath = this.pullUntar(dependency).resolve(dependency.getName());
        this.updateDependency(untarPath);
        this.findAllCharts(untarPath, charts);
    }

    private Path pull(Dependency dependency) {
        return this.execute(null, "helm", "pull", "--repo", dependency.getRepository(), "--version", dependency.getVersion(), dependency.getName());
    }

    private Path pullUntar(Dependency dependency) {
        return this.execute(null, "helm", "pull", "--repo", dependency.getRepository(), "--version", dependency.getVersion(), "--untar", dependency.getName());
    }

    private void updateDependency(Path path) {
        if (path.resolve("requirements.yaml").toFile().exists()) {
            logger.info("Update helm dependencies {}", path);
            this.execute(path, "helm", "dependencies", "update");
            try {
                Files.newDirectoryStream(path.resolve("charts"), entry -> entry.toFile().isDirectory()).forEach(this::updateDependency);
            } catch (IOException e) {
                logger.warn("Failed to update helm dependencies {}", path, e);
            }
        }
    }

    private void findAllCharts(Path path, List<Path> charts) {
        try {
            Files.newDirectoryStream(path).forEach(p -> {
                if (p.toFile().isDirectory()) {
                    this.findAllCharts(p, charts);
                } else if (p.toFile().isFile() && p.toFile().getName().endsWith(".tgz")) {
                    charts.add(p);
                }
            });
        } catch (IOException e) {
            logger.warn("Failed to find charts in {}", path, e);
        }
    }

    private Path execute(Path path, String... commands) {
        try {
            Path tempDirectory = path != null ? path : Files.createTempDirectory("helm");
            new ProcessExecutor(commands)
                    .directory(tempDirectory.toFile())
                    .redirectOutput(Slf4jStream.of(logger).asInfo())
                    .redirectError(Slf4jStream.of(logger).asWarn())
                    .timeout(30, TimeUnit.SECONDS)
                    .execute();
            return tempDirectory;
        } catch (Exception e) {
            logger.warn("Failed to execute command {}", commands, e);
        }
        return path;
    }


}
