/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import data.DataParser;
import data.FileLoader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import languages.LanguageDependent;
import languages.LanguageHelper;

/**
 *
 * @author installer
 */
public class Menu extends JMenuBar implements LanguageDependent{
    MainPanel mainPanel;
    HashMap<String, JComponent> languageComponents;

    public Menu(MainPanel mainPanel){
        this.mainPanel = mainPanel;
        languageComponents = new HashMap<String, JComponent>();
        createMenuFile();
        createMenuTools();
        createMenuLanguage();
        createMenuSound();
    }

    private void createMenuFile(){
        JMenu menuFile = new JMenu("file");
        languageComponents.put("file", menuFile);
        
        JMenuItem itemOpen = new JMenuItem("open");
        languageComponents.put("open", itemOpen);        
        itemOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {                                               
                JFileChooser jfc = new JFileChooser();
                int result = jfc.showOpenDialog(null);
                if(result == JFileChooser.APPROVE_OPTION){
                    FileLoader fl = new FileLoader();
                    if(fl.isDataFile(jfc.getSelectedFile())){
                        Object[] options = {mainPanel.getResourceBundle().getString("add"),mainPanel.getResourceBundle().getString("replace"),mainPanel.getResourceBundle().getString("cancel")};
                        result = JOptionPane.showOptionDialog(mainPanel,mainPanel.getResourceBundle().getString("addOrReplace"), mainPanel.getResourceBundle().getString("warning"),JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                        if(result == JOptionPane.OK_OPTION){//add tracks
                            mainPanel.getTrackCollection().addTracks(fl.loadFile(jfc.getSelectedFile()));
                        }else if(result == JOptionPane.NO_OPTION){//replace tracks
                            mainPanel.getTrackCollection().replaceTracks(fl.loadFile(jfc.getSelectedFile()));
                        }
                    }else{
                        mainPanel.getTrackCollection().addTracks(fl.loadFile(jfc.getSelectedFile()));
                    }
                }                 
            }
        });
        menuFile.add(itemOpen);

        JMenuItem itemSave = new JMenuItem("save");
        languageComponents.put("save", itemSave);
        itemSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser jfc = new JFileChooser();
                int result = jfc.showSaveDialog(null);
                if(result == JFileChooser.APPROVE_OPTION){
                    DataParser dp = new DataParser();
                    dp.writeFile(jfc.getSelectedFile(), mainPanel.getTrackCollection().getTracks());
                }
            }
        });
        menuFile.add(itemSave);
        this.add(menuFile);        
    }

    private void createMenuTools(){
        JMenu menuTools = new JMenu("tools");
        languageComponents.put("tools", menuTools);
        JMenuItem itemTapping = new JMenuItem("tapping");
        languageComponents.put("tapping", itemTapping);
        itemTapping.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                TappingPanel td = new TappingPanel(mainPanel.getTrackCollection());
                td.setVisible(true);
            }
        });
        menuTools.add(itemTapping);
        this.add(menuTools);
    }

    private void createMenuLanguage(){
        JMenu menuLanguage = new JMenu("language");
        languageComponents.put("language",menuLanguage);
        JMenuItem itemEnglish = new JMenuItem("english");
        languageComponents.put("english",itemEnglish);
        itemEnglish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                mainPanel.setLanguage("en");
            }
        });
        menuLanguage.add(itemEnglish);
        JMenuItem itemDutch = new JMenuItem("dutch");
        languageComponents.put("dutch",itemDutch);
        itemDutch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                mainPanel.setLanguage("nl");
            }
        });
        menuLanguage.add(itemDutch);
        this.add(menuLanguage);
    }

    private void createMenuSound(){
        JMenu menuSound = new JMenu("sound");
        languageComponents.put("sound", menuSound);
        JMenuItem itemPlay = new JMenuItem("play");
        languageComponents.put("play", itemPlay);
        itemPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {                
                mainPanel.getTrackCollection().play();
            }
        });
        menuSound.add(itemPlay);
        JMenuItem itemPause = new JMenuItem("pause");
        languageComponents.put("pause", itemPause);
        itemPause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainPanel.getTrackCollection().pause();
            }
        });
        menuSound.add(itemPause);
        JMenuItem itemResume = new JMenuItem("resume");
        languageComponents.put("resume", itemResume);
        itemResume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainPanel.getTrackCollection().resume();
            }
        });
        menuSound.add(itemResume);
        JMenuItem itemStop = new JMenuItem("stop");
        languageComponents.put("stop", itemStop);
        itemStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainPanel.getTrackCollection().stop();
            }
        });
        menuSound.add(itemStop);
        this.add(menuSound);
    }   

    public void updateLanguage(ResourceBundle labels) {
        LanguageHelper.updateLanguage(labels, languageComponents);
    }
}
