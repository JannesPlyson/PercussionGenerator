/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import gui.view.SimpleView;
import gui.view.View;
import gui.view.ViewOptions;
import gui.view.ViewPanel;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.sound.midi.Instrument;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import languages.LanguageDependent;
import model.Note;
import model.Pattern;

/**
 *
 * @author installer
 */
public class Track extends JPanel implements LanguageDependent{
    private Pattern pattern;    
    private Instrument instrument;    
    private long time;
    private boolean mute;

    public Track(Pattern pattern){
        instrument = null;                
        this.pattern = pattern;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setAlignmentX(0);
    }

    public boolean isMuted(){
        return mute;
    }

    public void setMute(boolean mute){
        this.mute = mute;
        updateTrackCollection();
    }

    public TrackCollection getTrackCollection(){
        if(this.getParent() instanceof TrackCollection){
            return (TrackCollection)this.getParent();
        }else{
            return null;
        }
    }

    public ViewOptions addView(int type){
        ViewOptions viewOptions = null;
        if(type == View.SIMPLE_VIEW){
            SimpleView sv = new SimpleView(this);
            ViewPanel vp = new ViewPanel(sv);
            this.add(vp);
            viewOptions = vp.getViewOption();
        }
        this.revalidate();
        this.repaint();
        updateTrackCollection();
        return viewOptions;
    }

    public ArrayList<View> getViews(){
        ArrayList<View> list = new ArrayList<View>();
        Component[] views = this.getComponents();
        for (int i = 0; i < views.length; i++) {
            if (views[i] instanceof ViewPanel) {
                list.add(((ViewPanel)views[i]).getView());
            }
        }
        return list;
    }

    public long getLength(){
        return pattern.getLength();        
    }

    public Pattern getPattern(){
        return pattern;
    }

    public Iterator<Note> getIterator(){
        return pattern.getIterator();
    }

    public void shift(int time){
        if(pattern != null){
            pattern.shift(time);
        }
        updateTrackCollection();
        this.repaint();
    }

    public void updateLanguage(ResourceBundle labels) {
        ArrayList<View> views = getViews();
        for(int i=0; i < views.size(); i++){
            views.get(i).updateLanguage(labels);
        }
    }

    public Instrument getInstrument(){
        return instrument;
    }

    public void setInstrument(Instrument instrument){
        this.instrument = instrument;
        updateTrackCollection();
    }        

    public static Track createMetronome(int bpm, Instrument[] instruments){
        long duration = 60000000000l/bpm;        
        Note n = new Note(0, duration, 90, 127);
        Pattern p = new Pattern(duration);
        p.addNote(n);
        Track track = new Track(p);        
        int i = 0;
        while(i< instruments.length && !instruments[i].getName().startsWith("Woodblock")){            
            i++;
        }
        if(i < instruments.length){
            track.setInstrument(instruments[i]);            
        }        
        return track;
    }

    public void timeUpdated(long time) {        
        this.time = time;
        ArrayList<View> views = getViews();
        for(int i = 0; i < views.size(); i++){
            views.get(i).repaint();
        }
    }

    public long getTime(){
        return time % getLength();
    }

    public Track getClone(){
        Track track = new Track(pattern.getClone());
        return track;
    }

    private void updateTrackCollection(){
        if(getTrackCollection() != null){
            getTrackCollection().updateListeners(this);
        }
    }
}
