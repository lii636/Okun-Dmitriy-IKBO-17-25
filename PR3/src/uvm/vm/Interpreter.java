package uvm.vm;

import java.util.List;

import uvm.binary.InstructionDecoder;
import uvm.exception.UvmException;
import uvm.ir.Abs;
import uvm.ir.Instruction;
import uvm.ir.Load;
import uvm.ir.Read;
import uvm.ir.Write;

public class Interpreter {
    private final InstructionDecoder decoder;

    public Interpreter(InstructionDecoder decoder) {
        this.decoder = decoder;
    }

    public MachineState run(byte[] machineCode) {
        List<Instruction> program = decoder.decodeAll(machineCode);
        MachineState state = new MachineState(machineCode);
        for (Instruction instruction : program) {
            execute(instruction, state);
        }
        return state;
    }

    private void execute(Instruction instruction, MachineState state) {
        switch (instruction) {
            case Load load -> state.setRegister(load.register(), load.constant());
            case Read read -> executeRead(read, state);
            case Write write -> state.setData(
                write.memoryAddress(),
                state.register(write.sourceRegister())
            );
            case Abs ignored -> throw new UvmException(
                "ABS is not implemented in stage 3"
            );
        }
    }

    private void executeRead(Read read, MachineState state) {
        int base = state.register(read.baseRegister());
        int address = base + read.offset();
        if (address < 0 || address >= MachineState.DATA_MEMORY_SIZE) {
            throw new UvmException("data address out of range: " + address);
        }
        state.setRegister(read.destination(), state.data(address));
    }
}
