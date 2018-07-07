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
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
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
import com.mycryptobinder.adapters.CurrencySpinnerAdapter;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.models.HistoDayPrice;
import com.mycryptobinder.viewmodels.CurrencyListViewModel;
import com.mycryptobinder.viewmodels.StatisticsViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StatisticsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private StatisticsViewModel statisticsViewModel;
    private LineChart lineChart;
    //private TextView nbDaysTextView;
    private TextView oneDayTextView;
    private TextView oneWeekTextView;
    private TextView twoWeeksTextView;
    private TextView oneMonthTextView;
    private TextView twoMonthsTextView;
    private TextView threeMonthsTextView;
    private TextView sixMonthsTextView;
    private TextView oneYearTextView;
    private Spinner currencySpinner;
    private CurrencySpinnerAdapter currencySpinnerAdapter;
    private String selectedIsoCode;

    public StatisticsFragment() {
        // required empty public constructor
    }

    public static StatisticsFragment newInstance() {
        final StatisticsFragment fragment = new StatisticsFragment();
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
        final View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // get UI components
        //nbDaysTextView = view.findViewById(R.id.historical_day_price_nb_days_textView);
        oneDayTextView = view.findViewById(R.id.historical_day_price_one_day_textView);
        oneWeekTextView = view.findViewById(R.id.historical_day_price_one_week_textView);
        twoWeeksTextView = view.findViewById(R.id.historical_day_price_two_weeks_textView);
        oneMonthTextView = view.findViewById(R.id.historical_day_price_one_month_textView);
        twoMonthsTextView = view.findViewById(R.id.historical_day_price_two_months_textView);
        threeMonthsTextView = view.findViewById(R.id.historical_day_price_three_months_textView);
        sixMonthsTextView = view.findViewById(R.id.historical_day_price_six_months_textView);
        oneYearTextView = view.findViewById(R.id.historical_day_price_one_year_textView);
        final SeekBar nbDaysSeekBar = view.findViewById(R.id.historical_day_price_nb_days_seekBar);
        lineChart = view.findViewById(R.id.historical_day_price);
        currencySpinner = view.findViewById(R.id.historical_day_price_currency_spinner);

        // initialize to 1 year
        nbDaysSeekBar.setProgress(7);
        //nbDaysTextView.setText(getString(R.string.label_one_year));

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

        // get view models
        statisticsViewModel = ViewModelProviders.of(this).get(StatisticsViewModel.class);
        final CurrencyListViewModel currencyListViewModel = ViewModelProviders.of(this).get(CurrencyListViewModel.class);

        // initialize currencies spinner adapter
        currencySpinnerAdapter = new CurrencySpinnerAdapter(new ArrayList<>(), currencySpinner.getContext(), android.R.layout.simple_spinner_dropdown_item);
        currencySpinnerAdapter.setDropDownViewResource(R.layout.row_spinner);
        currencySpinner.setAdapter(currencySpinnerAdapter);

        // observe the currency list from the view model so the currency spinner is always up to date
        currencyListViewModel.getCurrencyList().observe(StatisticsFragment.this, currencies -> currencySpinnerAdapter.addItems(currencies));

        // set selected item listener on the currency spinner to update the chart on item selection
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedIsoCode = currencySpinnerAdapter.getItem(currencySpinner.getSelectedItemPosition());
                statisticsViewModel.setDayPriceFilter(selectedIsoCode, 365);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // observe the historical prices data from the view model so it will always be up to date in the UI
        statisticsViewModel.getHistoricalDayPrice().observe(this, historicalDayPrices -> {
            if (historicalDayPrices != null && historicalDayPrices.getData() != null) {

                // fill chart entries with our values
                final ArrayList<Entry> entries = new ArrayList<>();
                for (HistoDayPrice histoDayPrice : historicalDayPrices.getData()) {
                    entries.add(new Entry(histoDayPrice.getTime(), histoDayPrice.getHigh().floatValue()));
                }

                // create a data set and customize it
                final LineDataSet set1 = new LineDataSet(entries, getString(R.string.label_one_day));
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
                final LineData data = new LineData(set1);
                data.setValueTextColor(Color.WHITE);
                data.setValueTextSize(9f);

                // set data
                lineChart.setData(data);
                lineChart.invalidate();

                // disable the legend (only possible after setting data)
                final Legend l = lineChart.getLegend();
                l.setEnabled(false);

                // customize X axis
                final XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
                xAxis.setTextSize(10f);
                xAxis.setTextColor(Color.WHITE);
                xAxis.setDrawAxisLine(false);
                xAxis.setDrawGridLines(true);
                xAxis.setTextColor(Color.rgb(255, 192, 56));
                xAxis.setCenterAxisLabels(true);
                xAxis.setGranularity(24f); // one day
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    private SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM", UtilsHelper.getCurrentLocale(getContext()));

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return mFormat.format(new Date((long) value * 1000L));
                    }
                });

                // customize Y axis
                final YAxis leftAxis = lineChart.getAxisLeft();
                leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                leftAxis.setTextColor(ColorTemplate.getHoloBlue());
                leftAxis.setDrawGridLines(true);
                leftAxis.setGranularityEnabled(true);
                leftAxis.setYOffset(-9f);
                leftAxis.setTextColor(Color.rgb(255, 192, 56));

                final YAxis rightAxis = lineChart.getAxisRight();
                rightAxis.setEnabled(false);
            }
        });

        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        // arrays to match values and labels depending on current progress
        final int[] valuesInt = {1, 7, 15, 30, 60, 90, 180, 365};
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

        //nbDaysTextView.setText(valuesStr[progress]);

        // reset label colors
        final int defaultColor = oneDayTextView.getCurrentTextColor();
        oneDayTextView.setTextColor(defaultColor);
        oneWeekTextView.setTextColor(defaultColor);
        twoWeeksTextView.setTextColor(defaultColor);
        oneMonthTextView.setTextColor(defaultColor);
        twoMonthsTextView.setTextColor(defaultColor);
        threeMonthsTextView.setTextColor(defaultColor);
        sixMonthsTextView.setTextColor(defaultColor);
        oneYearTextView.setTextColor(defaultColor);

        // change current item label color
        final int selectionColor = getResources().getColor(R.color.primary);
        if (progress == 0) {
            oneDayTextView.setTextColor(selectionColor);
        } else if (progress == 1) {
            oneWeekTextView.setTextColor(selectionColor);
        } else if (progress == 2) {
            twoWeeksTextView.setTextColor(selectionColor);
        } else if (progress == 3) {
            oneMonthTextView.setTextColor(selectionColor);
        } else if (progress == 4) {
            twoMonthsTextView.setTextColor(selectionColor);
        } else if (progress == 5) {
            threeMonthsTextView.setTextColor(selectionColor);
        } else if (progress == 6) {
            sixMonthsTextView.setTextColor(selectionColor);
        } else if (progress == 7) {
            oneYearTextView.setTextColor(selectionColor);
        }
        statisticsViewModel.setDayPriceFilter(selectedIsoCode, valuesInt[progress]);

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