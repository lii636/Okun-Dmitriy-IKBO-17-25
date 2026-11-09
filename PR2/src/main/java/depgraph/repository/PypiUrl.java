package depgraph.repository;

import java.net.URI;
import java.util.Locale;

import depgraph.exception.DepgraphException;

public final class PypiUrl {
    private static final String SIMPLE_SUFFIX = "/simple";

    private PypiUrl() {
    }

    public static URI jsonMetadata(String repo, String packageName) {
        return create(root(repo) + "/pypi/" + packageName + "/json");
    }

    public static URI jsonMetadata(String repo, String packageName, String version) {
        if (version == null || version.isBlank()) {
            return jsonMetadata(repo, packageName);
        }
        return create(root(repo) + "/pypi/" + packageName + "/" + version + "/json");
    }

    private static String root(String repo) {
        String value = trimTrailingSlashes(repo.trim());
        String lower = value.toLowerCase(Locale.ROOT);
        if (lower.endsWith(SIMPLE_SUFFIX)) {
            value = value.substring(0, value.length() - SIMPLE_SUFFIX.length());
        }
        if (value.isEmpty()) {
            throw new DepgraphException("empty repository root");
        }
        return value;
    }

    private static URI create(String path) {
        try {
            return URI.create(path);
        } catch (IllegalArgumentException exception) {
            throw new DepgraphException("invalid repository URL: " + path, exception);
        }
    }

    private static String trimTrailingSlashes(String value) {
        int end = value.length();
        while (end > 0 && value.charAt(end - 1) == '/') {
            end -= 1;
        }
        return value.substring(0, end);
    }
}
