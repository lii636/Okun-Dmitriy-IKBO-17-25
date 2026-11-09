package depgraph.application;

import depgraph.configuration.Config;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class App {
    private final Config config;

    public void run(Session session) {
        session.out().println(config.dump());
    }
}
