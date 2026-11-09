package depgraph.repository;

import java.nio.file.Path;

import depgraph.configuration.Config;

public final class PackageRepositories {
    private PackageRepositories() {
    }

    public static PackageRepository from(Config config, HttpTransport transport) {
        if (config.testRepo()) {
            return new FilePackageRepository(Path.of(config.repo()));
        }
        return new PypiPackageRepository(config.repo(), transport);
    }
}
