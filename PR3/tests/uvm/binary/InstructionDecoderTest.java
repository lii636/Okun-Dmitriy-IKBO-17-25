package uvm.binary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import uvm.exception.UvmException;
import uvm.ir.Abs;
import uvm.ir.Instruction;
import uvm.ir.Load;
import uvm.ir.Read;
import uvm.ir.Write;

class InstructionDecoderTest {
    private final InstructionEncoder encoder = new InstructionEncoder();
    private final InstructionDecoder decoder = new InstructionDecoder();

    @Test
    void roundTripsSpecInstructions() {
        List<Instruction> original = List.of(
            new Load(8, 266),
            new Read(24, 109, 499),
            new Write(7, 469),
            new Abs(33, 461)
        );
        assertEquals(original, decoder.decodeAll(encoder.encodeAll(original)));
    }

    @Test
    void roundTripsSignedLoadConstant() {
        List<Instruction> original = List.of(new Load(0, -7));
        assertEquals(original, decoder.decodeAll(encoder.encodeAll(original)));
    }

    @Test
    void decodesSpecBytesWithoutUsingEncoder() {
        byte[] bytes = bytes(
            0x8E, 0x50, 0x08, 0x00, 0x00,
            0x85, 0x69, 0xCF, 0x07, 0x00,
            0x76, 0xA8, 0x0E, 0x00, 0x00,
            0x1F, 0x6A, 0x0E, 0x00, 0x00
        );
        assertEquals(
            List.of(
                new Load(8, 266),
                new Read(24, 109, 499),
                new Write(7, 469),
                new Abs(33, 461)
            ),
            decoder.decodeAll(bytes)
        );
    }

    @Test
    void rejectsTruncatedCommand() {
        assertThrows(UvmException.class, () -> decoder.decodeAll(new byte[4]));
        assertThrows(UvmException.class, () -> decoder.decodeAt(new byte[5], 1));
    }

    @Test
    void rejectsUnknownOpcode() {
        assertThrows(UvmException.class, () -> decoder.decodeAll(bytes(0, 0, 0, 0, 0)));
    }

    @Test
    void rejectsNonZeroReservedBits() {
        assertThrows(
            UvmException.class,
            () -> decoder.decodeAll(bytes(0x0E, 0, 0, 0, 0x80))
        );
    }

    private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int index = 0; index < values.length; index++) {
            result[index] = (byte) values[index];
        }
        return result;
    }
}
