package depgraph.pypi;

import java.util.Locale;
import java.util.Optional;

public final class RequirementParser {
    private RequirementParser() {
    }

    public static boolean hasExtraMarker(String requirement) {
        return requirement.toLowerCase(Locale.ROOT).contains("extra ==");
    }

    public static Optional<String> packageName(String requirement) {
        String trimmed = requirement.trim();
        if (trimmed.isEmpty()) {
            return Optional.empty();
        }
        int end = trimmed.length();
        for (int index = 0; index < trimmed.length(); index++) {
            char character = trimmed.charAt(index);
            if (isNameTerminator(character)) {
                end = index;
                break;
            }
        }
        String name = trimmed.substring(0, end).trim();
        if (name.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(name);
    }

    private static boolean isNameTerminator(char character) {
        return character == ' '
            || character == ';'
            || character == '<'
            || character == '>'
            || character == '='
            || character == '!'
            || character == '~'
            || character == '[';
    }
}
