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

package com.mycryptobinder.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycryptobinder.R;
import com.mycryptobinder.entities.AppSetting;
import com.mycryptobinder.viewholders.AppSettingsViewHolder;

import java.util.List;

public class AppSettingsListAdapter extends RecyclerView.Adapter<AppSettingsViewHolder> {

    private List<AppSetting> appSettings;

    public AppSettingsListAdapter(List<AppSetting> appSettings) {
        this.appSettings = appSettings;
    }

    @NonNull
    @Override
    public AppSettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppSettingsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_app_setting, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AppSettingsViewHolder holder, int position) {
        AppSetting appSetting = appSettings.get(position);
        holder.nameTextView.setText(appSetting.getName());
        holder.valueTextView.setText(appSetting.getValue());
        holder.itemView.setTag(appSetting);
    }

    @Override
    public int getItemCount() {
        return appSettings.size();
    }

    public void addItems(List<AppSetting> appSettings) {
        this.appSettings = appSettings;
        notifyDataSetChanged();
    }

}