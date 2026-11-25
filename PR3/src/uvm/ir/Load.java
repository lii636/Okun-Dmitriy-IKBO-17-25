package uvm.ir;

import uvm.Opcodes;

public record Load(int register, int constant) implements Instruction {
    @Override
    public String toFieldDump() {
        return "LOAD A=" + Opcodes.LOAD
            + " B=" + register
            + " C=" + constant;
    }
}
