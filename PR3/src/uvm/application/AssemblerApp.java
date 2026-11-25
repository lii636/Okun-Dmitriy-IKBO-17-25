package uvm.application;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import lombok.RequiredArgsConstructor;
import uvm.asm.AssemblerParser;
import uvm.binary.InstructionEncoder;
import uvm.configuration.Config;
import uvm.exception.UvmException;
import uvm.ir.Instruction;

@RequiredArgsConstructor
public class AssemblerApp {
    private final Config config;
    private final AssemblerParser parser;
    private final InstructionEncoder encoder;

    public void run(Session session) {
        List<Instruction> program = parser.parseFile(config.inputAsm());
        byte[] machineCode = encoder.encodeAll(program);
        writeBinary(machineCode);
        session.out().println(machineCode.length);
        if (config.testMode()) {
            for (String line : encoder.toCommandDumps(program)) {
                session.out().println(line);
            }
        }
    }

    private void writeBinary(byte[] machineCode) {
        try {
            if (config.outputBin().getParent() != null) {
                Files.createDirectories(config.outputBin().getParent());
            }
            Files.write(config.outputBin(), machineCode);
        } catch (IOException exception) {
            throw new UvmException(
                "cannot write binary: " + config.outputBin(),
                exception
            );
        }
    }
}
