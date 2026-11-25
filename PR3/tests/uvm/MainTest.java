package uvm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainTest {
    @TempDir
    Path tempDir;

    @Test
    void assembleTestModePrintsSizeAndSpecBytes() throws Exception {
        Path output = tempDir.resolve("out.bin");
        String expectedBytes = Files.readString(
            Path.of("fixtures/expected/spec_bytes.txt"),
            StandardCharsets.UTF_8
        ).replace("\r\n", "\n").trim();

        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        int code = Main.run(
            new String[] {
                "assemble",
                "fixtures/asm/spec_tests.asm",
                output.toString(),
                "--test"
            },
            new PrintStream(outBuffer, true, StandardCharsets.UTF_8),
            new PrintStream(errBuffer, true, StandardCharsets.UTF_8)
        );

        assertEquals(0, code);
        assertEquals(
            "20\n" + expectedBytes,
            outBuffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n").trim()
        );
        assertTrue(errBuffer.toString(StandardCharsets.UTF_8).isEmpty());
        assertEquals(20, Files.size(output));
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
