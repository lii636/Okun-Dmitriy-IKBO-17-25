package depgraph.configuration;

import org.junit.jupiter.api.Test;

import depgraph.exception.DepgraphException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArgsParserTest {
    private final ArgsParser parser = new ArgsParser();

    @Test
    void parsesRemoteHappyPath() {
        ArgsParser.Start start = assertInstanceOf(
            ArgsParser.Start.class,
            parser.parse(new String[] {
                "--package", "requests",
                "--repo", "https://pypi.org/simple/",
                "--version", "2.31.0"
            })
        );

        Config config = start.config();
        assertEquals("requests", config.packageName());
        assertEquals("https://pypi.org/simple/", config.repo());
        assertEquals("2.31.0", config.version());
        assertFalse(config.testRepo());
        assertFalse(config.asciiTree());
        assertEquals(
            """
            package=requests
            repo=https://pypi.org/simple/
            version=2.31.0
            test-repo=false
            ascii-tree=false""",
            config.dump()
        );
    }

    @Test
    void parsesTestRepoAndAsciiFlags() {
        ArgsParser.Start start = assertInstanceOf(
            ArgsParser.Start.class,
            parser.parse(new String[] {
                "--package", "A",
                "--repo", "fixtures/repos/sample.txt",
                "--version", "1",
                "--test-repo",
                "--ascii-tree"
            })
        );

        Config config = start.config();
        assertTrue(config.testRepo());
        assertTrue(config.asciiTree());
        assertEquals(
            """
            package=A
            repo=fixtures/repos/sample.txt
            version=1
            test-repo=true
            ascii-tree=true""",
            config.dump()
        );
    }

    @Test
    void recognizesHelp() {
        assertInstanceOf(ArgsParser.Help.class, parser.parse(new String[] {"--help"}));
    }

    @Test
    void rejectsMissingRequiredOptions() {
        DepgraphException missingPackage = assertThrows(
            DepgraphException.class,
            () -> parser.parse(new String[] {
                "--repo", "https://pypi.org/simple/",
                "--version", "1.0"
            })
        );
        assertEquals("missing required option: --package", missingPackage.getMessage());

        DepgraphException missingVersion = assertThrows(
            DepgraphException.class,
            () -> parser.parse(new String[] {
                "--package", "requests",
                "--repo", "https://pypi.org/simple/"
            })
        );
        assertEquals("missing required option: --version", missingVersion.getMessage());
    }

    @Test
    void rejectsMissingValuesAndUnknownArgs() {
        assertEquals(
            "missing value for --package",
            assertThrows(DepgraphException.class, () -> parser.parse(new String[] {"--package"}))
                .getMessage()
        );
        assertEquals(
            "unknown option: --unknown",
            assertThrows(DepgraphException.class, () -> parser.parse(new String[] {"--unknown"}))
                .getMessage()
        );
        assertEquals(
            "unexpected argument: extra",
            assertThrows(DepgraphException.class, () -> parser.parse(new String[] {"extra"}))
                .getMessage()
        );
    }

    @Test
    void rejectsEmptyPackageAndVersion() {
        assertEquals(
            "empty package name",
            assertThrows(
                DepgraphException.class,
                () -> parser.parse(new String[] {
                    "--package", "  ",
                    "--repo", "https://pypi.org/simple/",
                    "--version", "1.0"
                })
            ).getMessage()
        );
        assertEquals(
            "empty version",
            assertThrows(
                DepgraphException.class,
                () -> parser.parse(new String[] {
                    "--package", "requests",
                    "--repo", "https://pypi.org/simple/",
                    "--version", " "
                })
            ).getMessage()
        );
    }

    @Test
    void rejectsNonUrlWithoutTestRepo() {
        DepgraphException exception = assertThrows(
            DepgraphException.class,
            () -> parser.parse(new String[] {
                "--package", "requests",
                "--repo", "fixtures/repos/sample.txt",
                "--version", "1.0"
            })
        );
        assertEquals(
            "repo must be an http(s) URL when --test-repo is not set",
            exception.getMessage()
        );
    }

    @Test
    void rejectsEmptyRepoInTestMode() {
        DepgraphException exception = assertThrows(
            DepgraphException.class,
            () -> parser.parse(new String[] {
                "--package", "A",
                "--repo", "  ",
                "--version", "1",
                "--test-repo"
            })
        );
        assertEquals("repo path is required when --test-repo is set", exception.getMessage());
    }

    @Test
    void rejectsInvalidPathInTestMode() {
        DepgraphException exception = assertThrows(
            DepgraphException.class,
            () -> parser.parse(new String[] {
                "--package", "A",
                "--repo", "bad\u0000path",
                "--version", "1",
                "--test-repo"
            })
        );
        assertEquals("invalid path: bad\u0000path", exception.getMessage());
    }
}
