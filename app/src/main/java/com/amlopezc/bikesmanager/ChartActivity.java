package com.amlopezc.bikesmanager;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {

    private PieChart mChart;
    private int mTotalBikes;
    private ArrayList<String> mXVals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO: HECHO TODO UN POCO RÁPIDO, PERO VA BIEN, REPASAR Y MEJORAR TANTO EN EL MAPS ACTIVITY COMO EN ESTA

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mChart = (PieChart) findViewById(R.id.chart_pieChart);

        //Setting the chart
        mChart.setDescription(null);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(7);
        mChart.setTransparentCircleRadius(10);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        //Adding a listener
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                if (entry == null)
                    return;

                Toast.makeText(getApplicationContext(),
                        String.format("%s: %d", mXVals.get(entry.getXIndex()), (int)entry.getVal()),
                        Toast.LENGTH_SHORT).
                        show();
            }

            @Override
            public void onNothingSelected() {
            }
        });

        addData();

        /*
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);*/
    }

    private void addData() {
        Intent intent = getIntent();

        ArrayList<Integer> intentData = intent.getIntegerArrayListExtra(MapsActivity.EXTRA_DATA);
        mTotalBikes = intentData.get(0);

        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 1; i < intentData.size(); i++)  //don't want to show total bikes
            yData.add(new Entry(intentData.get(i), i-1));

        mXVals = new ArrayList<>();
        mXVals.add("Available");
        mXVals.add("Broken");
        mXVals.add("Reserved");
        mXVals.add("Occupied");

        PieDataSet pieDataSet = new PieDataSet(yData, String.format("Total bikes: %d", mTotalBikes));
        pieDataSet.setSliceSpace(3);
        pieDataSet.setSelectionShift(5);

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
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.GRAY);

        mChart.setData(pieData);
        mChart.highlightValues(null);
        mChart.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
