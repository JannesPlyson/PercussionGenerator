/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package quantisation;

import java.util.ArrayList;
import java.util.Iterator;
import model.Note;
import model.Pair;
import model.Pattern;

/**
 *
 * @author jannes
 */
public class NoteQuantizer{
    private int smallestValue;
    private boolean startOnly;
    private Grid grid;    
    private ArrayList<Long> times;
    long shortestDuration;

    public NoteQuantizer(){
        smallestValue = 192/16;
        startOnly = true;
    }

    public ArrayList<Pair<Integer,Note>> getNotes(Pattern pattern){
        initTimes(pattern);
        long gridInterval = pattern.getLength()/Math.round(1.0*pattern.getLength()/shortestDuration);
        grid = new Grid(gridInterval);
        ArrayList<Pair<Long,Long>> quantizedTimes = grid.getGridValues(times, 0);
        //these times are a multiple of smallestValue;
        return evaluateTimes(quantizedTimes,pattern);        
    }

    private ArrayList<Pair<Integer,Note>> evaluateTimes(ArrayList<Pair<Long,Long>> quantizedTimes, Pattern pattern){
        if(startOnly){
            ArrayList<Pair<Integer,Note>> result = new ArrayList<Pair<Integer, Note>>();
            Iterator<Note> it = pattern.getIterator();
            int i = 0;
            while(it.hasNext()){
                Note n = it.next();
                int noteValue = (int)(quantizedTimes.get(i+1).first-quantizedTimes.get(i).first)*smallestValue;
                result.add(new Pair<Integer,Note>(noteValue,n));
                i++;
            }
            return result;
        }else{
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private void initTimes(Pattern pattern){
        times = new ArrayList<Long>();
        if(startOnly){
            Iterator<Note> it = pattern.getIterator();
            shortestDuration = Long.MAX_VALUE;
            //todo rust in het begin controleren
            if(it.hasNext()){
                Note n1 = it.next();
                times.add(n1.getStart());
                while(it.hasNext()){
                    Note n2 = it.next();
                    times.add(n2.getStart());
                    long duration = n2.getStart()-n1.getStart();
                    shortestDuration = Math.min(shortestDuration, duration);
                    n1 = n2;
                }
                long duration = pattern.getLength()-n1.getStart();
                shortestDuration = Math.min(shortestDuration, duration);
                times.add(pattern.getLength());
            }            
        }else{
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private ArrayList<Pair<Integer,Note>> getNotesStartOnly(Pattern pattern){
        return null;
    }

    private ArrayList<Pair<Integer,Note>> getNotesDuration(Pattern pattern){
        
        return null;
    }

}
