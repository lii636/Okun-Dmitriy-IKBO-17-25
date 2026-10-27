package shell;

import shell.application.Session;
import shell.application.Shell;
import shell.command.Command;
import shell.command.CommandRegistry;
import shell.command.builtin.CdCommand;
import shell.command.builtin.ConfDumpCommand;
import shell.command.builtin.ExitCommand;
import shell.command.builtin.LsCommand;
import shell.configuration.ArgsParser;
import shell.configuration.Config;
import shell.exception.ShellException;
import shell.parsing.LineParser;
import shell.vfs.VirtualFileSystem;
import shell.vfs.XmlVfsLoader;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

public class Main {
    private static final int SUCCESS = 0;
    private static final int ERROR = 1;

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
        Config config = new ArgsParser().parse(args);
        VirtualFileSystem filesystem = loadFilesystem(config);
        Shell shell = new Shell(
            new CommandRegistry(commands(config, filesystem)),
            new LineParser(),
            config,
            filesystem
        );
        shell.run(session);
        return SUCCESS;
    }

    private static VirtualFileSystem loadFilesystem(Config config) {
        if (config.vfsPath() == null) {
            return VirtualFileSystem.empty();
        }
        return new XmlVfsLoader().load(config.vfsPath());
    }

    private static List<Command> commands(Config config, VirtualFileSystem filesystem) {
        return List.of(
            new LsCommand(filesystem),
            new CdCommand(filesystem),
            new ConfDumpCommand(config, filesystem),
            new ExitCommand()
        );
    }
}
