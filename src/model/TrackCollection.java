/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import data.FileLoader;
import gui.ChooseInstrument;
import gui.PopupMenuUser;
import gui.view.View;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.sound.midi.Instrument;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import languages.LanguageDependent;
import languages.LanguageHelper;
import sound.MidiPlayer;

/**
 *
 * @author installer
 */
public abstract class TrackCollection extends JPanel implements MouseListener, LanguageDependent, PopupMenuUser {

    private MidiPlayer midiPlayer;
    private Timer timer;
    private ArrayList<TrackCollectionListener> listeners;

    public TrackCollection(MidiPlayer midiPlayer) {
        listeners = new ArrayList<TrackCollectionListener>();
        //BoxLayout bl = new BoxLayout(this, BoxLayout.X_AXIS);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.midiPlayer = midiPlayer;
        this.addMouseListener(this);
        timer = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<Track> tracks = getTracks();
                for(int i=0; i<tracks.size(); i++){
                    Track track = tracks.get(i);
                    long time = track.getTime();                                        
                }
            }
        });
        timer.start();
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateTrackWidth();
                revalidate();
                repaint();
            }
        });
    }

    public void addListener(TrackCollectionListener listener){
        listeners.add(listener);
    }

    public void removeListener(TrackCollectionListener listener){
        listeners.remove(listener);
    }

    public void updateListeners(Track track){
        for(int i=0;i<listeners.size();i++){
            listeners.get(i).TrackUpdated(track);
        }
    }

    public void addTrack(Track track) {
        if (track.getViews().isEmpty()) {
            track.addView(View.SIMPLE_VIEW);
        }
        this.add(track);
        updateTrackWidth();
        this.revalidate();
        this.repaint();
    }

    public void addTracks(ArrayList<Track> tracks) {
        for (int i = 0; i < tracks.size(); i++) {
            addTrack(tracks.get(i));
        }
    }

    public void removeTrack(Track track) {
        this.remove(track);
        this.revalidate();
        this.repaint();
    }

    public void removeAllTracks() {
        this.removeAll();
        this.revalidate();
        this.repaint();
    }

    public void replaceTracks(ArrayList<Track> newTracks) {
        this.removeAll();
        addTracks(newTracks);
    }

    public ArrayList<Track> getTracks() {
        ArrayList<Track> list = new ArrayList<Track>();
        Component[] track = this.getComponents();
        for (int i = 0; i < track.length; i++) {
            if (track[i] instanceof Track) {
                list.add((Track) track[i]);
            }
        }
        return list;
    }
    
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {        
            JPopupMenu popupMenu = new JPopupMenu();
            Container c = getParent();
            while (c != null) {
                if (c instanceof PopupMenuUser) {
                    ((PopupMenuUser) c).addElements(popupMenu,this);
                }
                c = c.getParent();
            }
            addElements(popupMenu,this);
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void updateLanguage(ResourceBundle labels) {
        ArrayList<Track> tracks = getTracks();
        for (int i = 0; i < tracks.size(); i++) {
            tracks.get(i).updateLanguage(labels);
        }
        //todo: update popupMenu
    }    

    public void play() {        
        midiPlayer.play();
    }

    public void pause() {
        midiPlayer.pause();
    }

    public void resume() {
        midiPlayer.resume();
    }

    public void stop() {
        midiPlayer.stop();
    }

    public Instrument[] getInstruments() {
        return midiPlayer.getInstruments();
    }

    public long getLongest(){
        ArrayList<Track> list = getTracks();
        long max = 0;
        for(int i = 0; i < list.size(); i++){
            max = Math.max(max, list.get(i).getLength());
        }
        return max;
    }

    private void updateTrackWidth(){
        long maxLength = getLongest();
        ArrayList<Track> list = getTracks();
        for(int i = 0; i < list.size(); i++){
            Track track = list.get(i);
            int width = (int)(((double)track.getLength()/maxLength)*getWidth());
            if(width < 150){
                width = 150;
            }
            list.get(i).setPreferredSize(new Dimension(width,50));
            list.get(i).setMaximumSize(new Dimension(width,50));
        }
    }

    public void timeUpdated(long time){
        ArrayList<Track> tracks = getTracks();
        for(int i = 0; i < tracks.size(); i++){
            tracks.get(i).timeUpdated(time);
        }
    }

    public void addMenuLoadFile(JMenu menuAddTrack){
        JMenuItem itemLoadFile = new JMenuItem("From file");
        itemLoadFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                ResourceBundle bundle = LanguageHelper.getResourceBundle();
                JFileChooser jfc = new JFileChooser();
                int result = jfc.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    FileLoader fl = new FileLoader();
                    if (fl.isDataFile(jfc.getSelectedFile())) {
                        Object[] options = {bundle.getString("add"), bundle.getString("cancel")};
                        result = JOptionPane.showOptionDialog(null, bundle.getString("addOrReplace"), bundle.getString("warning"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                        if (result == JOptionPane.OK_OPTION) {//add tracks
                            addTracks(fl.loadFile(jfc.getSelectedFile()));
                        } else if (result == JOptionPane.NO_OPTION) {//replace tracks
                            replaceTracks(fl.loadFile(jfc.getSelectedFile()));
                        }
                    } else {
                        addTracks(fl.loadFile(jfc.getSelectedFile()));
                    }
                }
            }
        });
        menuAddTrack.add(itemLoadFile);
    }

    public void addMenuRemoveTrack(JMenu menuTrack, final Object caller){
        if(caller instanceof Track){
            JMenuItem itemRemoveTrack = new JMenuItem("remove track");
            itemRemoveTrack.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeTrack((Track)caller);
                }
            });
            menuTrack.add(itemRemoveTrack);
        }
    }

    public void addMenuMetronome(JMenu menuAddTrack){
        JMenuItem itemAddMetronome = new JMenuItem("Add metronome");
        itemAddMetronome.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String result = JOptionPane.showInputDialog("bpm?");
                try{
                    int bpm = Integer.parseInt(result);
                    addTrack(Track.createMetronome(bpm, midiPlayer.getInstruments()));
                }catch(NumberFormatException exc){
                    System.out.println("bpm is not a number");
                }
            }
        });
        menuAddTrack.add(itemAddMetronome);
    }

    public void addChooseInstrument(JMenu trackMenu,final Object caller){
        if(midiPlayer.isPlayerLoaded() && midiPlayer.getInstruments().length > 0 && caller instanceof Track){
            JMenuItem itemChooseInstrument = new JMenuItem("Choose instrument");
            trackMenu.add(itemChooseInstrument);
            itemChooseInstrument.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {                    
                    new ChooseInstrument((Track)caller, midiPlayer.getInstruments()).setVisible(true);
                }
            });
        }        
    }

    public void addMenuSound(JPopupMenu popup, final Object caller){
        final TrackCollection thisObject = this;
        if(midiPlayer.isPlayerLoaded() && getTracks().size() > 0){
            JMenu soundMenu = new JMenu("Sound");
            popup.add(soundMenu);
            if(!midiPlayer.isPlaying()){
                JMenuItem itemPlay = new JMenuItem("Play");
                soundMenu.add(itemPlay);
                itemPlay.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        midiPlayer.stop();
                        midiPlayer.setTrackCollection(thisObject);
                        play();
                    }
                });
                JMenuItem itemResume = new JMenuItem("Resume");
                soundMenu.add(itemResume);
                itemResume.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        resume();
                    }
                });                            
            }else{
                JMenuItem itemPause = new JMenuItem("Pause");
                soundMenu.add(itemPause);
                itemPause.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        pause();
                    }
                });

                JMenuItem itemStop = new JMenuItem("Stop");
                soundMenu.add(itemStop);
                itemStop.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        stop();
                    }
                });
            }            
        }
    }

    

    public abstract void addElements(JPopupMenu popup, Object caller);

    

}
