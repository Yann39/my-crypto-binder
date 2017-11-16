package com.mycryptobinder.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.entities.AppSetting;

import java.util.List;

/**
 * Created by Yann
 * Created on 08/11/2017
 */

public class AppSettingsListAdapter extends RecyclerView.Adapter<AppSettingsListAdapter.AppSettingsViewHolder> {

    private List<AppSetting> appSettings;
    private View.OnLongClickListener longClickListener;

    public AppSettingsListAdapter(List<AppSetting> appSettings, View.OnLongClickListener longClickListener) {
        this.appSettings = appSettings;
        this.longClickListener = longClickListener;
    }

    @Override
    public AppSettingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppSettingsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_app_settings, parent, false));
    }

    @Override
    public void onBindViewHolder(final AppSettingsViewHolder holder, int position) {
        AppSetting appSetting = appSettings.get(position);
        holder.nameTextView.setText(appSetting.getName());
        holder.valueTextView.setText(appSetting.getValue());
        holder.itemView.setTag(appSetting);
        holder.itemView.setOnLongClickListener(longClickListener);
    }

    @Override
    public int getItemCount() {
        return appSettings.size();
    }

    public void addItems(List<AppSetting> appSettings) {
        this.appSettings = appSettings;
        notifyDataSetChanged();
    }

    static class AppSettingsViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView valueTextView;

        AppSettingsViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.app_settings_name_column_header_text);
            valueTextView = view.findViewById(R.id.app_settings_value_column_header_text);
        }
    }
}