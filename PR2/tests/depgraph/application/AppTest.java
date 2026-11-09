package depgraph.application;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import depgraph.Main;
import depgraph.configuration.Config;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    @Test
    void dumpsConfigToStdout() {
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

        new App(config).run(session);

        assertEquals(
            """
            package=requests
            repo=https://pypi.org/simple/
            version=2.31.0
            test-repo=false
            ascii-tree=false
            """,
            out.toString(StandardCharsets.UTF_8)
        );
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

    @Test
    void mainRunDumpsOnSuccess() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        int code = Main.run(
            new String[] {
                "--package", "requests",
                "--repo", "http://example.com/simple/",
                "--version", "1.0.0",
                "--ascii-tree"
            },
            new PrintStream(out, true, StandardCharsets.UTF_8),
            new PrintStream(err, true, StandardCharsets.UTF_8)
        );

        assertEquals(0, code);
        assertEquals(
            """
            package=requests
            repo=http://example.com/simple/
            version=1.0.0
            test-repo=false
            ascii-tree=true
            """,
            out.toString(StandardCharsets.UTF_8)
        );
        assertEquals("", err.toString(StandardCharsets.UTF_8));
    }
}
