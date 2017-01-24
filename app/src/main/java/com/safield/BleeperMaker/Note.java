

package com.safield.BleeperMaker;

public class Note {

	public static final int WHOLE=1;
	public static final int HALF=2;
	public static final int THIRD=3;
	public static final int QUARTER=4;
	public static final int SIXTH=6;
	public static final int EIGHT=8;
	public static final int TWELVTH=12;
	public static final int SIXTEENTH=16;
	public static final int THIRTYTWO=32;
	
	
	private int semitone, noteType;// sampleLength;

	private TonePattern parent;

	public Note (TonePattern parent, int noteType, int semitone)
	{
		this.parent = parent;
		this.semitone = semitone;
		
		this.noteType = noteType;
	}

	public int getLengthInSamples(int tempo)
	{
        if (tempo <= 0)
            throw new AssertionError("Note.getLengthInSamples - tempo value <= 0");

		int result;

        if(noteType == WHOLE)tempo=tempo/4;
        if(noteType == HALF)tempo=tempo/2;
        if(noteType == THIRD)tempo=(int) (tempo/1.333333);
        if(noteType == SIXTH)tempo=(int) (tempo*1.5);
        if(noteType == EIGHT)tempo=tempo*2;
        if(noteType == TWELVTH)tempo=tempo*3;
        if(noteType == SIXTEENTH)tempo=tempo*4;
        if(noteType == THIRTYTWO)tempo=tempo*8;

        int period = 60000/tempo;

        result = (int) Math.ceil(44.1 * period);

		return result;
	}

	public int getSemitone()
	{
        return semitone;
	}
}
