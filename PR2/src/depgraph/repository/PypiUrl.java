package depgraph.repository;

import java.net.URI;
import java.util.Locale;

import depgraph.exception.DepgraphException;

public final class PypiUrl {
    private static final String SIMPLE_SUFFIX = "/simple";

    private PypiUrl() {
    }

    public static URI jsonMetadata(String repo, String packageName, String version) {
        String root = trimTrailingSlashes(repo.trim());
        String lower = root.toLowerCase(Locale.ROOT);
        if (lower.endsWith(SIMPLE_SUFFIX)) {
            root = root.substring(0, root.length() - SIMPLE_SUFFIX.length());
        }
        if (root.isEmpty()) {
            throw new DepgraphException("empty repository root");
        }
        String path = root + "/pypi/" + packageName + "/" + version + "/json";
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
