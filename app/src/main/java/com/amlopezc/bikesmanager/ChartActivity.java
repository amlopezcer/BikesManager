package com.amlopezc.bikesmanager;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.amlopezc.bikesmanager.util.ViewPagerAdapter;


public class ChartActivity extends AppCompatActivity {

    public static final int NUM_ITEMS_CHART = 2; //Tabs (fixed)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        //Set tabs view
        Toolbar toolbar;
        TabLayout tabLayout;
        ViewPager viewPager;

        toolbar = (Toolbar) findViewById(R.id.toolbar_chart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewPager_chart);
        initViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs_chart);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChartActivityFragmentBikes(), i18n(R.string.text_bikes));
        adapter.addFragment(new ChartActivityFragmentSlots(), i18n(R.string.text_slots));
        viewPager.setAdapter(adapter);
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }

}
