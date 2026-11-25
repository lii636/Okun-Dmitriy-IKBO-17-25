package uvm.ir;

import uvm.Opcodes;

public record Abs(int destinationRegister, int memoryAddress) implements Instruction {
    @Override
    public String toFieldDump() {
        return "ABS A=" + Opcodes.ABS
            + " B=" + destinationRegister
            + " C=" + memoryAddress;
    }
}
