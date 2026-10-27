package shell.command.builtin;

import shell.command.Command;
import shell.command.CommandResult;

import java.util.List;

public class CdCommand implements Command {
    private static final String NAME = "cd";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public CommandResult execute(List<String> arguments) {
        return CommandResult.show(format(arguments));
    }

    private String format(List<String> arguments) {
        return arguments.isEmpty() ? NAME : NAME + ": " + String.join(" ", arguments);
    }
}
