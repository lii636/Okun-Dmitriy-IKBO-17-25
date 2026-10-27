package shell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShellTest {
    @Test
    void printsHelp() {
        Capture capture = run(new String[] {"--help"}, "");

        assertEquals(0, capture.code());
        assertTrue(capture.out().contains("Usage:"));
        assertTrue(capture.err().isEmpty());
    }

    @Test
    void reportsMissingScript() {
        Capture script = run(new String[] {"--script", "missing.txt"}, "");

        assertEquals(1, script.code());
        assertTrue(script.err().contains("script file not found"));
    }

    @Test
    void reportsInvalidPath() {
        Capture capture = run(new String[] {"--script", "bad\u0000path"}, "");

        assertEquals(1, capture.code());
        assertTrue(capture.err().contains("invalid path: bad\u0000path"));
    }

    @Test
    void recordsVfsPathWithoutOpeningIt() {
        Capture capture = run(new String[] {"--vfs", "missing.xml"}, "exit\n");

        assertEquals(0, capture.code());
        assertTrue(capture.err().contains("vfs=missing.xml"));
        assertTrue(capture.err().contains("vfs-name=default"));
        assertFalse(capture.err().contains("vfs file not found"));
    }

    @Test
    void interactiveModePrintsPromptStubsAndRecoversFromErrors() {
        Capture capture = run(new String[0], "unknown\nls a b\nconf-dump\nexit\n");

        assertEquals(0, capture.code());
        assertTrue(capture.out().contains("default> "));
        assertTrue(capture.out().contains("ls: a b"));
        assertTrue(capture.out().contains("vfs-name=default"));
        assertTrue(capture.err().contains("unknown command: unknown"));
    }

    @Test
    void scriptEchoesLinesContinuesAfterErrorsAndStopsOnExit(@TempDir Path directory) throws Exception {
        Path script = directory.resolve("commands.txt");
        Files.writeString(script, "unknown\nls a b\nexit\nconf-dump\n");

        Capture capture = run(new String[] {"--script", script.toString()}, "");

        assertEquals(0, capture.code());
        assertTrue(capture.out().contains("default> unknown"));
        assertTrue(capture.out().contains("default> ls a b"));
        assertTrue(capture.out().contains("ls: a b"));
        assertFalse(capture.out().contains("default> conf-dump"));
        assertTrue(capture.err().contains("unknown command: unknown"));
        assertTrue(capture.err().contains("script=" + script));
    }

    private Capture run(String[] arguments, String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        int code = Main.run(
            arguments,
            new ByteArrayInputStream(input.getBytes()),
            new PrintStream(output, true),
            new PrintStream(errors, true)
        );
        return new Capture(code, output.toString(), errors.toString());
    }

    private record Capture(int code, String out, String err) {
    }
}
