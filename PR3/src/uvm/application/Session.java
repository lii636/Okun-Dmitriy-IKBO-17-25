package uvm.application;

import java.io.PrintStream;

import lombok.Value;

@Value
public class Session {
    PrintStream out;
    PrintStream err;
}
