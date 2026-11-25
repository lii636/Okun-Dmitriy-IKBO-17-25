package uvm;

import java.io.PrintStream;

import uvm.application.AssemblerApp;
import uvm.application.Session;
import uvm.asm.AssemblerParser;
import uvm.binary.InstructionEncoder;
import uvm.configuration.ArgsParser;
import uvm.configuration.Config;
import uvm.exception.UvmException;

public final class Main {
    private static final int SUCCESS = 0;
    private static final int ERROR = 1;
    private static final String USAGE =
        "Usage: uvm assemble <input.asm> <output.bin> [--test]"
            + System.lineSeparator()
            + "       uvm --help";

    private Main() {
    }

    public static void main(String[] args) {
        int code = run(args, System.out, System.err);
        if (code != SUCCESS) {
            System.exit(code);
        }
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        try {
            return start(args, new Session(out, err));
        } catch (UvmException exception) {
            err.println(exception.getMessage());
            return ERROR;
        }
    }

    private static int start(String[] args, Session session) {
        ArgsParser.Result result = new ArgsParser().parse(args);
        if (result instanceof ArgsParser.Help) {
            session.out().println(USAGE);
            return SUCCESS;
        }
        Config config = ((ArgsParser.Start) result).config();
        new AssemblerApp(config, new AssemblerParser(), new InstructionEncoder())
            .run(session);
        return SUCCESS;
    }
}
