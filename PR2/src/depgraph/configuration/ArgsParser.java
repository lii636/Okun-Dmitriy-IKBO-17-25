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
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    public Result parse(String[] args) {
        String packageName = null;
        String repo = null;
        String version = null;
        boolean testRepo = false;
        boolean asciiTree = false;
        int index = 0;
        while (index < args.length) {
            String option = args[index];
            if (HELP.equals(option)) {
                return new Help();
            }
            if (TEST_REPO.equals(option)) {
                testRepo = true;
                index += 1;
                continue;
            }
            if (ASCII_TREE.equals(option)) {
                asciiTree = true;
                index += 1;
                continue;
            }
            String value = valueAfter(args, index);
            if (PACKAGE.equals(option)) {
                packageName = value;
            } else if (REPO.equals(option)) {
                repo = value;
            } else if (VERSION.equals(option)) {
                version = value;
            } else {
                reject(option);
            }
            index += 2;
        }
        return new Start(buildConfig(packageName, repo, version, testRepo, asciiTree));
    }

    private Config buildConfig(
        String packageName,
        String repo,
        String version,
        boolean testRepo,
        boolean asciiTree
    ) {
        requirePresent(PACKAGE, packageName);
        requirePresent(REPO, repo);
        requirePresent(VERSION, version);
        String trimmedPackage = packageName.trim();
        String trimmedRepo = repo.trim();
        String trimmedVersion = version.trim();
        if (trimmedPackage.isEmpty()) {
            throw new DepgraphException("empty package name");
        }
        if (trimmedVersion.isEmpty()) {
            throw new DepgraphException("empty version");
        }
        if (trimmedRepo.isEmpty()) {
            throw new DepgraphException(
                testRepo
                    ? "repo path is required when --test-repo is set"
                    : "empty repo"
            );
        }
        if (testRepo) {
            validatePath(trimmedRepo);
        } else if (!isHttpUrl(trimmedRepo)) {
            throw new DepgraphException(
                "repo must be an http(s) URL when --test-repo is not set"
            );
        }
        return new Config(trimmedPackage, trimmedRepo, trimmedVersion, testRepo, asciiTree);
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
