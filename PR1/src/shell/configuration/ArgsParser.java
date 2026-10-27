package shell.configuration;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import shell.exception.ShellException;

public class ArgsParser {
    private static final String HELP = "--help";
    private static final String VFS = "--vfs";
    private static final String SCRIPT = "--script";

    public Result parse(String[] args) {
        Path vfs = null;
        Path script = null;
        int index = 0;
        while (index < args.length) {
            String option = args[index];
            if (HELP.equals(option)) {
                return new Help();
            }
            String value = valueAfter(args, index);
            if (VFS.equals(option)) {
                vfs = path(value);
            } else if (SCRIPT.equals(option)) {
                script = path(value);
            } else {
                reject(option);
            }
            index += 2;
        }
        return new Start(new Config(vfs, script));
    }

    private String valueAfter(String[] args, int optionIndex) {
        String option = args[optionIndex];
        if (!VFS.equals(option) && !SCRIPT.equals(option)) {
            reject(option);
        }
        int valueIndex = optionIndex + 1;
        if (valueIndex >= args.length || args[valueIndex].startsWith("--")) {
            throw new ShellException("missing value for " + option);
        }
        return args[valueIndex];
    }

    private Path path(String value) {
        try {
            return Path.of(value);
        } catch (InvalidPathException exception) {
            throw new ShellException("invalid path: " + value, exception);
        }
    }

    private void reject(String argument) {
        String kind = argument.startsWith("--") ? "unknown option: " : "unexpected argument: ";
        throw new ShellException(kind + argument);
    }

    public sealed interface Result permits Help, Start {
    }

    public record Help() implements Result {
    }

    public record Start(Config config) implements Result {
    }
}
