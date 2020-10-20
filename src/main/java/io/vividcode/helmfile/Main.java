package io.vividcode.helmfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Path outputPath = Paths.get("/charts");

    static {
        try {
            Files.createDirectories(outputPath);
        } catch (IOException e) {
            logger.warn("Failed to create charts directory");
        }
    }

    public static void main(String[] args) {
        Path path = Paths.get(Optional.ofNullable(System.getenv("HELMFILE_PATH")).orElse("/opt/helmfile"));
        List<Dependency> dependencies = new DependencyHandler().extract(path);
        HelmHelper helmHelper = new HelmHelper();
        List<Path> charts = new ArrayList<>();
        dependencies.forEach(dependency -> helmHelper.process(dependency, charts));
        Map<Path, Path> uniqueCharts = new HashMap<>();
        charts.forEach(p -> uniqueCharts.put(p.getFileName(), p));
        uniqueCharts.values().forEach(Main::installChart);
    }

    private static void installChart(Path path) {
        try {
            Files.copy(path, outputPath.resolve(path.getFileName()));
            logger.info("Chart {} is installed", path.getFileName());
        } catch (IOException e) {
            logger.warn("Failed to copy chart from {} to {}", path.toAbsolutePath(), outputPath.toAbsolutePath(), e);
        }
    }
}
