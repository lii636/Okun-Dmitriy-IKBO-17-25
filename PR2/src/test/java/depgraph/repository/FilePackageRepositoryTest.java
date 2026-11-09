package depgraph.repository;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import depgraph.exception.DepgraphException;
import depgraph.model.Dependency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilePackageRepositoryTest {
    @Test
    void readsLinearFixture() {
        PackageRepository repository = new FilePackageRepository(
            Path.of("fixtures/repos/linear.txt")
        );

        assertEquals(List.of(new Dependency("B")), repository.directDependencies("A", "1"));
        assertEquals(List.of(new Dependency("C")), repository.directDependencies("B", "ignored"));
        assertEquals(List.of(), repository.directDependencies("C", ""));
    }

    @Test
    void rejectsMissingFile() {
        assertThrows(
            DepgraphException.class,
            () -> new FilePackageRepository(Path.of("fixtures/repos/missing.txt"))
        );
    }
}
