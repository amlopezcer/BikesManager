package com.amlopezc.bikesmanager;

import android.graphics.Color;
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

public class ChartActivity extends AppCompatActivity implements AsyncTaskListener<String>{
    //TODO: mostrar los datos en % ?

    private PieChart mChart;
    private ArrayList<String> mXVals;
    private HttpDispatcher dispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mChart = (PieChart) findViewById(R.id.chart_pieChart);
        dispatcher = new HttpDispatcher(this);

        //Setting the chart
        mChart.setDescription(null);
        mChart.setHoleColorTransparent(true);
        mChart.setDrawSliceText(true);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setUsePercentValues(true);

        //Adding a listener
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                if (entry == null)
                    return;

                Toast.makeText(getApplicationContext(),
                        String.format("%s: %d", mXVals.get(entry.getXIndex()), (int) entry.getVal()),
                        Toast.LENGTH_LONG).
                        show();
            }

            @Override
            public void onNothingSelected() {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchUpdatedServerData();
    }

    private void fetchUpdatedServerData() {
        dispatcher.doGet(this);
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

    //Private class to set the value formatter  for my DataSet
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

    @Override
    public void processResult(String result, int operation) {
        switch (operation) {
            case HttpDispatcher.OPERATION_GET:
                try {
                    ObjectMapper mapper = dispatcher.getMapper();
                    List<BikeStation> bikeStationList = mapper.readValue(result, new TypeReference<List<BikeStation>>() {});
                    ArrayList<Integer> data = readData(bikeStationList);
                    updateLocalLayout(data);
                } catch (Exception e) {
                    Log.e("JSON", e.getLocalizedMessage(), e);
                    Toast.makeText(this, "Error al sincronizar con el servidor", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

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

    private void updateLocalLayout(ArrayList<Integer> data) {
        int totalBikes = data.get(0);

        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 1; i < data.size(); i++)  //don't want to show total bikes
            yData.add(new Entry(data.get(i), i-1));

        mXVals = new ArrayList<>();
        mXVals.add("Available");
        mXVals.add("Broken");
        mXVals.add("Reserved");
        mXVals.add("Occupied");

        PieDataSet pieDataSet = new PieDataSet(yData, "");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setSelectionShift(5);
        pieDataSet.setValueFormatter(new MyValueFormatter());

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

        PieData pieData = new PieData(mXVals, pieDataSet);
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.WHITE);

        mChart.setCenterText(String.format("Total bikes: %d", totalBikes));
        mChart.setCenterTextColor(Color.rgb(60, 145, 210)); //grey - blue
        mChart.setCenterTextSize(16f);

        mChart.setData(pieData);
        mChart.highlightValues(null);
        mChart.invalidate();

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        /*l.setTextSize(10f);
        l.setTextColor(Color.DKGRAY);*/
    }


}
