package com.detroitteatime.caffeinecounter;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;

public class SendDialog extends Activity {


    private Button send, cancel, delete;
    private int task = 0;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send);
        progress = (ProgressBar) findViewById(R.id.progressBar);

        send = (Button) findViewById(R.id.send);
        send.getBackground().setColorFilter(Color.parseColor("#663033"), PorterDuff.Mode.MULTIPLY);
        delete = (Button) findViewById(R.id.delete);
        delete.getBackground().setColorFilter(Color.parseColor("#663033"), PorterDuff.Mode.MULTIPLY);
        cancel = (Button) findViewById(R.id.cancel);
        cancel.getBackground().setColorFilter(Color.parseColor("#663033"), PorterDuff.Mode.MULTIPLY);


        File myDir = new File(Environment.getExternalStorageDirectory() + "/Caffeine_Counter_History/Caffeine_History.csv");
        if (!myDir.exists()) {
            delete.setVisibility(View.GONE);
        }

        send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (task > 1)
                    task = 0;

                if (task == 0) {

                    new QueryDatabaseTask().execute();


                }


                if (task == 1) {


                    final Intent emailIntent = new Intent(Intent.ACTION_SEND);

                    String subjectString = "My Caffeine History";

					/* Fill it with Data */
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subjectString);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Attached is my caffeine history.");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/Caffeine_Counter_History/Caffeine_History.csv"));
                    /* Send it off to the Activity-Chooser */
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                }

                task++;

            }
        });


        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();

            }
        });


        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                File myDir = new File(Environment.getExternalStorageDirectory() + "/Caffeine_Counter_History/Caffeine_History.csv");
                if (myDir.exists()) {
                    HelperMethodHolder.deleteCSV();
                    Toast.makeText(SendDialog.this, "File deleted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SendDialog.this, "File doesn't exist.", Toast.LENGTH_SHORT).show();
                }

                finish();

            }
        });

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (task == 0) {
            send.setText("Save");
        } else {
            send.setText("Send");
        }


    }


    private class QueryDatabaseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.setVisibility(View.GONE);
            send.setText("Send File");
            delete.setText("Delete File");
            setTitle("Send File?");
            delete.setVisibility(View.VISIBLE);


        }

        @Override
        protected Void doInBackground(Void... params) {

            HelperMethodHolder.saveDrinkstoCSVFile(SendDialog.this);
            return null;

        }

    }

}
