package depgraph.render;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;

import depgraph.application.Session;
import depgraph.exception.DepgraphException;

public class DesktopImageOpener implements ImageOpener {
    @Override
    public void open(Path image, Session session) {
        if (!Desktop.isDesktopSupported()) {
            session.err().println("image saved (no desktop): " + image.toAbsolutePath());
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            session.err().println("image saved (open unsupported): " + image.toAbsolutePath());
            return;
        }
        try {
            desktop.open(image.toFile());
        } catch (IOException exception) {
            throw new DepgraphException(
                "failed to open image: " + image.toAbsolutePath(),
                exception
            );
        }
    }
}
