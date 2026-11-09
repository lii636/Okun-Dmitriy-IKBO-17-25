package depgraph.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import depgraph.model.Dependency;
import depgraph.repository.PackageRepository;

public class BfsGraphBuilder {
    private static final String CHILD_VERSION = "";

    public DependencyGraph build(
        PackageRepository repository,
        String root,
        String rootVersion
    ) {
        Map<String, List<String>> adjacency = new LinkedHashMap<>();
        List<String> visitOrder = new ArrayList<>();
        Set<String> visited = new LinkedHashSet<>();
        Queue<Node> queue = new ArrayDeque<>();

        queue.add(new Node(root, rootVersion));
        visited.add(root);

        while (!queue.isEmpty()) {
            Node current = queue.remove();
            visitOrder.add(current.name());
            List<Dependency> dependencies = repository.directDependencies(
                current.name(),
                current.version()
            );
            List<String> neighbors = new ArrayList<>(dependencies.size());
            for (Dependency dependency : dependencies) {
                String neighbor = dependency.name();
                neighbors.add(neighbor);
                if (visited.add(neighbor)) {
                    queue.add(new Node(neighbor, CHILD_VERSION));
                }
            }
            adjacency.put(current.name(), List.copyOf(neighbors));
        }

        return new DependencyGraph(List.copyOf(visitOrder), Map.copyOf(adjacency));
    }

    private record Node(String name, String version) {
    }
}
