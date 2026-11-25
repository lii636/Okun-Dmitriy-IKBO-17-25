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
            case Abs abs -> executeAbs(abs, state);
        }
    }

    private void executeRead(Read read, MachineState state) {
        int address = state.register(read.baseRegister()) + read.offset();
        checkDataAddress(address);
        state.setRegister(read.destination(), state.data(address));
    }

    private void executeAbs(Abs abs, MachineState state) {
        int address = abs.memoryAddress();
        checkDataAddress(address);
        int value = state.data(address);
        state.setRegister(abs.destinationRegister(), absolute(value));
    }

    private int absolute(int value) {
        return value < 0 ? -value : value;
    }

    private void checkDataAddress(int address) {
        if (address < 0 || address >= MachineState.DATA_MEMORY_SIZE) {
            throw new UvmException("data address out of range: " + address);
        }
    }
}
