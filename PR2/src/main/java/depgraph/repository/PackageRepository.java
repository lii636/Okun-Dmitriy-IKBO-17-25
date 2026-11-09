package depgraph.repository;

import java.util.List;

import depgraph.model.Dependency;

public interface PackageRepository {
    List<Dependency> directDependencies(String packageName, String version);
}
