package shell.command;

import org.junit.jupiter.api.Test;

import shell.command.builtin.CdCommand;
import shell.command.builtin.ConfDumpCommand;
import shell.command.builtin.ExitCommand;
import shell.command.builtin.LsCommand;
import shell.configuration.Config;
import shell.exception.ShellException;
import shell.vfs.VirtualFileSystem;
import shell.vfs.XmlVfsLoader;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandRegistryTest {
    @Test
    void sharesFilesystemBetweenNavigationAndListing() {
        Path source = Path.of("fixtures/vfs/minimal.xml");
        VirtualFileSystem filesystem = new XmlVfsLoader().load(source);
        Config config = new Config(source, null);
        LsCommand ls = new LsCommand(filesystem);
        CommandRegistry registry = new CommandRegistry(List.of(
            ls,
            new CdCommand(filesystem),
            new ConfDumpCommand(config, filesystem),
            new ExitCommand()
        ));

        assertSame(ls, registry.require("ls"));
        assertEquals("home", registry.require("ls").execute(List.of()).output());
        assertFalse(registry.require("cd").execute(List.of("home")).exits());
        assertEquals("", registry.require("ls").execute(List.of()).output());
        assertEquals(
            "vfs=" + source + "\nscript=\nvfs-name=minimal",
            registry.require("conf-dump").execute(List.of()).output()
        );
        assertThrows(
            ShellException.class,
            () -> registry.require("conf-dump").execute(List.of("extra"))
        );
        assertTrue(registry.require("exit").execute(List.of()).exits());
    }

    @Test
    void rejectsDuplicateAndUnknownCommands() {
        VirtualFileSystem filesystem = VirtualFileSystem.empty();
        assertThrows(
            IllegalStateException.class,
            () -> new CommandRegistry(List.of(
                new LsCommand(filesystem),
                new LsCommand(filesystem)
            ))
        );
        CommandRegistry registry = new CommandRegistry(List.of());
        assertThrows(ShellException.class, () -> registry.require("missing"));
    }
}
