package depgraph.graph;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import depgraph.model.Dependency;
import depgraph.repository.PackageRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BfsGraphBuilderTest {
    private final BfsGraphBuilder builder = new BfsGraphBuilder();

    @Test
    void traversesLinearGraph() {
        PackageRepository repository = mapRepo(Map.of(
            "A", List.of("B"),
            "B", List.of("C"),
            "C", List.of()
        ));

        DependencyGraph graph = builder.build(repository, "A", "1");

        assertEquals(List.of("A", "B", "C"), graph.visitOrder());
    }

    @Test
    void traversesBranchInNeighborOrder() {
        PackageRepository repository = mapRepo(Map.of(
            "A", List.of("B", "C"),
            "B", List.of(),
            "C", List.of()
        ));

        assertEquals(
            List.of("A", "B", "C"),
            builder.build(repository, "A", "1").visitOrder()
        );
    }

    @Test
    void stopsOnCycle() {
        PackageRepository repository = mapRepo(Map.of(
            "A", List.of("B"),
            "B", List.of("A")
        ));

        DependencyGraph graph = builder.build(repository, "A", "1");

        assertEquals(List.of("A", "B"), graph.visitOrder());
        assertEquals(List.of("B"), graph.adjacency().get("A"));
        assertEquals(List.of("A"), graph.adjacency().get("B"));
    }

    @Test
    void traversesDiamondOnce() {
        PackageRepository repository = mapRepo(Map.of(
            "A", List.of("B", "C"),
            "B", List.of("D"),
            "C", List.of("D"),
            "D", List.of()
        ));

        assertEquals(
            List.of("A", "B", "C", "D"),
            builder.build(repository, "A", "1").visitOrder()
        );
    }

    @Test
    void usesEmptyVersionForChildren() {
        Map<String, String> seenVersions = new LinkedHashMap<>();
        PackageRepository repository = (name, version) -> {
            seenVersions.put(name, version);
            return switch (name) {
                case "A" -> List.of(new Dependency("B"));
                case "B" -> List.of();
                default -> List.of();
            };
        };

        builder.build(repository, "A", "2.31.0");

        assertEquals("2.31.0", seenVersions.get("A"));
        assertEquals("", seenVersions.get("B"));
    }

    private static PackageRepository mapRepo(Map<String, List<String>> edges) {
        return (name, version) -> edges.getOrDefault(name, List.of()).stream()
            .map(Dependency::new)
            .toList();
    }
}
