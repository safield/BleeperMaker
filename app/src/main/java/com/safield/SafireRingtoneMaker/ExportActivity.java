package com.safield.SafireRingtoneMaker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;


public class ExportActivity extends Activity {


    private EditText exportName;
    private CheckBox ringtoneCheckbox;
    private CheckBox notificationCheckbox;
    private Button exportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.export);

        final Context thisCtx = this;
        exportName = (EditText)findViewById(R.id.exportText);
        exportButton = (Button)findViewById(R.id.exportButton);

        ringtoneCheckbox = (CheckBox)findViewById(R.id.ringtoneCheckbox);
        notificationCheckbox = (CheckBox)findViewById(R.id.noticficationCheckbox);
        ringtoneCheckbox.setChecked(true);
        notificationCheckbox.setChecked(true);

        exportName.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        exportToFile();
                        return false;
                    }
                }
        );

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == exportButton.getId()) {

                    exportToFile();
                }
            }
        };

        OnClickListener checkListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getId() == notificationCheckbox.getId()) {

                    // at least one must be check at all times
                    if(!notificationCheckbox.isChecked() && !notificationCheckbox.isChecked())
                        ringtoneCheckbox.setChecked(true);
                }
                else if(v.getId() == ringtoneCheckbox.getId()) {

                    // at least one must be check at all times
                    if(!ringtoneCheckbox.isChecked() && !notificationCheckbox.isChecked())
                        notificationCheckbox.setChecked(true);
                }
            }
        };

        exportButton.setOnClickListener(listener);
        ringtoneCheckbox.setOnClickListener(checkListener);
        notificationCheckbox.setOnClickListener(checkListener);
    }

    private void exportToFile()
    {
        String text = exportName.getText().toString();
        text = text.replace("\n", "").replace("\r", "");

        if(text.length() < 1) {
            Toast.makeText(this, "Enter a name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(ringtoneCheckbox.isChecked())
            MainActivity.tMaker.generateOutTrack( text , ToneMaker.RINGTONE);

        if(notificationCheckbox.isChecked())
            MainActivity.tMaker.generateOutTrack( text , ToneMaker.NOTIFICATION);

        Toast.makeText(this, "Tone exported successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
