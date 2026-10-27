package shell;

import org.junit.jupiter.api.Test;
import shell.application.Session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShellTest {
    @Test
    void printsPromptAndStubCommands() {
        Capture capture = run("ls\ncd /tmp\nls a b\nexit\n");

        assertEquals(0, capture.code());
        assertEquals(
            "default> ls\ndefault> cd: /tmp\ndefault> ls: a b\ndefault> ",
            capture.out()
        );
        assertTrue(capture.err().isEmpty());
    }

    @Test
    void recoversFromErrorsAndStopsOnExit() {
        Capture errors = run("unknown\n\"\n\"\"\nexit\n");
        Capture stopped = run("exit\nls\n");

        assertEquals(0, errors.code());
        assertEquals("default> default> default> default> ", errors.out());
        assertEquals(
            "unknown command: unknown\nunclosed quote\nempty command\n",
            errors.err()
        );
        assertEquals("default> ", stopped.out());
        assertFalse(stopped.out().contains("ls"));
    }

    @Test
    void handlesBlankLinesAndEndOfInput() {
        Capture blank = run("\nexit\n");
        Capture endOfInput = run("");

        assertEquals("default> default> ", blank.out());
        assertEquals("default> ", endOfInput.out());
        assertTrue(blank.err().isEmpty());
        assertTrue(endOfInput.err().isEmpty());
    }

    private Capture run(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        Session session = new Session(
            new ByteArrayInputStream(input.getBytes()),
            new PrintStream(output, true),
            new PrintStream(errors, true)
        );
        int code = Main.run(session);
        return new Capture(code, output.toString(), errors.toString());
    }

    private record Capture(int code, String out, String err) {
    }
}
