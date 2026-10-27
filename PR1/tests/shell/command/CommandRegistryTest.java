package shell.command;

import org.junit.jupiter.api.Test;

import shell.command.builtin.CdCommand;
import shell.command.builtin.ExitCommand;
import shell.command.builtin.LsCommand;
import shell.exception.ShellException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandRegistryTest {
    @Test
    void resolvesRegisteredStrategies() {
        LsCommand ls = new LsCommand();
        CommandRegistry registry = new CommandRegistry(List.of(ls, new CdCommand(), new ExitCommand()));

        assertSame(ls, registry.require("ls"));
        assertEquals("ls: a b", registry.require("ls").execute(List.of("a", "b")).output());
        assertEquals("cd", registry.require("cd").execute(List.of()).output());
        assertFalse(registry.require("cd").execute(List.of()).exits());
        assertTrue(registry.require("exit").execute(List.of()).exits());
    }

    @Test
    void rejectsDuplicateAndUnknownCommands() {
        assertThrows(
            IllegalStateException.class,
            () -> new CommandRegistry(List.of(new LsCommand(), new LsCommand()))
        );
        CommandRegistry registry = new CommandRegistry(List.of());
        assertThrows(ShellException.class, () -> registry.require("missing"));
    }
}
