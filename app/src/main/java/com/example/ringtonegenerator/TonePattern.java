package com.example.ringtonegenerator;
import java.util.ArrayList;

import android.util.Log;

// TonePattern is a class that manages a list of notes
public class TonePattern {

    private ArrayList<Note> notes;

    private int tempo, position, sampleLength;
	private boolean tail;
	String name;

	public TonePattern(String name)
	{
		sampleLength = 0;
		this.name = name;
		tempo = 120;
		notes = new ArrayList<Note>();
		position = 0;
		tail = false;
	}
	
	public String getName(){return name;}
	
	public void addNote(int noteType, int semiTone)
	{
		notes.add(new Note(this,noteType,semiTone));
	}
	public void setTail(boolean tail)
	{
		this.tail = tail;
	}

    // returns the next note in the pattern, or null if it has reached the end of the pattern
	public Note nextNote()
	{

        if(position >= notes.size()) {
            position = 0; // reset pattern
            return null;
        }

        Note temp = notes.get(position);

        // if we are on the last note and tail is enabled, let the entire sample play as opposed to cutting off at note length
		if((position == notes.size() - 1) && tail)
			temp.enableTail(sampleLength);

        position++;

		return temp;
	}
	
	public int getTempo()
	{
		return tempo;
	}
	
	public void setTempo(int tempo)
	{
		this.tempo = tempo;
		
		for(int i = 0; i < notes.size(); i++)
			notes.get(i).setPeriod();
	}
	
	public void setSampleLength(int sampleLength)
	{
		this.sampleLength = sampleLength;
	}
	
	/**
    * to get the play time in samples, we must be passed the length of the sample we are
	* going to be playing
    */
	public int getPlayTimeInSmps()
	{
		int result = sampleLength;
		
		// we iterate through all but the last note, since we want sampleLength(tail) at end, instead of note length.
		for(int i=0;i<notes.size()-1;i++)
			result = result+notes.get(i).getPeriodInSmps();

        return result;
	}
	
	public void pitchMod(int semitone)
	{
		for(int i=0;i<notes.size();i++)
			notes.get(i).modPitch(semitone);
	}
	
	public String toString() {
		return name+" "+notes.size()+" notes";
	}
}
