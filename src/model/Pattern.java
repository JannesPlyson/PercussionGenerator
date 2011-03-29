/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.PriorityQueue;

/**
 *
 * @author installer
 */
public class Pattern extends Observable implements Observer{
    ArrayList<Note> notes;
    private long length;

    public Pattern(long length){
        this.length = length;
        notes = new ArrayList<Note>();
    }

    public Pattern(int length,ArrayList<Note> notes){
        this.length = length;
        this.notes = notes;        
    }

    public long getLength(){
        return length;
    }

    public boolean isEmpty(){
        return notes.isEmpty();
    }

    public void addNote(long start, long duration, int pitch,int volume){
        Note n = new Note(start,duration,pitch,volume);
        addNote(n);
    }

    public void addNote(Note n){
        n.addObserver(this);
        notes.add(n);
    }

    public void removeNote(Note n){
        n.deleteObserver(this);
        notes.remove(n);
    }

    public Iterator<Note> getIterator(){
        return new PatternIterator();
    }

    public Iterator<Note> getLoopIterator(){
        return new PatternLoopIterator();
    } 

    public void shift(int time){
        time %= length;
        for(int i = 0; i < notes.size(); i++){
            Note n = notes.get(i);
            long shiftedStart = (n.getStart() + time)%length;
            if(shiftedStart < 0){
                shiftedStart += length;
            }
            n.setStart(shiftedStart);
        }
    }
    
    public void update(Observable o, Object arg) {
        Note n = (Note)o;
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString(){
        String s = "length: " + (((double)length)/1000000000);
        for(int i = 0; i < notes.size(); i++){
            s+= ("\n   " + notes.get(i).toString());
        }
        return s;
    }

    private class NoteComparator implements Comparator<Note>{
        public int compare(Note o1, Note o2) {
            long diff = o1.getStart()-o2.getStart();
            if(diff < 0){
                return -1;
            }else if(diff == 0){
                return 0;
            }else{
                return 1;
            }
        }
    }

    private class PatternIterator implements Iterator<Note>{
        private PriorityQueue<Note> queue;

        public PatternIterator(){
            queue = new PriorityQueue<Note>(2,new NoteComparator());
            queue.addAll(notes);
        }

        public boolean hasNext() {
            return !queue.isEmpty();
        }

        public Note next() {
            return queue.poll();
        }

        public void remove() {
            queue.poll();
        }

    }

    private class PatternLoopIterator implements Iterator<Note>{
        private PriorityQueue<Note> queue;
        private int loopNumber;
        public PatternLoopIterator(){
            queue = new PriorityQueue<Note>(2,new NoteComparator());
            loopNumber = 1;
            queue.addAll(notes);
        }

        public boolean hasNext() {
            return true;
        }

        public Note next() {
            return queue.poll();
        }

        public void remove() {
            queue.poll();
            if(queue.isEmpty()){
                loopNumber++;
                queue.addAll(notes);
            }
        }

    }

    public Pattern getClone(){
        Pattern p = new Pattern(length);
        for(int i = 0; i < notes.size(); i++){
            p.addNote(notes.get(i).getClone());
        }
        return p;
    }

    public static Pattern getTestPattern(){
        Pattern pattern = new Pattern(3000);
        pattern.addNote(50,100,50,50);
        pattern.addNote(2500,100,50,50);
        pattern.addNote(300,100,50,50);
        pattern.addNote(601,100,50,50);
        pattern.addNote(500,100,50,50);
        return pattern;
    }
}
