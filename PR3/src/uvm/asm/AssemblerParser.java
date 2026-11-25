package uvm.asm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uvm.Opcodes;
import uvm.exception.UvmException;
import uvm.ir.Abs;
import uvm.ir.Instruction;
import uvm.ir.Load;
import uvm.ir.Read;
import uvm.ir.Write;

public class AssemblerParser {
    private static final String MNEMONIC_LOAD = "LOAD";
    private static final String MNEMONIC_READ = "READ";
    private static final String MNEMONIC_WRITE = "WRITE";
    private static final String MNEMONIC_ABS = "ABS";
    private static final int LOAD_ARGS = 2;
    private static final int READ_ARGS = 3;
    private static final int WRITE_ARGS = 2;
    private static final int ABS_ARGS = 2;

    public List<Instruction> parseFile(Path path) {
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UvmException("cannot read source: " + path, exception);
        }
        return parseLines(lines);
    }

    public List<Instruction> parseLines(List<String> lines) {
        List<Instruction> program = new ArrayList<>();
        for (int index = 0; index < lines.size(); index++) {
            int lineNumber = index + 1;
            String stripped = stripComment(lines.get(index)).trim();
            if (stripped.isEmpty()) {
                continue;
            }
            program.add(parseStatement(stripped, lineNumber));
        }
        return List.copyOf(program);
    }

    private Instruction parseStatement(String statement, int lineNumber) {
        String[] parts = splitMnemonic(statement, lineNumber);
        String mnemonic = parts[0].toUpperCase(Locale.ROOT);
        int[] args = parseArgs(parts[1], lineNumber);
        return switch (mnemonic) {
            case MNEMONIC_LOAD -> parseLoad(args, lineNumber);
            case MNEMONIC_READ -> parseRead(args, lineNumber);
            case MNEMONIC_WRITE -> parseWrite(args, lineNumber);
            case MNEMONIC_ABS -> parseAbs(args, lineNumber);
            default -> throw error(lineNumber, "unknown mnemonic: " + parts[0]);
        };
    }

    private Load parseLoad(int[] args, int lineNumber) {
        requireArity(MNEMONIC_LOAD, args, LOAD_ARGS, lineNumber);
        int register = requireRange(args[0], Opcodes.B_BITS, "register", lineNumber);
        int constant = requireSignedRange(
            args[1],
            Opcodes.LOAD_C_BITS,
            "constant",
            lineNumber
        );
        return new Load(register, constant);
    }

    private Read parseRead(int[] args, int lineNumber) {
        requireArity(MNEMONIC_READ, args, READ_ARGS, lineNumber);
        int destination = requireRange(args[0], Opcodes.READ_C_BITS, "dst", lineNumber);
        int base = requireRange(args[1], Opcodes.B_BITS, "base", lineNumber);
        int offset = requireRange(args[2], Opcodes.READ_D_BITS, "offset", lineNumber);
        return new Read(base, destination, offset);
    }

    private Write parseWrite(int[] args, int lineNumber) {
        requireArity(MNEMONIC_WRITE, args, WRITE_ARGS, lineNumber);
        int source = requireRange(args[0], Opcodes.B_BITS, "src", lineNumber);
        int address = requireRange(args[1], Opcodes.WRITE_C_BITS, "addr", lineNumber);
        return new Write(source, address);
    }

    private Abs parseAbs(int[] args, int lineNumber) {
        requireArity(MNEMONIC_ABS, args, ABS_ARGS, lineNumber);
        int destination = requireRange(args[0], Opcodes.B_BITS, "dst", lineNumber);
        int address = requireRange(args[1], Opcodes.ABS_C_BITS, "addr", lineNumber);
        return new Abs(destination, address);
    }

    private String[] splitMnemonic(String statement, int lineNumber) {
        int space = indexOfWhitespace(statement);
        if (space < 0) {
            throw error(lineNumber, "missing operands: " + statement);
        }
        return new String[] {
            statement.substring(0, space),
            statement.substring(space + 1).trim()
        };
    }

    private int indexOfWhitespace(String text) {
        for (int index = 0; index < text.length(); index++) {
            if (Character.isWhitespace(text.charAt(index))) {
                return index;
            }
        }
        return -1;
    }

    private int[] parseArgs(String rawArgs, int lineNumber) {
        String[] tokens = rawArgs.split(",");
        int[] values = new int[tokens.length];
        for (int index = 0; index < tokens.length; index++) {
            String token = tokens[index].trim();
            if (token.isEmpty()) {
                throw error(lineNumber, "empty operand");
            }
            values[index] = parseInteger(token, lineNumber);
        }
        return values;
    }

    private int parseInteger(String token, int lineNumber) {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException exception) {
            throw error(lineNumber, "not an integer: " + token);
        }
    }

    private void requireArity(String mnemonic, int[] args, int expected, int line) {
        if (args.length != expected) {
            throw error(
                line,
                mnemonic + " expects " + expected + " argument(s), got " + args.length
            );
        }
    }

    private int requireRange(int value, int bits, String name, int lineNumber) {
        int max = (1 << bits) - 1;
        if (value < 0 || value > max) {
            throw error(
                lineNumber,
                name + " out of range 0.." + max + ": " + value
            );
        }
        return value;
    }

    private int requireSignedRange(int value, int bits, String name, int lineNumber) {
        int min = -(1 << (bits - 1));
        int max = (1 << (bits - 1)) - 1;
        if (value < min || value > max) {
            throw error(
                lineNumber,
                name + " out of range " + min + ".." + max + ": " + value
            );
        }
        return value;
    }

    private String stripComment(String line) {
        int hash = line.indexOf('#');
        int semicolon = line.indexOf(';');
        int cut = earliestComment(hash, semicolon);
        return cut < 0 ? line : line.substring(0, cut);
    }

    private int earliestComment(int hash, int semicolon) {
        if (hash < 0) {
            return semicolon;
        }
        if (semicolon < 0) {
            return hash;
        }
        return Math.min(hash, semicolon);
    }

    private UvmException error(int lineNumber, String detail) {
        return new UvmException("line " + lineNumber + ": " + detail);
    }
}
