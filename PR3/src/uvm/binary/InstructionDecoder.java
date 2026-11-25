package uvm.binary;

import java.util.ArrayList;
import java.util.List;

import uvm.Opcodes;
import uvm.exception.UvmException;
import uvm.ir.Abs;
import uvm.ir.Instruction;
import uvm.ir.Load;
import uvm.ir.Read;
import uvm.ir.Write;

public class InstructionDecoder {
    private static final int BYTE_MASK = 0xFF;
    private static final int BITS_PER_BYTE = 8;
    private static final int A_MASK = (1 << Opcodes.A_BITS) - 1;
    private static final int B_MASK = (1 << Opcodes.B_BITS) - 1;
    private static final int LOAD_C_MASK = (1 << Opcodes.LOAD_C_BITS) - 1;
    private static final int READ_C_MASK = (1 << Opcodes.READ_C_BITS) - 1;
    private static final int READ_D_MASK = (1 << Opcodes.READ_D_BITS) - 1;
    private static final int WRITE_C_MASK = (1 << Opcodes.WRITE_C_BITS) - 1;
    private static final int ABS_C_MASK = (1 << Opcodes.ABS_C_BITS) - 1;

    public List<Instruction> decodeAll(byte[] machineCode) {
        if (machineCode.length % Opcodes.COMMAND_SIZE_BYTES != 0) {
            throw new UvmException(
                "binary size must be multiple of " + Opcodes.COMMAND_SIZE_BYTES
            );
        }
        List<Instruction> program = new ArrayList<>(
            machineCode.length / Opcodes.COMMAND_SIZE_BYTES
        );
        for (int offset = 0; offset < machineCode.length; offset += Opcodes.COMMAND_SIZE_BYTES) {
            program.add(decodeAt(machineCode, offset));
        }
        return List.copyOf(program);
    }

    public Instruction decodeAt(byte[] machineCode, int offset) {
        if (offset < 0 || offset + Opcodes.COMMAND_SIZE_BYTES > machineCode.length) {
            throw new UvmException("truncated command at offset " + offset);
        }
        long packed = 0;
        for (int index = 0; index < Opcodes.COMMAND_SIZE_BYTES; index++) {
            packed |= (long) (machineCode[offset + index] & BYTE_MASK)
                << (index * BITS_PER_BYTE);
        }
        return decodePacked(packed);
    }

    private Instruction decodePacked(long packed) {
        int opcode = (int) (packed & A_MASK);
        int fieldB = (int) ((packed >> Opcodes.A_BITS) & B_MASK);
        return switch (opcode) {
            case Opcodes.LOAD -> new Load(
                fieldB,
                signExtend(
                    (int) ((packed >> (Opcodes.A_BITS + Opcodes.B_BITS)) & LOAD_C_MASK),
                    Opcodes.LOAD_C_BITS
                )
            );
            case Opcodes.READ -> new Read(
                fieldB,
                (int) ((packed >> (Opcodes.A_BITS + Opcodes.B_BITS)) & READ_C_MASK),
                (int) ((packed >> (Opcodes.A_BITS + Opcodes.B_BITS + Opcodes.READ_C_BITS))
                    & READ_D_MASK)
            );
            case Opcodes.WRITE -> new Write(
                fieldB,
                (int) ((packed >> (Opcodes.A_BITS + Opcodes.B_BITS)) & WRITE_C_MASK)
            );
            case Opcodes.ABS -> new Abs(
                fieldB,
                (int) ((packed >> (Opcodes.A_BITS + Opcodes.B_BITS)) & ABS_C_MASK)
            );
            default -> throw new UvmException("unknown opcode: " + opcode);
        };
    }

    private int signExtend(int value, int bits) {
        int signBit = 1 << (bits - 1);
        if ((value & signBit) == 0) {
            return value;
        }
        return value | ~((1 << bits) - 1);
    }
}
