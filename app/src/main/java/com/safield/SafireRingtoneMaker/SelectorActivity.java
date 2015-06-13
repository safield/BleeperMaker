package com.safield.SafireRingtoneMaker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectorActivity extends Activity {
	
	private ListView listView;
	private int selector;
	private boolean set;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selector_layout);
		set=false;
		Bundle extras = getIntent().getExtras();
		
		if(extras!=null)
		{
			selector=extras.getInt("selector");
		}
		
		listView=(ListView)findViewById(R.id.selector_layout);
		
		//set the string array for the listview
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.sounds_array, android.R.layout.simple_list_item_1);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		
		
		listView.setAdapter(adapter);
		
		
		
		highlightSelected();
		
		listView.setOnItemClickListener(
				new OnItemClickListener()
				{

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						Log.i("test","position="+arg2+" id="+arg3);
						findViewById(selector).setBackgroundResource(R.color.background);
						selector=arg2;
						findViewById(selector).setBackgroundResource(R.drawable.main_button_shape_pressed);
					}
					
				}
);
		
		
		
	}
	
	
	private void setBackground(int id)
	{
		Log.i("test",String.valueOf(listView.getAdapter().getCount()));
		
		for(int i=0;i<listView.getAdapter().getCount();i++)
		{
			
		}
	}
	//this method will highlight a selected listview once that listview is drawn
	private void highlightSelected()
	{
		if(!set)
		{
			new Thread(
					new Runnable()
					{

						@Override
						public void run() {
							// TODO Auto-generated method stub
							boolean trigger=true;
							while(trigger)
							{
								if(listView.getChildAt(selector)!=null)
								{
									
									set=true;
									trigger=false;
									//listView.getAdapter().getView(selector, new View(null), parent)
									
								}
								
							}
						}
						
					}
				).start();
			
		}
	}
	
}
