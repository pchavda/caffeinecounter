package com.detroitteatime.caffeinecounter;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Plot extends Activity {

    /**
     * Called when the activity is first created.
     */


    private TimeSeries time_series;
    private LinearLayout layout;
    private TextView timePeriod;


    private DateFormat formatter = new SimpleDateFormat(DataBaseHelper.DATE_FORMAT);
    private ProgressBar progress;

    // chart container

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.plot);


        timePeriod = (TextView) findViewById(R.id.timePeriod);
        layout = (LinearLayout) findViewById(R.id.chart);
        progress = (ProgressBar) findViewById(R.id.progressBar);

        Button set30 = (Button) findViewById(R.id.oneMonth);
        set30.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR),
                PorterDuff.Mode.MULTIPLY);

        Button set60 = (Button) findViewById(R.id.twoMonth);
        set60.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR),
                PorterDuff.Mode.MULTIPLY);

        Button set90 = (Button) findViewById(R.id.threeMonth);
        set90.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR),
                PorterDuff.Mode.MULTIPLY);

        Button setAll = (Button) findViewById(R.id.wholeHistory);
        setAll.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR),
                PorterDuff.Mode.MULTIPLY);

        set30.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                new SetUpPlotTask().execute(-30, null, null);
                timePeriod.setText("30 days");

            }
        });

        set60.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                new SetUpPlotTask().execute(-60, null, null);
                timePeriod.setText("60 days");

            }
        });

        set90.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                new SetUpPlotTask().execute(-90, null, null);
                timePeriod.setText("90 days");

            }
        });

        setAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                new SetUpPlotTask().execute(0, null, null);
                timePeriod.setText("All days");

            }
        });

        new SetUpPlotTask().execute(-30, null, null);
        timePeriod.setText("30 days");

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();


    }

    private boolean fillData(int x) {

        String lastDate = null;
        Calendar c2 = Calendar.getInstance();
        Calendar c1 = Calendar.getInstance();
        Calendar current = Calendar.getInstance();
        DataBaseHelper helper = new DataBaseHelper(this.getApplicationContext());
        helper.open(DataBaseHelper.READABLE);
        Cursor cursor;
        time_series = new TimeSeries("mg Caffeine");
        if (x != 0) {
            cursor = helper.getDaysSoManyDaysInThePast(x);
        } else {
            cursor = helper.getAllDays("ASC");
        }

        int count = cursor.getCount();

        if (count > 1) {

            Date date = null;

            String s;

            int dateCol = cursor.getColumnIndex("date");
            int caffCol = cursor.getColumnIndex("sum(mgCaffeine)");

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                int caff = cursor.getInt(caffCol);
                s = cursor.getString(dateCol);

                try {
                    date = formatter.parse(s);

                } catch (ParseException e) {

                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }

                // checking to see if there is a gap in the dates, if so, insert
                // a day with zero mg caffeine, keep cursor at current position
                // until
                // lastDate passes current date.
                Calendar ldate = Calendar.getInstance();

                if (lastDate != null) {
                    try {
                        ldate.setTime(formatter.parse(lastDate));
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (!isNextDate(lastDate, s, c1, c2, formatter)
                            && ldate.before(current)) {
                        date = c1.getTime(); // c1 should already be incremented

                        cursor.moveToPrevious(); // to stay at the same row,
                        // since
                        // it is not added until the
                        // gap
                        // is closed
                        caff = 0;
                    }

                }

                lastDate = formatter.format(date);// increment lastDate

                time_series.add(date, caff);
            }

            helper.close();
            cursor.close();
            return true;
        } else {
            helper.close();
            cursor.close();
            return false;
        }

    }

    public boolean isNextDate(String dateString1, String dateString2,
                              Calendar c1, Calendar c2, DateFormat formatter) {
        boolean result = false;

        try {

            c1.setTime(formatter.parse(dateString1));
            c1.add(Calendar.DATE, 1); // number of days to add
            c2.setTime(formatter.parse(dateString2));

            if (c2.equals(c1)) {
                result = true;
            } else {
                result = false;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    private void setUpChart(boolean works) {
        XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

        mRenderer.setAxisTitleTextSize(16);
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(15);
        mRenderer.setLegendTextSize(20);
        mRenderer.setPointSize(3f);
        mRenderer.setYAxisMin(0);

        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.GREEN);
        r.setPointStyle(PointStyle.CIRCLE);
        r.setFillPoints(true);
        mRenderer.addSeriesRenderer(r);
        mRenderer.setClickEnabled(true);
        mRenderer.setSelectableBuffer(20);
        mRenderer.setPanEnabled(true);
        mRenderer.setInScroll(true);

        mDataset.addSeries(time_series);
        GraphicalView mChartView;
        if (works) {
            mChartView = ChartFactory.getTimeChartView(this, mDataset,
                    mRenderer, DataBaseHelper.SHORT_DATE_FORMAT);
            layout.removeAllViews();
            layout.addView(mChartView);

        } else {
            Toast.makeText(this,
                    "Time period needs at least two data points to plot.",
                    Toast.LENGTH_LONG).show();
        }

    }

    private class SetUpPlotTask extends AsyncTask<Integer, Void, Void> {
        boolean works;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Void result) {

            progress.setVisibility(View.GONE);
            setUpChart(works);
        }

        @Override
        protected Void doInBackground(Integer... params) {
            works = fillData(params[0]);

            return null;

        }

    }

}
