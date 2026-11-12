package depgraph.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import depgraph.exception.DepgraphException;
import depgraph.model.Dependency;

public final class TestRepoParser {
    private static final Pattern PACKAGE_NAME = Pattern.compile("[A-Z]+");

    private TestRepoParser() {
    }

    public static Map<String, List<Dependency>> parse(List<String> lines) {
        Map<String, List<String>> raw = parseRaw(lines);
        validateKnownPackages(raw);
        return toDependencies(raw);
    }

    private static Map<String, List<String>> parseRaw(List<String> lines) {
        Map<String, List<String>> raw = new LinkedHashMap<>();
        for (int index = 0; index < lines.size(); index++) {
            String line = stripComment(lines.get(index)).trim();
            if (line.isEmpty()) {
                continue;
            }
            putPackageLine(raw, line, index + 1);
        }
        if (raw.isEmpty()) {
            throw new DepgraphException("empty test repository");
        }
        return raw;
    }

    private static void putPackageLine(
        Map<String, List<String>> raw,
        String line,
        int lineNumber
    ) {
        int colon = line.indexOf(':');
        if (colon < 0) {
            throw new DepgraphException(
                "invalid test repository line " + lineNumber + ": " + line
            );
        }
        String packageName = line.substring(0, colon).trim();
        requirePackageName(packageName, lineNumber);
        if (raw.containsKey(packageName)) {
            throw new DepgraphException(
                "duplicate package in test repository: " + packageName
            );
        }
        raw.put(packageName, parseDependencyTokens(line.substring(colon + 1), lineNumber));
    }

    private static List<String> parseDependencyTokens(String rest, int lineNumber) {
        String trimmed = rest.trim();
        List<String> deps = new ArrayList<>();
        if (trimmed.isEmpty()) {
            return deps;
        }
        for (String token : trimmed.split("\\s+")) {
            if (token.isEmpty()) {
                continue;
            }
            requirePackageName(token, lineNumber);
            deps.add(token);
        }
        return deps;
    }

    private static void validateKnownPackages(Map<String, List<String>> raw) {
        for (Map.Entry<String, List<String>> entry : raw.entrySet()) {
            for (String dep : entry.getValue()) {
                if (!raw.containsKey(dep)) {
                    throw new DepgraphException(
                        "unknown package in test repository: " + dep
                    );
                }
            }
        }
    }

    private static Map<String, List<Dependency>> toDependencies(
        Map<String, List<String>> raw
    ) {
        Map<String, List<Dependency>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : raw.entrySet()) {
            Set<String> unique = new LinkedHashSet<>(entry.getValue());
            List<Dependency> dependencies = new ArrayList<>(unique.size());
            for (String name : unique) {
                dependencies.add(new Dependency(name));
            }
            result.put(entry.getKey(), List.copyOf(dependencies));
        }
        return Map.copyOf(result);
    }

    private static String stripComment(String line) {
        int hash = line.indexOf('#');
        return hash < 0 ? line : line.substring(0, hash);
    }

    private static void requirePackageName(String name, int lineNumber) {
        if (!PACKAGE_NAME.matcher(name).matches()) {
            throw new DepgraphException(
                "invalid package name on line " + lineNumber + ": " + name
            );
        }
    }
}
