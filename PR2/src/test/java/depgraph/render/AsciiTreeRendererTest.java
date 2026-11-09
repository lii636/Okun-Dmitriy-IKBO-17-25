package depgraph.render;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import depgraph.graph.DependencyGraph;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AsciiTreeRendererTest {
    private final AsciiTreeRenderer renderer = new AsciiTreeRenderer();

    @Test
    void rendersBranch() {
        DependencyGraph graph = new DependencyGraph(
            List.of("A", "B", "C"),
            Map.of(
                "A", List.of("B", "C"),
                "B", List.of(),
                "C", List.of()
            )
        );

        assertEquals(
            """
            A
            ├── B
            └── C
            """,
            renderer.render(graph)
        );
    }

    @Test
    void marksSharedDiamondNode() {
        DependencyGraph graph = new DependencyGraph(
            List.of("A", "B", "C", "D"),
            Map.of(
                "A", List.of("B", "C"),
                "B", List.of("D"),
                "C", List.of("D"),
                "D", List.of()
            )
        );

        assertEquals(
            """
            A
            ├── B
            │   └── D
            └── C
                └── D (*)
            """,
            renderer.render(graph)
        );
    }

    @Test
    void marksCycleWithoutInfiniteExpansion() {
        DependencyGraph graph = new DependencyGraph(
            List.of("A", "B"),
            Map.of(
                "A", List.of("B"),
                "B", List.of("A")
            )
        );

        assertEquals(
            """
            A
            └── B
                └── A (*)
            """,
            renderer.render(graph)
        );
    }
}
