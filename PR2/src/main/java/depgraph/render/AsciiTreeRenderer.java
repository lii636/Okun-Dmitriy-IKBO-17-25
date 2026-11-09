package depgraph.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import depgraph.graph.DependencyGraph;

public class AsciiTreeRenderer {
    private static final String BRANCH = "├── ";
    private static final String LAST = "└── ";
    private static final String PIPE = "│   ";
    private static final String SPACE = "    ";
    private static final String REVISIT = " (*)";

    public String render(DependencyGraph graph) {
        if (graph.visitOrder().isEmpty()) {
            return "";
        }
        String root = graph.visitOrder().get(0);
        List<String> lines = new ArrayList<>();
        Set<String> ancestors = new HashSet<>();
        Set<String> expanded = new HashSet<>();
        lines.add(root);
        appendChildren(graph, root, "", lines, ancestors, expanded);
        return String.join("\n", lines) + "\n";
    }

    private void appendChildren(
        DependencyGraph graph,
        String parent,
        String prefix,
        List<String> lines,
        Set<String> ancestors,
        Set<String> expanded
    ) {
        List<String> children = graph.adjacency().getOrDefault(parent, List.of());
        for (int index = 0; index < children.size(); index++) {
            String child = children.get(index);
            boolean last = index == children.size() - 1;
            String connector = last ? LAST : BRANCH;
            boolean seen = ancestors.contains(child) || expanded.contains(child);
            lines.add(prefix + connector + child + (seen ? REVISIT : ""));
            if (seen) {
                continue;
            }
            expanded.add(child);
            ancestors.add(parent);
            String nextPrefix = prefix + (last ? SPACE : PIPE);
            appendChildren(graph, child, nextPrefix, lines, ancestors, expanded);
            ancestors.remove(parent);
        }
    }
}
