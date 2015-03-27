package com.detroitteatime.caffeinecounter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.detroitteatime.caffeinecounter.model.Drink;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class DrinkUpdater extends Activity implements OnItemSelectedListener {

    private String[] items;
    private int[] values = {-1, -1, -1, -1};
    private TextView time;
    private TextView date;
    private TextView sizePrompt;
    private DataBaseHelper helper;
    private EditText editSize;
    private EditText[] editArray = new EditText[1];
    private Spinner sizeSpinner;

    private final String SHORT = "Short";
    private final String TALL = "Tall";
    private final String GRANDE = "Grande";
    private final String VENTI = "Venti";

    private Drink drink;

    private Button setTime;
    private Button setDate;
    private Button cancel;
    private Button save;
    private Button delete;

    private long _id;
    private int year;
    private int month;
    private int day;
    private double size;
    private int mgCaffeine;
    private int pos = 0;

    private String timeString;
    private String dateString;
    private String type;

    private int hour;
    private int minute;

    private boolean isStarbucks;
    private boolean isMetric;

    private SharedPreferences settings;

    private static final int TIME_DIALOG_ID = 999;
    private static final int DATE_DIALOG_ID = 998;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_drink);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        _id = extras.getLong("_id");

        settings = getSharedPreferences("prefs", 0);
        isMetric = settings.getBoolean(DrinkDialog.METRIC, false); //check if metric (remembered in preferences)

        helper = new DataBaseHelper(this.getApplicationContext());

        helper.open(DataBaseHelper.READABLE);
        drink = helper.getDrink(_id);
        helper.close();

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        spin.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);
        items = HelperMethodHolder.setDrinkList(this.getApplicationContext());
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.my_spinner, items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);

        time = (TextView) findViewById(R.id.timeDisplay);
        date = (TextView) findViewById(R.id.dateDisplay);
        sizePrompt = (TextView) findViewById(R.id.sizePrompt);

        timeString = drink.getTime();
        dateString = drink.getDate();

        time.setText(timeString);
        date.setText(dateString);

        size = drink.getSize();

        if (isMetric && !isStarbucks) {

            size = (double) size / DrinkDialog.ozPerMl;
            sizePrompt.setText("Amount (ml)");

        }

        editSize = (EditText) findViewById(R.id.editSize);
        editArray[0] = editSize;
        editSize.setText(String.valueOf(Math.round(size)));

        type = drink.getType();
        int position = HelperMethodHolder.getPosition(items, type);
        spin.setSelection(position);

        //Determine if Starbucks, get values array

        helper.open(DataBaseHelper.READABLE);

        Cursor cursor = helper.getNLMgCaff(type);

        if (cursor.getCount() <= 0) { //is not Starbucks
            //if not starbucks make edittext visible, get caffamount and set edittext to that amount
            isStarbucks = false;

            editSize = (EditText) findViewById(R.id.editSize);
            editSize.setVisibility(View.VISIBLE);

        } else {//is Starbucks
            isStarbucks = true;
            cursor.moveToFirst();

            values[0] = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.MGCAFF8));
            values[1] = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.MGCAFF12));
            values[2] = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.MGCAFF16));
            values[3] = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.MGCAFFVENTI));

            setUpSizeSpinner();
        }

        stopManagingCursor(cursor);
        cursor.close();
        helper.close();

        setTime = (Button) findViewById(R.id.setTime);
        setTime.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);
        setTime.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);

            }
        });


        setDate = (Button) findViewById(R.id.setDate);
        setDate.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);

        setDate.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);

            }
        });

        cancel = (Button) findViewById(R.id.cancel);
        cancel.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);

        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        save = (Button) findViewById(R.id.save);
        save.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);

        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (HelperMethodHolder.checkEmpty(editArray)) {
                    insertCurrentData(_id);
                    finish();

                } else {
                    Toast.makeText(DrinkUpdater.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

        delete = (Button) findViewById(R.id.delete);
        delete.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);
        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DrinkUpdater.this);
                builder.setMessage("Are you sure you want to delete this entry?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                DataBaseHelper helper = new DataBaseHelper(DrinkUpdater.this);
                                helper.open(DataBaseHelper.WRITEABLE);
                                helper.deleteDrink(_id);
                                helper.close();
                                dialog.cancel();
                                DrinkUpdater.this.finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });


    }

    public void insertCurrentData(long id) {
        helper.open(DataBaseHelper.WRITEABLE);

        if (isStarbucks) {

            String item = (String) sizeSpinner.getSelectedItem();

            if (item.equals(SHORT)) {
                mgCaffeine = values[0];
                size = 8;

            } else if (item.equals(TALL)) {
                mgCaffeine = values[1];
                size = 12;

            } else if (item.equals(GRANDE)) {
                mgCaffeine = values[2];
                size = 16;

            } else if (item.equals(VENTI)) {
                mgCaffeine = values[3];
                size = 20;
            }

        } else if (isMetric) {

            size = Double.valueOf(editSize.getText().toString());
            size = size * DrinkDialog.ozPerMl;
            double mgPer = helper.getCaffPer(type);
            mgCaffeine = (int) (size * mgPer);

        } else {
            size = Double.valueOf(editSize.getText().toString());
            double mgPer = helper.getCaffPer(type);
            mgCaffeine = (int) Math.round(size * mgPer);

        }


        drink.setTime(timeString);
        drink.setDate(dateString);
        drink.setType(type);
        drink.setSize(size);
        drink.setMgCaffeine(mgCaffeine);

        helper.updateDrink(drink, id);
        helper.close();

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        type = items[arg2];
        helper.open(DataBaseHelper.READABLE);
        Cursor cursor = helper.getNLMgCaff(type);


        if (cursor.getCount() <= 0) { //is not Starbucks
            //if not starbucks make edittext visible, get caffamount and set edittext to that amount
            isStarbucks = false;

            editSize = (EditText) findViewById(R.id.editSize);
            editSize.setVisibility(View.VISIBLE);

            if (sizeSpinner != null) {
                sizeSpinner.setVisibility(View.GONE);

            }


        } else {//is Starbucks
            isStarbucks = true;
            cursor.moveToFirst();

            values[0] = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.MGCAFF8));
            values[1] = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.MGCAFF12));
            values[2] = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.MGCAFF16));
            values[3] = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.MGCAFFVENTI));

            setUpSizeSpinner();

            if (editSize != null) {
                editSize.setVisibility(View.GONE);
            }
        }

        cursor.close();
        helper.close();

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {


    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        helper.close();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        helper.close();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();


    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case TIME_DIALOG_ID:
                int[] timeData = parseTimeString(timeString);

                return new TimePickerDialog(this,
                        timePickerListener, timeData[0], timeData[1], false);


            case DATE_DIALOG_ID:
                Calendar mydate = new GregorianCalendar();

                Date thedate = null;
                try {
                    thedate = new SimpleDateFormat(DataBaseHelper.DATE_FORMAT, Locale.ENGLISH).parse(dateString);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mydate.setTime(thedate);

                month = mydate.get(Calendar.MONTH);
                year = mydate.get(Calendar.YEAR);
                day = mydate.get(Calendar.DATE);

                return new DatePickerDialog(this, datePickerListener,
                        year, month, day);

        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    hour = selectedHour;
                    minute = selectedMinute;

                    if (hour > 12) {
                        hour -= 12;
                        time.setText(new StringBuilder().append(String.valueOf(hour))
                                .append(":").append(pad(minute)).append("pm"));

                    } else {
                        time.setText(new StringBuilder().append(String.valueOf(hour))
                                .append(":").append(pad(minute)).append("am"));
                    }

                    timeString = (String) time.getText();

                }
            };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            dateString = HelperMethodHolder
                    .buildSQLiteDateString(selectedYear, selectedMonth, selectedDay);

            date.setText(dateString);

        }
    };

    public int[] parseTimeString(String s) {
        int hour;
        int minute;

        String[] parts = s.split(":");
        hour = Integer.valueOf(parts[0]);

        minute = Integer.valueOf(parts[1].substring(0, 2));

        String suff = parts[1].substring(2);


        int[] result = new int[2];

        result[0] = hour;
        result[1] = minute;


        if (suff.equals("pm")) {
            result[0] += 12;
        }

        return result;
    }

    public void setUpSizeSpinner() {
        sizeSpinner = (Spinner) findViewById(R.id.setAmount);
        sizeSpinner.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);
        ArrayAdapter<String> aa = null;

        int s = (int) Math.round(size);

        if (values[1] <= 0) {


            aa = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.my_spinner, new String[]{GRANDE});
            pos = 0;


        } else if (values[0] <= 0) {
            aa = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.my_spinner, new String[]{TALL, GRANDE, VENTI});


            switch (s) {
                case 12:
                    pos = 0;
                    break;

                case 16:
                    pos = 1;
                    break;

                case 20:
                    pos = 2;
                    break;

                case 24:
                    pos = 2;
                    break;

            }

        } else {
            aa = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.my_spinner, new String[]{SHORT, TALL, GRANDE, VENTI});

            switch (s) {

                case 8:
                    pos = 0;
                    break;

                case 12:
                    pos = 1;
                    break;

                case 16:
                    pos = 2;
                    break;

                case 20:
                    pos = 3;
                    break;

                case 24:
                    pos = 3;
                    break;

            }
        }


        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(aa);
        sizeSpinner.setSelection(pos, true);
        sizeSpinner.setVisibility(View.VISIBLE);


        sizeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int arg2, long arg3) {

                String item = (String) parent.getSelectedItem();

                if (item.equals(SHORT)) {
                    mgCaffeine = values[0];
                    size = 8;

                } else if (item.equals(TALL)) {
                    mgCaffeine = values[1];
                    size = 12;

                } else if (item.equals(GRANDE)) {
                    mgCaffeine = values[2];
                    size = 16;

                } else if (item.equals(VENTI)) {
                    mgCaffeine = values[3];
                    size = 20;
                } else {

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });


    }


}
