package uvm.ir;

public sealed interface Instruction permits Load, Read, Write, Abs {
    String toFieldDump();
}
