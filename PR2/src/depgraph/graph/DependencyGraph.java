package depgraph.graph;

import java.util.List;
import java.util.Map;

import lombok.Value;

@Value
public class DependencyGraph {
    List<String> visitOrder;
    Map<String, List<String>> adjacency;
}
