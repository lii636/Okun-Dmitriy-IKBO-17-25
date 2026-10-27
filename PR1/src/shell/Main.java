package shell;

import shell.application.Session;
import shell.application.Shell;
import shell.command.Command;
import shell.command.CommandRegistry;
import shell.command.internal.CdCommand;
import shell.command.internal.ConfDumpCommand;
import shell.command.internal.ExitCommand;
import shell.command.internal.LsCommand;
import shell.configuration.ArgsParser;
import shell.configuration.Config;
import shell.exception.ShellException;
import shell.parsing.LineParser;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

public class Main {
    private static final int SUCCESS = 0;
    private static final int ERROR = 1;
    private static final String USAGE =
        "Usage: shell [--vfs PATH] [--script PATH] [--help]";

    public static void main(String[] args) {
        int code = run(args, System.in, System.out, System.err);
        if (code != SUCCESS) {
            System.exit(code);
        }
    }

    public static int run(String[] args, InputStream input, PrintStream out, PrintStream err) {
        try {
            return start(args, new Session(input, out, err));
        } catch (ShellException exception) {
            err.println(exception.getMessage());
            return ERROR;
        }
    }

    private static int start(String[] args, Session session) {
        ArgsParser.Result result = new ArgsParser().parse(args);
        if (result instanceof ArgsParser.Help) {
            session.out().println(USAGE);
            return SUCCESS;
        }
        Config config = ((ArgsParser.Start) result).config();
        Shell shell = new Shell(
            new CommandRegistry(commands(config)),
            new LineParser(),
            config
        );
        shell.run(session);
        return SUCCESS;
    }

    private static List<Command> commands(Config config) {
        return List.of(
            new LsCommand(),
            new CdCommand(),
            new ConfDumpCommand(config),
            new ExitCommand()
        );
    }
}
