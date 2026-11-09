package depgraph.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PypiUrlTest {
    @Test
    void stripsSimpleSuffixAndBuildsJsonUrl() {
        assertEquals(
            "https://pypi.org/pypi/requests/2.31.0/json",
            PypiUrl.jsonMetadata(
                "https://pypi.org/simple/",
                "requests",
                "2.31.0"
            ).toString()
        );
    }

    @Test
    void acceptsRootWithoutSimple() {
        assertEquals(
            "https://pypi.org/pypi/idna/3.0/json",
            PypiUrl.jsonMetadata("https://pypi.org", "idna", "3.0").toString()
        );
    }
}
