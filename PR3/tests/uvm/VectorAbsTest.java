package uvm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class VectorAbsTest {
    @TempDir
    Path tempDir;

    static Stream<Arguments> examples() {
        return Stream.of(
            Arguments.of("vector_abs_ex1"),
            Arguments.of("vector_abs_ex2"),
            Arguments.of("vector_abs_ex3")
        );
    }

    @ParameterizedTest
    @MethodSource("examples")
    void runsVectorAbsExample(String name) throws Exception {
        Path binary = tempDir.resolve(name + ".bin");
        Path dump = tempDir.resolve(name + ".xml");
        Path asm = Path.of("fixtures/asm/" + name + ".asm");
        Path expectedPath = Path.of("fixtures/expected/" + name + "_dump.xml");

        assertEquals(0, runMain("assemble", asm.toString(), binary.toString()));
        assertEquals(
            0,
            runMain("run", binary.toString(), dump.toString(), "0-9")
        );

        String expected = Files.readString(expectedPath, StandardCharsets.UTF_8)
            .replace("\r\n", "\n")
            .trim();
        String actual = Files.readString(dump, StandardCharsets.UTF_8)
            .replace("\r\n", "\n")
            .trim();
        assertEquals(expected, actual);
    }

    private int runMain(String... args) {
        return Main.run(
            args,
            new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
            new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8)
        );
    }
}
