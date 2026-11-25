package uvm.vm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import uvm.asm.AssemblerParser;
import uvm.binary.InstructionDecoder;
import uvm.binary.InstructionEncoder;
import uvm.exception.UvmException;
import uvm.ir.Abs;
import uvm.ir.Instruction;
import uvm.ir.Load;
import uvm.ir.Read;
import uvm.ir.Write;

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

    @Test
    void executesAtLastDataMemoryAddress() {
        int lastAddress = MachineState.DATA_MEMORY_SIZE - 1;
        List<Instruction> program = List.of(
            new Load(1, -42),
            new Write(1, lastAddress),
            new Abs(2, lastAddress),
            new Write(2, 0)
        );
        MachineState state = interpreter.run(encoder.encodeAll(program));
        assertEquals(-42, state.data(lastAddress));
        assertEquals(42, state.data(0));
    }

    @Test
    void rejectsNegativeEffectiveReadAddress() {
        List<Instruction> program = List.of(
            new Load(0, -1),
            new Read(0, 1, 0)
        );
        assertThrows(
            UvmException.class,
            () -> interpreter.run(encoder.encodeAll(program))
        );
    }
}
