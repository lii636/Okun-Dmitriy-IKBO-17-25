package depgraph.repository;

import depgraph.configuration.Config;

public final class PackageRepositories {
    private PackageRepositories() {
    }

    public static PackageRepository from(Config config, HttpTransport transport) {
        if (config.testRepo()) {
            return new UnsupportedTestPackageRepository();
        }
        return new PypiPackageRepository(config.repo(), transport);
    }
}
