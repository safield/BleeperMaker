

package com.example.ringtonegenerator;

import android.util.Log;

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
	
	
	private int semitone, pitchMod, noteType, sampleLength;
	private long period;

	private TonePattern parent;

	public Note (TonePattern parent, int noteType, int semitone)
	{
		this.parent = parent;
		this.semitone = semitone;
		
		this.noteType = noteType;
		setPeriod();
		sampleLength = -1;
	}
	
	public void modPitch(int mod)
	{
		pitchMod = mod;
	}
	
	//returns period between notes in milliseconds
	public void setPeriod()
	{
		long result = 60000;
		            
		int tempo = parent.getTempo();

		if(noteType == WHOLE)tempo=tempo/4;
		if(noteType == HALF)tempo=tempo/2;
		if(noteType == THIRD)tempo=(int) (tempo/1.333333);
		if(noteType == SIXTH)tempo=(int) (tempo*1.5);
		if(noteType == EIGHT)tempo=tempo*2;
		if(noteType == TWELVTH)tempo=tempo*3;
		if(noteType == SIXTEENTH)tempo=tempo*4;
		if(noteType == THIRTYTWO)tempo=tempo*8;
		
		period = result/tempo;
	}

	public int getPeriodInSmps()
	{
		int result;

        // if sampleLength > 0 that means tail has been enabled, and this note should ring on
		if(sampleLength > 0)
			result = sampleLength;
		else
			result = (int) (44.1*period);

		return result;
	}
	
	
	public void enableTail(int sampleLength)
	{
		this.sampleLength = sampleLength;
	}
	
	
	public float getPitch()
	{
		return (float) Math.pow(2, (semitone + pitchMod) / 12.0);
	}
	
	
}
