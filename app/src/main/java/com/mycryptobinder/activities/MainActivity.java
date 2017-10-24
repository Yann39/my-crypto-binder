package com.mycryptobinder.activities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.SectionsPagerAdapter;

/**
 * Main activity responsible for displaying the application main content
 * <p>
 * Created by Yann
 * Created on 21/05/2017
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // create the adapter that will return a fragment for each of the primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        // set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // set up the tabs layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.getTabAt(0).setIcon(R.drawable.tab_home_selector);
        tabLayout.getTabAt(1).setIcon(R.drawable.tab_transactions_selector);
        tabLayout.getTabAt(2).setIcon(R.drawable.tab_ico_selector);
        tabLayout.getTabAt(3).setIcon(R.drawable.tab_chart_selector);
        tabLayout.getTabAt(4).setIcon(R.drawable.tab_settings_selector);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // settings menu item click
        if (id == R.id.action_settings) {
            return true;
        }

        // back arrow click (close this activity and return to preview activity if there is any)
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}