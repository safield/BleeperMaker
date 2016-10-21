package com.safield.SafireRingtoneMaker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class WavFile
{

    private int numChannels;
    private int sampleRate;
    private float[] data;

    public WavFile(int numChannels, int sampleRate, float[] data)
    {
        this.numChannels = numChannels;
        this.sampleRate = sampleRate;
        this.data = data;

        if(data.length <= 0)
            throw new AssertionError("WavFile: Data length is 0");
    }

    public WavFile(int numChannels, int sampleRate)
    {
        this.numChannels = numChannels;
        this.sampleRate = sampleRate;
    }

    public WavFile (InputStream dataInStream)
    {

        int dataLength = 0;

        try {

            DataInputStream inStream = new DataInputStream(dataInStream);

            byte[] four_bytes = new byte[4];

            // readRiff
            inStream.read(four_bytes);
            String riff = new String(four_bytes);
            if(!riff.equals("RIFF"))
                throw new IOException("WavFileReader: Invalid file format");

            // readChunkSize
            if(readInt(inStream) < 36)
                throw new IOException("WavFileReader: Invalid file format");

            // readFormat
            inStream.read(four_bytes);
            String format = new String(four_bytes);
            if(!format.equals("WAVE"))
                throw new IOException("WavFileReader: Invalid file format");

            // readSubChunkID
            inStream.read(four_bytes);
            String fmt = new String(four_bytes);
            if(!fmt.equals("fmt "))
                throw new IOException("WavFileReader: Invalid file format");

            // readSubChunk1Size
            if(readInt(inStream) != 16)
                throw new IOException("WavFileReader: Invalid file format");

            // readAudioFormat
            if(readShort(inStream) != 1)
                throw new IOException("WavFileReader: Invalid file format");

            // readNumChannels
            numChannels = readShort(inStream);
            if(numChannels < 1 || numChannels > 2)
                throw new IOException("WavFileReader: Unsupported number of channels - value = "+numChannels);

            // readSmpRate
            sampleRate = readInt(inStream);
            if(sampleRate != 44100 && sampleRate != 48000)
                throw new IOException("WavFileReader: Unsupported samplerate -  value = "+sampleRate);

            // readByteRate (byterate = sampleRate * numChannels * bytesPerSample)
            int byteRate = readInt(inStream);
            if(byteRate != sampleRate * numChannels * 2)
                throw new IOException("WavFileReader: Invalid byterate - sampleRate="+sampleRate+" numChannels="+numChannels+" - expected ByteRate="+sampleRate*numChannels*2+" actual=" + byteRate);

            // readBlockAlign
            if(readShort(inStream) != numChannels * 2)
                throw new IOException("WavFileReader: Invalid file format");

            // readBitsPerSmp
            if(readShort(inStream) != 16)
                throw new IOException("WavFileReader: Unsupported bits per sample - only 16 bit wave files are supported");

            // readSubChunk2ID
            inStream.read(four_bytes);
            String dataStr = new String(four_bytes);
            if(!dataStr.equals("data"))
                throw new IOException("WavFileReader: Invalid file format");

            // readSubChunk2Size
            dataLength = readInt(inStream);
            if(dataLength < 1)
                throw new IOException("WavFileReader: Invalid file format - data length should be greater than 1");

            // readPCMData
            data = new float[dataLength / 2];
            byte[] bytes = new byte[dataLength];
            short temp = 0;

            inStream.read(bytes);

            for (int i = 0; i < dataLength / 2; i++) {
                // take two little endian bytes and convert to float between -1.0 an 1.0
                temp=(short) ((bytes[i*2] & 0xFF) | ((bytes[i*2+1] & 0xFF) << 8 ));
                data[i] = (temp/32768.0f);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean writeToFile (File file)
    {
        boolean result = false;

        try {
            DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));

            //ChunkID 4
            stream.writeBytes("RIFF");

            //ChunkSize 4 -- 36+data size in bytes
            writeInt(data.length*2+36 , stream); // assumes 16 bit wave
            //Format 4
            stream.writeBytes("WAVE");
            //Subchunk1ID 4
            stream.writeBytes("fmt ");
            //Subchunk1Size 4
            writeInt(16 , stream);
            //AudioFormat 2 PCM = 1
            writeShort((short)1 , stream);
            //NumChannels 2
            writeShort((short)numChannels , stream);
            //SampleRate 4
            writeInt(sampleRate , stream);
            //ByteRate 4
            writeInt(sampleRate * numChannels * 2 , stream);
            //BlockAlign  2
            writeShort((short)(numChannels * 2) , stream);
            //BitsPerSample
            writeShort((short)16 , stream);
            //Subchunk2ID 4
            stream.writeBytes("data");
            //Subchunk2Size 4
            writeInt(data.length * 2 , stream); // assumes 16 bit wave
            //WRITE THE PCM DATA
            stream.write(floatAudioToShort(data));

            stream.close();

            result = true;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public float get(int index) {
        return data[index];
    }

    // read in a short of little endian
    private short readShort(DataInputStream inStream) throws IOException
    {
        int result;
        byte[] bytes= new byte[2];
        inStream.read(bytes);
        result=((bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8 ));

        return (short) result;
    }

    // read in an int of little endian
    private int readInt(DataInputStream inStream) throws IOException
    {
        long result;
        byte[] bytes= new byte[4];
        inStream.read(bytes);
        result = ((bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8 ) | ((bytes[2] & 0xFF) << 16 ) | ((bytes[3] & 0xFF) << 24 ));
        return (int) result;
    }

    //write an short with swapped endian
    private void writeShort(short out , DataOutputStream outStream) throws IOException {
        byte[] bytes = new byte[2];
        bytes[0] = (byte)(out & 0xff);
        bytes[1] = (byte)((out >> 8) & 0xff);
        outStream.write(bytes);
    }

    //write an int with swapped endian
    private void writeInt(int out , DataOutputStream outStream) throws IOException
    {
        byte[] bytes = new byte[4];
        bytes[0] = (byte)(out & 0xff);
        bytes[1] = (byte)((out >> 8) & 0xff);
        bytes[2] = (byte)((out >> 16) & 0xff);
        bytes[3] = (byte)((out >> 24) & 0xff);
        outStream.write(bytes);
    }

    private byte[] floatAudioToShort (float[] data)
    {
        byte[] bytes = new byte[data.length*2]; // assumes 16 bit wave

        short out;

        for(int i = 0; i < data.length; i++) {
            // should there be clipping protection here?
            out = (short) ((data[i])*32768);
            bytes[i*2]=(byte)(out & 0xFF);
            bytes[i*2+1]=(byte)((out>>8) & 0xFF);
        }

        return bytes;
    }

    public int getChannels(){ return numChannels; }
    public int getSampleRate(){ return sampleRate; }
    public int size(){ return data.length; }
    public float[] getData(){ return data; }
    public void setData(float[]data){this.data = data;}
    public String toString(){
        return  " chnls="+numChannels+
                " sampleRate="+sampleRate+
                " dataSize="+data.length;
    }
}

	
