/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 *
 * @author installer
 */
public class NoteWithOffset {
    private int offset,duration;
    private int pitch;
    private int volume;

    public NoteWithOffset(int offset, int duration,int pitch,int volume){
        this.offset = offset;
        this.duration = duration;
        this.pitch = pitch;
        this.volume = volume;
    }

    public int getDuration() {
        return duration;
    }

    public int getOffset() {
        return offset;
    }

    public int getPitch() {
        return pitch;
    }

    public int getVolume() {
        return volume;
    }

    public void setOffset(int offset){
        this.offset = offset;
    }

    public void shift(int time){
        offset += time;
    }

    public void stretch(int time){
        duration += time;
    }
    
}