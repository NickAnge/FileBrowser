package ce325.hw4;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.FileFilter;
import java.util.*;

public class createChildrenNodeTree {
    private DefaultMutableTreeNode rootNode;
    private File file;

    public createChildrenNodeTree(DefaultMutableTreeNode rootNode, File file) {
        this.rootNode = rootNode;
        this.file = file;
    }
    public void childrenTree(DefaultMutableTreeNode root,File rootFile,fileNode fileNode){
        File[] files = rootFile.listFiles(new FileFilter(){
              @Override
              public boolean accept(File file) {
                  return !file.isHidden();
              }
          }
        );
        if(files == null){
            return;
        }
        Set<File> subFiles = new TreeSet<>(new MyNameComp());
        subFiles.addAll(Arrays.asList(files));

        fileNode.setSubFiles(subFiles);
        Map<File,fileNode> nodes = new TreeMap<>();
        Map<File,fileNode> filesOnly = new TreeMap<>();
        for(File file:files){
            if(file.isDirectory()){
                fileNode newDir = new fileNode(file);
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(newDir);
                nodes.put(file,newDir);
                newDir.setSelfTreenode(childNode);
                newDir.setParent(root);
                root.add(childNode);
                childrenTree(childNode,file,newDir);
            }
            else {
                fileNode newFile = new fileNode(file);
                newFile.setParent(root);
                filesOnly.put(file,newFile);
            }
        }
        fileNode.setFiles(filesOnly);//files only
        fileNode.setFileNodes(nodes);//directories
    }
    public  void revursiveCreateTree(DefaultMutableTreeNode root,File File,fileNode fileNode){
        File[] files = File.listFiles(new FileFilter(){
              @Override
              public boolean accept(File file) {
                  return !file.isHidden();
              }
          }
        );
        if(files == null){
            return;
        }
        Set<File> subFiles = new TreeSet<>();
        subFiles.addAll(Arrays.asList(files));
        Map<File,fileNode> nodes = new TreeMap<>();
        Map<File,fileNode> filesOnly = new TreeMap<>();
        for(File file:files) {
            if (file.isDirectory()) {
                fileNode newDir = new fileNode(file);
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(newDir);
                nodes.put(file, newDir);
                newDir.setSelfTreenode(childNode);
                newDir.setParent(root);
                root.add(childNode);
            } else {
                fileNode newFile = new fileNode(file);
                newFile.setParent(root);
                filesOnly.put(file, newFile);
            }
            fileNode.setFiles(filesOnly);//files only
            fileNode.setFileNodes(nodes);//directories
        }
    }
    public void addTreeNode(DefaultMutableTreeNode parent,fileNode newChild,boolean isDirectory){
        fileNode fileNodeParent = (fileNode)parent.getUserObject();
        if(isDirectory) {
            Set<File> subFiles = fileNodeParent.getSubFiles();
            Set<File> newFiles = new TreeSet<>(new MyNameComp());
            if (subFiles.isEmpty()) {
                newFiles.add(newChild.getFile());
            } else {
                newFiles.addAll(subFiles);
                newFiles.add(newChild.getFile());
            }
            fileNodeParent.setSubFiles(newFiles);
            Map<File, fileNode> nodes = fileNodeParent.getFileNodes();

            DefaultMutableTreeNode treeChild = new DefaultMutableTreeNode(newChild);
            nodes.put(newChild.getFile(), newChild);
            newChild.setSelfTreenode(treeChild);
            newChild.setParent(parent);
            parent.add(treeChild);
        } else  {
            Set<File> subFiles = fileNodeParent.getSubFiles();
            Set<File> newFiles = new TreeSet<>(new MyNameComp());
            if (subFiles.isEmpty()) {
                newFiles.add(newChild.getFile());
            } else {
                newFiles.addAll(subFiles);
                newFiles.add(newChild.getFile());
            }
            fileNodeParent.setSubFiles(newFiles);
            Map<File,fileNode> filesTxt = fileNodeParent.getFiles();
            filesTxt.put(newChild.getFile(),newChild);
            fileNodeParent.setFiles(filesTxt);
            newChild.setParent(parent);
        }
    }
    public void deleteNode(DefaultMutableTreeNode parent,fileNode deleteChild){
        fileNode fileNodeParent = (fileNode)parent.getUserObject();
        Set<File> subFiles = fileNodeParent.getSubFiles();
        Set<File> newFiles = new TreeSet<>(new MyNameComp());
        for(File element : subFiles){
            if(!element .equals(deleteChild.getFile()) ) {
                newFiles.add(element);
            }
        }
        fileNodeParent.setSubFiles(newFiles);
        if(deleteChild.getFile().isDirectory()){
            if(deleteChild.getSubFiles().isEmpty()){
                Map<File,fileNode> parentFiles = fileNodeParent.getFileNodes();
                Map<File,fileNode> newparentFiles = new TreeMap<>();
                for(Map.Entry<File,fileNode> element : parentFiles.entrySet()){
                    if(element.getValue().equals(deleteChild))
                        continue;
                    newparentFiles.put(element.getKey(),element.getValue());
                }
                fileNodeParent.setFileNodes(newparentFiles);
                parent.remove(deleteChild.getSelfTreenode());
            }
            else{
                Set<File> direcFiles = deleteChild.getSubFiles();
                for(File entry:direcFiles){
                    if(entry.isDirectory()) {
                        deleteNode(deleteChild.getSelfTreenode(), deleteChild.getFileNodes().get(entry));
                    }
                    else{
                        deleteNode(deleteChild.getSelfTreenode(),deleteChild.getFiles().get(entry));
                    }
                    boolean resultDelete = entry.delete();
                    if(resultDelete){
                        System.out.println("Subfile deleted successdully");
                    }
                }
                Map<File,fileNode> parentFiles = fileNodeParent.getFileNodes();
                Map<File,fileNode> newparentFiles = new TreeMap<>();
                for(Map.Entry<File,fileNode> element : parentFiles.entrySet()){
                    if(element.getValue().equals(deleteChild))
                        continue;
                    newparentFiles.put(element.getKey(),element.getValue());
                }
                fileNodeParent.setFileNodes(newparentFiles);
                parent.remove(deleteChild.getSelfTreenode());
            }
        }else{
            Map<File,fileNode> parentFiles = fileNodeParent.getFiles();
            Map<File,fileNode> newparentFiles = new TreeMap<>();
            for(Map.Entry<File,fileNode> element : parentFiles.entrySet()){
                if(element.getValue().equals(deleteChild))
                    continue;
                newparentFiles.put(element.getKey(),element.getValue());
            }
            fileNodeParent.setFiles(newparentFiles);
        }
    }
    public  void renameDirFile(DefaultMutableTreeNode parent,fileNode renameChild,File destFile){
        fileNode fileNodeParent = (fileNode)parent.getUserObject();
        Set<File> subFiles = fileNodeParent.getSubFiles();
        Set<File> newFiles = new TreeSet<>(new MyNameComp());
        for(File element:subFiles){
            if(!element.equals(renameChild.getFile())){
                newFiles.add(element);
            }
            else{
                newFiles.add(destFile);
            }
        }
        fileNodeParent.setSubFiles(newFiles);
        if(renameChild.getFile().isDirectory()){
            File original = renameChild.getFile();
            boolean result = original.renameTo(destFile);
            Set<File> dirFiles = renameChild.getSubFiles();
            for(File entry : dirFiles){
                if(entry.isDirectory()) {
                    renameChild.getFileNodes().get(entry).setParent(renameChild.getSelfTreenode());
                }
                else {
                    renameChild.getFiles().get(entry).setParent(renameChild.getSelfTreenode());
                }
            }
        }
        else {
            Map<File,fileNode> files = fileNodeParent.getFiles();
            Map<File,fileNode> filesRename = new TreeMap<>(new MyNameComp());
            for(Map.Entry<File,fileNode> element : files.entrySet()){
                if(element.getValue().equals(renameChild)){
                    File original = renameChild.getFile();
                    boolean result = original.renameTo(destFile);
                    renameChild.setFile(destFile);
                    filesRename.put(destFile,renameChild);
                }
                else {
                    filesRename.put(element.getKey(), element.getValue());
                }
            }
            fileNodeParent.setFiles(filesRename);
        }
    }
    public static class MyNameComp implements Comparator<File>{
        int leastSize;
        int itsTheSame = 0;
        @Override
        public int compare(File o1, File o2) {
           if(o1.getName().length() > o2.getName().length()){
               itsTheSame = 1;
                leastSize = o2.getName().length();
            }
            else if (o1.getName().length() < o2.getName().length()){
                itsTheSame =2;
                leastSize = o1.getName().length();
            }
            else {
               leastSize = o1.getName().length();
            }
            for(int i = 0; i<leastSize ;i++){
                if(o1.getName().toLowerCase().charAt(i)< o2.getName().toLowerCase().charAt(i)){
                    return -1;
                }
                else if(o1.getName().toLowerCase().charAt(i) > o2.getName().toLowerCase().charAt(i)){
                    return 1;
                }
            }
            if(itsTheSame == 1){
                return 1;
            }
            else if(itsTheSame ==2){
                return -1;
            }
            return 0;
        }
    }

}