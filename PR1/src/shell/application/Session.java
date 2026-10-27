package shell.application;

import java.io.InputStream;
import java.io.PrintStream;

public record Session(InputStream input, PrintStream out, PrintStream err) {
}
