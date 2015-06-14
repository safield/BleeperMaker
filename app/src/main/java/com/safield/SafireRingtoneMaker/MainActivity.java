package com.safield.SafireRingtoneMaker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static ToneMaker tMaker;
	public static final int SOUND_BROWSER = 123;
	public static final int PATTERN_BROWSER = 234;
	
	private final int SOUND_BROWSE_REQUEST = 999;
	private final int PATTERN_BROWSE_REQUEST = 888;

    private int soundSelector;
    private int patternSelector;

	private SeekBar pitchSeekbar;
	private SeekBar speedSeekbar;
    private SeekBar loopSeekbar;

	private TextView pitchDisplay;
	private TextView speedDisplay;
    private TextView loopDisplay;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		patternSelector=0;
		soundSelector=0;
		assignWidgets();
		assignListeners();
		
		tMaker = new ToneMaker(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == SOUND_BROWSE_REQUEST) {

            soundSelector = resultCode;
		}
		else if(requestCode == PATTERN_BROWSE_REQUEST) {

            patternSelector = resultCode;
			tMaker.setPattern(patternSelector);
		}
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save:
                return true;
            case R.id.load:
                return true;
            case R.id.export:
                Intent i = new Intent(this,ExportActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	public void buttonOnClick(View v)
	{
		if(v.getId()==R.id.soundButton) {

			Intent i = new Intent(this,BrowserActivity.class);
			i.putExtra("selector", soundSelector);
			i.putExtra("browserType", SOUND_BROWSER);
			this.startActivityForResult(i, SOUND_BROWSE_REQUEST);
		}
		else if(v.getId()==R.id.patternButton) {

			Intent i = new Intent(this,BrowserActivity.class);
			i.putExtra("selector", patternSelector);
			i.putExtra("browserType", PATTERN_BROWSER);
			this.startActivityForResult(i, PATTERN_BROWSE_REQUEST);
		}
		else if(v.getId()==R.id.previewButton) {

            tMaker.playTrack();
		}
		//else if(v.getId() == R.id.exportButton) { EXPORT STUFF - NOT IMPLEMENTED
		//}
	}
	
	//---PRIVATE METHODS---
	
	private void assignWidgets()
	{
		
		pitchSeekbar = (SeekBar)findViewById(R.id.pitchSeekbar);
		speedSeekbar = (SeekBar)findViewById(R.id.speedSeekbar);
        loopSeekbar = (SeekBar)findViewById(R.id.loopSeekbar);

		pitchSeekbar.setMax(24);
		speedSeekbar.setMax(24);
        loopSeekbar.setMax(11);

		pitchSeekbar.setProgress(12);
		speedSeekbar.setProgress(12);
        loopSeekbar.setProgress(0);
		
		pitchDisplay = (TextView)findViewById(R.id.pitchDisplay);
		speedDisplay = (TextView)findViewById(R.id.speedDisplay);
        loopDisplay = (TextView)findViewById(R.id.loopDisplay);
	}
	
	private void assignListeners()
	{
		OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

                if(arg0.getId() == loopSeekbar.getId()) {

                    loopDisplay.setText(""+(arg0.getProgress()+1));
                    return;
                }

				String value = "";
				int progress = arg0.getProgress() - 12;
				
				if(progress > 0)
					value = "+" + progress;
				else
					value = String.valueOf(progress);

				if(arg0.getId() == pitchSeekbar.getId())
					pitchDisplay.setText(value);
				else if(arg0.getId()==speedSeekbar.getId())
					speedDisplay.setText(value);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				
				int value = arg0.getProgress();
				
				if(arg0.getId() == pitchSeekbar.getId())
					tMaker.setPitch(value - 12);
				else if(arg0.getId() == speedSeekbar.getId())
					tMaker.setTempo(((value) * 7) + 70);
                else if(arg0.getId() == loopSeekbar.getId())
                    tMaker.setLoop(value+1);
			}
		};
		
		speedSeekbar.setOnSeekBarChangeListener(listener);
		pitchSeekbar.setOnSeekBarChangeListener(listener);
        loopSeekbar.setOnSeekBarChangeListener(listener);
	}
}
