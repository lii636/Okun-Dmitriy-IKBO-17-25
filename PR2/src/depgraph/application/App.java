package depgraph.application;

import java.util.List;

import depgraph.configuration.Config;
import depgraph.exception.DepgraphException;
import depgraph.graph.BfsGraphBuilder;
import depgraph.graph.DependencyGraph;
import depgraph.graph.ReverseDepsFinder;
import depgraph.repository.FilePackageRepository;
import depgraph.repository.PackageRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class App {
    private final Config config;
    private final PackageRepository repository;
    private final BfsGraphBuilder graphBuilder;
    private final ReverseDepsFinder reverseDepsFinder;

    public App(Config config, PackageRepository repository) {
        this(config, repository, new BfsGraphBuilder(), new ReverseDepsFinder());
    }

    public void run(Session session) {
        if (config.reverseDeps()) {
            printLines(session, reverseDependents());
            return;
        }
        DependencyGraph graph = graphBuilder.build(
            repository,
            config.packageName(),
            config.version()
        );
        printLines(session, graph.visitOrder());
    }

    private List<String> reverseDependents() {
        if (!(repository instanceof FilePackageRepository fileRepository)) {
            throw new DepgraphException("reverse dependencies require --test-repo");
        }
        return reverseDepsFinder.find(
            fileRepository,
            fileRepository.packageNames(),
            config.packageName()
        );
    }

    private void printLines(Session session, List<String> lines) {
        for (String line : lines) {
            session.out().println(line);
        }
    }
}
