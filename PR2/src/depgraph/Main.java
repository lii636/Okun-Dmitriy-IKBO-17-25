package depgraph;

import java.io.PrintStream;

import depgraph.application.App;
import depgraph.application.Session;
import depgraph.configuration.ArgsParser;
import depgraph.configuration.Config;
import depgraph.exception.DepgraphException;
import depgraph.repository.JdkHttpTransport;
import depgraph.repository.PackageRepositories;
import depgraph.repository.PackageRepository;

public class Main {
    private static final int SUCCESS = 0;
    private static final int ERROR = 1;
    private static final String USAGE =
        "Usage: depgraph --package NAME --repo URL_OR_PATH --version VERSION"
            + " [--test-repo] [--reverse-deps] [--ascii-tree] [--help]";

    public static void main(String[] args) {
        int code = run(args, System.out, System.err);
        if (code != SUCCESS) {
            System.exit(code);
        }
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        try {
            return start(args, new Session(out, err));
        } catch (DepgraphException exception) {
            err.println(exception.getMessage());
            return ERROR;
        }
    }

    private static int start(String[] args, Session session) {
        ArgsParser.Result result = new ArgsParser().parse(args);
        if (result instanceof ArgsParser.Help) {
            session.out().println(USAGE);
            return SUCCESS;
        }
        Config config = ((ArgsParser.Start) result).config();
        PackageRepository repository = PackageRepositories.from(
            config,
            new JdkHttpTransport()
        );
        new App(config, repository).run(session);
        return SUCCESS;
    }
}
