package com.detroitteatime.caffeinecounter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LimitDialog extends Activity {
    private EditText limit;
    private EditText[] editArray = new EditText[1];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.limit_dialog);

        Button button = (Button) findViewById(R.id.Finish);
        limit = (EditText) findViewById(R.id.Amount);
        editArray[0] = limit;
        button.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (HelperMethodHolder.checkEmpty(editArray)) {
                    SharedPreferences settings = getSharedPreferences("prefs", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("limit", Integer.valueOf(limit.getText().toString()));
                    editor.commit();
                    finish();

                } else {
                    Toast.makeText(LimitDialog.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}

