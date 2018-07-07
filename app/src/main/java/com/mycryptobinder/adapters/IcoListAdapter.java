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

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.activities.EditExchangeActivity;
import com.mycryptobinder.activities.EditIcoActivity;
import com.mycryptobinder.entities.Ico;
import com.mycryptobinder.helpers.UtilsHelper;
import com.mycryptobinder.viewholders.IcoListViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;

public class IcoListAdapter extends RecyclerView.Adapter<IcoListViewHolder> {

    private List<Ico> icos;

    public IcoListAdapter(List<Ico> icos) {
        this.icos = icos;
    }

    @NonNull
    @Override
    public IcoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IcoListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ico, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final IcoListViewHolder holder, int position) {

        // set date format that will be used for date pickers
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", UtilsHelper.getCurrentLocale(holder.itemView.getContext()));

        Ico ico = icos.get(position);
        holder.nameTextView.setText(ico.getName());
        holder.dateTextView.setText(sdf.format(ico.getInvestDate()));
        holder.quantityTextView.setText(String.format("%s %s", ico.getAmount(), ico.getCurrencyIsoCode()));

        // set exchange as tag so we can get it in the click listener later
        holder.itemView.setTag(ico);

        // row click
        holder.itemView.setOnClickListener(v -> {
            // store element values in the intent so we can access them later
            Intent edit_ico = new Intent(v.getContext(), EditIcoActivity.class);
            edit_ico.putExtra("id", icos.get(position).getId());
            edit_ico.putExtra("name", icos.get(position).getName());
            edit_ico.putExtra("investDate", icos.get(position).getInvestDate());
            edit_ico.putExtra("amount", icos.get(position).getAmount());
            edit_ico.putExtra("currency", icos.get(position).getCurrencyIsoCode());
            edit_ico.putExtra("token", icos.get(position).getToken());
            edit_ico.putExtra("tokenDate", icos.get(position).getTokenDate());
            edit_ico.putExtra("tokenQuantity", icos.get(position).getTokenQuantity());
            edit_ico.putExtra("fee", icos.get(position).getFee());
            edit_ico.putExtra("bonus", icos.get(position).getBonus());
            edit_ico.putExtra("comment", icos.get(position).getComment());
            v.getContext().startActivity(edit_ico);
        });
    }

    @Override
    public int getItemCount() {
        return icos.size();
    }

    public void addItems(List<Ico> icos) {
        this.icos = icos;
        notifyDataSetChanged();
    }

}