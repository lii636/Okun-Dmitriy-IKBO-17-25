package shell.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import shell.exception.ShellException;

@RequiredArgsConstructor
public class LineParser {
    private static final int NOT_FOUND = -1;

    private final Function<String, String> environment;

    public LineParser() {
        this(System::getenv);
    }

    public List<String> parse(String line) {
        List<String> tokens = new ArrayList<>();
        int index = skipWhitespace(line, 0);
        while (index < line.length()) {
            index = readToken(line, index, tokens);
            index = skipWhitespace(line, index);
        }
        return List.copyOf(tokens);
    }

    private int readToken(String line, int start, List<String> tokens) {
        StringBuilder token = new StringBuilder();
        boolean quoted = false;
        int index = start;
        while (index < line.length() && !Character.isWhitespace(line.charAt(index))) {
            char current = line.charAt(index);
            if (current == '"' || current == '\'') {
                index = readQuoted(line, index + 1, current, token);
                quoted = true;
            } else if (current == '$') {
                index = appendVariable(line, index, token);
            } else {
                token.append(current);
                index++;
            }
        }
        if (quoted || !token.isEmpty()) {
            tokens.add(token.toString());
        }
        return index;
    }

    private int readQuoted(String line, int start, char quote, StringBuilder token) {
        int index = start;
        while (index < line.length()) {
            char current = line.charAt(index);
            if (current == quote) {
                return index + 1;
            }
            if (current == '$' && quote == '"') {
                index = appendVariable(line, index, token);
            } else {
                token.append(current);
                index++;
            }
        }
        throw new ShellException("unclosed quote");
    }

    private int appendVariable(String line, int dollar, StringBuilder token) {
        int start = dollar + 1;
        if (start >= line.length()) {
            token.append('$');
            return start;
        }
        if (line.charAt(start) == '{') {
            return appendBraced(line, start + 1, token);
        }
        int end = scanName(line, start);
        if (end == start) {
            token.append('$');
            return start;
        }
        token.append(valueOf(line.substring(start, end)));
        return end;
    }

    private int appendBraced(String line, int start, StringBuilder token) {
        int end = line.indexOf('}', start);
        if (end == NOT_FOUND) {
            throw new ShellException("unclosed variable");
        }
        String name = line.substring(start, end);
        if (!isName(name)) {
            throw new ShellException("invalid variable: " + name);
        }
        token.append(valueOf(name));
        return end + 1;
    }

    private int scanName(String line, int start) {
        if (start >= line.length() || !isNameStart(line.charAt(start))) {
            return start;
        }
        int index = start + 1;
        while (index < line.length() && isNamePart(line.charAt(index))) {
            index++;
        }
        return index;
    }

    private boolean isName(String name) {
        if (name.isEmpty() || !isNameStart(name.charAt(0))) {
            return false;
        }
        for (int index = 1; index < name.length(); index++) {
            if (!isNamePart(name.charAt(index))) {
                return false;
            }
        }
        return true;
    }

    private String valueOf(String name) {
        String value = environment.apply(name);
        return value == null ? "" : value;
    }

    private int skipWhitespace(String line, int start) {
        int index = start;
        while (index < line.length() && Character.isWhitespace(line.charAt(index))) {
            index++;
        }
        return index;
    }

    private boolean isNameStart(char character) {
        return Character.isLetter(character) || character == '_';
    }

    private boolean isNamePart(char character) {
        return isNameStart(character) || Character.isDigit(character);
    }
}
