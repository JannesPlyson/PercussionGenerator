package model;

import java.util.Observable;

public class Note extends Observable{
    private long start,duration;
    private int pitch;
    private int volume;

    public Note(long start, long duration,int pitch,int volume){
        this.start = start;
        this.duration = duration;
        this.pitch = pitch;
        this.volume = volume;
    }

    public long getDuration() {
        return duration;
    }

    public long getStart() {
        return start;
    }

    public int getPitch() {
        return pitch;
    }

    public int getVolume() {
        return volume;
    }

    public void setStart(long start){
        this.start = start;
        setChanged();
        notifyObservers();
    }

    public void shift(long time){
        start += time;
        setChanged();
        notifyObservers();
    }

    public void stretch(long time){
        duration += time;
        setChanged();
        notifyObservers();
    }

    public String toString(){
        String s = "(";
        s += ((((double)start)/1000000000) + ",");
        s += ((((double)duration)/1000000000) + ",");
        s += ")";
        return s;
    }

    public Note getClone(){
        return new Note(start, duration, pitch, volume);
    }
}
