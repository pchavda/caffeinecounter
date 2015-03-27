package com.detroitteatime.caffeinecounter;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DayList extends ListActivity {
    private Cursor cursor;
    private SimpleCursorAdapter mAdapter;
    private DataBaseHelper helper;
    private ProgressBar progress;
    private Button plot;
    private int dataPoints;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.day_list);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        plot = (Button) findViewById(R.id.plot);
        plot.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR),
                PorterDuff.Mode.MULTIPLY);

        plot.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (dataPoints > 1) {
                    Intent intent = new Intent(DayList.this, Plot.class);

                    startActivity(intent);
                } else {
                    Toast.makeText(DayList.this,
                            "Must have at least two days of data to plot.",
                            Toast.LENGTH_LONG).show();
                }

            }

        });

    }

    @Override
    protected void onPause() {
        try {
            super.onPause();

            if (this.mAdapter != null) {
                this.mAdapter.getCursor().close();
                this.mAdapter = null;
            }

            if (this.cursor != null) {
                stopManagingCursor(cursor);
                this.cursor.close();
            }

            if (this.helper != null) {
                this.helper.close();
            }

        } catch (Exception error) {
            /** Error Handler Code **/
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        new QueryDatabaseTask().execute();
    }

    public void onListItemClick(ListView parent, View v, int position, long id) {

        Intent intent = new Intent(DayList.this, DayDrinkList.class);

        TextView text = (TextView) v.findViewById(R.id.date);
        String date = text.getText().toString();

        intent.putExtra("date", date);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return HelperMethodHolder.inflateMenu(this, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return HelperMethodHolder.setMenuItemClicks(this, item);
    }

    private class QueryDatabaseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Void result) {
            dataPoints = cursor.getCount();

            if (dataPoints != 0) {
                cursor.moveToFirst();

            }

            startManagingCursor(cursor);

            // the desired columns to be bound
            String[] columns = new String[]{"date", "sum(mgCaffeine)"};
            // the XML defined views which the data will be bound to
            int[] to = new int[]{R.id.date, R.id.total};

            // create the adapter using the cursor pointing to the desired data
            // as well as the layout information
            mAdapter = new SimpleCursorAdapter(DayList.this, R.layout.row,
                    cursor, columns, to);

            // set this adapter as your ListActivity's adapter
            setListAdapter(mAdapter);
            progress.setVisibility(View.GONE);

        }

        @Override
        protected Void doInBackground(Void... params) {

            helper = new DataBaseHelper(DayList.this);

            helper.open(DataBaseHelper.READABLE);
            cursor = helper.getAllDays("DESC");

            return null;

        }

    }

}