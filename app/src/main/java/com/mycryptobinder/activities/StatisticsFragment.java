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

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1F, 1252.457F));
        entries.add(new Entry(2F, 1042.11F));
        entries.add(new Entry(3F, 1430.15475F));
        entries.add(new Entry(4F, 789.97845F));
        entries.add(new Entry(5F, 899.5F));
        entries.add(new Entry(6F, 884.545F));
        entries.add(new Entry(7F, 987F));
        entries.add(new Entry(8F, 1384.1145F));
        entries.add(new Entry(9F, 1400.998F));
        entries.add(new Entry(10F, 1778.92782F));
        entries.add(new Entry(11F, 1832.2F));
        entries.add(new Entry(12F, 1705.8902F));
        entries.add(new Entry(13F, 1751.3F));
        entries.add(new Entry(14F, 1698.75F));
        entries.add(new Entry(15F, 1766.33341F));

        LineDataSet dataSet = new LineDataSet(entries, "Portfolio value"); // add entries to dataset
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.GREEN);

        LineChart chart = view.findViewById(R.id.chart_example);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();

        return view;
    }

}