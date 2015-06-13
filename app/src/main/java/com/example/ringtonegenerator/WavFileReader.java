package com.example.ringtonegenerator;

import android.content.Context;

public  class WavFileReader{
		
		private WavFileInStream stream;
		private boolean error;
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
			
			WavFile result=null;
			
			stream= new WavFileInStream(name);
			error=false;
			
			//these methods must be called in proper order
			if(!error)readRiff();
			if(!error)readChunkSize();
			if(!error)readFormat();
			if(!error)readSubChunkID();
			if(!error)readSubChunk1Size();
			if(!error)readAudioFormat();
			if(!error)readNumChannels();
			if(!error)readSmpRate();
			if(!error)readByteRate();
			if(!error)readBlockAlign();
			if(!error)readBitsPerSmp();
			if(!error)readSubChunk2ID();
			if(!error)readSubChunk2Size();
			if(!error)readPCMData();
			
			if(!error)result=new WavFile(numChannels,smpRate,data);
			
			reset();
			return result;
		}
		
		
		public WavFile readWave(Context ctx, int id)
		{
			
			WavFile result=null;
			
			stream= new WavFileInStream(ctx,id);
			error=false;
			
			//these methods must be called in proper order
			if(!error)readRiff();
			if(!error)readChunkSize();
			if(!error)readFormat();
			if(!error)readSubChunkID();
			if(!error)readSubChunk1Size();
			if(!error)readAudioFormat();
			if(!error)readNumChannels();
			if(!error)readSmpRate();
			if(!error)readByteRate();
			if(!error)readBlockAlign();
			if(!error)readBitsPerSmp();
			if(!error)readSubChunk2ID();
			if(!error)readSubChunk2Size();
			if(!error)readPCMData();
			
			if(!error)result=new WavFile(numChannels,smpRate,data);
			
			reset();
			return result;
		}
		
		
		public void close()
		{
			stream.close();
		}
		
		private void reset()
		{
			error=false;
			smpRate=0;
			numChannels=0;
			dataLength=0;
			data=null;
			stream.close();
		}
		
		private  void readRiff()
		{
			String riff=stream.readStringFour();
			error=(!riff.equals("RIFF"));
			if(error&&debug)System.out.println("ERROR: Chunk ID error");
		}
		private void readChunkSize()
		{
			int chunkSize=stream.readIntSwapped();
			
			if(chunkSize<36)
			{
				error=true;
				if(debug)System.out.println("ERROR: ChunkSize Error!");
			}
		}
		
		private void readFormat()
		{
			String read=stream.readStringFour();
			error=(!read.equals("WAVE"));
			if(error&&debug)System.out.println("ERROR: Format tag error.");
		}
		
		private void readSubChunkID()
		{
			String read=stream.readStringFour();
			error=(!read.equals("fmt "));
			if(error&&debug)System.out.println("ERROR: SubChunk1ID tag error.");
		}
		
		private void readSubChunk1Size()
		{
			int read = stream.readIntSwapped();
			
			if(read!=16)
			{
				error=true;
				if(debug)System.out.println("ERROR: SubChunk1 size error.");
			}
		}
		
		private void readAudioFormat()
		{
			int read = stream.readShortSwapped();
			
			if(read!=1)
			{
				error=true;
				if(debug)System.out.println("ERROR: unknown compression");
			}
		}
		
		private int readNumChannels()
		{
			int read = stream.readShortSwapped();
			
			if(read<1||read>2)
			{
				error=true;
				if(debug)System.out.println("ERROR: Unsupported number of channels "+read);
			}
			else
			{
				numChannels=read;
			}
			return read;
		}
	
		private int readSmpRate()
		{
			int read = stream.readIntSwapped();
			
			if(!(read==44100||read==48000))
			{
				error=true;
				if(debug)System.out.println("ERROR: Unsupported sample rate "+read);
			}
			else
			{
				smpRate=read;
			}
			
			return read;
		}
		
		private void readByteRate()
		{
			int read = stream.readIntSwapped();
			
			if(read!=smpRate*numChannels*2)
			{
				error=true;
				if(debug)System.out.println("ERROR: ByteRate tag error "+read);
			}
		}
		
		private void readBlockAlign()
		{
			int read = stream.readShortSwapped();
			
			if(read!=numChannels*2)
			{
				error=true;
				if(debug)System.out.println("ERROR: BlockAlign tag error "+read);
			}
		}
		
		private void readBitsPerSmp()
		{
			int read = stream.readShortSwapped();
			
			if(read!=16)
			{
				error=true;
				if(debug)System.out.println("ERROR: unsupported bit depth "+read);
			}
		}
		
		private void readSubChunk2ID()
		{
			String read=stream.readStringFour();
			error=(!read.equals("data"));
			if(error&&debug)System.out.println("ERROR: Format tag error.");
		}
		
		private int readSubChunk2Size()
		{
			int read = stream.readIntSwapped();
			
			if(read<1)
			{
				error=true;
				if(debug)System.out.println("ERROR: no file data "+read);
			}
			else
			{
				dataLength=read;
			}
			
			return read;
		}
		
		private void readPCMData()
		{
			data=stream.readData(dataLength);			
			
		}
		
		
	}
	
