package com.safield.SafireRingtoneMaker;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActSaveFile extends Activity {

    private Context ctx;
    private EditText inputText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savefile_layout);

        ctx = this;
        inputText = (EditText)findViewById(R.id.save_text);
        inputText.setText(ToneMaker.Instance().getSaveName());
        saveButton = (Button)findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String input = inputText.getText().toString();

                if (input.isEmpty()) {
                    Toast.makeText(ctx, "enter a save name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ToneMaker.Instance().writeStateToFile(input))
                    Toast.makeText(ctx, "File Saved", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ctx, "File Save failed", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }

}
