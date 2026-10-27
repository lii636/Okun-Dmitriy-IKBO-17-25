package shell.vfs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import shell.exception.ShellException;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VirtualFileSystemTest {
    private final XmlVfsLoader loader = new XmlVfsLoader();

    @ParameterizedTest
    @ValueSource(strings = {"minimal.xml", "multi-file.xml", "deep-3levels.xml"})
    void loadsRequiredFixtures(String fixture) {
        VirtualFileSystem vfs = loader.load(Path.of("fixtures/vfs", fixture));

        assertFalse(vfs.name().isBlank());
        assertEquals("/", vfs.workingDirectory());
    }

    @Test
    void navigatesAndListsDeepPaths() {
        VirtualFileSystem vfs = loader.load(Path.of("fixtures/vfs/deep-3levels.xml"));

        vfs.changeDirectory("/a/b/c");
        assertEquals("/a/b/c", vfs.workingDirectory());
        assertEquals(List.of("note.txt"), vfs.listCurrent());
        assertEquals(List.of("note.txt"), vfs.list("./note.txt"));
        vfs.changeDirectory("../../..");
        vfs.changeDirectory("..");
        assertEquals("/", vfs.workingDirectory());
    }

    @Test
    void preservesBinaryFilesAndRejectsInvalidPaths() {
        VirtualFileSystem vfs = loader.load(Path.of("fixtures/vfs/multi-file.xml"));

        assertArrayEquals("hello".getBytes(), vfs.readBytes("bin.dat"));
        assertThrows(ShellException.class, () -> vfs.list("missing"));
        assertThrows(ShellException.class, () -> vfs.changeDirectory("bin.dat"));
        assertThrows(ShellException.class, () -> vfs.list("bin.dat/"));
        assertThrows(ShellException.class, () -> vfs.list("bin.dat/."));
        assertThrows(ShellException.class, () -> vfs.list("bin.dat/.."));
    }

    @Test
    void rejectsUnsafeOrAmbiguousXml() {
        assertInvalid("<vfs><file name=\"a\"/><file name=\"a\"/></vfs>");
        assertInvalid("<vfs><file name=\"a\" encoding=\"base64\">A===</file></vfs>");
        assertInvalid("<vfs><file name=\"a\"><dir name=\"nested\"/></file></vfs>");
        assertInvalid("<vfs><dir name=\"../bad\"/></vfs>");
        assertInvalid("<!DOCTYPE vfs [<!ENTITY x SYSTEM \"file:///etc/passwd\">]><vfs>&x;</vfs>");
    }

    private void assertInvalid(String xml) {
        ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());
        assertThrows(ShellException.class, () -> loader.load(input, "test"));
    }
}
