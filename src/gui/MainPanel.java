/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.event.ActionEvent;
import model.TrackCollection;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.sound.midi.Instrument;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import languages.LanguageDependent;
import model.Note;
import model.Pair;
import model.Track;
import quantisation.NoteQuantizer;
import sound.MidiPlayer;
import sound.MidiPlayerTimer;

/**
 *
 * @author installer
 */
public class MainPanel extends JPanel implements LanguageDependent{    
    private Menu menu;
    private ResourceBundle labels;
    private MidiPlayer midiPlayer;    
    private TrackCollection trackCollection;

    public MainPanel(){
        this.setMinimumSize(new Dimension(800,600));
        this.setPreferredSize(new Dimension(800,600));
        labels = ResourceBundle.getBundle("languages.labels");        
        this.setLayout(new BorderLayout());
        //menu = new Menu(this);
        //this.add(menu,BorderLayout.NORTH);
        //midiPlayer = new MidiPlayerTimer();
        midiPlayer = new MidiPlayerTimer();
        createTrackCollection();
        ((MidiPlayerTimer)midiPlayer).setTrackCollection(trackCollection);        
        trackCollection.setBorder(BorderFactory.createTitledBorder("Tracks"));
        this.add(trackCollection,BorderLayout.CENTER);
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        //inputPanel.setPreferredSize(new Dimension(250,100));
        this.add(inputPanel,BorderLayout.SOUTH);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));        
        inputPanel.add(new TappingPanel(trackCollection));        
        inputPanel.add(new MicrophoneTappingPanel(trackCollection));
        inputPanel.add(new LoadFilePanel(trackCollection));
        updateLanguage(labels);
    }

    public void createTrackCollection(){
        trackCollection = new TrackCollection(midiPlayer) {
            @Override
            public void addElements(JPopupMenu popup, final Object caller) {
                JMenu menuTrack = new JMenu("Track");
                popup.add(menuTrack);                
                JMenu menuAddTrack = new JMenu("Add track(s)");
                menuTrack.add(menuAddTrack);
                trackCollection.addMenuLoadFile(menuAddTrack);
                trackCollection.addMenuMetronome(menuAddTrack);
                trackCollection.addChooseInstrument(menuTrack, caller);
                trackCollection.addMenuRemoveTrack(menuTrack, caller);                
                trackCollection.addMenuSound(popup,caller);
                if(caller instanceof Track){
                    JMenuItem itemQuantize = new JMenuItem("quantize");
                    popup.add(itemQuantize);
                    itemQuantize.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ae) {
                            NoteQuantizer nq = new NoteQuantizer();
                            ArrayList<Pair<Integer,Note>> notes = nq.getNotes(((Track)caller).getPattern());
                            if(notes == null){
                                System.out.println("no notes");
                            }else{
                                for(int i = 0; i < notes.size(); i++){
                                    System.out.println(notes.get(i).first);
                                }
                            }
                        }
                    });
                }
            }
        };
    }

    public MidiPlayer getMidiPlayer(){
        return midiPlayer;
    }

    public TrackCollection getTrackCollection(){
        return trackCollection;
    }    

    public Instrument[] getInstruments(){        
        return midiPlayer.getInstruments();
    }

    public void setLanguage(String language){
        labels = ResourceBundle.getBundle("languages.labels",new Locale(language));
        updateLanguage(labels);
    }

    public ResourceBundle getResourceBundle(){
        return labels;
    }

    public void updateLanguage(ResourceBundle labels) {
        //menu.updateLanguage(labels);
        trackCollection.updateLanguage(labels);
    }    
    
}
