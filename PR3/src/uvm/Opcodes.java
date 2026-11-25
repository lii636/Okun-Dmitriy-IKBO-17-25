package uvm;

public final class Opcodes {
    public static final int LOAD = 14;
    public static final int READ = 5;
    public static final int WRITE = 6;
    public static final int ABS = 15;

    public static final int COMMAND_SIZE_BYTES = 5;

    public static final int A_BITS = 4;
    public static final int B_BITS = 7;
    public static final int LOAD_C_BITS = 16;
    public static final int READ_C_BITS = 7;
    public static final int READ_D_BITS = 14;
    public static final int WRITE_C_BITS = 23;
    public static final int ABS_C_BITS = 23;

    private Opcodes() {
    }
}
