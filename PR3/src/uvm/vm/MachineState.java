package uvm.vm;

import uvm.Opcodes;
import uvm.exception.UvmException;

public class MachineState {
    public static final int REGISTER_COUNT = 1 << Opcodes.B_BITS;
    public static final int DATA_MEMORY_SIZE = 4096;

    private final int[] registers = new int[REGISTER_COUNT];
    private final int[] dataMemory = new int[DATA_MEMORY_SIZE];
    private final byte[] commandMemory;

    public MachineState(byte[] commandMemory) {
        this.commandMemory = commandMemory.clone();
    }

    public byte[] commandMemory() {
        return commandMemory;
    }

    public int register(int index) {
        checkRegister(index);
        return registers[index];
    }

    public void setRegister(int index, int value) {
        checkRegister(index);
        registers[index] = value;
    }

    public int data(int address) {
        checkDataAddress(address);
        return dataMemory[address];
    }

    public void setData(int address, int value) {
        checkDataAddress(address);
        dataMemory[address] = value;
    }

    private void checkRegister(int index) {
        if (index < 0 || index >= REGISTER_COUNT) {
            throw new UvmException("register out of range: " + index);
        }
    }

    private void checkDataAddress(int address) {
        if (address < 0 || address >= DATA_MEMORY_SIZE) {
            throw new UvmException("data address out of range: " + address);
        }
    }
}
