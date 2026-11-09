package depgraph.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import depgraph.exception.DepgraphException;
import depgraph.model.Dependency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PypiPackageRepositoryTest {
    @Test
    void returnsSortedDirectDependenciesWithoutExtras() throws IOException {
        String json = Files.readString(Path.of("fixtures/pypi/requests-2.31.0.json"));
        PackageRepository repository = new PypiPackageRepository(
            "https://pypi.org/simple/",
            url -> json
        );

        List<Dependency> dependencies = repository.directDependencies(
            "requests",
            "2.31.0"
        );

        assertEquals(
            List.of(
                new Dependency("certifi"),
                new Dependency("charset-normalizer"),
                new Dependency("idna"),
                new Dependency("urllib3")
            ),
            dependencies
        );
    }

    @Test
    void wrapsInvalidJson() {
        PackageRepository repository = new PypiPackageRepository(
            "https://pypi.org/simple/",
            url -> "not-json"
        );

        assertThrows(
            DepgraphException.class,
            () -> repository.directDependencies("requests", "2.31.0")
        );
    }
}
