package ce325.hw4;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.*;

public class fileNode {
    private File file;
    private String name;
    private DefaultMutableTreeNode parent;
    private Set<File> subFiles;
    private DefaultMutableTreeNode selfTreenode;
    private Map<File, fileNode> fileNodes;
    private Map<File,fileNode> files;

    public fileNode(File file) {
        this.file = file;
        this.subFiles = new TreeSet<>(new createChildrenNodeTree.MyNameComp());
        this. fileNodes = new TreeMap<>();
        this.files = new TreeMap<>();
        this.parent = new DefaultMutableTreeNode();
    }

    public DefaultMutableTreeNode getParent() {
        return parent;
    }

    public void setParent(DefaultMutableTreeNode parent) {
        this.parent = parent;
    }

    public Map<File, fileNode> getFileNodes() {
        return fileNodes;
    }

    public void setFileNodes(Map<File, fileNode> fileNodes) {
        this.fileNodes = fileNodes;
    }
    public fileNode(File file, Set<File> subFiles) {
        this.file = file;
        this.subFiles = subFiles;
    }
    public Set<File> getSubFiles() {
        return subFiles;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setSubFiles(Set<File> subFiles) {
        this.subFiles = subFiles;
    }
    public DefaultMutableTreeNode getSelfTreenode() {
        return selfTreenode;
    }
    public void setSelfTreenode(DefaultMutableTreeNode selfTreenode) {
        this.selfTreenode = selfTreenode;
    }
    public Map<File, fileNode> getFiles() {
        return files;
    }
    public void setFiles(Map<File, fileNode> files) {
        this.files = files;
    }
    public File getFile() {
        return file;
    }
    @Override
    public String toString() {
        String name = file.getName();
        if (name.equals("")) {
            return file.getAbsolutePath();
        } else {
            return name;
        }
    }
}

