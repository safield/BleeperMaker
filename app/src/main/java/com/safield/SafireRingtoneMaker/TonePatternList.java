package com.safield.SafireRingtoneMaker;
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
     * There should be a much better comment here detailing the format of the patterns.txt flat file.
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
