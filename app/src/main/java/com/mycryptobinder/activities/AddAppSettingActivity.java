package com.mycryptobinder.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
                appSettingNameEditText.setError("Setting name is required!");
            } else if (settingValue.trim().equals("")) {
                appSettingValueEditText.setError("Setting value is required!");
            } else {
                // add record to the view model who will trigger the insert
                addAppSettingsViewModel.addAppSetting(new AppSetting(settingName, settingValue));

                // close current activity and return to previous activity if there is any
                finish();

                // show a notification about the created item
                Toast.makeText(view.getContext(), view.getResources().getString(R.string.msg_app_setting_created, settingName), Toast.LENGTH_SHORT).show();
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
