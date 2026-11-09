package depgraph.render;

import java.util.ArrayList;
import java.util.List;

import depgraph.graph.DependencyGraph;

public class PlantUmlRenderer {
    public String render(DependencyGraph graph) {
        List<String> lines = new ArrayList<>();
        lines.add("@startuml");
        lines.add("left to right direction");
        for (String node : graph.visitOrder()) {
            lines.add("rectangle \"" + node + "\"");
        }
        for (String source : graph.visitOrder()) {
            List<String> targets = graph.adjacency().getOrDefault(source, List.of());
            for (String target : targets) {
                lines.add("\"" + source + "\" --> \"" + target + "\"");
            }
        }
        lines.add("@enduml");
        return String.join("\n", lines) + "\n";
    }
}
