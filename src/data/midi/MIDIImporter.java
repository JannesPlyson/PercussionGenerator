/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data.midi;

import gui.view.SimpleViewOptions;
import gui.view.View;
import input.InputNotes;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import model.Pattern;

/**
 *
 * @author installer
 */
public class MIDIImporter {
    public ArrayList<model.Track> readFile(File midiFile){
        Sequence sequence;        
        try{
            sequence = MidiSystem.getSequence(midiFile);
            double tickLength = sequence.getMicrosecondLength()*1000/sequence.getTickLength();
            ArrayList<model.Track> list = new ArrayList<model.Track>();
            HashMap<Integer,SimpleNote> simpleNotes = new HashMap<Integer, SimpleNote>();            
            for(int j =0; j < sequence.getTracks().length; j++){                
                Track track = sequence.getTracks()[j];
                InputNotes input = new InputNotes();
                //Pattern pattern = new Pattern(sequence.getMicrosecondLength()*1000);
                for(int i=0; i < track.size(); i++){
                    MidiEvent event = track.get(i);
                    MidiMessage midiMessage = event.getMessage();
                    if (midiMessage instanceof ShortMessage){
                        ShortMessage sm = (ShortMessage) midiMessage;
                        if(sm.getCommand() == ShortMessage.NOTE_ON){
                            SimpleNote sn = new SimpleNote();
                            sn.velocity = sm.getData2();
                            sn.pitch = sm.getData1();
                            sn.start = event.getTick();
                            simpleNotes.put(sn.pitch, sn);
                        }else if(sm.getCommand() == ShortMessage.NOTE_OFF){
                            if(simpleNotes.containsKey(sm.getData1())){
                                SimpleNote sn = simpleNotes.get(sm.getData1());
                                simpleNotes.remove(sm.getData1());
                                sn.duration = event.getTick();
                                input.addInput((long)(sn.start*tickLength), (long)(sn.duration*tickLength), sn.pitch, sn.velocity);
                            }
                        }else{
                            System.out.println("unhandled short message:");
                            System.out.println("message command:" + sm.getCommand());
                            System.out.println("message data1:" + sm.getData1());
                            System.out.println("message data2:" + sm.getData2());
                        }
                    }else{
                        System.out.println("unhandled long message:");
                        byte[] mesg = midiMessage.getMessage();
                        for(int k = 0; k < mesg.length; k++){
                            System.out.print("" + mesg[k] + ";");
                        }
                        System.out.println("");
                    }
                }
                Pattern pattern = input.getPattern();
                if(pattern != null){
                    model.Track t = new model.Track(pattern);
                    t.addView(View.SIMPLE_VIEW);
                    list.add(t);
                }
            }            
            return list;
        }
        catch (Exception exc){
            exc.printStackTrace();
        }
        return null;
    }

    private class SimpleNote{
        public long start;
        public long duration;
        public int pitch;
        public int velocity;
    }

}
