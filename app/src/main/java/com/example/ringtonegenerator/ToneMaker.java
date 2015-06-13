package com.example.ringtonegenerator;
import java.util.ArrayList;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/*
 * This class generates the output tones based on its sample and pattern
 */
public class ToneMaker {

	public static final int NOTIFICATION = 1234;
	public static final int  RINGTONE = 2345;
	private static final int SMP_RATE = 44100;
	private final int NUM_RAW_SAMPLES = 1; //this must match number of samples in raw folder
	public static int glbPitch;

    private static ToneMaker instance = null;

	private WavFile sample;
	private TonePattern pattern;
	private float[] outTrack;
	private PatternList list;
	private ArrayList<WavFile> samples;
	private AudioTrack track;
	private boolean played;
	private int loop,
                repeat,
                spacing,
                tempo;

	Context ctx;
	
	public ToneMaker(Context ctx)
	{

        this.ctx = ctx;

        tempo = 154;
		loop = 1;
		repeat = 1; // repeat is
		spacing = 0;
        played = false;
        glbPitch = 0;

        readSamples(); // must come after ctx is assigned
		list = new PatternList(ctx,R.raw.patterns);
		pattern = list.get(0);
		initAudio();
	}

	public void setPitch(int semitone)
	{
		glbPitch = semitone;
	}

    public void setLoop(int loop) { this.loop = loop; }

	public String getPatternName(int index)
	{
		return list.get(index).getName();
	}

	public String[] patternNames()
	{

		String[]result = new String[list.size()];
		
		for(int i = 0; i < list.size(); i++)
			result[i]=list.get(i).getName();

        return result;
	}
	
	public void logPatternList()
	{
		Log.i("test",list.toString());
	}

	public void setPattern(int index)
	{
        pattern = list.get(index);
		pattern.setTempo(tempo);
	}
	
	public void setSample(WavFile sample)
	{
		this.sample=sample;
	}
	
	public void setTempo(int tempo)
	{

        this.tempo = tempo;

		if(pattern != null)
			pattern.setTempo(tempo);
	}
	
	public void readSampleFile(String name)
	{
        WavFileReader reader = new WavFileReader();
		sample=reader.readWave(name);
	}
	
	public void setPattern(TonePattern pattern)
	{
		this.pattern = pattern;
	}
	
	public void setSample(String name)
	{
        WavFileReader reader= new WavFileReader();
		sample=reader.readWave(name);
	}
	
	/*
	 * This method plays generates a wave file using one single sample,
	 * and note information that specifies duration and pitch of sample.
	 */
	public void playTrack()
	{
		generateOutTrack(null,0,true);
	}
	public void generateOutTrack(String fileName,int directory)
	{
		generateOutTrack(fileName,directory,false);
	}
	private void generateOutTrack(String fileName,int directory, boolean play)
	{
		String debug="";
		
		Note currNote;

		float[] currSample = sample.getData();//the pcm data of the sample to be used
		int sampleLength = (int)(currSample.length*Math.pow(2, glbPitch/12.0));//get sample length after pitch
		
		if(loop >1)
            pattern.setTail(false);
		
		pattern.setSampleLength(sampleLength); //send pattern the sampleLength after pitch
		pattern.pitchMod(glbPitch);
		float[] output = new float[pattern.getPlayTimeInSmps()* loop *repeat+((repeat-1)*spacing)];//returns total play time of pattern in #samples.
		int currSampleLength;
		debug="Sample length="+((int)(currSample.length*Math.pow(2, glbPitch/12.0)))+'\n';
		
		int debugCount = 1;
		int noteLength = 0;
		int outIndex = 0;
        int sampleIndex = 0;

        float pitch;
		float linearIntp = 0;
		float phasePtr = 0;

		for(int q = 0; q < repeat; q++) {
			for(int j = 0; j < loop; j++) {
				while((currNote = pattern.nextNote()) != null) { //each iteration plays one note

                    noteLength = currNote.getPeriodInSmps();//length of current note in samples

                    debug = debug + "note " + debugCount + " length=" + noteLength + '\n';
                    debugCount++;

                    pitch = currNote.getPitch(); //pitch of current note
                    currSampleLength = (int) Math.ceil(currSample.length * (1 / pitch) - 1);
                    debug = debug + (currSampleLength) + '\n';

                    for (int i = 0; i < noteLength; i++) {    //run for length of note

                        sampleIndex = (int) phasePtr; //floor of floating point index
                        linearIntp = phasePtr - sampleIndex;

                        //if we are not at end of sample copy data to output
                        if (sampleIndex < currSampleLength)
                            output[outIndex] = (currSample[sampleIndex + 1] * linearIntp) + (currSample[sampleIndex] * (1 - linearIntp)); // pitch shift linear interp
                        else
                            output[outIndex] = 0; // silence

                        outIndex++;
                        phasePtr = phasePtr + pitch;
                    }

                    phasePtr = 0;
                }
			}

			//if(q < repeat-1)
				//outIndex = outIndex + spacing;
		}

		JavAud.quietEnd(output);

		if(!play) {

			WavFileWriter writer = new WavFileWriter(ctx);
			writer.writeWave(new WavFile(1, JavAud.GLB_SMP_RATE, output), fileName , directory);
		}
		else {
			
			playTrack(output);
			/*
			 * short[] shorts = JavAud.floatToShort(output);
			if(played)
			{
				track.stop();
				track.flush();
				track.reloadStaticData();
			}
				
			track.write(shorts, 0, shorts.length);
			track.play();
			played=true;*/
		}
	}
	
	private void readSamples()
	{

		WavFileReader reader = new WavFileReader();
		sample = reader.readWave(ctx, R.raw.ensoniq);
	}
	
	private void initAudio()
	{
		/*int buffsize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, 
                AudioFormat.ENCODING_PCM_16BIT);
		//create an audiotrack object
		//track = new AudioTrack(AudioManager.STREAM_RING, 44100, 
        AudioFormat.CHANNEL_OUT_MONO, 
        AudioFormat.ENCODING_PCM_16BIT, 
        buffsize*32, 
        AudioTrack.MODE_STATIC);*/
	}
	
	private void playTrack(float[] output)
	{
		if(track != null) {

			track.pause();
			track.flush();
			track.release();
		}
		
		track = new AudioTrack(AudioManager.STREAM_RING, 44100, 
        AudioFormat.CHANNEL_OUT_MONO, 
        AudioFormat.ENCODING_PCM_16BIT, 
        (output.length*4), 
        AudioTrack.MODE_STATIC);
		
		track.write(JavAud.floatToShort(output), 0, output.length);
		track.play();
	}
}
