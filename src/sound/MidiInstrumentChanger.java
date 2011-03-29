/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sound;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;

/**
 *
 * @author installer
 */
public class MidiInstrumentChanger implements Runnable{
    private MidiChannel channel;
    private Patch patch;

    public MidiInstrumentChanger(Instrument instrument, MidiChannel channel){
        patch = instrument.getPatch();
        this.channel = channel;
    }

    public void run() {
        channel.programChange(patch.getBank(), patch.getProgram());
    }

}
