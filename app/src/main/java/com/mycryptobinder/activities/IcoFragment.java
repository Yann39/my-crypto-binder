package com.mycryptobinder.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mycryptobinder.R;
import com.mycryptobinder.managers.TransactionManager;
import com.mycryptobinder.models.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment responsible for displaying ICOs
 * <p>
 * Created by Yann on 07/10/2017
 */

public class IcoFragment extends Fragment {

    public IcoFragment() {
        // required empty public constructor
    }

    public static IcoFragment newInstance() {
        IcoFragment fragment = new IcoFragment();
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
        View view = inflater.inflate(R.layout.fragment_ico, container, false);

        // set click listener for the add ico button
        Button button = (Button) view.findViewById(R.id.btn_add_ico);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_ico = new Intent(view.getContext(), AddIcoActivity.class);
                startActivity(add_ico);
            }
        });

        return view;
    }

}