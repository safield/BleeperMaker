package com.safield.SafireRingtoneMaker;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.view.View.OnClickListener;


public class ExportActivity extends Activity {

    private EditText exportName;
    private CheckBox ringtoneCheckbox;
    private CheckBox notificationCheckbox;
    private Button exportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.export);

        exportName = (EditText)findViewById(R.id.exportText);
        ringtoneCheckbox = (CheckBox)findViewById(R.id.ringtoneCheckbox);
        notificationCheckbox = (CheckBox)findViewById(R.id.noticficationCheckbox);
        exportButton = (Button)findViewById(R.id.exportButton);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == exportButton.getId()) {

                    MainActivity.tMaker.setPattern(0);

                }
            }
        };

        exportButton.setOnClickListener(listener);
    }

}
