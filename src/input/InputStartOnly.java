/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package input;

import java.util.ArrayList;
import java.util.Collections;
import model.Pattern;

/**
 *
 * @author jannes
 */
public class InputStartOnly {
    protected ArrayList<Long> times;
    protected int defaultPitch,defaultVelocity;

    public InputStartOnly(){
        times = new ArrayList<Long>();
        defaultPitch = 50;
        defaultVelocity = 120;
    }

    public void setDefaultPitch(int pitch){
        defaultPitch = pitch;
    }

    public void setDefaultVelocity(int velocity){
        defaultVelocity = velocity;
    }

    public Pattern getPattern(){
        if(times.size() > 1){
            Collections.sort(times);
            long start = times.get(0);
            long patternLength = times.get(times.size()-1)-start;
            Pattern pattern = new Pattern(patternLength);
            for(int i = 0; i < times.size()-1; i++){
                long duration = times.get(i+1)-times.get(i)-1;
                long startTime = times.get(i)-start;
                pattern.addNote(startTime,duration,defaultPitch,defaultVelocity);
            }
            return pattern;
        }else{
            return null;
        }
    }

    public void addInput(long nanoTime){
        times.add(nanoTime);
    }

    public void addInput(double seconds){
        long nanoTime = (long)(seconds*1000000000);
        times.add(nanoTime);
    }
}
