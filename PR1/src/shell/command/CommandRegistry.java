package shell.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shell.exception.ShellException;

public class CommandRegistry {
    private final Map<String, Command> commands;

    public CommandRegistry(List<Command> commands) {
        Map<String, Command> registry = new HashMap<>();
        for (Command command : commands) {
            if (registry.put(command.name(), command) != null) {
                throw new IllegalStateException("duplicate command: " + command.name());
            }
        }
        this.commands = Map.copyOf(registry);
    }

    public Command require(String name) {
        Command command = commands.get(name);
        if (command == null) {
            throw new ShellException("unknown command: " + name);
        }
        return command;
    }
}
