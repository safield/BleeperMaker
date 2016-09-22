package com.safield.SafireRingtoneMaker;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ActLoadFile extends Activity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadfile_layout);

        mListView = (ListView)findViewById(R.id.loadfile_listview);
        mListView.setAdapter(new SaveInfoAdapter(this , ToneMaker.Instance().getSaveInfos()));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ToneMaker.SaveInfo saveInfo = (ToneMaker.SaveInfo)mListView.getItemAtPosition(i);

                if (ToneMaker.Instance().loadStateFromFile(saveInfo.name))
                    Toast.makeText(LocalApp.getAppContext() , "Loaded "+saveInfo.name , Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(LocalApp.getAppContext() , "Failed to load "+saveInfo.name , Toast.LENGTH_LONG).show();

                finish();
            }
        });



        /* TODO: Implement long click deletion of save files
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                arg1.setBackgroundColor(0xffcfcfcf);
                return true;
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.load_file_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete_all:
                ToneMaker.Instance().deleteAllSaves();
                mListView.setAdapter(new SaveInfoAdapter(this , ToneMaker.Instance().getSaveInfos()));
                Toast.makeText(this, "Deleted all saves" , Toast.LENGTH_SHORT).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
