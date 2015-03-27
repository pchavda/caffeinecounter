package com.detroitteatime.caffeinecounter;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class DrinkDialog extends Activity implements OnSeekBarChangeListener {

    private SeekBar bar;
    private TextView textProgress, mgCaff;

    private Button finish, convert, info;
    protected String type = null;
    private String unit = " oz";
    private double mgPerOz, caffAmnt;
    private ImageView image;
    public static final double ozPerMl = 0.033814;
    private SharedPreferences.Editor editor;
    private SharedPreferences settings;

    private boolean isMetric;
    public static final String METRIC = "metric";
    private static final String MG = " mg Caf";

    private double drinkSize, displaySize;
    private DataBaseHelper helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new DataBaseHelper(this.getApplicationContext());
        setContentView(R.layout.drink_dialog);

        // Get caffeine info from intent
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        mgPerOz = intent.getDoubleExtra(DataBaseHelper.MGCAFF, -1);
        settings = getSharedPreferences("prefs", 0);
        editor = settings.edit();

        isMetric = settings.getBoolean(METRIC, false); //check if metric (remembered in preferences)


        //UI setup

        bar = (SeekBar) findViewById(R.id.seekBar);
        bar.setOnSeekBarChangeListener(this);//Seekbar change listener implemented by class

        textProgress = (TextView) findViewById(R.id.size);
        mgCaff = (TextView) findViewById(R.id.mgCaff);

        convert = (Button) findViewById(R.id.convert);//button converts from US to metric and vice versa

        //set text for conversion button
        if (type.contains("spresso")) {// make conversion button invisible
            convert.setVisibility(View.GONE);
        }

        if (isMetric) {
            convert.setText(R.string.Ounces);
        } else {
            convert.setText(R.string.Milliliters);
        }


        convert.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR),
                PorterDuff.Mode.MULTIPLY);

        //what convert does when clicked depends on user prefs
        convert.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isMetric) {
                    editor.putBoolean(METRIC, false);
                    editor.commit();
                    convert.setText(R.string.Milliliters);
                    isMetric = false;
                    unit = " oz";

                } else {
                    editor.putBoolean(METRIC, true);
                    editor.commit();
                    convert.setText(R.string.Ounces);
                    isMetric = true;
                    unit = " ml";

                }

                setupDisplayDrinkSize(bar.getProgress());
                caffAmnt = setupDrinkSize(displaySize) * mgPerOz;
                setUpTextFields(displaySize, unit);
            }
        });

        finish = (Button) findViewById(R.id.finish);
        finish.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR),
                PorterDuff.Mode.MULTIPLY);
        finish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                HelperMethodHolder.addDrink(drinkSize, type, (int) Math.round(caffAmnt),
                        helper);
                finish();
            }
        });

        info = (Button) findViewById(R.id.moreInfo);
        info.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);

        info.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DrinkDialog.this, InfoView.class);
                intent.putExtra("drink_type", type);

                startActivity(intent);

            }
        });

        image = (ImageView) findViewById(R.id.sizeClue);

        // set up initial value of progress bar
        if (type.contains("spresso")) {

            image.setImageDrawable(this.getResources().getDrawable(
                    R.drawable.shot));
            bar.setProgress(10);
            unit = " sht";

        } else {
            bar.setProgress(50);
        }

        setupDisplayDrinkSize(bar.getProgress());
        caffAmnt = setupDrinkSize(displaySize) * mgPerOz;
        setUpTextFields(displaySize, unit);

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        setupDisplayDrinkSize(progress);
        caffAmnt = setupDrinkSize(displaySize) * mgPerOz;
        setUpTextFields(displaySize, unit);

        if (type.contains("spresso")) {// don't change image, or get some
            // appropriate images
        }
        if (drinkSize > 0 && drinkSize < 2) {
            image.setImageDrawable(this.getResources().getDrawable(
                    R.drawable.shot));
        } else if (drinkSize > 2 && drinkSize < 6) {
            image.setImageDrawable(this.getResources().getDrawable(
                    R.drawable.sma));
        } else if (drinkSize > 6 && drinkSize < 10) {
            image.setImageDrawable(this.getResources().getDrawable(
                    R.drawable.eight));
        } else if (drinkSize >= 10 && drinkSize < 14) {
            image.setImageDrawable(this.getResources().getDrawable(
                    R.drawable.tall));
        } else if (drinkSize >= 14 && drinkSize < 18) {
            image.setImageDrawable(this.getResources().getDrawable(
                    R.drawable.grande));
        } else if (drinkSize >= 18 && drinkSize < 22) {
            image.setImageDrawable(this.getResources().getDrawable(
                    R.drawable.venti));
        } else if (drinkSize >= 22 && drinkSize < 26) {
            image.setImageDrawable(this.getResources().getDrawable(
                    R.drawable.twentyfour));
        } else if (drinkSize >= 26) {
            image.setImageDrawable(this.getResources().getDrawable(
                    R.drawable.thirty_two));
        } else {

        }


    }

    private double setupDrinkSize(double p) {

        if (type.contains("spresso") || !isMetric) {
            drinkSize = p;

        } else {
            drinkSize = p * ozPerMl;
        }

        return drinkSize;
    }

    private void setupDisplayDrinkSize(double p) {

        if (type.contains("spresso")) {
            displaySize = p / 10;
            unit = " sht";
        } else if (isMetric) {
            // I want the bar to go from 0 to 1000 in ml, round to nearest 10.
            displaySize = p * 10;
            unit = " ml";

        } else {
            // I want the bar to go from 0 to 32 oz, round to nearest oz.
            displaySize = Math.round(p * 0.32);
            unit = " oz";
        }

    }

    private void setUpTextFields(double d, String u) {

        textProgress.setText(Math.round(d) + u);
        mgCaff.setText(Math.round(caffAmnt) + MG);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

}
