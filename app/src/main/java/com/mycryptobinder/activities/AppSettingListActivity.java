package com.mycryptobinder.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.AppSettingsListAdapter;
import com.mycryptobinder.entities.AppSetting;
import com.mycryptobinder.viewmodels.AppSettingListViewModel;

import java.util.ArrayList;
import java.util.List;

public class AppSettingListActivity extends AppCompatActivity implements View.OnLongClickListener {

    private AppSettingListViewModel appSettingListViewModel;
    private AppSettingsListAdapter appSettingsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // prepare the recycler view with a linear layout
        RecyclerView appSettingsListRecyclerView = findViewById(R.id.app_settings_list_recycler_view);
        appSettingsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize the adapter for the list
        appSettingsListAdapter = new AppSettingsListAdapter(new ArrayList<AppSetting>(), this);
        appSettingsListRecyclerView.setAdapter(appSettingsListAdapter);

        // get view model
        appSettingListViewModel = ViewModelProviders.of(this).get(AppSettingListViewModel.class);

        // observe the application settings list from the view model so it will always be up to date
        appSettingListViewModel.getAppSettingsList().observe(AppSettingListActivity.this, new Observer<List<AppSetting>>() {
            @Override
            public void onChanged(@Nullable List<AppSetting> appSettings) {
                appSettingsListAdapter.addItems(appSettings);
            }
        });

        // set click listener for the add currency button
        FloatingActionButton button = findViewById(R.id.btn_add_app_setting);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddAppSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onLongClick(View v) {
        AppSetting appSetting = (AppSetting) v.getTag();
        appSettingListViewModel.deleteItem(appSetting);
        return true;
    }
}