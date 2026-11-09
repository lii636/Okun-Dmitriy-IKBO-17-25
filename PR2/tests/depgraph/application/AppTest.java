package depgraph.application;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import depgraph.Main;
import depgraph.configuration.Config;
import depgraph.model.Dependency;
import depgraph.repository.PackageRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    @Test
    void printsBfsVisitOrder() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        Session session = new Session(
            new PrintStream(out, true, StandardCharsets.UTF_8),
            new PrintStream(err, true, StandardCharsets.UTF_8)
        );
        Config config = new Config("A", "unused", "1", true, false);
        Map<String, List<String>> edges = Map.of(
            "A", List.of("B", "C"),
            "B", List.of(),
            "C", List.of()
        );
        PackageRepository repository = (name, version) -> edges.getOrDefault(name, List.of())
            .stream()
            .map(Dependency::new)
            .toList();

        new App(config, repository).run(session);

        assertEquals("A\nB\nC\n", out.toString(StandardCharsets.UTF_8));
        assertEquals("", err.toString(StandardCharsets.UTF_8));
    }

    @Test
    void mainRunWalksTestRepoFixture() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        int code = Main.run(
            new String[] {
                "--package", "A",
                "--repo", "fixtures/repos/linear.txt",
                "--version", "1",
                "--test-repo"
            },
            new PrintStream(out, true, StandardCharsets.UTF_8),
            new PrintStream(err, true, StandardCharsets.UTF_8)
        );

        assertEquals(0, code);
        assertEquals("A\nB\nC\n", out.toString(StandardCharsets.UTF_8));
        assertEquals("", err.toString(StandardCharsets.UTF_8));
    }

    @Test
    void mainRunWritesErrorAndReturnsOne() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        int code = Main.run(
            new String[] {"--package", "requests"},
            new PrintStream(out, true, StandardCharsets.UTF_8),
            new PrintStream(err, true, StandardCharsets.UTF_8)
        );

        assertEquals(1, code);
        assertEquals("", out.toString(StandardCharsets.UTF_8));
        assertEquals(
            "missing required option: --repo\n",
            err.toString(StandardCharsets.UTF_8)
        );
    }
}
