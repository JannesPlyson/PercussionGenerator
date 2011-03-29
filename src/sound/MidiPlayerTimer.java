/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sound;

import com.sun.media.sound.AudioSynthesizer;
import com.sun.media.sound.EmergencySoundbank;
import model.Track;
import model.TrackCollection;
import model.TrackCollectionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import model.Note;
import model.Pair;

/**
 *
 * @author installer
 */
public class MidiPlayerTimer implements MidiPlayer,TrackCollectionListener{
    private Receiver receiver;
    private boolean playerLoaded;    
    private AudioSynthesizer synth;
    private TrackCollection trackCollection;
    private Timer timer,timerViews;
    private long lastStarted,lastAdded,timeOffset;
    private boolean play;
    TimerTask timerTask;
    private HashMap<Instrument,Integer> channelMapping;
    private long interval;

    public MidiPlayerTimer(){
        playerLoaded = false;
        new Thread(new Runnable(){
            public void run() {
                try {
                    synth = findAudioSynthesizer();
                    if(synth != null){
                        synth.loadAllInstruments(EmergencySoundbank.createSoundbank());
                        synth.open();
                        receiver = synth.getReceiver();
                        playerLoaded = true;
                        System.out.println("midi loaded");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }).start();        
        lastStarted = 0;
        timeOffset = 0;        
        play = false;
        interval = 20;
    }

    private void resetTimer(){
        if(timer != null){
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            public void run(){
                addTimerEvents();
            }
        },0,interval);
    }

    public void setTrackCollection(final TrackCollection trackCollection){
        if(trackCollection != null){
            trackCollection.removeListener(this);
        }
        this.trackCollection = trackCollection;
        trackCollection.addListener(this);
        mapChannels();
        if(timerViews != null){
            timerViews.cancel();
        }
        timerViews = new Timer();
        timerViews.scheduleAtFixedRate(new TimerTask(){
            public void run(){
                if(play && trackCollection != null){
                    trackCollection.timeUpdated(System.nanoTime()-lastStarted);
                }
            }
        }, 0, 50);
    }

    private void mapChannels(){
        ArrayList<Track> tracks = trackCollection.getTracks();
        channelMapping = new HashMap<Instrument, Integer>();
        for(int i = 0; i < tracks.size(); i++){
            Track track = tracks.get(i);
            Instrument instrument = track.getInstrument();
            Integer channel = channelMapping.get(instrument);
            if(channel == null){
                int channelNumber = channelMapping.size();
                channelMapping.put(instrument, channelNumber);
                if(instrument != null){                    
                    synth.getChannels()[channelNumber].programChange(instrument.getPatch().getBank(), instrument.getPatch().getProgram());
                }else{
                    synth.getChannels()[channelNumber].programChange(0, 0);
                }
            }
        }
    }

    public boolean isPlayerLoaded(){
        return playerLoaded;
    }

    public static AudioSynthesizer findAudioSynthesizer() throws MidiUnavailableException {
        // First check if default synthesizer is AudioSynthesizer.
        Synthesizer synth = MidiSystem.getSynthesizer();
        if (synth instanceof AudioSynthesizer)
                return (AudioSynthesizer) synth;

        // If default synhtesizer is not AudioSynthesizer, check others.
        Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
                MidiDevice dev = MidiSystem.getMidiDevice(infos[i]);
                if (dev instanceof AudioSynthesizer)
                        return (AudioSynthesizer) dev;
        }
        // No AudioSynthesizer was found, return null.
        return null;
    }

    public Instrument[] getInstruments() {
        Instrument[] test = synth.getAvailableInstruments();
        return synth.getAvailableInstruments();
    }

    public void play() {
        timeOffset = 0;
        lastAdded = -1;
        mapChannels();
        resume();
    }

    public void pause() {
        timeOffset = (System.nanoTime()-lastStarted);
        lastAdded = timeOffset;        
        play = false;
        timer.cancel();        
    }

    public void stop() {
        play = false;
        lastAdded = -1;
        timeOffset = 0;
        if(timer != null){
            timer.cancel();
        }        
    }

    public void resume() {
        play = true;
        lastStarted = System.nanoTime()-timeOffset;
        resetTimer();        
    }   

    public void addTimerEvents(){
        ArrayList<Track> tracks = trackCollection.getTracks();
        ArrayList<Pair<Note,Track>> firstNotes = new ArrayList<Pair<Note,Track>>();
        ArrayList<Pair<Iterator<Note>,Track>> iterators = new ArrayList<Pair<Iterator<Note>, Track>>();        
        for(int i = 0; i < tracks.size(); i++){
            Track track = tracks.get(i);
            if(!track.isMuted()){
                long time = lastAdded % track.getLength();                
                Iterator<Note> it = track.getIterator();
                boolean doNext = true;
                while(it.hasNext() && doNext){
                    Note n = it.next();                    
                    if(time < n.getStart()){
                        firstNotes.add(new Pair<Note,Track>(n,track));
                        doNext = false;
                    }
                }
                iterators.add(new Pair<Iterator<Note>,Track>(it,track));
            }
        }
        long newLastAdded = (System.nanoTime()-lastStarted) + 2000000*interval;
        for(int i = 0; i < firstNotes.size(); i++){            
            Note n = firstNotes.get(i).first;
            long time = newLastAdded%firstNotes.get(i).second.getLength();
            if(n.getStart() < time){
                scheduleNote(n,firstNotes.get(i).second);              
            }
        }                
        for(int i = 0; i < iterators.size(); i++){
            Iterator<Note> it = iterators.get(i).first;
            boolean doNext = true;
            while(it.hasNext() && doNext){
                Note n = it.next();
                long time = newLastAdded%iterators.get(i).second.getLength();
                if(n.getStart() < time){                    
                    scheduleNote(n,iterators.get(i).second);                   
                }
            }            
        }
        for(int i = 0; i < iterators.size(); i++){
            Track track = iterators.get(i).second;
            if(newLastAdded%track.getLength() < lastAdded%track.getLength()){ //passing the end marker                
                long offset = track.getLength()-(lastAdded%track.getLength());                
                Iterator<Note> it = track.getIterator();
                boolean doNext = true;
                while(it.hasNext() && doNext){
                    Note n = it.next();
                    long time = newLastAdded%track.getLength();
                    if(n.getStart() < time){                        
                        scheduleNote(n,track);
                    }
                }
            }
        }
        lastAdded = newLastAdded;
    }

    private void scheduleNote(final Note n, Track track){
        final ShortMessage messageNoteOn = new ShortMessage();
        final ShortMessage messageNoteOff = new ShortMessage();
        try{
            messageNoteOn.setMessage(ShortMessage.NOTE_ON, getChannel(track), n.getPitch(), n.getVolume());
            messageNoteOff.setMessage(ShortMessage.NOTE_OFF, getChannel(track), n.getPitch(), n.getVolume());
            long delay = 0;
            try{
                delay = (System.nanoTime()-lastStarted)%track.getLength();                
                if(lastAdded == -1 && n.getStart() == 0){
                    receiver.send(messageNoteOn,-1);
                }
                else if(n.getStart() < delay){
                    delay = (n.getStart() + track.getLength() - delay)/1000000;
                }else{                    
                    delay = (n.getStart()-delay)/1000000;
                }                
                timer.schedule(new TimerTask(){
                    public void run(){
                        receiver.send(messageNoteOn, -1);                        
                    }
                },delay);                
            }catch(IllegalArgumentException exc){}
            try{
                timer.schedule(new TimerTask(){
                    public void run(){
                        receiver.send(messageNoteOff, -1);
                    }
                }, delay + n.getDuration());
            }catch(IllegalArgumentException exc){
                receiver.send(messageNoteOff, -1);
            }
        }catch(Exception exc){} //timer renewing cancel everthing
    }    

    private int getChannel(Track track){
        int channel = 0;
        try{
            channel = channelMapping.get(track.getInstrument());
            return channel;
        }catch(Exception exc){
            mapChannels();
            return getChannel(track);
        }
    }   

    public boolean isPlaying() {
        return play;
    }

    public void TrackUpdated(Track track) {
        resetTimer();
        mapChannels();
    }
}
