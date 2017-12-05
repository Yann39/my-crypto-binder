package com.mycryptobinder.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.viewmodels.SettingsViewModel;

/**
 * Fragment responsible for displaying settings
 * <p>
 * Created by Yann on 21/05/2017
 */

public class SettingsFragment extends Fragment {

    private CheckBox checkBox;

    public SettingsFragment() {
        // required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // get the list view and add items
        ListView listView = view.findViewById(R.id.settings_listView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(listView.getContext(), android.R.layout.simple_list_item_1, new String[]{"Currencies", "Exchanges", "AppSetting"});
        listView.setAdapter(adapter);

        // set click listener for list view items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // currencies item click
                if (position == 0) {
                    Intent intent = new Intent(getActivity(), CurrencyListActivity.class);
                    startActivity(intent);
                }

                // exchanges item click
                else if (position == 1) {
                    Intent intent = new Intent(getActivity(), ExchangeListActivity.class);
                    startActivity(intent);
                }

                // exchanges item click
                else if (position == 2) {
                    Intent intent = new Intent(getActivity(), AppSettingListActivity.class);
                    startActivity(intent);
                }
            }
        });

        final SettingsViewModel settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);

        TextView synchronizeLogEditText = view.findViewById(R.id.synchronize_log_editText);
        settingsViewModel.getCurrentLogs().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                synchronizeLogEditText.setText(s);
            }
        });

        ProgressBar synchronizeLogProgressBar = view.findViewById(R.id.synchronize_log_progressBar);
        settingsViewModel.getPercentDone().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer percent) {
                synchronizeLogProgressBar.setProgress(percent);
            }
        });

        Button synchronizeButton = view.findViewById(R.id.btn_synchronize);
        checkBox = view.findViewById(R.id.checkbox_clean_synchronize);

        // set click listener for the synchronize button
        synchronizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                settingsViewModel.populateDatabase();

            }
        });

        return view;
    }

}