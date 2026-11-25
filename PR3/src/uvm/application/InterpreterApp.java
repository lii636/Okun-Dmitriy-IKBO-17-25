package uvm.application;

import java.io.IOException;
import java.nio.file.Files;

import lombok.RequiredArgsConstructor;
import uvm.binary.InstructionDecoder;
import uvm.configuration.RunConfig;
import uvm.dump.MemoryXmlDumper;
import uvm.exception.UvmException;
import uvm.vm.Interpreter;
import uvm.vm.MachineState;

@RequiredArgsConstructor
public class InterpreterApp {
    private final RunConfig config;
    private final Interpreter interpreter;
    private final MemoryXmlDumper dumper;

    public InterpreterApp(RunConfig config) {
        this(config, new Interpreter(new InstructionDecoder()), new MemoryXmlDumper());
    }

    public void run(Session session) {
        byte[] machineCode = readBinary();
        MachineState state = interpreter.run(machineCode);
        dumper.dump(state, config.dumpFrom(), config.dumpTo(), config.dumpXml());
        session.out().println(
            "dump written: " + config.dumpXml()
                + " [" + config.dumpFrom() + "-" + config.dumpTo() + "]"
        );
    }

    private byte[] readBinary() {
        try {
            return Files.readAllBytes(config.programBin());
        } catch (IOException exception) {
            throw new UvmException(
                "cannot read binary: " + config.programBin(),
                exception
            );
        }
    }
}
