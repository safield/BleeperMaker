package com.safield.SafireRingtoneMaker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.content.Context;

public class PatternList {
	private ArrayList<TonePattern> list;
	
	public PatternList(Context ctx, int id)
	{
		list = new ArrayList<TonePattern>();
		readPatterns(ctx,id);
	}
	
	public TonePattern get(int index)
	{
		return list.get(index);
	}
	
	public int size(){return list.size();}
	
	private void readPatterns(Context ctx,int id)
	{
		try {
			BufferedReader reader = new BufferedReader(
					                new InputStreamReader(
					                ctx.getResources().openRawResource(id)));
			
			String line="";
			String[] array;
			TonePattern pattern=null;
			int semitone=0;
			boolean first=true;
		
			while((line=reader.readLine())!=null) {
				
				array=line.split(" ");
				
				if(array.length==2) {
					if(!first) {
						list.add(pattern);
					}
					first=false;
					pattern=new TonePattern(array[0]);
					
					if(array[1].equals("nt"))pattern.setTail(false);
				}
				else if(array.length==3) {
					semitone=Integer.parseInt(array[2]);
					if(array[1].equals("-")) {
						semitone=semitone-(semitone*2);
					}
					pattern.addNote(Integer.parseInt(array[0]), semitone);
				}
			}
			list.add(pattern);
			reader.close();
			
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString()
	{
		String result="";
		
		for(int i=0;i<list.size();i++) {
			result=result+list.get(i).toString()+'\n';
		}
		return result;
	}
}
