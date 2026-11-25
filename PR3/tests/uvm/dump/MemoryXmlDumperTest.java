package uvm.dump;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import uvm.vm.MachineState;

class MemoryXmlDumperTest {
    @TempDir
    Path tempDir;

    @Test
    void writesExpectedCopyArrayDump() throws Exception {
        MachineState state = new MachineState(new byte[0]);
        state.setData(100, 10);
        state.setData(101, 20);
        state.setData(102, 30);

        Path output = tempDir.resolve("dump.xml");
        new MemoryXmlDumper().dump(state, 100, 102, output);

        String expected = Files.readString(
            Path.of("fixtures/expected/copy_array_dump.xml"),
            StandardCharsets.UTF_8
        ).replace("\r\n", "\n").trim();
        String actual = Files.readString(output, StandardCharsets.UTF_8)
            .replace("\r\n", "\n")
            .trim();
        assertEquals(expected, actual);
    }
}
