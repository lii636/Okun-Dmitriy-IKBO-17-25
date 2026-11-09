package depgraph.pypi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PypiVersionResponseTest {
    @Test
    void parsesRequiresDistFromFixture() throws IOException {
        String json = Files.readString(
            Path.of("fixtures/pypi/requests-2.31.0.json")
        );

        PypiVersionResponse response = new Gson().fromJson(json, PypiVersionResponse.class);

        assertNotNull(response.info());
        assertEquals(6, response.info().requiresDist().size());
        assertEquals(
            "charset-normalizer (<4,>=2)",
            response.info().requiresDist().getFirst()
        );
    }
}
