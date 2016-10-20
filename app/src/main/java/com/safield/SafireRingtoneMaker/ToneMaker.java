package com.safield.SafireRingtoneMaker;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 *  Singleton Class ToneMaker - this is the model
 */
public class ToneMaker {

    public static final int MAX_LOOP = 10;
    public static final int SEMITONE_MOD_OFFSET = -12;
    public static final int SEMITONE_MOD_AMOUNT = 24;
    public static final int TEMPO_MOD_OFFSET = -12;
    public static final int TEMPO_MOD_AMOUNT = 24;
    public static final int TEMPO_START = 100;
    public static final int TEMPO_INC = 7;

    private static final float ATTACK_DAMPEN = 234;
    private static final float DECAY_DAMPEN = 976;

    /**
     *  Listener inteface for callBack when audioTrack completes play of one generated tone
     *  The UI that is using ToneMaker class does not know about sample play times and markers, so we don't want
     *  to expose AudioTrack.OnPlaybackPositionUpdateListener
     */
    public interface OnPlayCompleteListener {
        public void onPlayComplete();
    }

    /**
     * Info describing a save file
     */
    public class SaveInfo implements Comparable<SaveInfo> {

        public Date lastModified;
        public String name;

        public SaveInfo (long lastModifed , String name) {
            this.lastModified = new Date(lastModifed);
            this.name = name;
        }

        public int compareTo (SaveInfo compare){
            return compare.lastModified.compareTo(this.lastModified); // invert date comparison to get order of newest first
        }
    }

    /**
     * This represents the State of the Tonemaker, it is intended to be saved to and loaded from file
     */
    private class State {

        public int semitoneMod; // how many semitones up or down we want to modify the pitch of the sample
        public int patternIndex;
        public int sampleIndex;
        public int loop;
        public int tempo;
    }

    /**
     *  An association of a WavFile and a display name
     */
    private class SampleInfo {
        public String displayName;
        public WavFile wavFile;
    }

    private static final int SAMPLE_RATE = 44100;
    private static final int NUM_CHANNELS = 1;

    // this is a singleton
    private static ToneMaker instance;

    // State and save to file stuff
    private State state;
    private ArrayList<SaveInfo> saveinfoCached;
    private boolean loadedSave;
    private String loadedSaveName;

    private AudioTrack audioTrack;
    private AudioTrack.OnPlaybackPositionUpdateListener audiotrackListener;
    private OnPlayCompleteListener playCompleteListener;
	private TonePatternList tonePatternList;
	private ArrayList<WavFile> samples;

    public static ToneMaker Instance()
    {
        if (instance == null)
            instance = new ToneMaker();
        return instance;
    }

	private ToneMaker()
	{
        // load the defaults
        state = new State();
        state.loop = 1;
        state.patternIndex = 0;
        state.semitoneMod = 0;
        setTempoMod(0);

        loadedSaveName = "";

        readSamples();
        setSampleIndex(0);
        tonePatternList = new TonePatternList(LocalApp.getAppContext() , R.raw.patterns);


        audiotrackListener = new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack audioTrack) {

                if (playCompleteListener != null)
                    playCompleteListener.onPlayComplete();
            }
            @Override
            public void onPeriodicNotification(AudioTrack audioTrack) {}
        };
	}

    public void setLoop(int loop)
    {
        this.state.loop = loop;
    }

    public int getLoop () {
        return state.loop;
    }

	public void setSemitoneMod(int semitone) {
		state.semitoneMod = semitone;
	}

    public int getSemitoneMod() {
        return state.semitoneMod;
    }

    public void setOnPlayCompleteListener(OnPlayCompleteListener listener) {
        playCompleteListener = listener;
    }

	public List<String> getPatternNames()
	{

        List<String> result = new ArrayList<String>();

		for(int i = 0; i < tonePatternList.size(); i++)
			result.add(tonePatternList.get(i).getName());

        return result;
	}

	public void setPatternIndex(int index) {
        state.patternIndex = index;
	}

    public int getPatternIndex() {
        return state.patternIndex;
    }

    public void setTempoMod(int speed) {
        this.state.tempo = TEMPO_START + speed * TEMPO_INC;
    }

    public int getTempoMod() {
        return (state.tempo - TEMPO_START) / TEMPO_INC;
    }

    public void setSampleIndex(int position) {
        state.sampleIndex = position;
	}

    public int getSampleIndex() {
        return state.sampleIndex;
    }

    public int getNumSamples() {
        return samples.size();
    }

    public String getSampleName(int index) {
        //String result = samples.get(index).getName();
        //return samples[index]
        return new String();
    }

    public String getSaveName() {
        return loadedSaveName;
    }

    public boolean writeSaveFile(String fileName) {

        boolean success = false;

        try {

            DataOutputStream dos = new DataOutputStream(LocalApp.getAppContext().openFileOutput(fileName+".save", Context.MODE_PRIVATE));
            dos.writeInt(state.loop);
            dos.writeInt(state.patternIndex);
            dos.writeInt(state.sampleIndex);
            dos.writeInt(state.semitoneMod);
            dos.writeInt(state.tempo);
            dos.close();

            Log.d("ToneMaker.writeSaveFile" , "Successfully saved file name = "+fileName);
            saveinfoCached = null;
            loadedSaveName = fileName;
            success = true;
        } catch (IOException i) {
            Log.e("ToneMaker.writeSaveFile" , i.toString());
            i.printStackTrace();
        }

        return success;
    }

    public boolean loadStateFromFile(String fileName) {

        boolean success = false;

        try {
            DataInputStream dis = new DataInputStream(LocalApp.getAppContext().openFileInput(fileName+".save"));
            state.loop = dis.readInt();
            state.patternIndex = dis.readInt();
            state.sampleIndex = dis.readInt();
            state.semitoneMod = dis.readInt();
            state.tempo = dis.readInt();
            Log.d("ToneMaker.loadStateFromFile" , "Successfully loaded file name = "+fileName);
            loadedSaveName = fileName;
            success = true;
        } catch (IOException i) {
            Log.e("ToneMaker.loadStateFromFile" , i.toString());
            i.printStackTrace();
        }

        return success;
    }

    public void deleteAllSaves ()
    {

        File fileDir = LocalApp.getAppContext().getFilesDir();
        String[] allFiles = fileDir.list();
        File tempFile;
        saveinfoCached = null;

        for (int i = 0; i < allFiles.length; i++) {

            String substring = allFiles[i].substring(allFiles[i].length() - 5, allFiles[i].length());

            if (substring.equals(".save")) {
                tempFile = new File(fileDir, allFiles[i]);
                tempFile.delete();
            }
        }
    }

    public ArrayList<SaveInfo> getSaveInfos ()
    {

        if (saveinfoCached == null) {

            saveinfoCached = new ArrayList<SaveInfo>();
            File fileDir = LocalApp.getAppContext().getFilesDir();
            String[] allFiles = fileDir.list();
            File tempFile;

            for (int i = 0; i < allFiles.length; i++) {

                String substring = allFiles[i].substring(allFiles[i].length() - 5, allFiles[i].length());

                if (substring.equals(".save")) {
                    tempFile = new File(fileDir, allFiles[i]);
                    saveinfoCached.add(new SaveInfo(tempFile.lastModified(), allFiles[i].substring(0, allFiles[i].length() - 5)));
                }
            }

            Collections.sort(saveinfoCached);
        }

        return saveinfoCached;
    }

    public void onPause()
    {
        audioTrack.release();
    }

    /**
     * This method creates a buffer of PCM data that is the complete output tone ready to be played or written to file
     */
	private float[] createTone()
	{

		Note currNote;
        WavFile smp = samples.get(state.sampleIndex);
        TonePattern pattern = tonePatternList.get(state.patternIndex);

        // we determine the final length of the output sample before we start generating it
        int total_length = 0;

        // sum the length of all the notes
        for (int i = 0; i < pattern.getNumNotes(); i++)
            total_length += pattern.getNote(i).getLengthInSamples(state.tempo);

        total_length *= state.loop;

        float[] output = new float[total_length];

		int outIndex = 0;
        int index = 0;

        float pitch;
		float linearIntp = 0;
		float phasePtr = 0;

        for(int i = 0; i < state.loop; i++) {
            for (int k = 0; k < pattern.getNumNotes(); k++) {

                currNote = pattern.getNote(k);
                pitch = (float) Math.pow(2, (currNote.getSemitone() + state.semitoneMod) / 12.0);

                int currNoteLength = currNote.getLengthInSamples(state.tempo);

                for (int j = 0; j < currNoteLength; j++) {

                    index = (int) phasePtr;
                    linearIntp = phasePtr - index;

                    //if we are not at end of sample copy data to output
                    if (index < smp.size() - 1)
                        output[outIndex] = (smp.get(index + 1) * linearIntp) + (smp.get(index) * (1 - linearIntp)); // pitch shift linear interp
                    else
                        output[outIndex] = 0;

                    // this will increasingly dampen the onset and end volume of the sample to prevent popping artifacts
                    if (j < ATTACK_DAMPEN)
                        output[outIndex] *= j / ATTACK_DAMPEN;
                    else if (j > currNoteLength - DECAY_DAMPEN)
                        output[outIndex] *= (j - currNoteLength) / -DECAY_DAMPEN;

                    outIndex++;
                    phasePtr += pitch;
                }

                phasePtr = 0;
            }
		}

        return output;
	}

    /**
     *  This method takes the output PCM data from CreateTone, and the plays it using AudioTrack
     *  Return the time in milliseconds for the play to complete
     */
    public int play() {

        float[] output = createTone();

        stopAudioTrack();

        audioTrack = new AudioTrack(AudioManager.STREAM_RING, SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                (output.length*4),
                AudioTrack.MODE_STATIC);

        if (audiotrackListener != null) {
            Log.e("ToneMaker.ToneMaker" , "Listener set on audiotrack (not null)");
            audioTrack.setPlaybackPositionUpdateListener(audiotrackListener);
        }

        short[] result =  new short[output.length];

        for(int i = 0; i < output.length; i++)
            result[i] = (short) ((output[i])*32768);

        audioTrack.setNotificationMarkerPosition(output.length-1);

        audioTrack.write(result, 0, output.length);
        audioTrack.play();

        return (int)(output.length / (SAMPLE_RATE / 1000.0f)); // play time in milliseconds
    }

    public void stopAudioTrack() {
        if (audioTrack != null && audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            audioTrack.stop();
            audioTrack.release();
        }
    }

    /**
     * This method takes the output PCM data of creatTone and then writes it to file [fileName].wav in directory [directory]
     */
    public void writeToneToFile(String fileName, String directory)
    {
        WavFile out_wave = new WavFile(NUM_CHANNELS, SAMPLE_RATE, createTone());
        out_wave.writeToFile(fileName , directory);
    }

	private void readSamples()
	{
		WavFileReader reader = new WavFileReader(LocalApp.getAppContext());
		samples = new ArrayList<WavFile>();

        Resources res = LocalApp.getAppContext().getResources();
        samples.add(new WavFile(res.openRawResource(R.raw.sine)));
        samples.add(new WavFile(res.openRawResource(R.raw.sine_tail)));
        samples.add(new WavFile(res.openRawResource(R.raw.saw)));
        samples.add(new WavFile(res.openRawResource(R.raw.square)));
        samples.add(new WavFile(res.openRawResource(R.raw.plucked_saw)));
        samples.add(new WavFile(res.openRawResource(R.raw.plucked_square)));
        samples.add(new WavFile(res.openRawResource(R.raw.poly_saw)));

        // WavFile can support 1 or 2 channels, but for now ToneMaker only supports 1
        for ( int i = 0; i < samples.size(); i++)
            if (samples.get(i).getChannels() != 1)
                throw new AssertionError("ToneMaker.readSamples: invalid wave file read - incompatible number of channels = "+samples.get(i).getChannels()+" expected NUM_CHANNELS = 1");
	}
}
