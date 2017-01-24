package com.safield.BleeperMaker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.content.Context;
import android.util.Log;

public class TonePatternList {

	private ArrayList<TonePattern> list;

	public TonePatternList(Context ctx, int id)
	{
		list = new ArrayList<TonePattern>();
		readPatterns(ctx,id);
	}

	public TonePattern get(int index)
	{
		return list.get(index);
	}

	public int size()
    {
        return list.size();
    }

    /**
     * The patterns file contains the following elements
     *
     * [sameple_name] = this is used by the UI
     * [note_length] = Possible notes are 1 = whole  2 = half  4 = quarter  8 = eight  16 = sixteenth  32 = thrity second
     * [note_pitch] = semitones up or down to modify the pitch in relation to the sameples pitch. eg - 1 to pitch 1 semitone down or + 7 to pitch up 7 semitones
     *
     * The patterns file follows the following format
     *
     * [pattern_name]
     * [note_legnth] [note_pitch]
     * [note_legnth] [note_pitch]
     * [...]         [...]
     * [pattern_name]
     * [note_legnth] [note_pitch]
     * [note_legnth] [note_pitch]
     * [...]         [...]
     *
     * So defining 3 patterns might look as follows....
     *
     * test t
     * 16 + 0
     * 16 + 2
     * pingpong t
     * 16 + 6
     * 16 - 6
     * 16 + 6
     * march t
     * 8 + 0
     * 16 + 0
     * 16 + 0
     * 8 + 0
     *
     */
    private void readPatterns(Context ctx,int id)
    {
        try {

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            ctx.getResources().openRawResource(id)));

            String line = "";
            String[] array;
            TonePattern pattern = null;
            int semitone = 0;
            boolean first = true;

            while((line = reader.readLine()) != null) {

                array = line.split(" ");

                if (array.length == 2) {

                    if (!first)
                        list.add(pattern);

                    first = false;
                    pattern = new TonePattern(array[0]);
                }
                else if(array.length == 3) {

                    semitone = Integer.parseInt(array[2]);

                    if(array[1].equals("-"))
                        semitone = semitone - (semitone*2);

                    pattern.addNote(Integer.parseInt(array[0]), semitone);
                }
            }

            // add the last pattern
            list.add(pattern);
            reader.close();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }
}
