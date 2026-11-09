package depgraph.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import depgraph.exception.DepgraphException;
import depgraph.model.Dependency;

public class FilePackageRepository implements PackageRepository {
    private final Map<String, List<Dependency>> packages;

    public FilePackageRepository(Path path) {
        this.packages = load(path);
    }

    FilePackageRepository(Map<String, List<Dependency>> packages) {
        this.packages = packages;
    }

    @Override
    public List<Dependency> directDependencies(String packageName, String version) {
        List<Dependency> dependencies = packages.get(packageName);
        if (dependencies == null) {
            throw new DepgraphException("unknown package: " + packageName);
        }
        return dependencies;
    }

    private static Map<String, List<Dependency>> load(Path path) {
        if (!Files.isRegularFile(path)) {
            throw new DepgraphException("test repository file not found: " + path);
        }
        try {
            return TestRepoParser.parse(Files.readAllLines(path));
        } catch (IOException exception) {
            throw new DepgraphException(
                "failed to read test repository: " + path,
                exception
            );
        }
    }
}
