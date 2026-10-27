package shell.vfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import shell.exception.ShellException;

public class VirtualFileSystem {
    public static final String DEFAULT_NAME = "default";

    private final Node root;
    private final String name;
    private Node current;

    VirtualFileSystem(Node root, String name) {
        this.root = root;
        this.current = root;
        this.name = name;
    }

    public static VirtualFileSystem empty() {
        return new VirtualFileSystem(Node.directory(""), DEFAULT_NAME);
    }

    public String name() {
        return name;
    }

    String workingDirectory() {
        List<String> parts = new ArrayList<>();
        Node node = current;
        while (node.parent != null) {
            parts.addFirst(node.name);
            node = node.parent;
        }
        return parts.isEmpty() ? "/" : "/" + String.join("/", parts);
    }

    public void changeToRoot() {
        current = root;
    }

    public void changeDirectory(String path) {
        Node target = find(path, "cd: no such directory: ");
        if (!target.isDirectory()) {
            throw new ShellException("cd: not a directory: " + path);
        }
        current = target;
    }

    public List<String> listCurrent() {
        return labels(current);
    }

    public List<String> list(String path) {
        return labels(find(path, "ls: no such path: "));
    }

    byte[] readBytes(String path) {
        Node target = find(path, "no such path: ");
        if (target.isDirectory()) {
            throw new ShellException("not a file: " + path);
        }
        return target.content.clone();
    }

    private Node find(String path, String errorPrefix) {
        if (path.isEmpty()) {
            throw new ShellException(errorPrefix + path);
        }
        Node target = resolve(path);
        if (target == null) {
            throw new ShellException(errorPrefix + path);
        }
        return target;
    }

    private Node resolve(String path) {
        Node node = path.startsWith("/") ? root : current;
        for (String part : path.split("/", -1)) {
            node = step(node, part);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    private Node step(Node node, String part) {
        if (part.isEmpty() || ".".equals(part)) {
            return node.isDirectory() ? node : null;
        }
        if ("..".equals(part)) {
            if (!node.isDirectory()) {
                return null;
            }
            return node.parent == null ? node : node.parent;
        }
        if (!node.isDirectory() || !node.children.containsKey(part)) {
            return null;
        }
        return node.children.get(part);
    }

    private List<String> labels(Node node) {
        if (!node.isDirectory()) {
            return List.of(node.name);
        }
        return node.children.keySet().stream().sorted().toList();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    static class Node {
        private final String name;
        private final byte[] content;
        private final Map<String, Node> children;
        private Node parent;

        static Node directory(String name) {
            return new Node(name, null, new HashMap<>());
        }

        static Node file(String name, byte[] content) {
            return new Node(name, content.clone(), null);
        }

        boolean isDirectory() {
            return children != null;
        }

        void add(Node child) {
            if (!isDirectory()) {
                throw new ShellException("file cannot contain elements");
            }
            if (children.putIfAbsent(child.name, child) != null) {
                throw new ShellException("duplicate name: " + child.name);
            }
            child.parent = this;
        }
    }

}
