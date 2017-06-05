package com.mycryptobinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mycryptobinder.R;

/**
 * Fragment responsible for displaying settings
 * <p>
 * Created by Yann on 21/05/2017
 */

public class SettingsFragment extends Fragment {

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
        ListView listView = (ListView) view.findViewById(R.id.settings_listView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(listView.getContext(), android.R.layout.simple_list_item_1, new String[]{"Currencies", "Exchanges"});
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
            }
        });

        return view;
    }

}