package uvm.configuration;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import lombok.Value;
import uvm.exception.UvmException;

public class ArgsParser {
    private static final String HELP = "--help";
    private static final String TEST = "--test";
    private static final String ASSEMBLE = "assemble";
    private static final String RUN = "run";
    private static final int ASSEMBLE_POSITIONAL = 2;
    private static final int RUN_POSITIONAL = 3;
    private static final String RANGE_SEPARATOR = "-";
    private static final int POSITION_0 = 0;
    private static final int POSITION_1 = 1;
    private static final int POSITION_2 = 2;

    public Result parse(String[] args) {
        if (args.length == 0) {
            throw new UvmException("missing subcommand; try --help");
        }
        if (HELP.equals(args[0])) {
            return new Help();
        }
        if (ASSEMBLE.equals(args[0])) {
            return parseAssemble(args);
        }
        if (RUN.equals(args[0])) {
            return parseRun(args);
        }
        throw new UvmException("unknown subcommand: " + args[0]);
    }

    private Result parseAssemble(String[] args) {
        String input = null;
        String output = null;
        boolean testMode = false;
        int positionals = 0;
        for (int index = 1; index < args.length; index++) {
            String token = args[index];
            if (HELP.equals(token)) {
                return new Help();
            }
            if (TEST.equals(token)) {
                testMode = true;
                continue;
            }
            if (token.startsWith("--")) {
                throw new UvmException("unknown option: " + token);
            }
            if (positionals == 0) {
                input = token;
            } else if (positionals == 1) {
                output = token;
            } else {
                throw new UvmException("unexpected argument: " + token);
            }
            positionals += 1;
        }
        if (positionals != ASSEMBLE_POSITIONAL) {
            throw new UvmException(
                "assemble requires <input.asm> <output.bin> [--test]"
            );
        }
        return new Start(new AssembleConfig(toPath(input), toPath(output), testMode));
    }

    private Result parseRun(String[] args) {
        String program = null;
        String dump = null;
        String range = null;
        int positionals = 0;
        for (int index = 1; index < args.length; index++) {
            String token = args[index];
            if (HELP.equals(token)) {
                return new Help();
            }
            if (token.startsWith("--")) {
                throw new UvmException("unknown option: " + token);
            }
            if (positionals == POSITION_0) {
                program = token;
            } else if (positionals == POSITION_1) {
                dump = token;
            } else if (positionals == POSITION_2) {
                range = token;
            } else {
                throw new UvmException("unexpected argument: " + token);
            }
            positionals += 1;
        }
        if (positionals != RUN_POSITIONAL) {
            throw new UvmException(
                "run requires <program.bin> <dump.xml> <from>-<to>"
            );
        }
        int[] bounds = parseRange(range);
        return new Start(new RunConfig(
            toPath(program),
            toPath(dump),
            bounds[0],
            bounds[1]
        ));
    }

    private int[] parseRange(String range) {
        int separator = range.indexOf(RANGE_SEPARATOR);
        if (separator <= 0 || separator == range.length() - 1) {
            throw new UvmException("invalid dump range: " + range);
        }
        if (range.indexOf(RANGE_SEPARATOR, separator + 1) >= 0) {
            throw new UvmException("invalid dump range: " + range);
        }
        try {
            int from = Integer.parseInt(range.substring(0, separator));
            int to = Integer.parseInt(range.substring(separator + 1));
            if (from < 0 || to < from) {
                throw new UvmException("invalid dump range: " + range);
            }
            return new int[] {from, to};
        } catch (NumberFormatException exception) {
            throw new UvmException("invalid dump range: " + range, exception);
        }
    }

    private Path toPath(String value) {
        try {
            return Path.of(value);
        } catch (InvalidPathException exception) {
            throw new UvmException("invalid path: " + value, exception);
        }
    }

    public sealed interface Result permits Help, Start {
    }

    @Value
    public static class Help implements Result {
    }

    @Value
    public static class Start implements Result {
        Config config;
    }
}
