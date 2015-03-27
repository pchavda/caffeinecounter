package com.detroitteatime.caffeinecounter;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DrinkSelector extends ListActivity {
    private Cursor cursor;
    private SimpleCursorAdapter mAdapter;
    private DataBaseHelper helper;
    private int queryType;
    public static final int ADD_TYPE = -1;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.soda_list);

        Intent intent = getIntent();
        queryType = intent.getIntExtra(DataBaseHelper.QUERY_TYPE, 0);

        Button notFound = (Button) findViewById(R.id.search);
        notFound.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);
        notFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrinkSelector.this, AddDrinkView.class);
                intent.putExtra(DataBaseHelper.QUERY_TYPE, queryType);

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

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        helper = new DataBaseHelper(this);
        helper.open(DataBaseHelper.READABLE);

        cursor = helper.getTypesByCategory(queryType);

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();

            String t = cursor.getString(cursor
                    .getColumnIndex(DataBaseHelper.TYPE));

            Cursor caffCursor = helper.getNLMgCaff(t);

            toDrinkDialog(caffCursor, t);

        } else if (cursor.getCount() != 0) {
            cursor.moveToFirst();

        } else {
            Toast.makeText(this, "No drinks in this category.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        startManagingCursor(cursor);

        // the desired columns to be bound
        String[] columns = new String[]{"type"};
        // the XML defined views which the data will be bound to
        int[] to = new int[]{R.id.date};

        // create the adapter using the cursor pointing to the desired data as
        // well as the layout information
        mAdapter = new SimpleCursorAdapter(this, R.layout.selector_row, cursor,
                columns, to);

        // set this adapter as your ListActivity's adapter
        this.setListAdapter(mAdapter);

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        closeDownDB();
    }

    public void onListItemClick(ListView parent, View v, int position, long id) {

        TextView text = (TextView) v.findViewById(R.id.date);
        String type = text.getText().toString();

        helper.open(DataBaseHelper.READABLE);
        cursor = helper.getNLMgCaff(type);

        toDrinkDialog(cursor, type);

    }

    public void toDrinkDialog(Cursor cursor, String type) {

        double mgPer;
        Intent intent;
        int[] caffInfo = new int[4];
        if (cursor.getCount() == 0) {
            mgPer = helper.getCaffPer(type);

            intent = new Intent(DrinkSelector.this, DrinkDialog.class);
            intent.putExtra(DataBaseHelper.MGCAFF, mgPer);
            intent.putExtra(DataBaseHelper.TYPE, type);
            startActivity(intent);

        } else {

            cursor.moveToFirst();
            caffInfo[0] = cursor.getInt(cursor
                    .getColumnIndex(DataBaseHelper.MGCAFF8));
            caffInfo[1] = cursor.getInt(cursor
                    .getColumnIndex(DataBaseHelper.MGCAFF12));
            caffInfo[2] = cursor.getInt(cursor
                    .getColumnIndex(DataBaseHelper.MGCAFF16));
            caffInfo[3] = cursor.getInt(cursor
                    .getColumnIndex(DataBaseHelper.MGCAFFVENTI));

            boolean isFrozen = false;

            if (!cursor.isNull(cursor.getColumnIndex(DataBaseHelper.IS_FROZEN))
                    && cursor.getInt(cursor
                    .getColumnIndex(DataBaseHelper.IS_FROZEN)) == 1) {
                isFrozen = true;
            }

            intent = new Intent(DrinkSelector.this, DrinkDialogNonLinear.class);
            intent.putExtra(DataBaseHelper.MGCAFF, caffInfo);
            intent.putExtra(DataBaseHelper.TYPE, type);
            intent.putExtra(DataBaseHelper.IS_FROZEN, isFrozen);
            startActivity(intent);
            finish();

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

}
