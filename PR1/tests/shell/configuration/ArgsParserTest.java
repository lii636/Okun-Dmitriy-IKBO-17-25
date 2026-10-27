package shell.configuration;

import org.junit.jupiter.api.Test;

import shell.exception.ShellException;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArgsParserTest {
    private final ArgsParser parser = new ArgsParser();

    @Test
    void readsOptionalPaths() {
        ArgsParser.Start both = assertInstanceOf(
            ArgsParser.Start.class,
            parser.parse(new String[] {"--vfs", "tree.xml", "--script", "start.txt"})
        );
        ArgsParser.Start scriptOnly = assertInstanceOf(
            ArgsParser.Start.class,
            parser.parse(new String[] {"--script", "start.txt"})
        );

        assertEquals(Path.of("tree.xml"), both.config().vfsPath());
        assertEquals(Path.of("start.txt"), both.config().scriptPath());
        assertNull(scriptOnly.config().vfsPath());
    }

    @Test
    void recognizesHelp() {
        assertInstanceOf(ArgsParser.Help.class, parser.parse(new String[] {"--help"}));
    }

    @Test
    void rejectsInvalidArguments() {
        assertThrows(ShellException.class, () -> parser.parse(new String[] {"--vfs"}));
        assertThrows(ShellException.class, () -> parser.parse(new String[] {"--unknown"}));
        assertThrows(ShellException.class, () -> parser.parse(new String[] {"extra"}));
    }

    @Test
    void rejectsPathJavaCannotRepresent() {
        ShellException exception = assertThrows(
            ShellException.class,
            () -> parser.parse(new String[] {"--vfs", "bad\u0000path"})
        );

        assertEquals("invalid path: bad\u0000path", exception.getMessage());
    }
}
