package com.detroitteatime.caffeinecounter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Reward extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward);

        Intent intent = this.getIntent();

        String time = intent.getStringExtra("timeUnder");
        String title = intent.getStringExtra("title");

        TextView lengthUnder = (TextView) findViewById(R.id.lengthUnder);
        TextView titleView = (TextView) findViewById(R.id.title);

        lengthUnder.setText("for " + time + " under your limit.\nYou are a ");
        titleView.setText(title + "!");

        Button thanks = (Button) findViewById(R.id.thanks);

        thanks.getBackground().setColorFilter(Color.parseColor("#d0eed0"), PorterDuff.Mode.MULTIPLY);

        thanks.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();

            }
        });
    }


}
