package com.safield.SafireRingtoneMaker;

import android.content.Context;

public  class WavFileReader{
		
		private WavFileInStream stream;
		private boolean debug;
		private int smpRate;
		private int numChannels;
		private int dataLength;
		private float[] data;
		
		public void setDebug(boolean debug)
		{
			this.debug=debug;
		}
		
		public WavFile readWave(String name)
		{
			
			WavFile result = null;
			
			stream = new WavFileInStream(name);

			//these methods must be called in proper order
			readRiff();
			readChunkSize();
			readFormat();
			readSubChunkID();
			readSubChunk1Size();
			readAudioFormat();
			readNumChannels();
			readSmpRate();
			readByteRate();
			readBlockAlign();
			readBitsPerSmp();
			readSubChunk2ID();
			readSubChunk2Size();
			readPCMData();
			
			result = new WavFile(numChannels , smpRate , data);
			
			reset();
			return result;
		}
		
		
		public WavFile readWave(Context ctx, int id)
		{
			
			WavFile result = null;
			
			stream = new WavFileInStream(ctx,id);

			//these methods must be called in proper order
			readRiff();
			readChunkSize();
			readFormat();
			readSubChunkID();
			readSubChunk1Size();
			readAudioFormat();
			readNumChannels();
			readSmpRate();
			readByteRate();
			readBlockAlign();
			readBitsPerSmp();
			readSubChunk2ID();
			readSubChunk2Size();
			readPCMData();
			
			result = new WavFile(numChannels , smpRate , data);

			reset();
			return result;
		}
		
		
		public void close()
		{
			stream.close();
		}
		
		private void reset()
		{
			smpRate=0;
			numChannels=0;
			dataLength=0;
			data = null;
			stream.close();
		}
		
		private  void readRiff()
		{
			String riff = stream.readStringFour();

			if(!riff.equals("RIFF"))
				throw new AssertionError("WavFileReader:readRiff() Error reading file");
		}

		private void readChunkSize()
		{
			int chunkSize=stream.readIntSwapped();

			if(chunkSize < 36)
				throw new AssertionError("WavFileReader:readChunkSize() Error reading file");
		}
		
		private void readFormat()
		{
			String read = stream.readStringFour();

			if(!read.equals("WAVE"))
				throw new AssertionError("WavFileReader:readFormat() Error reading file");
		}
		
		private void readSubChunkID()
		{
			String read=stream.readStringFour();

			if(!read.equals("fmt "))
				throw new AssertionError("WavFileReader:readSubChunkID() Error reading file");
		}
		
		private void readSubChunk1Size()
		{
			int read = stream.readIntSwapped();

			if(read != 16)
				throw new AssertionError("WavFileReader:readSubChunk1Size() Error reading file");
		}
		
		private void readAudioFormat()
		{
			int read = stream.readShortSwapped();

			if(read != 1)
				throw new AssertionError("WavFileReader:readAudioFormat() Error reading file");
		}
		
		private int readNumChannels()
		{
			int read = stream.readShortSwapped();
			
			if(read<1||read>2)
				throw new AssertionError("WavFileReader:readNumChannels() Error reading file");

			numChannels = read;

			return read;
		}
	
		private int readSmpRate()
		{
			int read = stream.readIntSwapped();
			
			if(!(read==44100||read==48000))
				throw new AssertionError("WavFileReader:readSmpRate() Error reading file");

			smpRate = read;

			return read;
		}
		
		private void readByteRate()
		{
			int read = stream.readIntSwapped();

			// smpRate * numChannels * bytesPerSample
			if(read != smpRate * numChannels * 2)
				throw new AssertionError("WavFileReader:readByteRate() smpRate="+smpRate+" numChannels="+numChannels+" - expected ByteRate="+smpRate*numChannels*2+" actual="+read);
		}
		
		private void readBlockAlign()
		{
			int read = stream.readShortSwapped();
			
			if(read != numChannels*2)
				throw new AssertionError("WavFileReader:readBlockAlign() Error reading file");
		}
		
		private void readBitsPerSmp()
		{
			int read = stream.readShortSwapped();

			if(read != 16)
				throw new AssertionError("WavFileReader:readBitsPerSmp() Error reading file");
		}
		
		private void readSubChunk2ID()
		{
			String read = stream.readStringFour();

			if(!read.equals("data"))
				throw new AssertionError("WavFileReader:readSubChunk2ID() Error reading file");
		}
		
		private void readSubChunk2Size()
		{
			int read = stream.readIntSwapped();
			if(read<1)
				throw new AssertionError("WavFileReader:readSubChunk2Size() Error reading file");

			dataLength = read;
		}
		
		private void readPCMData()
		{
			if(dataLength == 0)
				throw new AssertionError("WavFileReader:readPCMData() Error reading file - dataLength = 0");

			data = stream.readData(dataLength);
		}
		
		
	}
	
