package depgraph.graph;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import depgraph.model.Dependency;
import depgraph.repository.PackageRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReverseDepsFinderTest {
    private final ReverseDepsFinder finder = new ReverseDepsFinder();

    @Test
    void findsAncestorsOnLinearGraph() {
        Map<String, List<String>> edges = new LinkedHashMap<>();
        edges.put("A", List.of("B"));
        edges.put("B", List.of("C"));
        edges.put("C", List.of());

        assertEquals(
            List.of("B", "A"),
            finder.find(mapRepo(edges), edges.keySet(), "C")
        );
        assertEquals(List.of(), finder.find(mapRepo(edges), edges.keySet(), "A"));
    }

    @Test
    void findsDependentsOnDiamond() {
        Map<String, List<String>> edges = new LinkedHashMap<>();
        edges.put("A", List.of("B", "C"));
        edges.put("B", List.of("D"));
        edges.put("C", List.of("D"));
        edges.put("D", List.of());

        assertEquals(
            List.of("B", "C", "A"),
            finder.find(mapRepo(edges), edges.keySet(), "D")
        );
    }

    @Test
    void handlesCycle() {
        Map<String, List<String>> edges = new LinkedHashMap<>();
        edges.put("A", List.of("B"));
        edges.put("B", List.of("A"));

        assertEquals(
            List.of("B"),
            finder.find(mapRepo(edges), edges.keySet(), "A")
        );
    }

    private static PackageRepository mapRepo(Map<String, List<String>> edges) {
        return (name, version) -> edges.getOrDefault(name, List.of()).stream()
            .map(Dependency::new)
            .toList();
    }
}
