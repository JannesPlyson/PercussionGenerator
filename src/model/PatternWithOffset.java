package model;

import java.util.ArrayList;
import java.util.Collection;

public class PatternWithOffset {
    private int length;
    private Node startNode;
    private int startOffset;

    public PatternWithOffset(int length){
        this.length = length;
        startNode = null;
    }

    public int getLength(){
        return length;
    }
    public int getStartOffset(){
        return startOffset;
    }
    
    public void addNote(int start,int duration,int pitch, int volume){
        start = start%length;
        if(startNode == null){
            NoteWithOffset n = new NoteWithOffset(length,duration,pitch,volume);
            Node node = new Node(n);
            startNode = node;
            startOffset = start;
        }else{
            //new note is first note
            if(start < startOffset){ //new note will be the new start
                NoteWithOffset n = new NoteWithOffset(startNode.note.getOffset()-(startOffset-start),duration,pitch,volume);
                startNode.note.setOffset(startOffset-start);
                startNode = startNode.addBefore(n);
                startOffset = start;
                startNode.merge();
            }
            //new note is not the first note
            else{
                //find the note after the new one
                int time = startOffset;
                Node node = startNode;
                while(time < start){
                    node = node.next;
                    time += node.note.getOffset();
                }
                NoteWithOffset n;
                if(time > length){ //last note
                    time %= length;
                    int time_last_note = time - node.note.getOffset();
                    node.note.setOffset(node.note.getOffset()-(start-time));
                    n = new NoteWithOffset(start-time,duration,pitch,volume);
                }else{ //not last note
                    n = new NoteWithOffset(node.note.getOffset()-(time-start),duration,pitch,volume);
                    node.note.setOffset(time-start);
                }
                node = node.addBefore(n);
                node.merge();
            }
        }
    }
    public void removeNote(NoteWithOffset note){
        if(startNode.next == startNode){
            startNode = null;
        }else{
            Node node = startNode;
            while(node.note != note){
                node = node.next;
            }
            node.next.note.setOffset(node.next.note.getOffset() + node.note.getOffset());
            node.remove();
        }
    }

    public void shift(int time){
        //search new first note and set as first note
        time %= length;
        if(time < 0){
            time += length;
        }
        Pair<Node,Integer> pair = getNodeAfter(time);
        startNode = pair.first;
        startOffset = pair.second-time;
        //if last note is langer then length of pattern split last note;
        NoteWithOffset lastNote = startNode.previous.note;
        int timeLastNote = length - startNode.note.getOffset() + startOffset;
        if(timeLastNote + lastNote.getDuration() >= length){
            int newDuration = (timeLastNote + lastNote.getDuration())%(length-1);
            lastNote.stretch(-newDuration);
            lastNote.shift(newDuration);
            addNote(0, newDuration, lastNote.getPitch(), lastNote.getVolume());            
            //todo bad merge
        }
    }

    private Pair<Node,Integer> getNodeAfter(int startTime){
        int time = startOffset;
        Node node = startNode;
        while(time < startTime){
            node = node.next;
            time += node.note.getOffset();
        }
        return new Pair<Node,Integer>(node,time);
    }

    public Iterator getIterator(){
        return new Iterator();
    }
    public class Iterator implements java.util.Iterator<NoteWithOffset>{
        private Node startNodeIt;
        private int startOffsetIt;
        private Node currentNode;
        boolean started;
        public Iterator(){
            currentNode = startNode;
            startNodeIt = startNode;
            startOffsetIt = startOffset - startNodeIt.note.getOffset();
            started = false;
        }

        public Iterator(int startTime){
            Pair<Node,Integer> pair = getNodeAfter(startTime);
            Node node = pair.first;
            int time = pair.second;
            startNodeIt = node;
            currentNode = node;
            startOffsetIt = time-startTime - startNodeIt.note.getOffset();
            started = false;
        }

        public int getStartOffset(){
            return startOffsetIt;
        }

        public boolean hasNext() {
            return (!started || (currentNode != startNode)) && (startNodeIt != null);
        }

        public NoteWithOffset next() {
            started = true;
            NoteWithOffset note = currentNode.note;
            currentNode = currentNode.next;
            return note;
        }

        public void remove() {
            currentNode = currentNode.next;
        }

        public void reset(){
            started = false;
            currentNode = startNodeIt;
        }
    }
    public class TimeIterator implements java.util.Iterator<Collection<NoteWithOffset>>{
        private Node currentNode;
        private Node startNodeIt;
        private int startOffsetIt;

        public TimeIterator(){
            currentNode = startNode;
            startNodeIt = startNode;
            startOffsetIt = startOffset;
        }

        public TimeIterator(int startTime){
            int time = startOffset;
            Node node = startNode;
            while(time < startTime){
                node = node.next;
                time += node.note.getOffset();
            }
            startNode = node;
            currentNode = node;
            startOffsetIt = time-startTime;
        }

        public boolean hasNext() {
            return true;
        }

        public Collection<NoteWithOffset> next() {
            ArrayList<NoteWithOffset> notes = new ArrayList<NoteWithOffset>();
            notes.add(currentNode.note);
            currentNode = currentNode.next;
            while(currentNode.note.getOffset() == 0){
                notes.add(currentNode.note);
                currentNode = currentNode.next;
            }
            return notes;
        }

        public void remove() {
            currentNode = currentNode.next;
            while(currentNode.note.getOffset() == 0){
                currentNode = currentNode.next;
            }
        }

        public void reset(){
            currentNode = startNodeIt;
        }

        public int getStartOffset(){
            return startOffsetIt;
        }
    }
    private class Node{
        public NoteWithOffset note;
        public Node next;
        public Node previous;

        @SuppressWarnings("LeakingThisInConstructor")
        public Node(NoteWithOffset note){
            this.note = note;
            next = this;
            previous = this;
        }

        public Node addBefore(NoteWithOffset n){
            Node newNode = new Node(n);
            newNode.previous = previous;
            previous.next = newNode;
            newNode.next = this;
            previous = newNode;
            return newNode;
        }

        public Node addAfter(NoteWithOffset n){
            return next.addBefore(n);
        }
        
        private void mergeWithNext(){
            next = next.next;
            next.previous = this;            
        }

        //merge noetes if next note overlaps with this note (same pitch and volume)
        public void merge(){
            if(next.note.getPitch() == note.getPitch()
                    && next.note.getVolume() == note.getVolume()
                    && next.note.getOffset() <= note.getDuration()){
                note.stretch(next.note.getDuration()-(note.getDuration()-next.note.getOffset()));
                mergeWithNext();
            }
        }

        public void remove(){
            previous.next = next;
            next.previous = previous;
        }
    }
    public static PatternWithOffset getTestPattern(){
        PatternWithOffset pattern = new PatternWithOffset (3000);
        pattern.addNote(50,100,50,50);
        pattern.addNote(2500,100,50,50);
        pattern.addNote(300,100,50,50);
        pattern.addNote(601,100,50,50);
        pattern.addNote(500,100,50,50);       
        return pattern;
    }

}
