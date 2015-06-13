package com.safield.SafireRingtoneMaker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;


public class JavAud {
	
	public static final int GLB_SMP_RATE=44100;
	
	public static float cubicInterp(float x0, float x1, float x2, float x3, float t)
	{
	    float a0, a1, a2, a3;
	    a0 = x3 - x2 - x0 + x1;
	    a1 = x0 - x1 - a0;
	    a2 = x2 - x0;
	    a3 = x1;
	    return (a0 * (t * t * t)) + (a1 * (t * t)) + (a2 * t) + (a3);
	}
	
	public static float hermiteInterp(float x0, float x1, float x2, float x3, float t)
	{
	    float c0 = x1;
	    float c1 = .5F * (x2 - x0);
	    float c2 = x0 - (2.5F * x1) + (2 * x2) - (.5F * x3);
	    float c3 = (.5F * (x3 - x0)) + (1.5F * (x1 - x2));
	    return (((((c3 * t) + c2) * t) + c1) * t) + c0;
	}
	
	public static void checkRange(float[] data)
	{
		float min=2.0f;
		float max=0;
		
		for(int i=0;i<data.length;i++)
		{
			if(data[i]<min)
				min=data[i];
			
			if(data[i]>max)
				max=data[i];
		}
		
		System.out.println("Range of: "+min+" to "+max);
	}

	public static void compress(float[] data)
	{
		float max=0;

		for(int i=0;i<data.length;i++)
			if(Math.abs(data[i]) > max)
				max=Math.abs(data[i]);

		if(max>1)
			for(int i = 0; i < data.length; i++)
				data[i]=data[i]/max;
	}
	
	public static void printArrayToFile(float[]array , String name , int every)
	{
		String line = "";
		int lineNum = 1;
		int printed = 0;
		String value;
		String compare = "";
		
		if(every > 0) {

            try {
				FileWriter writer = new FileWriter(name);
			
				for(int i=1;i<array.length;i+=every) {

					//convert current value to string
					value=String.valueOf(array[i]);

					//make space for '-' symbol
					if(array[i] >= 0)
						line=line+"  "+value;
					else
						line=line+" "+value;

					printed++;
					
					if(printed > 10) {

                        line="LINE"+lineNum+": "+line+"\n\r";
						writer.write(line);
						writer.write('\n');
						line="";
						lineNum++;
						printed=0;
					}
				}

				writer.write(compare);
				writer.close();
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void printArrayToFile( short[]array , String name , int every)
	{
		String line="";
		int lineNum=1;
		int printed=0;
		
		if(every>0)
		{
			try {
				FileWriter writer = new FileWriter(name);
			
				for(int i=1;i<array.length;i+=every)
				{
					if(array[i]>=0)
					{
						line=line+"  "+array[i];
					}
					else
					{
						line=line+" "+array[i];
					}
					
					printed++;
					if(printed>10)
					{
						line="LINE"+lineNum+": "+line+"\n\r";
						writer.write(line);
						writer.write('\n');
						line="";
						lineNum++;
						printed=0;
					}
				}
			
				writer.close();
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void printArrayToFile( int[]array , String name , int every)
	{
		String line="";
		int lineNum=1;
		int printed=0;
		
		if(every>0)
		{
			try {
				FileWriter writer = new FileWriter(name);
			
				for(int i=1;i<array.length;i+=every)
				{
					if(array[i]>=0)
					{
						line=line+"  "+array[i];
					}
					else
					{
						line=line+" "+array[i];
					}
					
					printed++;
					if(printed>10)
					{
						line="LINE"+lineNum+": "+line+"\n\r";
						writer.write(line);
						writer.write('\n');
						line="";
						lineNum++;
						printed=0;
					}
				}
			
				writer.close();
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static short[] floatToShort(float[] data)
	{
		short[] result =  new short[data.length];
		for(int i=0;i<data.length;i++)
		{
			result[i]=(short) ((data[i])*32768);
		}
		
		return result;
	}
	
	public static void quietEnd(float[]data)
	{
		float factor=0.998f;
		
		for(int i = data.length-100;i<data.length;i++)
		{
			data[i]=data[i]*factor;
			factor=factor-0.001f;
		}
	}
}
