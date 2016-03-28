package com.amlopezc.bikesmanager;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeStation;
import com.amlopezc.bikesmanager.net.HttpDispatcher;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows a pie chart with the current bike stations state
 */

public class ChartActivity extends AppCompatActivity implements AsyncTaskListener<String>{

    private PieChart mChart;
    private ArrayList<String> mXVals;
    private HttpDispatcher mHttpDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mChart = (PieChart) findViewById(R.id.chart_pieChart);
        mHttpDispatcher = new HttpDispatcher(this);

        //Setting the chart basic format
        mChart.setDescription(null);
        mChart.setHoleColorTransparent(true);
        mChart.setDrawSliceText(true);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setUsePercentValues(true);

        //Adding a listener to show a Toast with the number of bikes in the group selected
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                if (entry == null)
                    return;

                Toast.makeText(getApplicationContext(),
                        String.format("%s: %d", mXVals.get(entry.getXIndex()), (int) entry.getVal()),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Getting update data form the server
        fetchUpdatedServerData();
    }

    private void fetchUpdatedServerData() {
        mHttpDispatcher.doGet(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            fetchUpdatedServerData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processResult(String result, int operation) {
        //Process the server response
        switch (operation) {
            case HttpDispatcher.OPERATION_GET:
                //Update data and layout
                try {
                    ObjectMapper mapper = mHttpDispatcher.getMapper();
                    List<BikeStation> bikeStationList = mapper.readValue(result, new TypeReference<List<BikeStation>>() {});
                    ArrayList<Integer> data = readData(bikeStationList);
                    updateLocalLayout(data);
                } catch (Exception e) {
                    Log.e("[GET Result]" + getClass().getCanonicalName(), e.getLocalizedMessage(), e);
                    Toast.makeText(this,
                            i18n(R.string.toast_sync_error),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //Read server data to update current state
    private ArrayList<Integer> readData(List<BikeStation> bikeStationList) {
        int total = 0;
        int available = 0;
        int broken = 0;
        int reserved = 0;

        for(BikeStation bikeStation : bikeStationList) {
            total += bikeStation.getmTotalBikes();
            available += bikeStation.getmAvailableBikes();
            broken += bikeStation.getmBrokenBikes();
            reserved += bikeStation.getmReservedBikes();
        }

        int occupied = total - available - broken - reserved;

        ArrayList<Integer> data = new ArrayList<>();
        data.add(total);
        data.add(available);
        data.add(broken);
        data.add(reserved);
        data.add(occupied);

        return data;
    }

    //Update layout (pie chart)
    private void updateLocalLayout(ArrayList<Integer> data) {
        int totalBikes = data.get(0);

        ArrayList<Entry> yData = new ArrayList<>();
        //Adding data to the chart, i = 0 contains total bikes figure, don't wanna to show it
        for(int i = 1; i < data.size(); i++)
            yData.add(new Entry(data.get(i), i-1));

        //Setting tags
        mXVals = new ArrayList<>();
        mXVals.add(i18n(R.string.text_available));
        mXVals.add(i18n(R.string.text_broken));
        mXVals.add(i18n(R.string.text_reserved));
        mXVals.add(i18n(R.string.text_occupied));

        PieDataSet pieDataSet = new PieDataSet(yData, "");
        setPieDataSetFormat(pieDataSet);

        PieData pieData = new PieData(mXVals, pieDataSet);
        setPieDataFormat(pieData);

        mChart.setCenterText(i18n(R.string.chart_total_msg, totalBikes));
        mChart.setCenterTextColor(R.color.secondaryTextColor);
        //mChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);

        mChart.setCenterTextSize(16f);

        mChart.setData(pieData);
        mChart.highlightValues(null);
        mChart.invalidate();

        Legend l = mChart.getLegend();
        setLegendFormat(l);
    }

    //<editor-fold desc="SET METHODS FOR CHART DATA FORMAT">
    private void setPieDataSetFormat(PieDataSet pieDataSet) {
        pieDataSet.setSliceSpace(2);
        pieDataSet.setSelectionShift(5);
        pieDataSet.setValueFormatter(new MyValueFormatter());

        //Adding colors
        ArrayList<Integer> colors = new ArrayList<>();
        for(int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for(int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for(int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for(int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for(int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        pieDataSet.setColors(colors);
    }

    private void setPieDataFormat(PieData pieData) {
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueTypeface(Typeface.DEFAULT_BOLD);
    }

    private void setLegendFormat(Legend l) {
        l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        l.setTextSize(12f);
        l.setTextColor(R.color.secondaryTextColor);
    }
    //</editor-fold>

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }

    //Private class to set the value formatter for my DataSet (percentage format)
    private class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("##.#");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex,
                                        ViewPortHandler viewPortHandler) {
            return String.format("%s %s", mFormat.format(value), "%");
        }
    }

}
