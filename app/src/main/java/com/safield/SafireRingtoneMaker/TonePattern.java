package com.safield.SafireRingtoneMaker;
import java.util.ArrayList;

// TonePattern is a class that manages a list of notes
public class TonePattern {

    private ArrayList<Note> notes;

    private int sampleLength;
	String name;

	public TonePattern(String name)
	{
		sampleLength = 0;
		this.name = name;
		notes = new ArrayList<Note>();
	}

	public String getName() { return name; }

	public void addNote(int noteType, int semiTone)
	{
		notes.add(new Note(this,noteType,semiTone));
	}

    public int getNumNotes()
    {
        return notes.size();
    }

    public Note getNote(int index)
    {
        return notes.get(index);
    }
}
