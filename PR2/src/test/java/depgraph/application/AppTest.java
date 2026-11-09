package depgraph.application;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import depgraph.Main;
import depgraph.configuration.Config;
import depgraph.graph.BfsGraphBuilder;
import depgraph.graph.ReverseDepsFinder;
import depgraph.model.Dependency;
import depgraph.render.AsciiTreeRenderer;
import depgraph.render.GraphImagePresenter;
import depgraph.render.PlantUmlRenderer;
import depgraph.repository.PackageRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {
    private static final String BRANCH_PLANTUML = """
        @startuml
        left to right direction
        rectangle "A"
        rectangle "B"
        rectangle "C"
        "A" --> "B"
        "A" --> "C"
        @enduml
        """;

    private static final String BRANCH_ASCII = """
        A
        ├── B
        └── C
        """;

    @Test
    void printsPlantUmlAndPresentsImage() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        Session session = new Session(
            new PrintStream(out, true, StandardCharsets.UTF_8),
            new PrintStream(err, true, StandardCharsets.UTF_8)
        );
        Config config = new Config("A", "unused", "1", true, false, false);
        AtomicBoolean presented = new AtomicBoolean(false);
        GraphImagePresenter presenter = (plantUml, ignored) -> {
            presented.set(true);
            assertEquals(BRANCH_PLANTUML, plantUml);
        };

        newApp(config, branchRepository(), presenter).run(session);

        assertEquals(BRANCH_PLANTUML, out.toString(StandardCharsets.UTF_8));
        assertEquals("", err.toString(StandardCharsets.UTF_8));
        assertTrue(presented.get());
    }

    @Test
    void printsAsciiTreeWhenEnabled() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        Session session = new Session(
            new PrintStream(out, true, StandardCharsets.UTF_8),
            new PrintStream(err, true, StandardCharsets.UTF_8)
        );
        Config config = new Config("A", "unused", "1", true, true, false);

        newApp(config, branchRepository(), (plantUml, ignored) -> { }).run(session);

        assertEquals(
            BRANCH_PLANTUML + BRANCH_ASCII,
            out.toString(StandardCharsets.UTF_8)
        );
        assertEquals("", err.toString(StandardCharsets.UTF_8));
    }

    @Test
    void mainRunPrintsReverseDeps() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        int code = Main.run(
            new String[] {
                "--package", "C",
                "--repo", "fixtures/repos/linear.txt",
                "--version", "1",
                "--test-repo",
                "--reverse-deps"
            },
            new PrintStream(out, true, StandardCharsets.UTF_8),
            new PrintStream(err, true, StandardCharsets.UTF_8)
        );

        assertEquals(0, code);
        assertEquals("B\nA\n", out.toString(StandardCharsets.UTF_8));
        assertEquals("", err.toString(StandardCharsets.UTF_8));
    }

    @Test
    void mainRunRejectsReverseWithoutTestRepo() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        int code = Main.run(
            new String[] {
                "--package", "requests",
                "--repo", "https://pypi.org/simple/",
                "--version", "2.31.0",
                "--reverse-deps"
            },
            new PrintStream(out, true, StandardCharsets.UTF_8),
            new PrintStream(err, true, StandardCharsets.UTF_8)
        );

        assertEquals(1, code);
        assertEquals("", out.toString(StandardCharsets.UTF_8));
        assertTrue(
            err.toString(StandardCharsets.UTF_8)
                .contains("reverse dependencies require --test-repo")
        );
    }

    @Test
    void mainRunWritesErrorAndReturnsOne() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        int code = Main.run(
            new String[] {"--package", "requests"},
            new PrintStream(out, true, StandardCharsets.UTF_8),
            new PrintStream(err, true, StandardCharsets.UTF_8)
        );

        assertEquals(1, code);
        assertEquals("", out.toString(StandardCharsets.UTF_8));
        assertEquals(
            "missing required option: --repo\n",
            err.toString(StandardCharsets.UTF_8)
        );
    }

    private static App newApp(
        Config config,
        PackageRepository repository,
        GraphImagePresenter presenter
    ) {
        return new App(
            config,
            repository,
            new BfsGraphBuilder(),
            new ReverseDepsFinder(),
            new PlantUmlRenderer(),
            new AsciiTreeRenderer(),
            presenter
        );
    }

    private static PackageRepository branchRepository() {
        Map<String, List<String>> edges = Map.of(
            "A", List.of("B", "C"),
            "B", List.of(),
            "C", List.of()
        );
        return (name, version) -> edges.getOrDefault(name, List.of())
            .stream()
            .map(Dependency::new)
            .toList();
    }
}
