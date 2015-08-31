package com.safield.SafireRingtoneMaker;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;


public class WavFileInStream
{
	private DataInputStream stream;
	
	public WavFileInStream(String fileName)
	{
		try {
			stream = new DataInputStream(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public WavFileInStream(Context ctx, int id)
	{
		stream = new DataInputStream(ctx.getResources().openRawResource(id));
	}
	
	public short readShort() 
	{
		short result=0;
		try {
			result= stream.readShort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
	}
	public int readInt() 
	{
		int result=0;
		try {
			result=stream.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	//read in an int and return and int of opposite endian
	public short readShortSwapped() 
	{
		int result;
		byte[] bytes= new byte[2];
		
		try {
			stream.read(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result=((bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8 ));
		
		return (short)result;
	}
	
	//read in an int and return and int of opposite endian
	public int readIntSwapped() 
	{
		long result;
		byte[] bytes= new byte[4];
		
		try {
			stream.read(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		result = ((bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8 ) | ((bytes[2] & 0xFF) << 16 ) | ((bytes[3] & 0xFF) << 24 ));
		
		return (int)result;
	}
	
	public void read(byte[] data)
	{
		try {
			stream.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String readStringFour()
	{
		byte[] bytes=new byte[4];
		try {
			stream.read(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(bytes);
	}
	
	public float[] readData(int length)
	{
		float[] result = new float[length/2];
		byte[] bytes = new byte[length];
		int min=0;
		int max=0;
		short temp=0;
		
		try {
			stream.read(bytes);//DataInputStream read bytes.length of data
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i=0;i<length/2;i++) {
			//take two little endian bytes and convert to float between -1.0 an 1.0
			temp=(short) ((bytes[i*2] & 0xFF) | ((bytes[i*2+1] & 0xFF) << 8 ));
			
			if(temp>max)
				max=temp;
			if(temp<min)
				min=temp;
			result[i]=(temp/32768.0f);		
		}
		return result;
	}

	public void close()
	{
	try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
