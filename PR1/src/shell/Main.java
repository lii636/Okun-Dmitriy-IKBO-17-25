package shell;

import shell.application.Session;
import shell.application.Shell;
import shell.command.Command;
import shell.command.CommandRegistry;
import shell.command.builtin.CdCommand;
import shell.command.builtin.ExitCommand;
import shell.command.builtin.LsCommand;
import shell.parsing.LineParser;

import java.util.List;

public class Main {
    private static final int SUCCESS = 0;

    public static void main(String[] args) {
        run(new Session(System.in, System.out, System.err));
    }

    public static int run(Session session) {
        Shell shell = new Shell(new CommandRegistry(commands()), new LineParser());
        shell.run(session);
        return SUCCESS;
    }

    private static List<Command> commands() {
        return List.of(new LsCommand(), new CdCommand(), new ExitCommand());
    }
}
