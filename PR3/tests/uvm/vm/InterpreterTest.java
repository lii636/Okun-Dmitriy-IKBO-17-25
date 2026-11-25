package uvm.vm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import uvm.asm.AssemblerParser;
import uvm.binary.InstructionDecoder;
import uvm.binary.InstructionEncoder;
import uvm.ir.Instruction;

class InterpreterTest {
    private final AssemblerParser parser = new AssemblerParser();
    private final InstructionEncoder encoder = new InstructionEncoder();
    private final Interpreter interpreter = new Interpreter(new InstructionDecoder());

    @Test
    void copiesArrayBetweenAddresses() {
        List<Instruction> program = parser.parseFile(
            Path.of("fixtures/asm/copy_array.asm")
        );
        MachineState state = interpreter.run(encoder.encodeAll(program));
        assertEquals(10, state.data(0));
        assertEquals(20, state.data(1));
        assertEquals(30, state.data(2));
        assertEquals(10, state.data(100));
        assertEquals(20, state.data(101));
        assertEquals(30, state.data(102));
    }

    @Test
    void computesAbsForNegativeZeroPositive() {
        List<Instruction> program = parser.parseFile(
            Path.of("fixtures/asm/abs_demo.asm")
        );
        MachineState state = interpreter.run(encoder.encodeAll(program));
        assertEquals(-7, state.data(0));
        assertEquals(0, state.data(1));
        assertEquals(5, state.data(2));
        assertEquals(7, state.data(10));
        assertEquals(0, state.data(11));
        assertEquals(5, state.data(12));
    }
}
