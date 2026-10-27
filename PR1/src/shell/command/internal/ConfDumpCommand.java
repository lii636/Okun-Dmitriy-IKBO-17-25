package shell.command.internal;

import shell.command.Command;
import shell.command.CommandResult;
import shell.configuration.Config;
import shell.exception.ShellException;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfDumpCommand implements Command {
    private static final String NAME = "conf-dump";

    private final Config config;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public CommandResult execute(List<String> arguments) {
        if (!arguments.isEmpty()) {
            throw new ShellException(NAME + ": too many arguments");
        }
        return CommandResult.show(config.dump(Config.DEFAULT_VFS_NAME));
    }
}
