package uvm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void assembleTestModePrintsSpecIr() throws Exception {
        String expected = Files.readString(
            Path.of("fixtures/expected/spec_ir.txt"),
            StandardCharsets.UTF_8
        ).replace("\r\n", "\n").trim();

        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        int code = Main.run(
            new String[] {
                "assemble",
                "fixtures/asm/spec_tests.asm",
                "out.bin",
                "--test"
            },
            new PrintStream(outBuffer, true, StandardCharsets.UTF_8),
            new PrintStream(errBuffer, true, StandardCharsets.UTF_8)
        );

        assertEquals(0, code);
        assertEquals(
            expected,
            outBuffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n").trim()
        );
        assertTrue(errBuffer.toString(StandardCharsets.UTF_8).isEmpty());
    }

    @Test
    void missingSourceReportsError() {
        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        int code = Main.run(
            new String[] {
                "assemble",
                "fixtures/asm/missing.asm",
                "out.bin",
                "--test"
            },
            new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
            new PrintStream(errBuffer, true, StandardCharsets.UTF_8)
        );
        assertEquals(1, code);
        assertTrue(errBuffer.toString(StandardCharsets.UTF_8).contains("cannot read source"));
    }
}
