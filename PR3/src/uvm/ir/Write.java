package uvm.ir;

import uvm.Opcodes;

public record Write(int sourceRegister, int memoryAddress) implements Instruction {
    @Override
    public String toFieldDump() {
        return "WRITE A=" + Opcodes.WRITE
            + " B=" + sourceRegister
            + " C=" + memoryAddress;
    }
}
