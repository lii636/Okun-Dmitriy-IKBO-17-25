package uvm.ir;

import uvm.Opcodes;

public record Read(int baseRegister, int destination, int offset) implements Instruction {
    @Override
    public String toFieldDump() {
        return "READ A=" + Opcodes.READ
            + " B=" + baseRegister
            + " C=" + destination
            + " D=" + offset;
    }
}
