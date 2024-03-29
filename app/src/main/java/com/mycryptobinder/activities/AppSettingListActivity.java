/*
 * Copyright (c) 2018 by Yann39.
 *
 * This file is part of MyCryptoBinder.
 *
 * MyCryptoBinder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyCryptoBinder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyCryptoBinder. If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycryptobinder.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.AppSettingsListAdapter;
import com.mycryptobinder.viewmodels.AppSettingListViewModel;

import java.util.ArrayList;

public class AppSettingListActivity extends AppCompatActivity {

    private AppSettingsListAdapter appSettingsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        // set toolbar as actionbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // prepare the recycler view with a linear layout
        final RecyclerView appSettingsListRecyclerView = findViewById(R.id.app_settings_list_recycler_view);
        appSettingsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize the adapter for the list
        appSettingsListAdapter = new AppSettingsListAdapter(new ArrayList<>());
        appSettingsListRecyclerView.setAdapter(appSettingsListAdapter);

        // get view model
        final AppSettingListViewModel appSettingListViewModel = ViewModelProviders.of(this).get(AppSettingListViewModel.class);

        // observe the application settings list from the view model so it is always up to date
        appSettingListViewModel.getAppSettingsList().observe(AppSettingListActivity.this, appSettings -> appSettingsListAdapter.addItems(appSettings));

        // set click listener for the add application setting button
        final FloatingActionButton button = findViewById(R.id.btn_add_app_setting);
        button.setOnClickListener(view -> {
            final Intent intent = new Intent(view.getContext(), AddAppSettingActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        // back arrow click
        if (id == android.R.id.home) {
            // close current activity and return to previous activity if there is any
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}