package com.mycryptobinder.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * Fragment responsible for displaying statistics
 * <p>
 * Created by Yann on 21/05/2017
 */

public class StatisticsFragment extends Fragment {

    public StatisticsFragment() {
        // required empty public constructor
    }

    public static StatisticsFragment newInstance() {
        StatisticsFragment fragment = new StatisticsFragment();
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
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // open database connections
        TransactionManager transactionManager = new TransactionManager(this.getContext());
        transactionManager.open();

        List<Transaction> transactions = transactionManager.getAll();
        List<Entry> entries = new ArrayList<>();

        for (Transaction data : transactions) {
            // turn your data into Entry objects
            entries.add(new Entry((float)data.getDate().getTime(), data.getPrice().floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.GREEN);

        LineChart chart = (LineChart) view.findViewById(R.id.chart_example);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();

        return view;
    }

}