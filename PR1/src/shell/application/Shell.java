package shell.application;

import shell.command.CommandResult;
import shell.exception.ShellException;
import shell.command.CommandRegistry;
import shell.parsing.LineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Shell {
    private static final String VFS_NAME = "default";
    private static final String PROMPT_SUFFIX = "> ";

    private final CommandRegistry commands;
    private final LineParser parser;

    public void run(Session session) {
        runInteractive(session);
    }

    private void runInteractive(Session session) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(session.input()));
        while (true) {
            session.out().print(prompt());
            session.out().flush();
            String line = readLine(reader, session.err());
            if (line == null || dispatch(line, session)) {
                return;
            }
        }
    }

    private boolean dispatch(String line, Session session) {
        try {
            return executeLine(line, session).exits();
        } catch (ShellException exception) {
            session.err().println(exception.getMessage());
            return false;
        }
    }

    private CommandResult executeLine(String line, Session session) {
        List<String> tokens = new ArrayList<>(parser.parse(line));
        if (tokens.isEmpty()) {
            return CommandResult.continueExecution();
        }
        String name = tokens.removeFirst();
        if (name.isEmpty()) {
            throw new ShellException("empty command");
        }
        CommandResult result = commands.require(name).execute(tokens);
        if (!result.output().isEmpty()) {
            session.out().println(result.output());
        }
        return result;
    }

    private String readLine(BufferedReader reader, PrintStream err) {
        try {
            return reader.readLine();
        } catch (IOException exception) {
            err.println("read error: " + exception.getMessage());
            return null;
        }
    }

    private String prompt() {
        return VFS_NAME + PROMPT_SUFFIX;
    }
}
