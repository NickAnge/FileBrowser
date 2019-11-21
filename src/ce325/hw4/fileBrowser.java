package ce325.hw4;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class fileBrowser extends JFrame {
    private static final int WIDTH =  1024;
    private static final int HEIGHT = 512;
    private static final String pathIcons = "hw4-icons/icons/";
    private JSplitPane splitPane;
    private JTree tree;
    private createChildrenNodeTree childs;
    private static String ourFolder;
    private static fileNode defaultnode ;
    private static contentsButton defaultFile;
    private  static  boolean clickedFileDir;

    public fileBrowser(){
        super();
        setTitle("CE325 File Browser");
        setSize(WIDTH,HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //CREATE OF THE TREE OF FILES
        File homeRoot = new File("/home/aggenikos");
        fileNode home = new fileNode(homeRoot);
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(home);
        tree = new JTree(top);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ImageIcon fileIcons = createImageIcon("hw4-icons/icons/folder.png");
        Image image = fileIcons.getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT);
        fileIcons = new ImageIcon(image);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setOpenIcon(fileIcons);
        renderer.setClosedIcon(fileIcons);
        renderer.setLeafIcon(fileIcons);
        tree.setCellRenderer(renderer);

        defaultFile = new contentsButton(home);
        //CREATE TREE OF FILES
        tree.setEditable(true);
        childs = new createChildrenNodeTree(top,homeRoot);
        childs.revursiveCreateTree(top,homeRoot,home);
        defaultnode = home;
        home.setSelfTreenode(top);
        //Creation of left and right Scroll page
        JScrollPane leftScroller = new JScrollPane(tree,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel rightScroller =  new JPanel();

        rightScroller.setLayout(new FlowLayout(FlowLayout.LEFT));
        Dimension browredsize = new Dimension(500,700);
        rightScroller.setPreferredSize(browredsize);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JScrollPane right = new JScrollPane(rightScroller,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //ADD COMPONENTS TO SPLIT PANE
        splitPane.setLeftComponent(leftScroller);
        splitPane.setRightComponent(right);
       // splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(300);
        createMenubar();
        searchBar();
        //POP UP MENU WHEN RIGHT CLICK is happening
        add(splitPane);
        setVisible(true);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
                fileNode nodeInfo =(fileNode) node.getUserObject();
                defaultnode  = nodeInfo;
                JPanel newPanel = new JPanel();
                newPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                childs.revursiveCreateTree(node,nodeInfo.getFile(),nodeInfo);
                openFolder(nodeInfo,newPanel);
                final JPopupMenu popup = new JPopupMenu();
                JMenuItem newTextFile = new JMenuItem("New Text File..");
                JMenuItem newDirectory = new JMenuItem("New Directory");
                popup.add(newDirectory);
                popup.add(newTextFile);
                splitPane.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if(SwingUtilities.isRightMouseButton(e)){
                            popup.show(e.getComponent(),e.getX(),e.getY());
                        }
                    }
                });
            }
        });
    }
    //METHODSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
    protected  static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = fileBrowser.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            //System.err.println("Couldn't find file: " + path);
            return  null;
        }
    }
    private void createMenubar(){
        //Whole menu bar
        JMenuBar menubar = new JMenuBar();
        //choice File
        JMenu fileMenu = new JMenu("File");
        //choice create new in file
        JMenu  createNew = new JMenu("Create New..");
        JMenuItem newFile = new JMenuItem("new File");
        newFile.addActionListener(new newFileListener(defaultnode));
        JMenuItem newDir = new JMenuItem("new Directory");
        newDir.addActionListener(new newDirectoryListener(defaultnode));
        JMenuItem remove = new JMenuItem("Delete");
        remove.addActionListener(new deleteListenerFile(defaultnode));
        JMenuItem rename = new JMenuItem("Rename");
        rename.addActionListener(new renmeListenerFile(defaultnode));
        createNew.add(newFile);
        createNew.add(newDir);
        fileMenu.add(createNew);
        fileMenu.add(remove);
        fileMenu.add(rename);
        menubar.add(fileMenu);
        setJMenuBar(menubar);
    }
    private void searchBar(){
        JButton searchButton = new JButton();
        searchButton.setText("Start Search");
        JTextField searchField = new JTextField("Search",15);
        searchField.setForeground(Color.gray);
        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if(searchField.getText().equals("Search")){
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if(searchField.getText().isEmpty()){
                    searchField.setForeground(Color.gray);
                    searchField.setText("Search");
                }
            }
        });
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout());
      //  p.add(searchField);
        p.add(searchField);
        p.add(searchButton);
        p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        searchButton.setPreferredSize(new Dimension(150,20));
        searchField.setPreferredSize(new Dimension(150,25));
        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        p2.add(p,BorderLayout.EAST);
        add(p2,BorderLayout.NORTH);
    }
    private void openFolder(fileNode fileNode,Container container){
        Set<File> insideFolders = fileNode.getSubFiles();
        Map<File,fileNode> dirOnly = fileNode.getFileNodes();
        Map<File,fileNode>filesOnly = fileNode.getFiles();
        for(Map.Entry<File,fileNode> element : dirOnly.entrySet()){
            ImageIcon fileIcons = createImageIcon("hw4-icons/icons/folder.png");
            fileNode fileButton =  fileNode.getFileNodes().get(element.getKey());
            contentsButton button = new contentsButton("" + element.getValue().getFile().getName(), fileIcons, fileButton);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setPreferredSize(new Dimension(100, 100));
            button.setContentAreaFilled(true);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setToolTipText(fileButton.getFile().getName());
            directoryListenerMouse bu = new directoryListenerMouse();
            button.addMouseListener(bu);
            container.add(button);
        }
        for(Map.Entry<File,fileNode> element : filesOnly.entrySet()){
            String newextension = getExtensionByStringHandling(element.getKey().getAbsolutePath());
            ImageIcon newIcon = createImageIcon(pathIcons + newextension + ".png");
            if (newIcon == null) {
                newIcon = createImageIcon(pathIcons + "question.png");
            }
            fileNode fileButton = element.getValue();
            contentsButton button = new contentsButton(element.getKey().getName(),newIcon,fileButton);
            fileListenerMouse bu = new fileListenerMouse();
            button.addMouseListener(bu);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setPreferredSize(new Dimension(100, 100));
            button.setContentAreaFilled(true);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setToolTipText(fileButton.getFile().getName());
            container.add(button);
        }
        Dimension browredsize = new Dimension(500,700);
        container.setPreferredSize(browredsize);
        container.addMouseListener(new panelListener());
        JScrollPane newRight =  new JScrollPane(container,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.splitPane.setRightComponent(newRight);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(300);
    }
    public class panelListener implements  MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            if(SwingUtilities.isRightMouseButton(e)){
                final JPopupMenu popup = new JPopupMenu();
                JMenuItem newDirectory = new JMenuItem("New Directory");
                newDirectory.addActionListener( new newDirectoryListener(defaultnode));
                JMenuItem newTextFile = new JMenuItem("New Text File..");
                newTextFile.addActionListener(new newFileListener(defaultnode));
                popup.add(newTextFile);
                popup.add(newDirectory);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
    public String getExtensionByStringHandling(String filename) {
        int i =  filename.lastIndexOf(".");
        if(i>0){
            return filename.substring(i+1);
        }
        return "";
    }
    public class fileListenerMouse implements  MouseListener{
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2){
                clickedFileDir = false;
                Object no= e.getSource();
                contentsButton variable = (contentsButton)no;
                fileNode fileNode = variable.getFileNode();
                String extension = getExtensionByStringHandling(fileNode.getFile().getAbsolutePath());
                Desktop desktop = Desktop.getDesktop();
                Runtime runtime = Runtime.getRuntime();

                try{
                    if(extension.equals("exe")){
                        runtime.exec(fileNode.getFile().getPath(),null,fileNode.getFile().getParentFile());
                    }
                    else {
                        desktop.open(fileNode.getFile());
                    }
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else if(SwingUtilities.isLeftMouseButton(e)) {
                Object no= e.getSource();
                contentsButton variable = (contentsButton)no;
                defaultFile = variable;
                clickedFileDir = true;
            }
            else if(SwingUtilities.isRightMouseButton(e)){
                final JPopupMenu popup = new JPopupMenu();
                Object no= e.getSource();
                contentsButton variable = (contentsButton)no;
                fileNode fileNode = variable.getFileNode();
                JMenuItem deleteItem = new JMenuItem("Delete");
                JMenuItem newTextFile = new JMenuItem("New Text File..");
                newTextFile.addActionListener(new newFileListener(fileNode));
                JMenuItem newDirectory = new JMenuItem("New Directory");
                newDirectory.addActionListener(new newDirectoryListener(fileNode));
                deleteItem.addActionListener(new deleteListener(fileNode));
                JMenuItem renameItem = new JMenuItem("Rename");
                renameItem.addActionListener(new renameListener(fileNode));
                popup.add(newTextFile);
                popup.add(newDirectory);
                popup.add(deleteItem);
                popup.add(renameItem);
                popup.show(e.getComponent(),e.getX(),e.getY());
            }
        }
        @Override
        public void mousePressed(MouseEvent e) {
        }
        @Override
        public void mouseReleased(MouseEvent e) {
        }
        @Override
        public void mouseEntered(MouseEvent e) {
        }
        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
    public class directoryListenerMouse implements MouseListener{
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2){
                clickedFileDir = false;
                Object no= e.getSource();
                contentsButton variable = (contentsButton)no;
                fileNode fileNode = variable.getFileNode();
                defaultnode = fileNode;
                DefaultMutableTreeNode defaultMutableTreeNode = fileNode.getSelfTreenode();
                childs.revursiveCreateTree(defaultMutableTreeNode,fileNode.getFile(),fileNode);
                TreePath newPath = new TreePath(defaultMutableTreeNode.getPath());
                tree.setSelectionPath(newPath);
                tree.expandPath(newPath);
                DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
                JPanel newPanel = new JPanel();
                newPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                openFolder(variable.getFileNode(),newPanel);
            }
            if(SwingUtilities.isLeftMouseButton(e)){
                Object no= e.getSource();
                contentsButton variable = (contentsButton)no;
                defaultFile = variable;
                clickedFileDir = true;

            }
            else if(SwingUtilities.isRightMouseButton(e)){
                final JPopupMenu popup = new JPopupMenu();
                Object no= e.getSource();
                contentsButton variable = (contentsButton)no;
                fileNode fileNode = variable.getFileNode();
                JMenuItem newTextFile = new JMenuItem("New Text File..");
                newTextFile.addActionListener(new newFileListener(fileNode));
                JMenuItem newDirectory = new JMenuItem("New Directory");
                newDirectory.addActionListener(new newDirectoryListener(fileNode));
                JMenuItem deleteItem = new JMenuItem("Delete");
                deleteItem.addActionListener(new deleteListener(fileNode));
                JMenuItem renameItem = new JMenuItem("Rename");
                renameItem.addActionListener(new renameListener(fileNode));
                popup.add(newTextFile);
                popup.add(newDirectory);
                popup.add(deleteItem);
                popup.add(renameItem);
                popup.show(e.getComponent(),e.getX(),e.getY());
                clickedFileDir = false;
            }
        }
        @Override
        public void mousePressed(MouseEvent e) {
        }
        @Override
        public void mouseReleased(MouseEvent e) {
        }
        @Override
        public void mouseEntered(MouseEvent e) {
        }
        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
    public class newDirectoryListener implements ActionListener{
        fileNode fileNode;
        public  newDirectoryListener(){
        }
        public newDirectoryListener(fileNode fileNode) {
            this.fileNode = fileNode;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            Object nikos = e.getSource();
            JOptionPane option = new JOptionPane("Enter new directory name");
            String name = JOptionPane.showInputDialog(null,
                    "Enter new directory name","Enter new Directory name",JOptionPane.INFORMATION_MESSAGE);
            DefaultMutableTreeNode thisFolder = fileNode.getParent();
            if(fileNode.getSelfTreenode().isRoot()){
                thisFolder = fileNode.getSelfTreenode();
            }
            fileNode takhs = (fileNode) thisFolder.getUserObject();
            File theDir = new File(takhs.getFile() +"/" +name);

            if(!theDir.exists()) {
                fileNode fileNodeDir = new fileNode(theDir);
                System.out.println("creating directory: " + theDir);
                childs.addTreeNode(thisFolder, fileNodeDir,true);
                theDir.mkdir();
                JPanel newPanel = new JPanel();
                newPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
                model.reload(root);
                openFolder(takhs, newPanel);
            } else{
                JOptionPane optionPane = new JOptionPane("This Directory already exists!TryAgain", JOptionPane.ERROR_MESSAGE);
                JDialog dialog = optionPane.createDialog("Failure");
                dialog.setAlwaysOnTop(true);
                dialog.setVisible(true);
            }
        }
    }
    public class newFileListener implements ActionListener{
        private fileNode fileNode;
        public newFileListener(fileNode fileNode) {
            this.fileNode = fileNode;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane option = new JOptionPane("Enter new File name");
            String name = JOptionPane.showInputDialog(null,
                    "Enter new File name","Enter new File name",JOptionPane.INFORMATION_MESSAGE);
            DefaultMutableTreeNode thisFolder = fileNode.getParent();

            if(fileNode.getSelfTreenode().isRoot()){
                thisFolder = fileNode.getSelfTreenode();
            }
            fileNode takhs = (fileNode) thisFolder.getUserObject();
            File theDir = new File(takhs.getFile() +"/" +name);
            if(!theDir.exists()) {
                fileNode fileNodeDir = new fileNode(theDir);
                System.out.println("creating File: " + theDir);
                childs.addTreeNode(thisFolder, fileNodeDir, false);
                try {
                    boolean ret = theDir.createNewFile();
                    if (ret = true) {
                        System.out.println("File is created");
                    } else {
                        System.out.println("File is not created");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                JPanel newPanel = new JPanel();
                newPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
                model.reload(root);
                openFolder(takhs, newPanel);
            }
            else{
                JOptionPane optionPane = new JOptionPane("This File already exists!TryAgain", JOptionPane.ERROR_MESSAGE);
                JDialog dialog = optionPane.createDialog("Failure");
                dialog.setAlwaysOnTop(true);
                dialog.setVisible(true);
            }
        }
    }
    public class deleteListener implements  ActionListener {
        fileNode fileNode;
        public deleteListener(fileNode fileNode) {
            this.fileNode = fileNode;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            clickedFileDir=false;
            File currDeletedFile = fileNode.getFile();
            String[] options = new String[2];
            options[0] = new String("Confirm delete");
            options[1] = new String("Cancel");
            DefaultMutableTreeNode thisFolder = fileNode.getParent();
            fileNode takhs = (fileNode) thisFolder.getUserObject();
            if (currDeletedFile.isDirectory()) {
                int dialogResult = JOptionPane.showOptionDialog(null, "Are u sure u want to delete directory " + fileNode.getFile().getName(), "Confirm Deletion", 0, JOptionPane.INFORMATION_MESSAGE, null, options, null);
                if (dialogResult == 0) {//confirm delete
                    JPanel newPanel = new JPanel();
                    newPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                    childs.deleteNode(thisFolder, fileNode);
                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    openFolder(takhs, newPanel);
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                    model.reload(root);
                    boolean delete = currDeletedFile.delete();
                    if (delete) {
                        System.out.println("Directory delete YES");
                    } else if (!currDeletedFile.exists()) {
                        System.out.println("DOESNT EXITS");
                    }
                } else if (dialogResult == 1) {
                    System.out.println("Directory delete CANCEL");//Cancel
                }
            } else {
                int dialogResult = JOptionPane.showOptionDialog(null, "Are u sure u want to delete File " + fileNode.getFile().getName(), "Confirm Deletion", 0, JOptionPane.INFORMATION_MESSAGE, null, options, null);
                if (dialogResult == 0) {
                    JPanel newPanel = new JPanel();
                    newPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                    childs.deleteNode(thisFolder, fileNode);
                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    openFolder(takhs, newPanel);
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                    model.reload(root);
                    boolean delete = currDeletedFile.delete();
                    if (delete) {
                        System.out.println("PERFECT DELETE");
                    }
                }
            }
        }
    }
    public class deleteListenerFile implements  ActionListener {
        fileNode fileNode;
        public deleteListenerFile(fileNode fileNode) {
            this.fileNode = fileNode;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if(clickedFileDir){
                clickedFileDir=false;
                fileNode fileNode =  defaultFile.getFileNode();
                File currDeletedFile = fileNode.getFile();
                String[] options = new String[2];
                options[0] = new String("Confirm delete");
                options[1] = new String("Cancel");
                DefaultMutableTreeNode thisFolder = fileNode.getParent();
                fileNode takhs = (fileNode) thisFolder.getUserObject();
                if (currDeletedFile.isDirectory()) {
                    int dialogResult = JOptionPane.showOptionDialog(null, "Are u sure u want to delete directory " + fileNode.getFile().getName(), "Confirm Deletion", 0, JOptionPane.INFORMATION_MESSAGE, null, options, null);
                    if (dialogResult == 0) {//confirm delete
                        JPanel newPanel = new JPanel();
                        newPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                        childs.deleteNode(thisFolder, fileNode);
                        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                        openFolder(takhs, newPanel);
                        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                        model.reload(root);
                        boolean delete = currDeletedFile.delete();
                        if (delete) {
                            System.out.println("Directory delete YES");
                        } else if (!currDeletedFile.exists()) {
                            System.out.println("DOESNT EXITS");
                        }
                    } else if (dialogResult == 1) {
                        System.out.println("Directory delete CANCEL");//Cancel
                    }
                } else {
                    int dialogResult = JOptionPane.showOptionDialog(null, "Are u sure u want to delete File " + fileNode.getFile().getName(), "Confirm Deletion", 0, JOptionPane.INFORMATION_MESSAGE, null, options, null);
                    if (dialogResult == 0) {
                        JPanel newPanel = new JPanel();
                        newPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                        childs.deleteNode(thisFolder, fileNode);
                        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                        openFolder(takhs, newPanel);
                        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                        model.reload(root);
                        boolean delete = currDeletedFile.delete();
                        if (delete) {
                            System.out.println("PERFECT DELETE");
                        }
                    }
                }
            }
            else{
                JOptionPane optionPane = new JOptionPane("Not selected file for delete", JOptionPane.ERROR_MESSAGE);
                JDialog dialog = optionPane.createDialog("Failure");
                dialog.setAlwaysOnTop(true);
                dialog.setVisible(true);
            }
        }
    }
    public class renmeListenerFile implements  ActionListener{
        private fileNode fileNode;
        public renmeListenerFile(fileNode fileNode) {
            this.fileNode = fileNode;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(clickedFileDir){
                JOptionPane option = new JOptionPane("Enter new Dir  name");
                String name = JOptionPane.showInputDialog(null,
                        "Enter new directory name","Rename",JOptionPane.INFORMATION_MESSAGE);
                fileNode =  defaultFile.getFileNode();
                //File currDeletedFile = fileNode.getFile();
                File originalFile = fileNode.getFile();
                File DestFIle = new File(fileNode.getFile().getParent() +"/"+ name);
                DefaultMutableTreeNode thisFolder = fileNode.getParent();
                fileNode takhs = (fileNode) thisFolder.getUserObject();
                childs.renameDirFile(thisFolder,fileNode,DestFIle);
                if(fileNode.getFile().isDirectory()) {
                    DefaultMutableTreeNode newNode = fileNode.getSelfTreenode();
                    fileNode.setSelfTreenode(newNode);
                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                    model.reload(root);
                }
                fileNode.setFile(DestFIle);
                JPanel newPanel = new JPanel();
                newPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                openFolder(takhs,newPanel);
                clickedFileDir = false;
            }
            else{
                JOptionPane optionPane = new JOptionPane("Not selected file for delete", JOptionPane.ERROR_MESSAGE);
                JDialog dialog = optionPane.createDialog("Failure");
                dialog.setAlwaysOnTop(true);
                dialog.setVisible(true);
            }
        }
    }
    public class renameListener implements  ActionListener{
        fileNode fileNode;
        public renameListener(fileNode fileNode) {
            this.fileNode = fileNode;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane option = new JOptionPane("Enter new Dir  name");
            String name = JOptionPane.showInputDialog(null,
                    "Enter new directory name","Rename",JOptionPane.INFORMATION_MESSAGE);
            File originalFile = fileNode.getFile();
            File DestFIle = new File(fileNode.getFile().getParent() +"/"+ name);
            DefaultMutableTreeNode thisFolder = fileNode.getParent();
            fileNode father = (fileNode) thisFolder.getUserObject();
            childs.renameDirFile(thisFolder,fileNode,DestFIle);
            if(fileNode.getFile().isDirectory()) {
                DefaultMutableTreeNode newNode = fileNode.getSelfTreenode();
                fileNode.setSelfTreenode(newNode);
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                model.reload(root);
            }
            fileNode.setFile(DestFIle);
            JPanel newPanel = new JPanel();
            newPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            openFolder(father,newPanel);
        }
    }
}
