package depgraph.render;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import depgraph.graph.DependencyGraph;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlantUmlRendererTest {
    private final PlantUmlRenderer renderer = new PlantUmlRenderer();

    @Test
    void rendersLinearEdgesInVisitOrder() {
        DependencyGraph graph = new DependencyGraph(
            List.of("A", "B", "C"),
            Map.of(
                "A", List.of("B"),
                "B", List.of("C"),
                "C", List.of()
            )
        );

        assertEquals(
            """
            @startuml
            left to right direction
            rectangle "A"
            rectangle "B"
            rectangle "C"
            "A" --> "B"
            "B" --> "C"
            @enduml
            """,
            renderer.render(graph)
        );
    }

    @Test
    void rendersDiamondEdges() {
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
            @startuml
            left to right direction
            rectangle "A"
            rectangle "B"
            rectangle "C"
            rectangle "D"
            "A" --> "B"
            "A" --> "C"
            "B" --> "D"
            "C" --> "D"
            @enduml
            """,
            renderer.render(graph)
        );
    }
}
