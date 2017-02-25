package com.amlopezc.bikesmanager;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeStation;
import com.amlopezc.bikesmanager.net.HttpConstants;
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
import java.util.Locale;

/**
 * Shows a pie chart with the current slots state
 */
public class ChartActivityFragmentSlots extends Fragment implements AsyncTaskListener<String> {

    private PieChart mChart;
    private ArrayList<String> mXValues;


    public ChartActivityFragmentSlots() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Listen to the Action Bar events
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart_slots, container, false);
        initUIComponents(view);

        return view;
    }

    //Setting the chart basic format
    private void initUIComponents(View view) {
        mChart = (PieChart) view.findViewById(R.id.chart_pieChart_slots);
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

                Toast.makeText(getActivity(),
                        String.format(Locale.getDefault(), "%s: %d", mXValues.get(entry.getXIndex()), (int) entry.getVal()),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //Getting update data form the server
        getStationsUpdatedServerData();
    }

    private void getStationsUpdatedServerData() {
        HttpDispatcher httpDispatcher = new HttpDispatcher(getActivity(), HttpConstants.ENTITY_STATION);
        httpDispatcher.doGet(this, HttpConstants.GET_FIND_ALL);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chart, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            getStationsUpdatedServerData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processServerResult(String result, int operation) {
        //Process the server response
        switch (operation) {
            case HttpConstants.OPERATION_GET:
                //Update data and layout
                try {
                    HttpDispatcher httpDispatcher = new HttpDispatcher(getActivity(), HttpConstants.ENTITY_STATION);
                    ObjectMapper mapper = httpDispatcher.getMapper();
                    List<BikeStation> bikeStationList = mapper.readValue(result, new TypeReference<List<BikeStation>>() {});
                    ArrayList<Integer> data = readData(bikeStationList);
                    updateLocalLayout(data);
                } catch (Exception e) {
                    Log.e("[GET Result]" + getClass().getCanonicalName(), e.getLocalizedMessage(), e);
                    showBasicErrorDialog(i18n(R.string.text_sync_error), i18n(R.string.text_ok));
                }
                break;
        }
    }

    //Read server data to update current state
    private ArrayList<Integer> readData(List<BikeStation> bikeStationList) {
        int totalSlots = 0;
        int availableSlots = 0;
        int reservedSlots = 0;

        for(BikeStation bikeStation : bikeStationList) {
            totalSlots += bikeStation.getmTotalSlots();
            availableSlots += bikeStation.getAvailableSlots();
            reservedSlots += bikeStation.getmReservedSlots();
        }

        int occupiedSlots = totalSlots - availableSlots - reservedSlots;

        ArrayList<Integer> data = new ArrayList<>();
        data.add(totalSlots);
        data.add(availableSlots);
        data.add(reservedSlots);
        data.add(occupiedSlots);

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
        mXValues = new ArrayList<>();
        mXValues.add(i18n(R.string.text_availability));
        mXValues.add(i18n(R.string.text_bookings));
        mXValues.add(i18n(R.string.text_occupied));

        PieDataSet pieDataSet = new PieDataSet(yData, "");
        setPieDataSetFormat(pieDataSet);

        PieData pieData = new PieData(mXValues, pieDataSet);
        setPieDataFormat(pieData);

        mChart.setCenterText(i18n(R.string.chart_total_slots_msg, totalBikes));
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

    // Show a basic error dialog with a custom message
    private void showBasicErrorDialog(String message, String positiveButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(i18n(R.string.text_error)).
                setIcon(R.drawable.ic_error_outline).
                setMessage(message).
                setPositiveButton(
                        positiveButtonText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

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
