package com.safield.SafireRingtoneMaker;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ActLoadFile extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadfile_layout);

        final ListView listView = (ListView)findViewById(R.id.loadfile_listview);
        listView.setAdapter(new SaveInfoAdapter(this , ToneMaker.Instance().getSaveInfos()));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ToneMaker.SaveInfo saveInfo = (ToneMaker.SaveInfo)listView.getItemAtPosition(i);

                if (ToneMaker.Instance().loadStateFromFile(saveInfo.name))
                    Toast.makeText(LocalApp.getAppContext() , "Loaded "+saveInfo.name , Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(LocalApp.getAppContext() , "Failed to load "+saveInfo.name , Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }
}
