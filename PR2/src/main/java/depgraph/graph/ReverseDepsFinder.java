package depgraph.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import depgraph.exception.DepgraphException;
import depgraph.model.Dependency;
import depgraph.repository.PackageRepository;

public class ReverseDepsFinder {
    public List<String> find(
        PackageRepository repository,
        Set<String> allPackages,
        String target
    ) {
        if (!allPackages.contains(target)) {
            throw new DepgraphException("unknown package: " + target);
        }
        Map<String, List<String>> reverse = invert(repository, allPackages);
        List<String> visitOrder = new ArrayList<>();
        Set<String> visited = new LinkedHashSet<>();
        Queue<String> queue = new ArrayDeque<>();

        visited.add(target);
        for (String neighbor : reverse.getOrDefault(target, List.of())) {
            if (visited.add(neighbor)) {
                queue.add(neighbor);
            }
        }

        while (!queue.isEmpty()) {
            String current = queue.remove();
            visitOrder.add(current);
            for (String neighbor : reverse.getOrDefault(current, List.of())) {
                if (visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }
        return List.copyOf(visitOrder);
    }

    private Map<String, List<String>> invert(
        PackageRepository repository,
        Set<String> allPackages
    ) {
        Map<String, List<String>> reverse = new LinkedHashMap<>();
        for (String name : allPackages) {
            reverse.putIfAbsent(name, new ArrayList<>());
        }
        for (String packageName : allPackages) {
            List<Dependency> dependencies = repository.directDependencies(
                packageName,
                ""
            );
            for (Dependency dependency : dependencies) {
                reverse.computeIfAbsent(dependency.name(), key -> new ArrayList<>())
                    .add(packageName);
            }
        }
        Map<String, List<String>> immutable = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : reverse.entrySet()) {
            immutable.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return Map.copyOf(immutable);
    }
}
