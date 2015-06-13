package com.example.ringtonegenerator;

import android.content.Context;

public class WavFileWriter {
	
	private WavFileOutStream stream;
	private Context ctx;
	
	public WavFileWriter(Context ctx)
	{
		this.ctx = ctx;
	}
	
	public void writeWave(WavFile outWave, String fileName, int directory)
	{

        stream = new WavFileOutStream(ctx,fileName,directory);
		int dataSize = outWave.getDataSize()*2; // in bytes
		int smpRate = outWave.getSampleRate();
		int channels = outWave.getChannels();

		//ChunkID 4
		stream.writeString("RIFF");

		//ChunkSize 4 -- 36+data size in bytes
		stream.writeIntSwapped(dataSize+36);
		//Format 4
		stream.writeString("WAVE");
		//Subchunk1ID 4
		stream.writeString("fmt ");
		//Subchunk1Size 4
		stream.writeIntSwapped(16);
		//AudioFormat 2 PCM = 1
		stream.writeShortSwapped((short)1);
		//NumChannels 2
		stream.writeShortSwapped((short)channels);
		//SampleRate 4
		stream.writeIntSwapped(smpRate);
		//ByteRate 4
		stream.writeIntSwapped(smpRate*channels*2);
		//BlockAlign  2    
		stream.writeShortSwapped((short)(channels*2));
		//BitsPerSample   
		stream.writeShortSwapped((short)16);
		//Subchunk2ID 4   
		stream.writeString("data");
		//Subchunk2Size 4
		stream.writeIntSwapped(dataSize);
		//WRITE THE PCM DATA
		stream.writeFloatPCMData(outWave.getData());
		
		stream.close();
	}
}
