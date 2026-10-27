package shell.configuration;

import java.nio.file.Path;

public record Config(Path vfsPath, Path scriptPath) {
    public String dump(String vfsName) {
        return "vfs=" + text(vfsPath) + "\n"
            + "script=" + text(scriptPath) + "\n"
            + "vfs-name=" + vfsName;
    }

    private String text(Path path) {
        return path == null ? "" : path.toString();
    }
}
