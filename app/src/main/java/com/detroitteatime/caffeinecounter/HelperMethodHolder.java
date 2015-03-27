package com.detroitteatime.caffeinecounter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.detroitteatime.caffeinecounter.model.Drink;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class HelperMethodHolder {

    public static final String REWARDS = "rewards";
    public static final String VIBRATE = "vibrate";
    public static final String DAY_REWARDED = "day_rewarded";
    public static final String WEEK_REWARDED = "week_rewarded";
    public static final String MONTH_REWARDED = "month_rewarded";
    public static final String NINETY_DAYS_REWARDED = "ninety_days_rewarded";
    public static final String SIX_MONTH_REWARDED = "six_month_rewarded";
    public static final String YEAR_REWARDED = "year_rewarded";
    public static final String NONE = "2000-01-01";
    public static final String BUTTON_COLOR = "#623f23";
    public static final String BUTTON_COLOR_BLUE = "#00c8ff";


    public static int maxOverPeriod(int period, DataBaseHelper helper) {
        int max = 0;

        Cursor cursor = helper.getMaxSoManyDaysInThePast(period);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            max = cursor.getInt(cursor.getColumnIndex("sum(mgCaffeine)"));


        }

        cursor.close();
        return max;
    }

    public static String lastTimeOver(Context context, DataBaseHelper helper, int limit) {
        String lastDate = null;
        helper.open(0);
        Cursor cursor = helper.getDaysOver(limit);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            lastDate = cursor.getString(0);


        } else {
            SharedPreferences settings = context.getSharedPreferences("prefs", 0);
            String defaultLastDate = DateFormat.format(DataBaseHelper.DATE_FORMAT, new Date()).toString();

            lastDate = settings.getString("start_date", defaultLastDate);

            if (lastDate.equals(defaultLastDate)) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("start_date", defaultLastDate);
                editor.commit();

            }

        }

        helper.close();
        cursor.close();
        //Log.i("my test", "date: " + lastDate);
        return lastDate;

    }

    public static Date lastTimeOver(Context context, int limit) {
        //Log.i("my test", "called lastTimeOver");
        DataBaseHelper helper = new DataBaseHelper(context);
        helper.open(DataBaseHelper.READABLE);
        String lastDate = lastTimeOver(context, helper, limit);

        Date lDate = getDateFromString(lastDate);
        return lDate;

    }

    public static Date earliestDate(DataBaseHelper helper) {

        Cursor cursor = helper.getEarliestDate();
        String d = null;
        Date date = null;

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            d = cursor.getString(cursor.getColumnIndex("date"));


            try {
                date = new SimpleDateFormat(DataBaseHelper.DATE_FORMAT).parse(d);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        cursor.close();
        return date;
    }


    public static boolean inflateMenu(Activity activity, Menu menu) {
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.set_rewards);

        SharedPreferences preferences = activity.getApplicationContext().getSharedPreferences("prefs", 0);

        boolean rewards = preferences.getBoolean(HelperMethodHolder.REWARDS, true);

        if (rewards) {

            item.setTitle("Disable Rewards");

        } else {

            item.setTitle("Enable Rewards");

        }

        item = menu.findItem(R.id.vibrate);
        boolean vibrate = preferences.getBoolean(HelperMethodHolder.VIBRATE, true);

        if (vibrate) {

            item.setTitle("Disable Vibrate");

        } else {

            item.setTitle("Enable Vibrate");

        }

        item = menu.findItem(R.id.units);
        boolean isMetric = preferences.getBoolean(DrinkDialog.METRIC, true);

        if (isMetric) {

            item.setTitle("Change to Non-Metric");

        } else {

            item.setTitle("Change to Metric");

        }

        return true;
    }

    public static boolean setMenuItemClicks(final Context context, MenuItem item) {

        switch (item.getItemId()) {

            case R.id.home:
                Intent intent1 = new Intent(
                        context,
                        CaffeineCounterActivity.class);

                context.startActivity(intent1);

                break;


            case R.id.reset_all:


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to reset to factory settings (delete all entries, new drink types, and preferences)?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                DataBaseHelper helper = new DataBaseHelper(context);
                                helper.open(DataBaseHelper.WRITEABLE);
                                helper.deleteEverything(context);
                                helper.close();


                                Intent intent4 = new Intent(
                                        context,
                                        CaffeineCounterActivity.class
                                );
                                context.startActivity(intent4);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


                break;

            case R.id.units:
                SharedPreferences settings = context.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor editor = settings.edit();


                boolean isMetric = settings.getBoolean(DrinkDialog.METRIC, false);

                if (isMetric) {
                    editor.putBoolean(DrinkDialog.METRIC, false);

                    item.setTitle("Change to Metric");

                } else {
                    editor.putBoolean(DrinkDialog.METRIC, true);
                    item.setTitle("Change to Non-Metric");

                }
                editor.commit();

                break;

            case R.id.change_limit:
                Intent intent5 = new Intent(
                        context,
                        LimitDialog.class
                );
                context.startActivity(intent5);

                break;

            case R.id.history:
                Intent intent6 = new Intent(
                        context,
                        DayList.class);

                context.startActivity(intent6);

                break;

            case R.id.info:
                Intent intent7 = new Intent(
                        context,
                        Info.class);

                context.startActivity(intent7);

                break;


            case R.id.edit_type:
                Intent intent8 = new Intent(
                        context,
                        TypeList.class);

                context.startActivity(intent8);

                break;

            case R.id.save_to_file:
                Intent intent9 = new Intent(
                        context,
                        SendDialog.class);

                context.startActivity(intent9);

                break;

            case R.id.set_rewards:
                SharedPreferences preferences = context.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor editor2 = preferences.edit();


                boolean rewards = preferences.getBoolean("rewards", true);

                if (rewards) {
                    editor2.putBoolean("rewards", false);

                    item.setTitle("Enable Rewards");

                } else {
                    editor2.putBoolean("rewards", true);
                    item.setTitle("Disable Rewards");

                }
                editor2.commit();

                break;

            case R.id.vibrate:
                SharedPreferences preferences1 = context.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor editor1 = preferences1.edit();


                boolean vibrate = preferences1.getBoolean(HelperMethodHolder.VIBRATE, true);

                if (vibrate) {
                    editor1.putBoolean(HelperMethodHolder.VIBRATE, false);

                    item.setTitle("Enable Vibrate");

                } else {
                    editor1.putBoolean(HelperMethodHolder.VIBRATE, true);
                    item.setTitle("Disable Vibtrate");

                }
                editor1.commit();

                break;


        }
        return true;
    }


    public static Drink addDrink(double size, String type, int mgCaffeine, DataBaseHelper helper) {

        String currentDate = DateFormat.format(DataBaseHelper.DATE_FORMAT, new Date()).toString();
        String timeString = DateFormat.format("h:mmaa", new Date()).toString();

        Drink drink = new Drink(size, type, currentDate, timeString, mgCaffeine);

        helper.open(DataBaseHelper.WRITEABLE);

        drink = helper.insertDrink(drink);

        helper.close();

        return drink;

    }


    public static String[] setDrinkList(Context context) {
        DataBaseHelper helper = new DataBaseHelper(context);
        helper.open(DataBaseHelper.READABLE);
        Cursor cursor = helper.getDrinkTypes();
        String[] items = new String[cursor.getCount()];


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String type = cursor.getString(cursor.getColumnIndex("type"));
            items[cursor.getPosition()] = type;

        }
        cursor.close();
        helper.close();

        return items;

    }

    public static String[] getCategoryList(Context context, DataBaseHelper helper) {

        Cursor cursor = helper.getCategories();
        String[] items = new String[cursor.getCount()];


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String category = cursor.getString(cursor.getColumnIndex(DataBaseHelper.CATEGORY));
            items[cursor.getPosition()] = category;

        }
        cursor.close();


        return items;

    }

    public static int getPosition(String[] array, String s) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(s)) {
                return (i);
            }
        }
        return (-1);
    }


    public static void saveDrinkstoCSVFile(Context context) {

        DataBaseHelper helper = new DataBaseHelper(context);
        helper.open(DataBaseHelper.READABLE);

        Cursor cursor = helper.getAllDrinks();
        BufferedWriter writer = null;
        File myDir = new File(Environment.getExternalStorageDirectory(), "Caffeine_Counter_History");
        myDir.mkdirs();

        File file = new File(myDir.getAbsolutePath(), "Caffeine_History.csv");


        try {
            writer = new BufferedWriter(new FileWriter(file));

            writer.write("Id");
            writer.write(",");
            writer.write("Time");
            writer.write(",");
            writer.write("Date");
            writer.write(",");
            writer.write("Type");
            writer.write(",");
            writer.write("Size");
            writer.write(",");
            writer.write("mg Caffeine");
            writer.write('\n');

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex("_id"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                int mgCaff = cursor.getInt(cursor.getColumnIndex("mgCaffeine"));
                int size = cursor.getInt(cursor.getColumnIndex("size"));

                writer.write(String.valueOf(id));
                writer.write(",");
                writer.write(time);
                writer.write(",");
                writer.write(date);
                writer.write(",");
                writer.write(type);
                writer.write(",");
                writer.write(String.valueOf(size));
                writer.write(",");
                writer.write(String.valueOf(mgCaff));
                writer.write('\n');

            }

            writer.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        cursor.close();
        helper.close();

    }

    public static void deleteCSV() {

        File myDir = new File(Environment.getExternalStorageDirectory() + "/Caffeine_Counter_History");

        deleteRecursive(myDir);

    }

    public static String buildSQLiteDateString(int year, int month, int day) {
        String dateString = null;
        if (Integer.valueOf(month) < 10 && Integer.valueOf(day) < 10) {
            dateString = new StringBuilder()
                    .append(year)
                    .append("-")
                    .append("0")
                    .append(month + 1)
                    .append("-")
                    .append("0")
                    .append(day)
                    .toString();


        } else if (Integer.valueOf(month) < 10) {
            dateString = new StringBuilder()
                    .append(year)
                    .append("-")
                    .append("0")
                    .append(month + 1)
                    .append("-")
                    .append(day)
                    .toString();


        } else if (Integer.valueOf(day) < 10) {
            dateString = new StringBuilder()
                    .append(year)
                    .append("-")
                    .append(month + 1)
                    .append("-")
                    .append("0")
                    .append(day)
                    .toString();


        } else {
            dateString = new StringBuilder()
                    .append(year)
                    .append("-")
                    .append(month + 1)
                    .append("-")
                    .append(day)
                    .toString();

        }

        return dateString;

    }


    public static void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

    }

    public static String getCategory(String typeName, DataBaseHelper helper) {
        Cursor cursor = helper.getDrinkCategory(typeName);

        cursor.moveToFirst();

        return cursor.getString(cursor.getColumnIndex(DataBaseHelper.CATEGORY));

    }

    public static void clearRewards(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("prefs", 0);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(DAY_REWARDED, NONE);
        editor.putString(WEEK_REWARDED, NONE);
        editor.putString(MONTH_REWARDED, NONE);
        editor.putString(NINETY_DAYS_REWARDED, NONE);
        editor.putString(SIX_MONTH_REWARDED, NONE);
        editor.putString(YEAR_REWARDED, NONE);
        editor.commit();

    }

    public static long getTimeFromString(String s) {
        SimpleDateFormat df = new SimpleDateFormat(DataBaseHelper.DATE_FORMAT);
        Date today;
        long result = -1;
        try {
            today = df.parse(s);
            result = today.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Date getDateFromString(String s) {

        SimpleDateFormat df = new SimpleDateFormat(DataBaseHelper.DATE_FORMAT);
        Date today = null;
        try {
            today = df.parse(s);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return today;

    }

    public static boolean checkEmpty(ArrayList<EditText> etText) {

        for (int i = 0; i < etText.size(); i++) {
            String text = etText.get(i).getText().toString();

            if (text.length() == 0 || text.equals("")) {

                return false;
            }

        }

        return true;

    }

    public static boolean checkEmpty(EditText[] etText) {

        for (int i = 0; i < etText.length; i++) {
            EditText e = etText[i];
            String text = e.getText().toString();

            if (text.length() == 0 || text.equals("")) {

                return false;
            }

        }

        return true;

    }


}
