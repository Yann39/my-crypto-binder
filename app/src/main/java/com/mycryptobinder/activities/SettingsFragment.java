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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mycryptobinder.R;

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
        ListView listView = view.findViewById(R.id.settings_listView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(listView.getContext(), android.R.layout.simple_list_item_1, new String[]{"Currencies", "Exchanges", "AppSetting", "Synchronize with exchanges"});
        listView.setAdapter(adapter);

        // set click listener for list view items
        listView.setOnItemClickListener((parent, view1, position, id) -> {

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

            // app settings item click
            else if (position == 2) {
                Intent intent = new Intent(getActivity(), AppSettingListActivity.class);
                startActivity(intent);
            }

            // synchronize with exchanges item click
            else if (position == 3) {
                Intent intent = new Intent(getActivity(), SynchronizeExchangesActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}