/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package input;

import java.util.ArrayList;
import model.Note;
import model.Pattern;

/**
 *
 * @author jannes
 */
public class InputNotes {
    ArrayList<SimpleNote> notes;

    public InputNotes(){
        notes = new ArrayList<SimpleNote>();
    }

    public Pattern getPattern(){
        if(notes.size() > 0){
            long start = Long.MAX_VALUE;
            long patternLength = 0;
            for(int i = 0; i < notes.size(); i++){
                start = Math.min(start, notes.get(i).start);
                patternLength = Math.max(patternLength, notes.get(i).end);
            }
            patternLength -= start;
            Pattern pattern = new Pattern(patternLength);
            for(int i = 0; i < notes.size(); i++){
                SimpleNote sn = notes.get(i);
                pattern.addNote(new Note(sn.start-start,sn.end-sn.start,sn.pitch,sn.velocity));
            }
            return pattern;
        }else{
            return null;
        }
    }

    public void addInput(long nanoTimeStart, long nanoTimeEnd, int pitch, int velocity){        
        notes.add(new SimpleNote(nanoTimeStart, nanoTimeEnd, pitch, velocity));
    }

    public void addInput(double secondsStart, double secondsEnd,int pitch, int velocity){
        long nanoTimeStart = (long)(secondsStart*1000000000);
        long nanoTimeEnd = (long)(secondsEnd*1000000000);
        addInput(nanoTimeStart,nanoTimeEnd,pitch,velocity);
    }

    private class SimpleNote{
        public long start;
        public long end;
        public int pitch;
        public int velocity;

        public SimpleNote(long start, long end, int pitch, int velocity) {
            this.start = start;
            this.end = end;
            this.pitch = pitch;
            this.velocity = velocity;
        }


    }

}
