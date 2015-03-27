package com.detroitteatime.caffeinecounter;


import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

public class DayDrinkList extends ListActivity {
    private Cursor cursor;
    private DrinkListCursorAdapter mAdapter;
    private DataBaseHelper helper;
    private String date;
    private ProgressBar progress;
    private Button convert;
    private boolean isMetric;
    private SharedPreferences.Editor editor;
    private SharedPreferences settings;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.day_drink_list);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        date = extras.getString("date");
        TextView day = (TextView) findViewById(R.id.dateDisplay);
        day.setText(date);
        progress = (ProgressBar) findViewById(R.id.progressBar);

        settings = getSharedPreferences("prefs", 0);
        editor = settings.edit();
        isMetric = settings.getBoolean(DrinkDialog.METRIC, false);

        convert = (Button) findViewById(R.id.convert);
        convert.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR),
                PorterDuff.Mode.MULTIPLY);
        if (isMetric) {
            convert.setText(R.string.Ounces);

        } else {
            convert.setText(R.string.Milliliters);
        }

        convert.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isMetric) {
                    editor.putBoolean(DrinkDialog.METRIC, false);
                    editor.commit();
                    convert.setText(R.string.Milliliters);
                    isMetric = false;
                    mAdapter = null;
                    new QueryDatabaseTask().execute();


                } else {
                    editor.putBoolean(DrinkDialog.METRIC, true);
                    editor.commit();
                    convert.setText(R.string.Ounces);
                    isMetric = true;
                    mAdapter = null;
                    new QueryDatabaseTask().execute();


                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();

            if (this.mAdapter != null) {
                this.mAdapter.getCursor().close();
                this.mAdapter = null;
            }

            if (this.cursor != null) {
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
    protected void onStop() {
        try {
            super.onStop();

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
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        helper = new DataBaseHelper(DayDrinkList.this);

        helper.open(DataBaseHelper.READABLE);
        new QueryDatabaseTask().execute();

    }

    public void onListItemClick(ListView parent, View v, int position, long id) {

        Intent intent = new Intent(DayDrinkList.this, DrinkUpdater.class);

        long _id = (Long) v.getTag();

        intent.putExtra("_id", _id);
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
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            progress.setVisibility(View.GONE);
            startManagingCursor(cursor);

            // the desired columns to be bound
            String[] columns = new String[]{"_id", "time", "size", "type",
                    "mgCaffeine"};
            // the XML defined views which the data will be bound to
            int[] to = new int[]{R.id.time, R.id.size, R.id.drink_name,
                    R.id.total};

            // create the adapter using the cursor pointing to the desired data
            // as well as the layout information
            mAdapter = new DrinkListCursorAdapter(DayDrinkList.this,
                    R.layout.drink_row, cursor, columns, to);

            // set this adapter as your ListActivity's adapter
            setListAdapter(mAdapter);

        }

        @Override
        protected Void doInBackground(Void... params) {

            cursor = helper.getAllDrinksPerDay(date);

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();

            }
            return null;

        }

    }

}