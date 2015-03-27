package com.detroitteatime.caffeinecounter;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


public class CaffeineCounterActivity extends Activity {
    private int mgCaffeine;
    private TextView amount;
    private TextView limitAmount;

    private int limit;
    private TextView dateInfo;
    private DataBaseHelper dbHelper;
    private int CAFFEINE_LIMIT = 300;
    private String currentDate;
    private Dialog dialog;
    private CheckBox checkbox;
    private SharedPreferences.Editor editor;
    private SharedPreferences settings;
    private Typeface cursive;
    private Date lastDateOver;
    private boolean thirty;
    private boolean ninety;
    private boolean halfYear;
    private boolean year;
    private boolean week;

    private boolean vibrate = false;
    private boolean vibratePrefOk;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Possible work around for market launches. See http://code.google.com/p/android/issues/detail?id=2373
        // for more details. Essentially, the market launches the main activity on top of other activities.
        // we never want this to happen. Instead, we check if we are the root and if not, we finish.
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {

                finish();
                return;
            }
        }

        settings = getSharedPreferences("prefs", 0);
        editor = settings.edit();
        dbHelper = new DataBaseHelper(this);

        limitAmount = (TextView) findViewById(R.id.Limit);

        dateInfo = (TextView) findViewById(R.id.DateNumber);


        amount = (TextView) findViewById(R.id.TotalCaffeine);


        //Typeface font = Typeface.createFromAsset(getAssets(), "petit.ttf");

        //total.setTypeface(font);
        //amount.setTypeface(font);


        Button reset = (Button) findViewById(R.id.Reset);
        reset.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);
        reset.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mgCaffeine = 0;
                amount.setText(String.valueOf(0) + " mg");

                amount.setTextColor(getResources().getColor(R.color.dark_green));
                amount.postInvalidate();

                //change database for the day
                //change days table
                String today = DateFormat.format(DataBaseHelper.DATE_FORMAT, new Date()).toString();
                dbHelper.open(DataBaseHelper.WRITEABLE);
                dbHelper.deleteAllDrinksPerDay(today);
                dbHelper.close();


            }
        });

        Button coffee = (Button) findViewById(R.id.Coffee);

        coffee.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        CaffeineCounterActivity.this,
                        DrinkSelector.class);

                intent.putExtra(DataBaseHelper.QUERY_TYPE, DataBaseHelper.QUERY_COFFEES);

                startActivity(intent);

            }
        });

        Button espresso = (Button) findViewById(R.id.Blended);

        espresso.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        CaffeineCounterActivity.this,
                        DrinkSelector.class);
                intent.putExtra(DataBaseHelper.QUERY_TYPE, DataBaseHelper.QUERY_BLENDED);

                startActivity(intent);

            }
        });

        Button blackTea = (Button) findViewById(R.id.Tea);

        blackTea.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        CaffeineCounterActivity.this,
                        DrinkSelector.class);
                intent.putExtra(DataBaseHelper.QUERY_TYPE, DataBaseHelper.QUERY_TEAS);

                startActivity(intent);

            }
        });

        Button energy = (Button) findViewById(R.id.Energy);

        energy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        CaffeineCounterActivity.this,
                        DrinkSelector.class);
                intent.putExtra(DataBaseHelper.QUERY_TYPE, DataBaseHelper.QUERY_ENERGY);

                startActivity(intent);

            }
        });

        Button soda = (Button) findViewById(R.id.Soda);

        soda.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        CaffeineCounterActivity.this,
                        DrinkSelector.class);
                intent.putExtra(DataBaseHelper.QUERY_TYPE, DataBaseHelper.QUERY_SODA);

                startActivity(intent);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();


        Eula.showEula(this);
        vibratePrefOk = settings.getBoolean(HelperMethodHolder.VIBRATE, true);
        //Log.i("My Code" , "Vibrate: " + String.valueOf(vibratePrefOk));

        vibrate = false;
        currentDate = DateFormat.format(DataBaseHelper.DATE_FORMAT, new Date()).toString();
        dateInfo.setText(currentDate);

       /*if(dateInfo.getTag() != null){
            String layoutTag = dateInfo.getTag().toString();
            Toast.makeText(this, "layout tag: " + layoutTag, Toast.LENGTH_LONG).show();
        }*/


        limit = settings.getInt("limit", CAFFEINE_LIMIT);
        mgCaffeine = 0;

        dbHelper.open(DataBaseHelper.READABLE);

        Cursor cursor = dbHelper.getDay(currentDate);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            mgCaffeine = cursor.getInt(cursor.getColumnIndex("sum(mgCaffeine)"));

        }

        cursor.close();

        amount.setText(String.valueOf(mgCaffeine) + " mg");

        limitAmount.setText(String.valueOf(limit) + " mg/day");


        amount.addTextChangedListener(new TextWatcher() {
            int before;
            int after;

            @Override
            public void afterTextChanged(Editable s) {
                after = Integer.valueOf(s.toString().substring(0, s.length() - 3));

                if (after > before && vibratePrefOk) {

                    vibrate = true;

                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                before = Integer.valueOf(s.toString().substring(0, s.length() - 3));
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });


        if (mgCaffeine > limit) {
            this.amount.setTextColor(Color.RED);


            if (vibrate) {
                Vibrator vibe = (Vibrator) CaffeineCounterActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(500);
            }


        } else {
            this.amount.setTextColor(getResources().getColor(R.color.dark_green));


            try {
                determineRewards();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }

        dbHelper.close();

    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return HelperMethodHolder.inflateMenu(this, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return HelperMethodHolder.setMenuItemClicks(this, item);
    }

    public void determineRewards() throws ParseException {

        boolean rewards = settings.getBoolean(HelperMethodHolder.REWARDS, true);

        //HelperMethodHolder.clearRewards(this);//remove after testing!!!

        if (!rewards)
            return;

        lastDateOver = HelperMethodHolder.lastTimeOver(this.getApplicationContext(), limit);

        week = checkIfRewardedInPeriod(-7, lastDateOver);
        thirty = checkIfRewardedInPeriod(-30, lastDateOver);
        ninety = checkIfRewardedInPeriod(-90, lastDateOver);
        halfYear = checkIfRewardedInPeriod(-180, lastDateOver);
        year = checkIfRewardedInPeriod(-360, lastDateOver);


		/*Log.i("My Code", "rewarded 30:" + thirty);
        Log.i("My Code", "rewarded 90:" + ninety);
		Log.i("My Code", "rewarded 180:" + halfYear);
		Log.i("My Code", "rewarded 360:" + year);*/


        if (checkRewardForTimePeriod(-360, lastDateOver)) {
            giveReward("1 year", "Caffeine Superhero");
            return;
        }
        if (checkRewardForTimePeriod(-180, lastDateOver) && year) {
            giveReward("6 months", "Caffeine Hero");
            return;
        }
        if (checkRewardForTimePeriod(-90, lastDateOver) && halfYear && year) {
            giveReward("90 days", "Caffeine Victor");
            return;
        }
        if (checkRewardForTimePeriod(-30, lastDateOver) && ninety && halfYear && year) {
            giveReward("30 days", "Caffeine Challenger");
            return;
        }

        if (checkRewardForTimePeriod(-7, lastDateOver) && thirty && ninety && halfYear && year) {
            giveReward("1 week", "Caffeine Competitor");
            return;
        }

    }

    public boolean checkRewardForTimePeriod(int days, Date lastDateOver) {

        String beginString = settings.getString("start_date", null);
        Date beginDate = HelperMethodHolder.getDateFromString(beginString);

        Calendar cal = Calendar.getInstance();
        Date periodBegin;

        //Check year

        cal.add(Calendar.DATE, days);
        periodBegin = cal.getTime();

        String setRewardString = getRewardStrings(days)[0];
        ;

        //check if lastDateOver is before the period and after the last time rewarded

			/*Log.i("My Code", "Days: " + days + " Period begin " + periodBegin.toString() + " Begin date: " + beginDate.toString() +
                    "last Date Over: "+ lastDateOver.toString() + " Rewarded: " + checkIfRewardedInPeriod(days, lastDateOver)); */

        boolean notRewarded = true;

        switch (days) {

            case -360:
                notRewarded = year;
                break;

            case -180:
                notRewarded = halfYear;
                break;

            case -90:
                notRewarded = ninety;
                break;

            case -30:
                notRewarded = thirty;
                break;

            case -7:
                notRewarded = week;
                break;

            default:
                break;

        }

        if (beginDate.before(periodBegin) && lastDateOver.before(periodBegin) && notRewarded) {

            editor.putString(setRewardString, currentDate);
            editor.commit();
            return true;
        } else {
            return false;
        }
    }

    public boolean checkIfRewardedInPeriod(int days, Date lastDateOver) {


        String given = getRewardStrings(days)[1];

        Date lastTimeGiven = null;
        if (given != null)
            lastTimeGiven = HelperMethodHolder.getDateFromString(given);

        //Log.i("My Code", "checIfRewardedInPeriod: given: " + lastTimeGiven + " lastDateOver: " + lastDateOver + " days: " + days);

        //check if lastDateOver is before the period and after the last time rewarded

        if (lastTimeGiven.before(lastDateOver) || lastTimeGiven.equals(lastDateOver)) {
            //Log.i("My Code", "checIfRewardedInPeriod: value: true" );
            return true;

        } else {

            //Log.i("My Code", "checIfRewardedInPeriod: value: false" );
            return false;

        }
    }

    public String[] getRewardStrings(int days) {
        String[] result = new String[2];
        String given = null;
        String setRewardString = null;

        switch (days) {

            case -360:
                setRewardString = HelperMethodHolder.YEAR_REWARDED;
                given = settings.getString(setRewardString, HelperMethodHolder.NONE);
			/*Log.i("My Code", "rewarded 360:" + given);*/
                break;

            case -180:
                setRewardString = HelperMethodHolder.SIX_MONTH_REWARDED;
                given = settings.getString(setRewardString, HelperMethodHolder.NONE);
			/*Log.i("My Code", " switch rewarded 180:" + given);*/
                break;

            case -90:
                setRewardString = HelperMethodHolder.NINETY_DAYS_REWARDED;
                given = settings.getString(setRewardString, HelperMethodHolder.NONE);
			/*Log.i("My Code", "switch rewarded 90:" + given);*/
                break;

            case -30:
                setRewardString = HelperMethodHolder.MONTH_REWARDED;
                given = settings.getString(setRewardString, HelperMethodHolder.NONE);
			/*Log.i("My Code", "switch rewarded 30:" + given);*/
                break;

            case -7:
                setRewardString = HelperMethodHolder.WEEK_REWARDED;
                given = settings.getString(setRewardString, HelperMethodHolder.NONE);
			/*Log.i("My Code", "switch rewarded 7:" + given);*/
                break;

            default:
                break;

        }

        result[0] = setRewardString;
        result[1] = given;
        return result;


    }

    public void giveReward(String timeLength, String title) {


        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.reward);


        TextView lengthUnder = (TextView) dialog.findViewById(R.id.lengthUnder);
        TextView titleView = (TextView) dialog.findViewById(R.id.title);
        TextView congrats = (TextView) dialog.findViewById(R.id.congratuations);

        congrats.setTypeface(cursive);

        lengthUnder.setText("for staying under your limit " + timeLength + ".\n\nYou are a ");
        titleView.setText(title + "!");

        checkbox = (CheckBox) dialog.findViewById(R.id.checkBox);

        Button thanks = (Button) dialog.findViewById(R.id.thanks);

        thanks.getBackground().setColorFilter(Color.parseColor("#d0eed0"), PorterDuff.Mode.MULTIPLY);

        thanks.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkbox.isChecked()) {
                    editor.putBoolean("rewards", false);
                    editor.commit();
                }

                dialog.dismiss();

            }
        });

        dialog.show();

    }

}