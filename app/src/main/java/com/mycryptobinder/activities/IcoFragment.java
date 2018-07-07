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
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.IcoListAdapter;
import com.mycryptobinder.viewmodels.IcoListViewModel;

import java.util.ArrayList;

public class IcoFragment extends Fragment {

    private IcoListAdapter icoListAdapter;

    public IcoFragment() {
        // required empty public constructor
    }

    public static IcoFragment newInstance() {
        final IcoFragment fragment = new IcoFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ico, container, false);

        // prepare the recycler view with a linear layout
        final RecyclerView icoListRecyclerView = view.findViewById(R.id.ico_list_recycler_view);
        icoListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // add horizontal separator between rows
        final DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(icoListRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        icoListRecyclerView.addItemDecoration(mDividerItemDecoration);

        // initialize the adapter for the list
        icoListAdapter = new IcoListAdapter(new ArrayList<>());
        icoListRecyclerView.setAdapter(icoListAdapter);

        // get view model
        final IcoListViewModel icoListViewModel = ViewModelProviders.of(this).get(IcoListViewModel.class);

        // observe the ICO list from the view model so it is always up to date
        icoListViewModel.getIcoList().observe(IcoFragment.this, icos -> icoListAdapter.addItems(icos));

        // set click listener for the add ico button
        final FloatingActionButton button = view.findViewById(R.id.btn_add_ico);
        button.setOnClickListener(view1 -> {
            final Intent add_ico = new Intent(view1.getContext(), AddIcoActivity.class);
            startActivity(add_ico);
        });

        return view;
    }

}