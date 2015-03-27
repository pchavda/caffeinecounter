package com.detroitteatime.caffeinecounter;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class DrinkListCursorAdapter extends SimpleCursorAdapter {
    private boolean isMetric;
    private int layout;

    @SuppressWarnings("deprecation")
    public DrinkListCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        this.layout = layout;
        SharedPreferences settings;
        settings = context.getSharedPreferences("prefs", 0);

        isMetric = settings.getBoolean(DrinkDialog.METRIC, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(layout, parent, false);

        setTextsToView(v, cursor);

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

        setTextsToView(v, c);

    }

    public void setTextsToView(View v, Cursor cursor) {

        int nameCol = cursor.getColumnIndex("type");
        int timeCol = cursor.getColumnIndex("time");
        int sizeCol = cursor.getColumnIndex("size");
        int caffCol = cursor.getColumnIndex("mgCaffeine");
        int idCol = cursor.getColumnIndex("_id");

        String name = cursor.getString(nameCol);
        String time = cursor.getString(timeCol);
        Double s = cursor.getDouble(sizeCol);
        String size;
        String total = String.valueOf(cursor.getInt(caffCol));

        v.setTag(cursor.getLong(idCol));

        if (name.contains("Espresso")) {
            size = String.valueOf(Math.round(s)) + " shots";
        } else if (isMetric) {
            size = String.valueOf((int) Math.round(Double.valueOf(s) / DrinkDialog.ozPerMl)) + " ml";
        } else {
            size = String.valueOf(Math.round(s)) + " oz";
        }

        /**
         * Next set the name of the entry.
         */
        TextView name_text = (TextView) v.findViewById(R.id.drink_name);
        if (name_text != null) {
            name_text.setText(name);
        }

        TextView time_text = (TextView) v.findViewById(R.id.time);
        if (time_text != null) {
            time_text.setText(time);
        }

        TextView size_text = (TextView) v.findViewById(R.id.size);
        if (size_text != null) {
            size_text.setText(size);
        }

        TextView total_text = (TextView) v.findViewById(R.id.total);
        if (total_text != null) {
            total_text.setText(total);
        }


    }


}
