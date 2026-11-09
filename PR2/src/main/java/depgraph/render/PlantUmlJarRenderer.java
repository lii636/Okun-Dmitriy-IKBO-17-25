package depgraph.render;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import depgraph.application.Session;
import depgraph.exception.DepgraphException;

public class PlantUmlJarRenderer implements GraphImagePresenter {
    private static final String DEFAULT_JAR = "tools/plantuml.jar";
    private static final String ENV_JAR = "PLANTUML_JAR";
    private static final String OUTPUT_DIR = "out";
    private static final String DIAGRAM_NAME = "graph";
    private static final int FIRST_DIAGRAM_INDEX = 1;
    private static final long RENDER_TIMEOUT_SECONDS = 60L;

    private final ImageOpener imageOpener;
    private final Path outputDirectory;
    private final Path jarPath;

    public PlantUmlJarRenderer() {
        this(new DesktopImageOpener(), Path.of(OUTPUT_DIR), resolveJarPath());
    }

    public PlantUmlJarRenderer(ImageOpener imageOpener, Path outputDirectory, Path jarPath) {
        this.imageOpener = imageOpener;
        this.outputDirectory = outputDirectory;
        this.jarPath = jarPath;
    }

    @Override
    public void present(String plantUml, Session session) {
        ensureJarExists();
        try {
            Files.createDirectories(outputDirectory);
            String baseName = nextDiagramBaseName();
            Path groupDir = outputDirectory.resolve(baseName);
            Files.createDirectories(groupDir);
            Path pumlFile = groupDir.resolve(baseName + ".puml");
            Files.writeString(pumlFile, plantUml, StandardCharsets.UTF_8);
            runPlantUml(pumlFile, groupDir);
            Path pngFile = groupDir.resolve(baseName + ".png");
            if (!Files.isRegularFile(pngFile)) {
                throw new DepgraphException("PlantUML did not create " + pngFile);
            }
            session.err().println("image: " + pngFile.toAbsolutePath());
            imageOpener.open(pngFile, session);
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new DepgraphException("failed to render PlantUML image", exception);
        }
    }

    String nextDiagramBaseName() {
        int index = FIRST_DIAGRAM_INDEX;
        while (true) {
            String baseName = DIAGRAM_NAME + "_" + index;
            if (!Files.exists(outputDirectory.resolve(baseName))) {
                return baseName;
            }
            index++;
        }
    }

    private void ensureJarExists() {
        if (!Files.isRegularFile(jarPath)) {
            throw new DepgraphException(
                "PlantUML JAR not found: " + jarPath
                    + " (set " + ENV_JAR + " or run scripts/fetch_plantuml.sh)"
            );
        }
    }

    private void runPlantUml(Path pumlFile, Path groupDir)
        throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(
            "java",
            "-jar",
            jarPath.toAbsolutePath().toString(),
            "-tpng",
            "-o",
            groupDir.toAbsolutePath().toString(),
            pumlFile.toAbsolutePath().toString()
        );
        builder.redirectErrorStream(true);
        Process process = builder.start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        boolean finished = process.waitFor(RENDER_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new DepgraphException("PlantUML render timed out");
        }
        if (process.exitValue() != 0) {
            throw new DepgraphException("PlantUML render failed: " + output.trim());
        }
    }

    private static Path resolveJarPath() {
        String fromEnv = System.getenv(ENV_JAR);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return Path.of(fromEnv);
        }
        return Path.of(DEFAULT_JAR);
    }
}
