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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.entities.AppSetting;
import com.mycryptobinder.viewmodels.AddAppSettingsViewModel;

public class AddAppSettingActivity extends AppCompatActivity {

    private AddAppSettingsViewModel addAppSettingsViewModel;
    private EditText appSettingNameEditText;
    private EditText appSettingValueEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_app_setting);

        // set toolbar as actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // get view components
        appSettingNameEditText = findViewById(R.id.add_app_setting_name_editText);
        appSettingValueEditText = findViewById(R.id.add_app_setting_value_editText);
        Button createAppSettingButton = findViewById(R.id.btn_create_app_setting);
        Button updateAppSettingButton = findViewById(R.id.btn_update_app_setting);

        // hide edit button and show create button
        updateAppSettingButton.setVisibility(View.INVISIBLE);
        createAppSettingButton.setVisibility(View.VISIBLE);

        // get the view model
        addAppSettingsViewModel = ViewModelProviders.of(this).get(AddAppSettingsViewModel.class);

        // set click listener for the create app setting button
        createAppSettingButton.setOnClickListener(view -> {
            // get field values
            String settingName = appSettingNameEditText.getText().toString();
            String settingValue = appSettingValueEditText.getText().toString();

            // check mandatory fields
            if (settingName.trim().equals("")) {
                appSettingNameEditText.setError(getString(R.string.error_setting_name_required));
            } else if (settingValue.trim().equals("")) {
                appSettingValueEditText.setError(getString(R.string.error_setting_value_required));
            } else {
                // add record to the view model who will trigger the insert
                addAppSettingsViewModel.addAppSetting(new AppSetting(settingName, settingValue));

                // close current activity and return to previous activity if there is any
                finish();

                // show a notification about the created item
                Toast.makeText(view.getContext(), getString(R.string.success_app_setting_created, settingName), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // back arrow click
        if (id == android.R.id.home) {
            // close current activity and return to previous activity if there is any
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
