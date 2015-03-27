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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class TypeList extends ListActivity {
    private Cursor cursor;
    private SimpleCursorAdapter mAdapter;
    private DataBaseHelper helper;
    private Button add;
    private ProgressBar progress;
    private boolean isMetric;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.type_list);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        TextView tv = (TextView) findViewById(R.id.caffeineAmount);

        SharedPreferences settings = getSharedPreferences("prefs", 0);
        isMetric = settings.getBoolean(DrinkDialog.METRIC, false);

        if (isMetric) {
            tv.setText("mg Caffeine per ml");
        } else {
            tv.setText("mg Caffeine per oz");
        }


        add = (Button) findViewById(R.id.AddType);
        add.getBackground().setColorFilter(Color.parseColor("#663033"), PorterDuff.Mode.MULTIPLY);
        add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        TypeList.this,
                        NewTypeDialog.class);
                startActivity(intent);
            }
        });


    }

    private void closeDownDB() {
        try {

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
            error.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onResume();
        new QueryDatabaseTask().execute();

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onPause();
        closeDownDB();
    }

    public void onListItemClick(ListView parent, View v,
                                int position, long id) {

        //determine if a starbucks drink

        TextView text = (TextView) v.findViewById(R.id.drink_name);
        String type = text.getText().toString();

        int[] caffInfo = new int[4];

        helper.open(DataBaseHelper.READABLE);

        Cursor c = helper.getNLMgCaff(type);

        if (c.getCount() != 0) {
            c.moveToFirst();
            caffInfo[0] = c.getInt(c.getColumnIndex(DataBaseHelper.MGCAFF8));
            caffInfo[1] = c.getInt(c.getColumnIndex(DataBaseHelper.MGCAFF12));
            caffInfo[2] = c.getInt(c.getColumnIndex(DataBaseHelper.MGCAFF16));
            caffInfo[3] = c.getInt(c.getColumnIndex(DataBaseHelper.MGCAFFVENTI));

            Intent intent = new Intent(TypeList.this, EditTypeStarbucks.class);
            intent.putExtra(DataBaseHelper.MGCAFF, caffInfo);
            intent.putExtra(DataBaseHelper.TYPE, type);
            startActivity(intent);
            finish();


        } else {
            Intent intent = new Intent
                    (TypeList.this,
                            EditType.class);

            intent.putExtra("type", type);
            startActivity(intent);

        }

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
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();

            }

            startManagingCursor(cursor);

            // the desired columns to be bound
            String[] columns = new String[]{"type", "mgCaffPerOzOrSht"};
            //the XML defined views which the data will be bound to
            int[] to = new int[]{R.id.date, R.id.total};

            // create the adapter using the cursor pointing to the desired data as well as the layout information
            mAdapter = new TypeListAdapter(TypeList.this, R.layout.type_row, cursor, columns, to);

            // set this adapter as your ListActivity's adapter
            TypeList.this.setListAdapter(mAdapter);

            // set this adapter as your ListActivity's adapter
            setListAdapter(mAdapter);
            progress.setVisibility(View.GONE);

        }


        @Override
        protected Void doInBackground(Void... params) {

            helper = new DataBaseHelper(TypeList.this);
            helper.open(DataBaseHelper.READABLE);
            cursor = helper.getTypesByCategory(DataBaseHelper.ALL);
            return null;


        }

    }


}