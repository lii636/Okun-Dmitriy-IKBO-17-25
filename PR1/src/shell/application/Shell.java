package shell.application;

import shell.command.CommandRegistry;
import shell.command.CommandResult;
import shell.configuration.Config;
import shell.exception.ShellException;
import shell.parsing.LineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Shell {
    private static final String PROMPT_SUFFIX = "> ";

    private final CommandRegistry commands;
    private final LineParser parser;
    private final Config config;

    public void run(Session session) {
        Path script = config.scriptPath();
        if (script != null) {
            requireScript(script);
        }
        session.err().println(config.dump(Config.DEFAULT_VFS_NAME));
        if (script == null) {
            runInteractive(session);
            return;
        }
        runScript(script, session);
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

    private void runScript(Path script, Session session) {
        for (String line : readScript(script)) {
            session.out().println(prompt() + line);
            if (dispatch(line, session)) {
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

    private List<String> readScript(Path script) {
        try {
            return Files.readAllLines(script);
        } catch (IOException exception) {
            throw new ShellException("cannot read script: " + script, exception);
        }
    }

    private void requireScript(Path script) {
        if (!Files.isRegularFile(script)) {
            throw new ShellException("script file not found: " + script);
        }
    }

    private String prompt() {
        return Config.DEFAULT_VFS_NAME + PROMPT_SUFFIX;
    }
}
