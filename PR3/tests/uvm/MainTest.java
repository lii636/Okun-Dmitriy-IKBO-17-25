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
    void runCopyArrayWritesExpectedDump() throws Exception {
        Path binary = tempDir.resolve("copy.bin");
        Path dump = tempDir.resolve("dump.xml");

        assertEquals(
            0,
            Main.run(
                new String[] {
                    "assemble",
                    "fixtures/asm/copy_array.asm",
                    binary.toString()
                },
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8)
            )
        );

        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        int code = Main.run(
            new String[] {
                "run",
                binary.toString(),
                dump.toString(),
                "100-102"
            },
            new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
            new PrintStream(errBuffer, true, StandardCharsets.UTF_8)
        );

        assertEquals(0, code);
        assertTrue(errBuffer.toString(StandardCharsets.UTF_8).isEmpty());

        String expected = Files.readString(
            Path.of("fixtures/expected/copy_array_dump.xml"),
            StandardCharsets.UTF_8
        ).replace("\r\n", "\n").trim();
        String actual = Files.readString(dump, StandardCharsets.UTF_8)
            .replace("\r\n", "\n")
            .trim();
        assertEquals(expected, actual);
    }

    @Test
    void runAbsDemoWritesExpectedDump() throws Exception {
        Path binary = tempDir.resolve("abs.bin");
        Path dump = tempDir.resolve("abs.xml");

        assertEquals(
            0,
            Main.run(
                new String[] {
                    "assemble",
                    "fixtures/asm/abs_demo.asm",
                    binary.toString()
                },
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8)
            )
        );

        assertEquals(
            0,
            Main.run(
                new String[] {
                    "run",
                    binary.toString(),
                    dump.toString(),
                    "10-12"
                },
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8)
            )
        );

        String expected = Files.readString(
            Path.of("fixtures/expected/abs_demo_dump.xml"),
            StandardCharsets.UTF_8
        ).replace("\r\n", "\n").trim();
        String actual = Files.readString(dump, StandardCharsets.UTF_8)
            .replace("\r\n", "\n")
            .trim();
        assertEquals(expected, actual);
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

    @Test
    void missingBinaryReportsError() {
        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        int code = Main.run(
            new String[] {
                "run",
                tempDir.resolve("missing.bin").toString(),
                tempDir.resolve("dump.xml").toString(),
                "0-0"
            },
            new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
            new PrintStream(errBuffer, true, StandardCharsets.UTF_8)
        );
        assertEquals(1, code);
        assertTrue(errBuffer.toString(StandardCharsets.UTF_8).contains("cannot read binary"));
    }

    @Test
    void corruptBinaryReportsErrorWithoutWritingDump() throws Exception {
        Path binary = tempDir.resolve("corrupt.bin");
        Path dump = tempDir.resolve("dump.xml");
        Files.write(binary, new byte[] {1, 2, 3, 4});
        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();

        int code = Main.run(
            new String[] {
                "run",
                binary.toString(),
                dump.toString(),
                "0-0"
            },
            new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
            new PrintStream(errBuffer, true, StandardCharsets.UTF_8)
        );

        assertEquals(1, code);
        assertTrue(errBuffer.toString(StandardCharsets.UTF_8).contains("multiple of 5"));
        assertTrue(Files.notExists(dump));
    }
}
