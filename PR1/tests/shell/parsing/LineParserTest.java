package shell.parsing;

import org.junit.jupiter.api.Test;

import shell.exception.ShellException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineParserTest {
    private final LineParser parser = new LineParser(name -> "HOME".equals(name) ? "/user" : null);

    @Test
    void tokenizesQuotesAndExpandsVariables() {
        assertEquals(List.of("ls", "a", "b"), parser.parse("ls a b"));
        assertEquals(List.of("ls", "a b"), parser.parse("ls \"a b\""));
        assertEquals(List.of("$HOME", "/user"), parser.parse("'$HOME' ${HOME}"));
    }

    @Test
    void handlesEmptyExpansionsAccordingToQuoteContext() {
        assertEquals(List.of("ls"), parser.parse("ls $MISSING"));
        assertEquals(List.of("ls", ""), parser.parse("ls \"$MISSING\""));
    }

    @Test
    void rejectsMalformedInput() {
        assertThrows(ShellException.class, () -> parser.parse("\"unclosed"));
        assertThrows(ShellException.class, () -> parser.parse("${INVALID-NAME}"));
        assertThrows(ShellException.class, () -> parser.parse("${UNCLOSED"));
    }
}
