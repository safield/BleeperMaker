package com.safield.SafireRingtoneMaker;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;


public class WavFileOutStream {
	
	private DataOutputStream stream;
	private String path;
	Context ctx;
	
	public WavFileOutStream(Context ctx,String fileName,int directory)
	{
			this.ctx=ctx;
			File file=null;
		
			if(directory == ToneMaker.NOTIFICATION)
			{
				file = new File(Environment.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_NOTIFICATIONS), fileName+".wav");
			}
			else if(directory==ToneMaker.RINGTONE)
			{
				file = new File(Environment.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_RINGTONES), fileName+".wav");
			}
				
			path=file.getAbsolutePath();
			
			try {
				FileOutputStream fileOut = new FileOutputStream(file);
				stream = new DataOutputStream(fileOut);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		
		
	}
	
	public void writeShort(short out) 
	{
		
		try {
			stream.writeShort(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void writeInt(int out) 
	{
		try {
			stream.writeInt(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//write an short with swapped endian
	public void writeShortSwapped(short out) 
	{
		byte[] bytes = new byte[2];
		
		
		bytes[0] = (byte)(out & 0xff);
		bytes[1] = (byte)((out >> 8) & 0xff);

		try {
			stream.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//write an int with swapped endian
	public void writeIntSwapped(int out) 
	{
		byte[] bytes = new byte[4];
		
		
		bytes[0] = (byte)(out & 0xff);
		bytes[1] = (byte)((out >> 8) & 0xff);
		bytes[2] = (byte)((out >> 16) & 0xff);
		bytes[3] = (byte)((out >> 24) & 0xff);

		try {
			stream.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void writeFloatPCMData(float[] data)
	{
		int min=0;
		int max=0;
		byte[] bytes=new byte[data.length*2];
		int[] compare=new int[data.length];
		
		short out;
		//int out;
		
		for(int i=0;i<data.length;i++)
		{
			//out=((int)((data[i]+1)*32768.0))& 0xffff;
			out=(short) ((data[i])*32768);
			compare[i]= out;
			if(out>max)
				max=out;
			
			if(out<min)
				min=out;
			
			bytes[i*2]=(byte)(out & 0xFF);
			bytes[i*2+1]=(byte)((out>>8) & 0xFF);
			
			
		}
		
		try {
			stream.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JavAud.printArrayToFile(compare, "shortOutput.txt", 100);
		JavAud.printArrayToFile(data, "floatOutput.txt", 100);
		//JavAud.ensureConvesion(data, compare);
		System.out.println("Out data range="+min+" to "+max);
	}
	
	
	
	public void write(byte[] data)
	{
		try {
			stream.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeString(String out)
	{
		
		try {
			stream.writeBytes(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	


	public void close()
	{
	try {
			stream.close();
			MediaScannerConnection.scanFile(ctx, new String[]{path}, null, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
