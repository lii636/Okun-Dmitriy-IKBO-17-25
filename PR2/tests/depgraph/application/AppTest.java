package depgraph.application;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import depgraph.Main;
import depgraph.configuration.Config;
import depgraph.model.Dependency;
import depgraph.repository.PackageRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {
    @Test
    void printsDirectDependencies() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        Session session = new Session(
            new PrintStream(out, true, StandardCharsets.UTF_8),
            new PrintStream(err, true, StandardCharsets.UTF_8)
        );
        Config config = new Config(
            "requests",
            "https://pypi.org/simple/",
            "2.31.0",
            false,
            false
        );
        PackageRepository repository = (name, version) -> List.of(
            new Dependency("certifi"),
            new Dependency("idna")
        );

        new App(config, repository).run(session);

        assertEquals("certifi\nidna\n", out.toString(StandardCharsets.UTF_8));
        assertEquals("", err.toString(StandardCharsets.UTF_8));
    }

    @Test
    void mainRunRejectsTestRepoMode() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        int code = Main.run(
            new String[] {
                "--package", "A",
                "--repo", "fixtures/repos/sample.txt",
                "--version", "1",
                "--test-repo"
            },
            new PrintStream(out, true, StandardCharsets.UTF_8),
            new PrintStream(err, true, StandardCharsets.UTF_8)
        );

        assertEquals(1, code);
        assertEquals("", out.toString(StandardCharsets.UTF_8));
        assertTrue(
            err.toString(StandardCharsets.UTF_8)
                .contains("test repository mode is available from stage 3")
        );
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
