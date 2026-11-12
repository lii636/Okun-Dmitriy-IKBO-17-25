package depgraph.configuration;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Locale;

import depgraph.exception.DepgraphException;
import lombok.Value;

public class ArgsParser {
    private static final String HELP = "--help";
    private static final String PACKAGE = "--package";
    private static final String REPO = "--repo";
    private static final String VERSION = "--version";
    private static final String TEST_REPO = "--test-repo";
    private static final String ASCII_TREE = "--ascii-tree";
    private static final String REVERSE_DEPS = "--reverse-deps";
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    public Result parse(String[] args) {
        ParseState state = new ParseState();
        int index = 0;
        while (index < args.length) {
            String option = args[index];
            if (HELP.equals(option)) {
                return new Help();
            }
            Integer flagsConsumed = applyFlag(option, state);
            if (flagsConsumed != null) {
                index += flagsConsumed;
                continue;
            }
            applyValuedOption(option, valueAfter(args, index), state);
            index += 2;
        }
        return new Start(buildConfig(state));
    }

    private Integer applyFlag(String option, ParseState state) {
        if (TEST_REPO.equals(option)) {
            state.testRepo = true;
            return 1;
        }
        if (ASCII_TREE.equals(option)) {
            state.asciiTree = true;
            return 1;
        }
        if (REVERSE_DEPS.equals(option)) {
            state.reverseDeps = true;
            return 1;
        }
        return null;
    }

    private void applyValuedOption(String option, String value, ParseState state) {
        if (PACKAGE.equals(option)) {
            state.packageName = value;
        } else if (REPO.equals(option)) {
            state.repo = value;
        } else if (VERSION.equals(option)) {
            state.version = value;
        } else {
            reject(option);
        }
    }

    private Config buildConfig(ParseState state) {
        requirePresent(PACKAGE, state.packageName);
        requirePresent(REPO, state.repo);
        requirePresent(VERSION, state.version);
        String trimmedPackage = requireNonBlank(
            PACKAGE,
            state.packageName,
            "empty package name"
        );
        String trimmedRepo = requireRepoValue(state.repo, state.testRepo);
        String trimmedVersion = requireNonBlank(
            VERSION,
            state.version,
            "empty version"
        );
        return new Config(
            trimmedPackage,
            trimmedRepo,
            trimmedVersion,
            state.testRepo,
            state.asciiTree,
            state.reverseDeps
        );
    }

    private String requireNonBlank(String option, String value, String emptyMessage) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new DepgraphException(emptyMessage);
        }
        return trimmed;
    }

    private String requireRepoValue(String repo, boolean testRepo) {
        String trimmedRepo = repo.trim();
        if (trimmedRepo.isEmpty()) {
            throw new DepgraphException(
                testRepo
                    ? "repo path is required when --test-repo is set"
                    : "empty repo"
            );
        }
        validateRepo(trimmedRepo, testRepo);
        return trimmedRepo;
    }

    private void validateRepo(String trimmedRepo, boolean testRepo) {
        if (testRepo) {
            validatePath(trimmedRepo);
            return;
        }
        if (!isHttpUrl(trimmedRepo)) {
            throw new DepgraphException(
                "repo must be an http(s) URL when --test-repo is not set"
            );
        }
    }

    private void requirePresent(String option, String value) {
        if (value == null) {
            throw new DepgraphException("missing required option: " + option);
        }
    }

    private String valueAfter(String[] args, int optionIndex) {
        String option = args[optionIndex];
        if (!PACKAGE.equals(option) && !REPO.equals(option) && !VERSION.equals(option)) {
            reject(option);
        }
        int valueIndex = optionIndex + 1;
        if (valueIndex >= args.length || args[valueIndex].startsWith("--")) {
            throw new DepgraphException("missing value for " + option);
        }
        return args[valueIndex];
    }

    private void validatePath(String value) {
        try {
            Path.of(value);
        } catch (InvalidPathException exception) {
            throw new DepgraphException("invalid path: " + value, exception);
        }
    }

    private boolean isHttpUrl(String value) {
        String lower = value.toLowerCase(Locale.ROOT);
        return lower.startsWith(HTTP) || lower.startsWith(HTTPS);
    }

    private void reject(String argument) {
        String kind = argument.startsWith("--") ? "unknown option: " : "unexpected argument: ";
        throw new DepgraphException(kind + argument);
    }

    private static final class ParseState {
        private String packageName;
        private String repo;
        private String version;
        private boolean testRepo;
        private boolean asciiTree;
        private boolean reverseDeps;
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
