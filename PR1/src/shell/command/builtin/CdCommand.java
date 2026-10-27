package shell.command.builtin;

import shell.command.Command;
import shell.command.CommandResult;
import shell.exception.ShellException;
import shell.vfs.VirtualFileSystem;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CdCommand implements Command {
    private static final String NAME = "cd";
    private static final int MAX_ARGUMENTS = 1;

    private final VirtualFileSystem filesystem;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public CommandResult execute(List<String> arguments) {
        requireFew(arguments);
        if (arguments.isEmpty()) {
            filesystem.changeToRoot();
        } else {
            filesystem.changeDirectory(arguments.getFirst());
        }
        return CommandResult.continueExecution();
    }

    private void requireFew(List<String> arguments) {
        if (arguments.size() > MAX_ARGUMENTS) {
            throw new ShellException(NAME + ": too many arguments");
        }
    }
}
