package uvm.binary;

import java.util.ArrayList;
import java.util.List;

import uvm.Opcodes;
import uvm.ir.Abs;
import uvm.ir.Instruction;
import uvm.ir.Load;
import uvm.ir.Read;
import uvm.ir.Write;

public class InstructionEncoder {
    private static final int BYTE_MASK = 0xFF;
    private static final int BITS_PER_BYTE = 8;

    public byte[] encodeAll(List<Instruction> program) {
        byte[] result = new byte[program.size() * Opcodes.COMMAND_SIZE_BYTES];
        int offset = 0;
        for (Instruction instruction : program) {
            byte[] command = encode(instruction);
            System.arraycopy(command, 0, result, offset, command.length);
            offset += command.length;
        }
        return result;
    }

    public byte[] encode(Instruction instruction) {
        long packed = switch (instruction) {
            case Load load -> packLoad(load);
            case Read read -> packRead(read);
            case Write write -> packWrite(write);
            case Abs abs -> packAbs(abs);
        };
        return toLittleEndian(packed);
    }

    public String toByteDump(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < bytes.length; index++) {
            if (index > 0) {
                builder.append(", ");
            }
            builder.append(String.format("0x%02X", bytes[index] & BYTE_MASK));
        }
        return builder.toString();
    }

    public List<String> toCommandDumps(List<Instruction> program) {
        List<String> lines = new ArrayList<>(program.size());
        for (Instruction instruction : program) {
            lines.add(toByteDump(encode(instruction)));
        }
        return List.copyOf(lines);
    }

    private long packLoad(Load load) {
        return Opcodes.LOAD
            | ((long) load.register() << Opcodes.A_BITS)
            | ((long) load.constant() << (Opcodes.A_BITS + Opcodes.B_BITS));
    }

    private long packRead(Read read) {
        return Opcodes.READ
            | ((long) read.baseRegister() << Opcodes.A_BITS)
            | ((long) read.destination() << (Opcodes.A_BITS + Opcodes.B_BITS))
            | ((long) read.offset() << (Opcodes.A_BITS + Opcodes.B_BITS + Opcodes.READ_C_BITS));
    }

    private long packWrite(Write write) {
        return Opcodes.WRITE
            | ((long) write.sourceRegister() << Opcodes.A_BITS)
            | ((long) write.memoryAddress() << (Opcodes.A_BITS + Opcodes.B_BITS));
    }

    private long packAbs(Abs abs) {
        return Opcodes.ABS
            | ((long) abs.destinationRegister() << Opcodes.A_BITS)
            | ((long) abs.memoryAddress() << (Opcodes.A_BITS + Opcodes.B_BITS));
    }

    private byte[] toLittleEndian(long packed) {
        byte[] bytes = new byte[Opcodes.COMMAND_SIZE_BYTES];
        for (int index = 0; index < Opcodes.COMMAND_SIZE_BYTES; index++) {
            bytes[index] = (byte) ((packed >> (index * BITS_PER_BYTE)) & BYTE_MASK);
        }
        return bytes;
    }
}
