/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sound;

import com.sun.media.sound.EmergencySoundbank;
import model.TrackCollection;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;
import model.Note;

/**
 *
 * @author installer
 */
public class MidiPlayerSequence implements MidiPlayer{
    private Sequencer	sequencer;
    private Synthesizer synthesizer;
    private TrackCollection trackCollection;

    public MidiPlayerSequence() throws MidiUnavailableException{
        synthesizer = MidiSystem.getSynthesizer();        
        try{
            synthesizer.loadAllInstruments(EmergencySoundbank.createSoundbank());
        }catch(Exception exc){
            exc.printStackTrace();
        }        
        sequencer = MidiSystem.getSequencer();
        sequencer.setMasterSyncMode(Sequencer.SyncMode.INTERNAL_CLOCK);
        if(sequencer == null){
            System.out.println("can't get sequencer");
        }
        sequencer.addMetaEventListener(new MetaEventListener(){
            public void meta(MetaMessage event){
                if (event.getType() == 47){
                    //loops should be handeld here                                       
                    sequencer.close();
                    if (synthesizer != null)
                    {
                            synthesizer.close();
                    }                    
                }
            }
        });        
    }

    public Instrument[] getInstruments(){
        return synthesizer.getAvailableInstruments();
    }
    
    private void compileTracks(){
        try{
            Sequence sequence = new Sequence(Sequence.SMPTE_25, 40);//a tick is one milisecond
            ArrayList<model.Track> tracks = trackCollection.getTracks();
            for(int i = 0; i < tracks.size(); i++){
                if(!tracks.get(i).isMuted()){
                    Iterator<Note> it = tracks.get(i).getIterator();
                    Track track = sequence.createTrack();
                    Instrument instrument = tracks.get(i).getInstrument();
                    if(instrument != null){
                        track.add(createProgramChangeEvent(i,instrument.getPatch().getProgram(), 0));
                    }
                    while(it.hasNext()){
                        Note note = it.next();
                        track.add(createNoteOnEvent(i,note.getPitch(), note.getStart(), note.getVolume()));
                        track.add(createNoteOffEvent(i,note.getPitch(), note.getStart() + note.getDuration()));
                    }
                }
            }
            sequencer.setSequence(sequence);
        }catch(Exception exc){}
    }   
   
    public void play(){
         try{
            if (!(sequencer instanceof Synthesizer)){
                synthesizer = MidiSystem.getSynthesizer();
                synthesizer.open();
                Receiver synthReceiver = synthesizer.getReceiver();
                Transmitter seqTransmitter = sequencer.getTransmitter();
                seqTransmitter.setReceiver(synthReceiver);
            }
            //synthesizer.getChannels()[0].programChange(0,75);
            sequencer.setMasterSyncMode(Sequencer.SyncMode.INTERNAL_CLOCK);
            sequencer.open();            
            sequencer.start();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void pause(){
        sequencer.stop();
    }

    public void stop(){
        sequencer.stop();
        sequencer.setTickPosition(0);
    }

    public void resume(){
        sequencer.start();
    }

    private static MidiEvent createProgramChangeEvent(int channel,int instrument,long lTick){
        ShortMessage message = new ShortMessage();
        try{
            message.setMessage(ShortMessage.PROGRAM_CHANGE,channel,instrument,0);
        }catch (InvalidMidiDataException e){
            e.printStackTrace();        
        }
        MidiEvent event = new MidiEvent(message,lTick);
        return event;	
    }

    private static MidiEvent createNoteOnEvent(int channel, int nKey, long lTick, int velocity){
        return createNoteEvent(ShortMessage.NOTE_ON,channel,nKey,velocity,lTick);
    }

    private static MidiEvent createNoteOffEvent(int channel, int nKey, long lTick){
        return createNoteEvent(ShortMessage.NOTE_OFF,channel,nKey,0,lTick);
    }

    private static MidiEvent createNoteEvent(int nCommand,int channel,int nKey,int nVelocity,long lTick){
        ShortMessage	message = new ShortMessage();
        try{
            message.setMessage(nCommand,channel,nKey,nVelocity);
        }catch (InvalidMidiDataException e){
            e.printStackTrace();        
        }
        MidiEvent event = new MidiEvent(message,lTick);
        return event;
    }    

    public boolean isPlayerLoaded() {
        return true;
    }

    public boolean isPlaying(){
        return synthesizer.isOpen();
    }

    public void setTrackCollection(TrackCollection trackCollection) {
        this.trackCollection = trackCollection;
        compileTracks();
    }

    public void TrackUpdated(model.Track track) {
        compileTracks();
    }
}
