package com.example.ringtonegenerator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class BrowserActivity extends Activity {
	
	private LinearLayout container;
	private String[] names;
	private int selector;
	private int browserType;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browser_layout);
		Bundle extras = getIntent().getExtras();
		
		if(extras!=null) {
			selector=extras.getInt("selector");
			browserType=extras.getInt("browserType");
		}
		
		container=(LinearLayout)findViewById(R.id.scrollContainer);
		
		if(browserType==MainActivity.SOUND_BROWSER) {
			names=getResources().getStringArray(R.array.sounds_array);
		}
		else if(browserType==MainActivity.PATTERN_BROWSER)
		{
			names=MainActivity.tMaker.patternNames();
			
		}
	
		drawList();
	}

	private void drawList()
	{
		FrameLayout tempLayout;
		LayoutParams params;
		TextView tempText;
		int margin=(int)getResources().getDimension(R.dimen.item_margin);
		
		OnClickListener listener = new OnClickListener()
		{

			@Override
			public void onClick(View arg0) {
				
				if(selector>=0)
				{
					container.getChildAt(selector).setBackgroundResource(R.color.background);
				}
				
				arg0.setBackgroundResource(R.drawable.list_button_shape_pressed);
				selector=arg0.getId();
				
			}
			
		};
		
		for(int i=0;i<names.length;i++)
		{
			params=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)getResources().getDimension(R.dimen.item_height));
			params.gravity=Gravity.CENTER;
			
			if(i==names.length-1)
			{
				params.setMargins(0, 0, 0, 0);
				
			}
			else
			{
				params.setMargins(0, 0, 0, 1);
			}
			
			tempLayout= new FrameLayout(this);
			tempLayout.setId(i);
			tempLayout.setClickable(true);
			tempLayout.setLayoutParams(params);
			tempLayout.setBackgroundResource(R.color.background);
			
			if(i==selector)
				tempLayout.setBackgroundResource(R.drawable.list_button_shape_pressed);
			
			tempLayout.setOnClickListener(listener);
			
			tempText=new TextView(this);
			tempText.setText(names[i]);
			tempText.setTextSize(getResources().getDimension(R.dimen.item_text));
			tempText.setTextColor(getResources().getColor(R.color.black));
			tempText.setGravity(Gravity.CENTER);
			
			
			
			tempLayout.addView(tempText);
			
			container.addView(tempLayout);
			
		}
		
	}
	
	@Override
	public void finish()
	{
		
		setResult(selector);
		super.finish();
		
	}
	
}
