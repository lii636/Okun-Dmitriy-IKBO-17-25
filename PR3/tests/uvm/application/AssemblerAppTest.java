package uvm.application;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import uvm.asm.AssemblerParser;
import uvm.binary.InstructionEncoder;
import uvm.configuration.AssembleConfig;

class AssemblerAppTest {
    @TempDir
    Path tempDir;

    @Test
    void writesBinaryAndPrintsSizeAndBytes() throws Exception {
        Path output = tempDir.resolve("out.bin");
        Path expectedBytes = Path.of("fixtures/expected/spec_bytes.txt");
        String expectedDump = Files.readString(expectedBytes, StandardCharsets.UTF_8)
            .replace("\r\n", "\n")
            .trim();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        AssembleConfig config = new AssembleConfig(
            Path.of("fixtures/asm/spec_tests.asm"),
            output,
            true
        );
        new AssemblerApp(config, new AssemblerParser(), new InstructionEncoder())
            .run(new Session(out, out));

        assertTrue(Files.exists(output));
        byte[] written = Files.readAllBytes(output);
        assertEquals(20, written.length);
        assertArrayEquals(
            new InstructionEncoder().encodeAll(
                new AssemblerParser().parseFile(Path.of("fixtures/asm/spec_tests.asm"))
            ),
            written
        );

        String stdout = buffer.toString(StandardCharsets.UTF_8)
            .replace("\r\n", "\n")
            .trim();
        assertEquals("20\n" + expectedDump, stdout);
    }
}
