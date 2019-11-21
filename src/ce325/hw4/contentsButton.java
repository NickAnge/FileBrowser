package ce325.hw4;

import javax.swing.*;

public class contentsButton extends JButton {
    private fileNode fileNode;

    public contentsButton(ce325.hw4.fileNode fileNode) {
        this.fileNode = fileNode;
    }
    public contentsButton(String text, Icon icon, ce325.hw4.fileNode fileNode) {
        super(text, icon);
        this.fileNode = fileNode;
    }

    public ce325.hw4.fileNode getFileNode() {
        return fileNode;
    }
    public void setFileNode(ce325.hw4.fileNode fileNode) {
        this.fileNode = fileNode;
    }
}
