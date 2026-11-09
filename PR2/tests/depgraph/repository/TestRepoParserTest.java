package depgraph.repository;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import depgraph.exception.DepgraphException;
import depgraph.model.Dependency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestRepoParserTest {
    @Test
    void parsesAdjacencyList() {
        Map<String, List<Dependency>> packages = TestRepoParser.parse(List.of(
            "# comment",
            "A: B C",
            "B:",
            "C: "
        ));

        assertEquals(List.of(new Dependency("B"), new Dependency("C")), packages.get("A"));
        assertEquals(List.of(), packages.get("B"));
        assertEquals(List.of(), packages.get("C"));
    }

    @Test
    void rejectsUnknownDependency() {
        DepgraphException exception = assertThrows(
            DepgraphException.class,
            () -> TestRepoParser.parse(List.of("A: B", "B: C"))
        );
        assertEquals("unknown package in test repository: C", exception.getMessage());
    }

    @Test
    void rejectsLowercaseName() {
        assertThrows(
            DepgraphException.class,
            () -> TestRepoParser.parse(List.of("a: B", "B:"))
        );
    }
}
