package depgraph.repository;

import java.net.URI;

public interface HttpTransport {
    String get(URI url);
}
