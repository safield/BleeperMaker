package com.safield.BleeperMaker;
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

    // add a silent note aka a spacer
    public void addNote(int noteType)
    {
        notes.add(new Note(this,noteType));
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
