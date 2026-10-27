package shell.vfs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import shell.exception.ShellException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class XmlVfsLoader {
    private static final String DISALLOW_DOCTYPE =
        "http://apache.org/xml/features/disallow-doctype-decl";

    public VirtualFileSystem load(Path path) {
        if (!Files.isRegularFile(path)) {
            throw new ShellException("vfs file not found: " + path);
        }
        try (InputStream input = Files.newInputStream(path)) {
            return load(input, path.getFileName().toString());
        } catch (IOException exception) {
            throw new ShellException("cannot read vfs: " + path, exception);
        }
    }

    VirtualFileSystem load(InputStream input, String fallbackName) {
        Element root = document(input).getDocumentElement();
        if (root == null || !"vfs".equals(root.getTagName())) {
            throw new ShellException("root element must be vfs");
        }
        VirtualFileSystem.Node tree = VirtualFileSystem.Node.directory("");
        appendChildren(root, tree);
        return new VirtualFileSystem(tree, vfsName(root, fallbackName));
    }

    private Document document(InputStream input) {
        try {
            return factory().newDocumentBuilder().parse(input);
        } catch (ParserConfigurationException | SAXException | IOException exception) {
            throw new ShellException("invalid vfs xml: " + exception.getMessage(), exception);
        }
    }

    private DocumentBuilderFactory factory() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature(DISALLOW_DOCTYPE, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setExpandEntityReferences(false);
        factory.setXIncludeAware(false);
        return factory;
    }

    private void appendChildren(Element element, VirtualFileSystem.Node parent) {
        NodeList children = element.getChildNodes();
        for (int index = 0; index < children.getLength(); index++) {
            Node child = children.item(index);
            if (child instanceof Element childElement) {
                parent.add(readNode(childElement));
            } else if (isText(child) && !child.getTextContent().isBlank()) {
                throw new ShellException("unexpected text in directory");
            }
        }
    }

    private VirtualFileSystem.Node readNode(Element element) {
        String name = validName(element);
        if ("dir".equals(element.getTagName())) {
            VirtualFileSystem.Node directory = VirtualFileSystem.Node.directory(name);
            appendChildren(element, directory);
            return directory;
        }
        if ("file".equals(element.getTagName())) {
            rejectNestedElements(element);
            return VirtualFileSystem.Node.file(name, content(element));
        }
        throw new ShellException("unsupported element: " + element.getTagName());
    }

    private String validName(Element element) {
        String name = element.getAttribute("name").trim();
        if (name.isEmpty() || ".".equals(name) || "..".equals(name)
            || name.contains("/") || name.contains("\\")) {
            throw new ShellException("invalid name: " + name);
        }
        return name;
    }

    private byte[] content(Element element) {
        String encoding = element.getAttribute("encoding").trim();
        String text = element.getTextContent().trim();
        if (encoding.isEmpty() || "utf-8".equalsIgnoreCase(encoding)) {
            return text.getBytes(StandardCharsets.UTF_8);
        }
        if ("base64".equalsIgnoreCase(encoding)) {
            return decodeBase64(text);
        }
        throw new ShellException("unsupported encoding: " + encoding);
    }

    private byte[] decodeBase64(String text) {
        try {
            return Base64.getDecoder().decode(text.replaceAll("\\s+", ""));
        } catch (IllegalArgumentException exception) {
            throw new ShellException("invalid base64 content", exception);
        }
    }

    private void rejectNestedElements(Element element) {
        NodeList children = element.getChildNodes();
        for (int index = 0; index < children.getLength(); index++) {
            if (children.item(index) instanceof Element) {
                throw new ShellException("file cannot contain elements");
            }
        }
    }

    private String vfsName(Element root, String fallbackName) {
        String name = root.getAttribute("name").trim();
        if (!name.isEmpty()) {
            return name;
        }
        return fallbackName == null || fallbackName.isBlank()
            ? VirtualFileSystem.DEFAULT_NAME
            : fallbackName;
    }

    private boolean isText(Node node) {
        return node.getNodeType() == Node.TEXT_NODE
            || node.getNodeType() == Node.CDATA_SECTION_NODE;
    }
}
