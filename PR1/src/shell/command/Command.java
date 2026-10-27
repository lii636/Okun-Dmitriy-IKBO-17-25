package shell.command;

import java.util.List;

public interface Command {
    String name();

    CommandResult execute(List<String> arguments);
}
