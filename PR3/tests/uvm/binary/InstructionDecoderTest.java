package uvm.binary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

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
}
