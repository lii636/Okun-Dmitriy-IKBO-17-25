package uvm.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import uvm.asm.AssemblerParser;
import uvm.configuration.Config;

class AssemblerAppTest {
    @Test
    void testModeMatchesExpectedSpecIr() throws Exception {
        Path expectedPath = Path.of("fixtures/expected/spec_ir.txt");
        String expected = Files.readString(expectedPath, StandardCharsets.UTF_8)
            .replace("\r\n", "\n")
            .trim();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        Config config = new Config(
            Path.of("fixtures/asm/spec_tests.asm"),
            Path.of("out.bin"),
            true
        );
        new AssemblerApp(config, new AssemblerParser())
            .run(new Session(out, out));

        String actual = buffer.toString(StandardCharsets.UTF_8)
            .replace("\r\n", "\n")
            .trim();
        assertEquals(expected, actual);
    }
}
