package gui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileFilter;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class FileTree extends JTree {
    /** Construct a FileTree */
    public FileTree(FileFilter filter) {
        // Make a tree list with all the nodes, and make it a JTree
        super(new DefaultMutableTreeNode());
        this.setLayout(new BorderLayout());
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)getPathForRow(0).getLastPathComponent();
        File[] roots = File.listRoots();
        for(int i = 0; i < roots.length; i++){
            rootNode.add(new FileNode(roots[i],filter));
        }
        File home = new File(System.getProperty("user.home")){
            @Override
            public String toString(){
                return "home";
            }
        };
        rootNode.add(new FileNode(home,filter));        
        expandRow(0);
        setRootVisible(false);
        // Add a listener                
        addTreeSelectionListener(new TreeSelectionListener() {
          public void valueChanged(TreeSelectionEvent e) {            
            FileNode node = (FileNode)e.getNewLeadSelectionPath().getLastPathComponent();
            node.testNode();            
          }
        });                
    }


    public class FileNode extends DefaultMutableTreeNode{
        private boolean opened;
        private FileFilter fileFilter;
        public FileNode(File f,FileFilter fileFilter){
            this.fileFilter = fileFilter;
            setUserObject(f);
//            setUserObject(new File(f.getAbsolutePath()){
//                @Override
//                public String toString(){
//                    return getName();
//                }
//            });
            opened = f.isFile();
            System.out.println("created:" + f);            
            loadChildren();
        }

        private FileNode(File f){
            this(f,new FileFilter() {
                public boolean accept(File pathname) {
                    return true;
                }
            });
        }

        private FileNode(File f, boolean recursive){            
            setUserObject(new File(f.getAbsolutePath()){
                @Override
                public String toString(){
                    return getName();
                }
            });
            opened = f.isFile();            
        }

        public void testNode(){
            if(!opened){
                opened = true;
                if(children != null){
                    for(Object obj : children){
                        ((FileNode)obj).loadChildren();
                    }
                }
            }
        }

        public final void loadChildren(){
            File f = (File)userObject;
            if(f.isDirectory()){
                File[] files = f.listFiles(fileFilter);
                if (files != null){
                    for(int i = 0; i < files.length; i++){
                        this.add(new FileNode(files[i],false));
                    }
                }
            }
        }

    }
}