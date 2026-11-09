package depgraph.render;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import depgraph.application.Session;
import depgraph.exception.DepgraphException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantUmlJarRendererTest {
    @TempDir
    Path tempDir;

    @Test
    void rejectsMissingJar() {
        Path missingJar = tempDir.resolve("missing-plantuml.jar");
        PlantUmlJarRenderer renderer = new PlantUmlJarRenderer(
            (image, session) -> { },
            tempDir.resolve("out"),
            missingJar
        );
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        Session session = new Session(
            new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
            new PrintStream(err, true, StandardCharsets.UTF_8)
        );

        DepgraphException exception = assertThrows(
            DepgraphException.class,
            () -> renderer.present("@startuml\n@enduml\n", session)
        );

        assertTrue(exception.getMessage().contains("PlantUML JAR not found"));
        assertTrue(exception.getMessage().contains("fetch_plantuml.sh"));
    }

    @Test
    void startsIndexingAtOneWhenOutputEmpty() {
        Path out = tempDir.resolve("out");
        PlantUmlJarRenderer renderer = new PlantUmlJarRenderer(
            (image, session) -> { },
            out,
            tempDir.resolve("unused.jar")
        );

        assertEquals("graph_1", renderer.nextDiagramBaseName());
    }

    @Test
    void skipsOccupiedGraphGroups() throws IOException {
        Path out = tempDir.resolve("out");
        Files.createDirectories(out.resolve("graph_1"));
        Files.createDirectories(out.resolve("graph_2"));
        PlantUmlJarRenderer renderer = new PlantUmlJarRenderer(
            (image, session) -> { },
            out,
            tempDir.resolve("unused.jar")
        );

        assertEquals("graph_3", renderer.nextDiagramBaseName());
    }
}
