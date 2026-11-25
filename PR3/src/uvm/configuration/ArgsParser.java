package uvm.configuration;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import lombok.Value;
import uvm.exception.UvmException;

public class ArgsParser {
    private static final String HELP = "--help";
    private static final String TEST = "--test";
    private static final String ASSEMBLE = "assemble";
    private static final int ASSEMBLE_POSITIONAL = 2;

    public Result parse(String[] args) {
        if (args.length == 0) {
            throw new UvmException("missing subcommand; try --help");
        }
        if (HELP.equals(args[0])) {
            return new Help();
        }
        if (!ASSEMBLE.equals(args[0])) {
            throw new UvmException("unknown subcommand: " + args[0]);
        }
        return parseAssemble(args);
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
        return new Start(new Config(toPath(input), toPath(output), testMode));
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
