package uvm.configuration;

import java.nio.file.Path;

import lombok.Value;

@Value
public class AssembleConfig implements Config {
    Path inputAsm;
    Path outputBin;
    boolean testMode;
}
