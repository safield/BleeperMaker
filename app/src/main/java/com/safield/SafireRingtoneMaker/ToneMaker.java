package com.safield.SafireRingtoneMaker;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.CRC32;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 *  Singleton Class ToneMaker - this is the model
 */
public class ToneMaker {

    public static final int MAX_LOOP = 10;
    public static final int SEMITONE_MOD_OFFSET = -12;
    public static final int SEMITONE_MOD_AMOUNT = 24;
    public static final int TEMPO_MOD_OFFSET = -12;
    public static final int TEMPO_MOD_AMOUNT = 24;
    public static final int TEMPO_START = 550; // the default base tempo
    public static final int TEMPO_INC = 30; // amount 1 increase from the UI will increase tempo

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

        public final Date lastModified;
        public final String name;

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
     *  An association of a WavFile and a display name. We use the wave file name as the display name.
     */
    private class Sample {
        public final String displayName;
        public final WavFile wavFile;

        public Sample (String displayName , WavFile wavFile){
            this.displayName = displayName;
            this.wavFile = wavFile;
        }
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
	private ArrayList<Sample> samples;

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

        readSamplesFromAssets();
        setSample(0);
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

    public void setSample(int index) {
        state.sampleIndex = index;
	}

    public int getSampleIndex() {
        return state.sampleIndex;
    }

    public int getNumSamples() {
        return samples.size();
    }

    public int getNumPatterns () {
        return tonePatternList.size();
    }

    public String getSampleName(int index) {
        return samples.get(index).displayName;
    }

    public String getPatternName(int index) {
        return tonePatternList.get(index).getName();
    }

    public String getSaveName() {
        return loadedSaveName;
    }

    public boolean writeSaveFile(String fileName) {

        boolean success = false;

        try {

            DataOutputStream dos = new DataOutputStream(LocalApp.getAppContext().openFileOutput(fileName + '_' + getVersionSignature() + ".save", Context.MODE_PRIVATE));

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
        }
        catch (IOException i) {

            Log.e("ToneMaker.writeSaveFile" , i.toString());
            i.printStackTrace();
        }

        return success;
    }

    public boolean loadStateFromFile(String name) {

        boolean success = false;

        try {

            DataInputStream dis = new DataInputStream(LocalApp.getAppContext().openFileInput(name+ '_' + getVersionSignature() + ".save"));

            state.loop = dis.readInt();
            state.patternIndex = dis.readInt();
            state.sampleIndex = dis.readInt();
            state.semitoneMod = dis.readInt();
            state.tempo = dis.readInt();
            Log.d("ToneMaker.loadStateFromFile" , "Successfully loaded file name = "+name);
            loadedSaveName = name;
            success = true;
        }
        catch (IOException i) {

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
            String str;

            for (int i = 0; i < allFiles.length; i++) {

                str = allFiles[i];
                String substring = str.substring(str.length() - 5, str.length());

                if (substring.equals(".save")) {

                    String versionSigStr = str.substring(str.lastIndexOf('_') + 1 , str.lastIndexOf('.'));
                    long versionSignature = Long.parseLong(versionSigStr);

                    tempFile = new File(fileDir, str);

                    // if this save is an old version then delete it
                    if (versionSignature != getVersionSignature())
                        tempFile.delete();
                    else
                        saveinfoCached.add(new SaveInfo(tempFile.lastModified(), str.substring(0, str.lastIndexOf('_'))));
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
        WavFile smp = samples.get(state.sampleIndex).wavFile;
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
        File file = new File(Environment.getExternalStoragePublicDirectory(directory), fileName+".wav");
        WavFile out_wave = new WavFile(NUM_CHANNELS, SAMPLE_RATE, createTone());
        out_wave.writeToFile(file);
        MediaScannerConnection.scanFile(LocalApp.getAppContext(), new String[]{file.getAbsolutePath()}, null, null);
    }

    /**
     *  Loads all the wave files in the assets folder as samples.
     *
     *  This is not used currently, but is useful to quickly drop in a bunch of new wave files to audition.
     *  Downside is that there is no clean way to custom order the wave files when the UI wants to display them.
     */
	private void readSamplesFromAssets()
	{

        samples = new ArrayList<Sample>();

        try {

            AssetManager assets = LocalApp.getAppContext().getAssets();
            String[] fileList = assets.list("");

            // there should always be files in assets
            if (fileList.length <= 0)
                throw new AssertionError("ToneMaker.readSamples - assets folder is empty");

            for (int i = 0; i < fileList.length; i++) {
                int index = fileList[i].lastIndexOf('.');
                if (fileList[i].substring(index + 1).equals("wav")) {
                    WavFile wave = new WavFile(assets.open(fileList[i]));
                    String displayName = fileList[i].substring(0,index);
                    samples.add(new Sample(displayName , wave));
                }
            }
        }
        catch (IOException e) {
            Log.e(TAG,Log.getStackTraceString(e));
        }

        // there should always be at least one valid wave file in assets
        if (samples.size() <= 0)
            throw new AssertionError("ToneMaker.readSamples - no wave files were successfully loaded from the assets folder");

        // WavFile can support 1 or 2 channels, but for now ToneMaker only supports 1
        for ( int i = 0; i < samples.size(); i++)
            if (samples.get(i).wavFile.getChannels() != 1)
                throw new AssertionError("ToneMaker.readSamples: invalid wave file read - incompatible number of channels = "+samples.get(i).wavFile.getChannels()+" expected NUM_CHANNELS = 1");
	}

    /**
     * Return a checksum from the sample configuration for save file versioning
     */
    private long getVersionSignature() {

        CRC32 checkSum = new CRC32();

        try {

            ByteArrayOutputStream bao_stream = new ByteArrayOutputStream();
            bao_stream.write(samples.size());

            for (Sample smp : samples)
                bao_stream.write(smp.displayName.getBytes());

            checkSum.update(bao_stream.toByteArray());

            bao_stream.write(tonePatternList.size());
            for (int i = 0; i < tonePatternList.size(); i++)
                bao_stream.write(tonePatternList.get(i).getName().getBytes());
            checkSum.update(bao_stream.toByteArray());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return checkSum.getValue();
    }

    /** KEEP THIS AROUND FOR NOW
     * This explicity reads samples from the Raw resources.

    private void readSamples()
    {

        Field[] fields = R.raw.class.getFields();
        for(int count=0; count < fields.length; count++) {
            Log.i("Raw Asset: ", fields[count].getName());
        }

        samples = new ArrayList<Sample>();
        Resources res = LocalApp.getAppContext().getResources();
        samples.add(new WavFile(res.openRawResource(R.raw.sine)));

        // WavFile can support 1 or 2 channels, but for now ToneMaker only supports 1
        for ( int i = 0; i < samples.size(); i++)
            if (samples.get(i).getChannels() != 1)
                throw new AssertionError("ToneMaker.readSamples: invalid wave file read - incompatible number of channels = "+samples.get(i).getChannels()+" expected NUM_CHANNELS = 1");
    }*/
}
