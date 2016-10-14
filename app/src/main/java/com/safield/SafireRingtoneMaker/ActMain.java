package com.safield.SafireRingtoneMaker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class ActMain extends Activity
{
    private final int INTERUPTABLE_PLAY_TIME = 1234;

    private ToneMaker toneMaker;

    // UI components
    private Button playButton;
    private AnimationDrawable playButtonBlinkAnimation;

    private Spinner toneSpinner;
    private Spinner patternSpinner;

	private SeekBar pitchSeekbar;
	private SeekBar speedSeekbar;
	private SeekBar loopSeekbar;

	private TextView pitchDisplay;
	private TextView speedDisplay;
	private TextView loopDisplay;

    boolean isPlayButtonAnimating;

	@Override
	protected void onCreate(Bundle savedInstanceState)
    {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen_layout);

        toneMaker = ToneMaker.Instance();

        setViews();
        setListeners();

        // populate the Pattern spinner selector from the toneMaker patterns
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(ActMain.this,
                android.R.layout.simple_spinner_item, toneMaker.getPatternNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patternSpinner.setAdapter(adapter);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
    {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;

		// Handle item selection
		switch (item.getItemId()) {
			case R.id.save:
                intent = new Intent(this, ActSaveFile.class);
                startActivity(intent);
				return true;
			case R.id.load:
                if (toneMaker.getSaveInfos().size() > 0) {
                    intent = new Intent(this, ActLoadFile.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(this , "No files to load" , Toast.LENGTH_SHORT).show();
                }
				return true;
			case R.id.export:
				intent = new Intent(this, ActExport.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

    @Override
    public void onResume() {
        super.onResume();
        setViewsFromState();
    }

	public void buttonOnClick(View v)
    {
		if (v.getId() == R.id.play_button) {

            if (isPlayButtonAnimating) {
                isPlayButtonAnimating = false;
                resetPlayButton();
                toneMaker.stopAudioTrack();
                return;
            }

            int play_time_ms = toneMaker.play();
            if (play_time_ms > INTERUPTABLE_PLAY_TIME) {
                isPlayButtonAnimating = true;
                playButton.setText(R.string.stop);
                playButton.setBackground(playButtonBlinkAnimation);
                playButtonBlinkAnimation.start();
            }
        }
	}

	private void setViews()
    {
        Log.e("ActMain.setViews" , "setViews Called");
        playButton = (Button) findViewById(R.id.play_button);
        playButtonBlinkAnimation = (AnimationDrawable)  getResources().getDrawable(R.drawable.button_blink_animation);
        toneSpinner = (Spinner) findViewById(R.id.tone_spinner);
        patternSpinner = (Spinner) findViewById(R.id.pattern_spinner);
		pitchSeekbar = (SeekBar) findViewById(R.id.pitch_seekbar);
		speedSeekbar = (SeekBar) findViewById(R.id.speed_seekbar);
		loopSeekbar = (SeekBar) findViewById(R.id.loop_seekbar);
        pitchDisplay = (TextView) findViewById(R.id.pitch_display);
        speedDisplay = (TextView) findViewById(R.id.speed_display);
        loopDisplay = (TextView) findViewById(R.id.loop_display);
	}

    private void setViewsFromState() {

        pitchSeekbar.setMax(ToneMaker.SEMITONE_MOD_AMOUNT);
        speedSeekbar.setMax(ToneMaker.TEMPO_MOD_AMOUNT);
        loopSeekbar.setMax(ToneMaker.MAX_LOOP);
        pitchSeekbar.setProgress(toneMaker.getSemitoneMod() - ToneMaker.SEMITONE_MOD_OFFSET);
        speedSeekbar.setProgress(toneMaker.getTempoMod() - ToneMaker.TEMPO_MOD_OFFSET);
        loopSeekbar.setProgress(toneMaker.getLoop() - 1);
        toneSpinner.setSelection(toneMaker.getSampleIndex());
        patternSpinner.setSelection(toneMaker.getPatternIndex());
    }

    private void resetPlayButton() {
        playButton.setText(R.string.play);
        playButtonBlinkAnimation.stop();
        playButton.setBackgroundResource(R.drawable.main_button);
    }

	private void setListeners()
    {

        toneMaker.setOnPlayCompleteListener(new ToneMaker.OnPlayCompleteListener() {
            @Override
            public void onPlayComplete() {
                if (isPlayButtonAnimating) {
                    isPlayButtonAnimating = false;
                    resetPlayButton();
                }
            }
        });

		OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

                int offset;

				if (arg0.getId() == loopSeekbar.getId()) {

					loopDisplay.setText("" + (arg0.getProgress() + 1));
					return;
				}
                else if (arg0.getId() == speedSeekbar.getId())
                    offset = ToneMaker.TEMPO_MOD_OFFSET;
                else if (arg0.getId() == pitchSeekbar.getId())
                    offset = ToneMaker.SEMITONE_MOD_OFFSET;
                else
                    throw new AssertionError("ActMain: Unknown view assigned to listener");

				String value = "";
				int progress = arg0.getProgress() + offset;

                Log.e("ActMain" , "offset = "+offset+" progress = "+arg0.getProgress());

				if (progress > 0)
					value = "+" + progress;
				else
					value = String.valueOf(progress);

				if (arg0.getId() == pitchSeekbar.getId())
					pitchDisplay.setText(value);
				else if (arg0.getId() == speedSeekbar.getId())
					speedDisplay.setText(value);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) { }

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {

				int value = arg0.getProgress();

				if (arg0.getId() == pitchSeekbar.getId())
					toneMaker.setSemitoneMod(value + ToneMaker.SEMITONE_MOD_OFFSET);
				else if (arg0.getId() == speedSeekbar.getId())
					toneMaker.setTempoMod(value + ToneMaker.TEMPO_MOD_OFFSET);
				else if (arg0.getId() == loopSeekbar.getId())
					toneMaker.setLoop(value + 1);
			}
		};

        speedSeekbar.setOnSeekBarChangeListener(listener);
        pitchSeekbar.setOnSeekBarChangeListener(listener);
        loopSeekbar.setOnSeekBarChangeListener(listener);

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getId() == toneSpinner.getId())
                    toneMaker.setSampleIndex(i);
                else if (adapterView.getId() == patternSpinner.getId())
                    toneMaker.setPatternIndex(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        toneSpinner.setOnItemSelectedListener(spinnerListener);
        patternSpinner.setOnItemSelectedListener(spinnerListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        toneMaker.stopAudioTrack();
    }
}
