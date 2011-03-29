/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import data.FileLoader;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import model.Track;
import model.TrackCollection;

/**
 *
 * @author installer
 */
public class LoadFilePanel extends JPanel{
    private TrackCollection trackCollection;
    private FileTree fileTree;
    private FileLoader fileLoader;
    public LoadFilePanel(final TrackCollection trackCollection){
        this.trackCollection = trackCollection;
        fileLoader = new FileLoader();
        this.setBorder(BorderFactory.createTitledBorder("File"));        
        FileFilter filter = new FileFilter() {
            public boolean accept(File pathname) {
                if(pathname.getName().startsWith(".") || pathname.isHidden()){
                    return false;
                }else if(pathname.isFile()){
                    return pathname.getName().endsWith("mid")
                            || pathname.getName().endsWith("xml");
                }else{
                    return true;
                }
            }
        };
        fileTree = new FileTree(filter);

        fileTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)fileTree.getSelectionPath().getLastPathComponent();
                    File f = (File)node.getUserObject();
                    ArrayList<Track> tracks = fileLoader.loadFile(f);
                    if(tracks != null){
                        trackCollection.addTracks(tracks);
                    }
                }
            }
        });

        JScrollPane scrollpane = new JScrollPane(fileTree);
        scrollpane.setPreferredSize(new Dimension(200,200));
        this.add(scrollpane);
    }
}
