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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {
    //TODO: mostrar los datos en % ?

    private PieChart mChart;
    private ArrayList<String> mXVals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mChart = (PieChart) findViewById(R.id.chart_pieChart);

        //Setting the chart
        mChart.setDescription(null);

        mChart.setHoleColorTransparent(true);

        mChart.setDrawSliceText(false);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        //Adding a listener
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                if (entry == null)
                    return;

                Toast.makeText(getApplicationContext(),
                        String.format("%s: %d", mXVals.get(entry.getXIndex()), (int) entry.getVal()),
                        Toast.LENGTH_SHORT).
                        show();
            }

            @Override
            public void onNothingSelected() {}
        });

        addData();

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setTextSize(10f);
    }

    private void addData() {
        int totalBikes;
        Intent intent = getIntent();

        ArrayList<Integer> intentData = intent.getIntegerArrayListExtra(MapsActivity.EXTRA_DATA);
        totalBikes = intentData.get(0);

        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 1; i < intentData.size(); i++)  //don't want to show total bikes
            yData.add(new Entry(intentData.get(i), i-1));

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
        mChart.setCenterTextColor(Color.rgb(0, 60, 245)); //dark blue
        mChart.setCenterTextSize(16f);

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
        if (id == R.id.action_refresh) {
            Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("#####"); // no decimals
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex,
                                        ViewPortHandler viewPortHandler) {
           return mFormat.format(value);
        }
    }
}
