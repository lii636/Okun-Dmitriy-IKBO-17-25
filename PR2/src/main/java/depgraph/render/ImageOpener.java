package depgraph.render;

import java.nio.file.Path;

import depgraph.application.Session;

public interface ImageOpener {
    void open(Path image, Session session);
}
