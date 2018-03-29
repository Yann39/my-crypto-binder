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
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mycryptobinder.R;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.models.HistoDayPrice;
import com.mycryptobinder.viewmodels.StatisticsViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StatisticsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private StatisticsViewModel statisticsViewModel;
    private LineChart lineChart;
    private SeekBar nbDaysSeekBar;
    private TextView nbDaysTextView;
    private UtilsHelper uh;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        UtilsHelper uh = new UtilsHelper(getContext());

        // get UI components
        nbDaysTextView = view.findViewById(R.id.historical_day_price_nb_days_textView);
        nbDaysSeekBar = view.findViewById(R.id.historical_day_price_nb_days_seekBar);
        lineChart = view.findViewById(R.id.historical_day_price);

        // initialize to 30 days
        nbDaysSeekBar.setProgress(7);
        nbDaysTextView.setText(getString(R.string.label_one_year));

        // add seek bar listener
        nbDaysSeekBar.setOnSeekBarChangeListener(this);

        // no description text
        lineChart.getDescription().setEnabled(false);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);

        // set view offsets
        lineChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // get view model
        statisticsViewModel = ViewModelProviders.of(this).get(StatisticsViewModel.class);

        // set the currency for which we want historical price
        statisticsViewModel.setDayPriceFilter("BTC", 365);

        // observe the historical prices data from the view model so it will always be up to date in the UI
        statisticsViewModel.getHistoricalDayPrice().observe(this, historicalDayPrices -> {
            if (historicalDayPrices != null && historicalDayPrices.getData() != null) {

                // fill chart entries with our values
                ArrayList<Entry> entries = new ArrayList<>();
                for (HistoDayPrice histoDayPrice : historicalDayPrices.getData()) {
                    entries.add(new Entry(histoDayPrice.getTime(), histoDayPrice.getHigh().floatValue()));
                }

                // create a data set and customize it
                LineDataSet set1 = new LineDataSet(entries, "BTC " + getString(R.string.label_one_day));
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColor(ColorTemplate.getHoloBlue());
                set1.setValueTextColor(ColorTemplate.getHoloBlue());
                set1.setLineWidth(1.5f);
                set1.setDrawCircles(false);
                set1.setDrawValues(false);
                set1.setFillAlpha(65);
                set1.setFillColor(ColorTemplate.getHoloBlue());
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setDrawCircleHole(false);

                // create a data object with the data set
                LineData data = new LineData(set1);
                data.setValueTextColor(Color.WHITE);
                data.setValueTextSize(9f);

                // set data
                lineChart.setData(data);
                lineChart.invalidate();

                // disable the legend (only possible after setting data)
                Legend l = lineChart.getLegend();
                l.setEnabled(false);

                // customize X axis
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
                xAxis.setTextSize(10f);
                xAxis.setTextColor(Color.WHITE);
                xAxis.setDrawAxisLine(false);
                xAxis.setDrawGridLines(true);
                xAxis.setTextColor(Color.rgb(255, 192, 56));
                xAxis.setCenterAxisLabels(true);
                xAxis.setGranularity(24f); // one day
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    private SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM", uh.getCurrentLocale());

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {

                        return mFormat.format(new Date((long) value * 1000L));
                    }
                });

                // customize Y axis
                YAxis leftAxis = lineChart.getAxisLeft();
                leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                leftAxis.setTextColor(ColorTemplate.getHoloBlue());
                leftAxis.setDrawGridLines(true);
                leftAxis.setGranularityEnabled(true);
                leftAxis.setYOffset(-9f);
                leftAxis.setTextColor(Color.rgb(255, 192, 56));

                YAxis rightAxis = lineChart.getAxisRight();
                rightAxis.setEnabled(false);
            }
        });

        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int[] valuesInt = {1, 7, 15, 30, 60, 90, 180, 365};
        String[] valuesStr = {
                getString(R.string.label_one_day),
                getString(R.string.label_seven_days),
                getString(R.string.label_fifteen_days),
                getString(R.string.label_one_month),
                getString(R.string.label_two_months),
                getString(R.string.label_three_months),
                getString(R.string.label_six_months),
                getString(R.string.label_one_year)
        };
        nbDaysTextView.setText(valuesStr[nbDaysSeekBar.getProgress()]);
        statisticsViewModel.setDayPriceFilter("BTC", valuesInt[nbDaysSeekBar.getProgress()]);
        // redraw
        lineChart.invalidate();
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

}