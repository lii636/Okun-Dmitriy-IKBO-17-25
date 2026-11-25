package uvm.configuration;

import java.nio.file.Path;

import lombok.Value;

@Value
public class RunConfig implements Config {
    Path programBin;
    Path dumpXml;
    int dumpFrom;
    int dumpTo;
}
