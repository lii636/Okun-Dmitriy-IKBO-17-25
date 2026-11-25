package uvm.binary;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import uvm.asm.AssemblerParser;
import uvm.exception.UvmException;
import uvm.ir.Abs;
import uvm.ir.Instruction;
import uvm.ir.Load;
import uvm.ir.Read;
import uvm.ir.Write;

class InstructionEncoderTest {
    private final InstructionEncoder encoder = new InstructionEncoder();

    @Test
    void encodesSpecLoadBytes() {
        assertArrayEquals(
            bytes(0x8E, 0x50, 0x08, 0x00, 0x00),
            encoder.encode(new Load(8, 266))
        );
    }

    @Test
    void encodesSpecReadBytes() {
        assertArrayEquals(
            bytes(0x85, 0x69, 0xCF, 0x07, 0x00),
            encoder.encode(new Read(24, 109, 499))
        );
    }

    @Test
    void encodesSpecWriteBytes() {
        assertArrayEquals(
            bytes(0x76, 0xA8, 0x0E, 0x00, 0x00),
            encoder.encode(new Write(7, 469))
        );
    }

    @Test
    void encodesSpecAbsBytes() {
        assertArrayEquals(
            bytes(0x1F, 0x6A, 0x0E, 0x00, 0x00),
            encoder.encode(new Abs(33, 461))
        );
    }

    @Test
    void fixtureMatchesExpectedByteDump() throws Exception {
        List<Instruction> program = new AssemblerParser()
            .parseFile(Path.of("fixtures/asm/spec_tests.asm"));
        String expected = Files.readString(Path.of("fixtures/expected/spec_bytes.txt"))
            .replace("\r\n", "\n")
            .trim();
        String actual = String.join("\n", encoder.toCommandDumps(program));
        assertEquals(expected, actual);
    }

    @Test
    void rejectsInvalidDirectIrInsteadOfTruncatingFields() {
        assertThrows(UvmException.class, () -> encoder.encode(new Load(128, 0)));
        assertThrows(UvmException.class, () -> encoder.encode(new Load(0, 32768)));
        assertThrows(UvmException.class, () -> encoder.encode(new Read(0, 0, 16384)));
        assertThrows(UvmException.class, () -> encoder.encode(new Write(0, 1 << 23)));
        assertThrows(UvmException.class, () -> encoder.encode(new Abs(-1, 0)));
    }

    private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int index = 0; index < values.length; index++) {
            result[index] = (byte) values[index];
        }
        return result;
    }
}
