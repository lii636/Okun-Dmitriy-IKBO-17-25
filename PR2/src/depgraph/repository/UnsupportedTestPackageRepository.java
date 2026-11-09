package depgraph.repository;

import java.util.List;

import depgraph.exception.DepgraphException;
import depgraph.model.Dependency;

public class UnsupportedTestPackageRepository implements PackageRepository {
    @Override
    public List<Dependency> directDependencies(String packageName, String version) {
        throw new DepgraphException(
            "test repository mode is available from stage 3"
        );
    }
}
