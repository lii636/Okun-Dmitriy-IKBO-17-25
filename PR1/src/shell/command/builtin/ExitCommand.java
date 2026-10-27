package shell.command.builtin;

import shell.command.Command;
import shell.command.CommandResult;

import java.util.List;

public class ExitCommand implements Command {
    private static final String NAME = "exit";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public CommandResult execute(List<String> arguments) {
        return CommandResult.exit();
    }
}
