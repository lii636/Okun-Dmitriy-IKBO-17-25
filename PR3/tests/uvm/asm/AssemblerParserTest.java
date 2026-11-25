package uvm.asm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import uvm.exception.UvmException;
import uvm.ir.Abs;
import uvm.ir.Instruction;
import uvm.ir.Load;
import uvm.ir.Read;
import uvm.ir.Write;

class AssemblerParserTest {
    private final AssemblerParser parser = new AssemblerParser();

    @Test
    void parsesSpecFixtureFields() {
        Path fixture = Path.of("fixtures/asm/spec_tests.asm");
        List<Instruction> program = parser.parseFile(fixture);
        assertEquals(4, program.size());
        assertEquals(new Load(8, 266), program.get(0));
        assertEquals(new Read(24, 109, 499), program.get(1));
        assertEquals(new Write(7, 469), program.get(2));
        assertEquals(new Abs(33, 461), program.get(3));
    }

    @Test
    void ignoresCommentsAndBlankLines() {
        List<Instruction> program = parser.parseLines(List.of(
            "# header",
            "",
            "LOAD 1, 2 ; trailing",
            "  ; only comment"
        ));
        assertEquals(List.of(new Load(1, 2)), program);
    }

    @Test
    void rejectsUnknownMnemonic() {
        assertThrows(
            UvmException.class,
            () -> parser.parseLines(List.of("NOP 1"))
        );
    }

    @Test
    void rejectsOutOfRangeRegister() {
        assertThrows(
            UvmException.class,
            () -> parser.parseLines(List.of("LOAD 128, 1"))
        );
    }
}
