package uvm.application;

import java.util.List;

import lombok.RequiredArgsConstructor;
import uvm.asm.AssemblerParser;
import uvm.configuration.Config;
import uvm.ir.Instruction;

@RequiredArgsConstructor
public class AssemblerApp {
    private final Config config;
    private final AssemblerParser parser;

    public void run(Session session) {
        List<Instruction> program = parser.parseFile(config.inputAsm());
        if (config.testMode()) {
            printIr(program, session);
        }
    }

    private void printIr(List<Instruction> program, Session session) {
        for (Instruction instruction : program) {
            session.out().println(instruction.toFieldDump());
        }
    }
}
