/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sound;

import model.Track;
import model.TrackCollection;
import javax.sound.midi.Instrument;

/**
 *
 * @author jannes
 */
public interface MidiPlayer {
    public Instrument[] getInstruments();
    public void play();
    public void pause();
    public void stop();
    public void resume();
    public void setTrackCollection(TrackCollection trackCollection);    
    public boolean isPlayerLoaded();
    public boolean isPlaying();
    public void TrackUpdated(Track track);
}
