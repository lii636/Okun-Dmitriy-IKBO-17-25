package uvm.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import uvm.exception.UvmException;

class ArgsParserTest {
    private final ArgsParser parser = new ArgsParser();

    @Test
    void parsesAssembleWithTestFlag() {
        ArgsParser.Result result = parser.parse(new String[] {
            "assemble", "in.asm", "out.bin", "--test"
        });
        ArgsParser.Start start = assertInstanceOf(ArgsParser.Start.class, result);
        AssembleConfig config = assertInstanceOf(AssembleConfig.class, start.config());
        assertEquals(Path.of("in.asm"), config.inputAsm());
        assertEquals(Path.of("out.bin"), config.outputBin());
        assertTrue(config.testMode());
    }

    @Test
    void parsesAssembleWithoutTestFlag() {
        ArgsParser.Result result = parser.parse(new String[] {
            "assemble", "a.asm", "b.bin"
        });
        ArgsParser.Start start = assertInstanceOf(ArgsParser.Start.class, result);
        AssembleConfig config = assertInstanceOf(AssembleConfig.class, start.config());
        assertFalse(config.testMode());
    }

    @Test
    void parsesRunWithDumpRange() {
        ArgsParser.Result result = parser.parse(new String[] {
            "run", "prog.bin", "dump.xml", "100-102"
        });
        ArgsParser.Start start = assertInstanceOf(ArgsParser.Start.class, result);
        RunConfig config = assertInstanceOf(RunConfig.class, start.config());
        assertEquals(Path.of("prog.bin"), config.programBin());
        assertEquals(Path.of("dump.xml"), config.dumpXml());
        assertEquals(100, config.dumpFrom());
        assertEquals(102, config.dumpTo());
    }

    @Test
    void returnsHelp() {
        assertInstanceOf(ArgsParser.Help.class, parser.parse(new String[] {"--help"}));
        assertInstanceOf(
            ArgsParser.Help.class,
            parser.parse(new String[] {"assemble", "--help"})
        );
    }

    @Test
    void rejectsMissingPaths() {
        assertThrows(
            UvmException.class,
            () -> parser.parse(new String[] {"assemble", "only.asm"})
        );
    }

    @Test
    void rejectsInvalidDumpRange() {
        assertThrows(
            UvmException.class,
            () -> parser.parse(new String[] {"run", "a.bin", "d.xml", "10"})
        );
    }

    @Test
    void rejectsUnknownSubcommand() {
        assertThrows(
            UvmException.class,
            () -> parser.parse(new String[] {"fly", "a.bin"})
        );
    }
}
