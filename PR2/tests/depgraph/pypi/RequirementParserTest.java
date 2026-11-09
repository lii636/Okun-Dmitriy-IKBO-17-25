package depgraph.pypi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequirementParserTest {
    @Test
    void extractsNameBeforeOperators() {
        assertEquals(
            "charset-normalizer",
            RequirementParser.packageName("charset-normalizer (<4,>=2)").orElseThrow()
        );
        assertEquals(
            "certifi",
            RequirementParser.packageName("certifi (>=2017.4.17)").orElseThrow()
        );
        assertEquals(
            "urllib3",
            RequirementParser.packageName("urllib3>=1.21.1,<3").orElseThrow()
        );
    }

    @Test
    void detectsExtraMarker() {
        assertTrue(
            RequirementParser.hasExtraMarker(
                "PySocks (!=1.5.7,>=1.5.6) ; extra == 'socks'"
            )
        );
        assertFalse(RequirementParser.hasExtraMarker("idna (<4,>=2.5)"));
    }
}
