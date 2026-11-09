package depgraph.configuration;

import lombok.Value;

@Value
public class Config {
    String packageName;
    String repo;
    String version;
    boolean testRepo;
    boolean asciiTree;

    public String dump() {
        return "package=" + packageName + "\n"
            + "repo=" + repo + "\n"
            + "version=" + version + "\n"
            + "test-repo=" + testRepo + "\n"
            + "ascii-tree=" + asciiTree;
    }
}
