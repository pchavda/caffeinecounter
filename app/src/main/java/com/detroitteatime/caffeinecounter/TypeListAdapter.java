package com.detroitteatime.caffeinecounter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TypeListAdapter extends SimpleCursorAdapter {

    private boolean isMetric;
    private int layout;

    public TypeListAdapter(Context context, int layout, Cursor c,
                           String[] from, int[] to) {
        super(context, layout, c, from, to);

        this.layout = layout;

        // get preference and set isMetric
        SharedPreferences settings = context.getSharedPreferences("prefs", 0);
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
        int caffCol = cursor.getColumnIndex("mgCaffPerOzOrSht");
        int idCol = cursor.getColumnIndex("_id");

        String name = cursor.getString(nameCol);

        String total = String.valueOf(cursor.getDouble(caffCol));
        double ttl = Double.valueOf(total);

        v.setTag(cursor.getLong(idCol));

        if (isMetric) {
            // convert total to metric
            ttl = ttl * 0.033814;

            NumberFormat f = new DecimalFormat("#.000");

            total = f.format(ttl);

        } else {

            NumberFormat f = new DecimalFormat("#.0");

            total = f.format(ttl);
        }

        TextView name_text = (TextView) v.findViewById(R.id.drink_name);
        if (name_text != null) {
            name_text.setText(name);
        }

        TextView total_text = (TextView) v.findViewById(R.id.total);
        if (total_text != null) {
            total_text.setText(String.valueOf(total));
        }

    }

}
