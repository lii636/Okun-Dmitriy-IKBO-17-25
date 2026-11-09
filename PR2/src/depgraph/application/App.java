package depgraph.application;

import depgraph.configuration.Config;
import depgraph.graph.BfsGraphBuilder;
import depgraph.graph.DependencyGraph;
import depgraph.repository.PackageRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class App {
    private final Config config;
    private final PackageRepository repository;
    private final BfsGraphBuilder graphBuilder;

    public App(Config config, PackageRepository repository) {
        this(config, repository, new BfsGraphBuilder());
    }

    public void run(Session session) {
        DependencyGraph graph = graphBuilder.build(
            repository,
            config.packageName(),
            config.version()
        );
        for (String packageName : graph.visitOrder()) {
            session.out().println(packageName);
        }
    }
}
