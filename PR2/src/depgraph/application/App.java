package depgraph.application;

import java.util.List;

import depgraph.configuration.Config;
import depgraph.model.Dependency;
import depgraph.repository.PackageRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class App {
    private final Config config;
    private final PackageRepository repository;

    public void run(Session session) {
        List<Dependency> dependencies = repository.directDependencies(
            config.packageName(),
            config.version()
        );
        for (Dependency dependency : dependencies) {
            session.out().println(dependency.name());
        }
    }
}
