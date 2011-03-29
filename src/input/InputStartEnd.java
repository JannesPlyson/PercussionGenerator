/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package input;

import java.util.ArrayList;
import model.Note;
import model.Pair;
import model.Pattern;

/**
 *
 * @author jannes
 */
public class InputStartEnd{       
    protected ArrayList<Pair<Long,Long>> times;
    protected int defaultPitch,defaultVelocity;

    public InputStartEnd(){
        times = new ArrayList<Pair<Long, Long>>();
    }

    public void setDefaultPitch(int pitch){
        defaultPitch = pitch;
    }

    public void setDefaultVelocity(int velocity){
        defaultVelocity = velocity;
    }

    public Pattern getPattern(){
        if(times.size() > 0){
            long start = Long.MAX_VALUE;
            long patternLength = 0;
            for(int i = 0; i < times.size(); i++){
                start = Math.min(start, times.get(i).second);
                patternLength = Math.max(patternLength, times.get(i).second);
            }
            patternLength -= start;
            Pattern pattern = new Pattern(patternLength);
            for(int i = 0; i < times.size(); i++){                
                pattern.addNote(new Note(times.get(i).first-start, times.get(i).second-times.get(i).first, defaultPitch, defaultVelocity));
            }
            return pattern;
        }else{
            return null;
        }
    }
    
    public void addInput(long nanoTimeStart, long nanoTimeEnd){
        times.add(new Pair<Long, Long>(nanoTimeStart,nanoTimeEnd));
    }
    
    public void addInput(double secondsStart, double secondsEnd){
        long nanoTimeStart = (long)(secondsStart*1000000000);
        long nanoTimeEnd = (long)(secondsEnd*1000000000);
        addInput(nanoTimeStart,nanoTimeEnd);
    }
    
}
