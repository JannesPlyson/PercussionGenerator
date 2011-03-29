package data.midi;
/**
 * creation: 15-nov-2007
 */

/**
 * @author Wijnand
 *
 */
public class MidiConstants
{
	// meta event types:
	public final static int SEQUENCENUMBER = 0x00;
	public final static int TEXT = 0x01; 	
	public final static int COPYRIGHT = 0x02; 	
	public final static int SEQUENCEORTRACKNAME = 0x03; 	
	public final static int INSTRUMENT = 0x04; 	
	public final static int LYRIC = 0x05; 	
	public final static int MARKER = 0x06; 	
	public final static int CUEPOINT = 0x07;
	public final static int PROGRAMORPATCHNAME = 0x08;
	public final static int DEVICEORPORTNAME = 0x09;
	public final static int CHANNELPREFIXASSIGNMENT = 0x20;
	public final static int ENDOFTRACK = 0x2F;
	public final static int TEMPOSETTING = 0x51;
	public final static int SMPTEOFFSET = 0x54;
	public final static int TIMESIGNATURE = 0x58;
	public final static int KEYSIGNATURE = 0x59;
	public final static int PROPRIETARY = 0x7F;	// sequence specific
}
