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
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mycryptobinder.R;
import com.mycryptobinder.models.InternalFile;
import com.mycryptobinder.viewholders.InternalFilesViewHolder;

import java.util.List;

public class InternalFilesListAdapter extends RecyclerView.Adapter<InternalFilesViewHolder> {

    private List<InternalFile> files;
    private int selectedItemPos = -1;

    public InternalFilesListAdapter() {
    }

    @NonNull
    @Override
    public InternalFilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InternalFilesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_file_type, parent, false), this);
    }

    @Override
    public void onBindViewHolder(@NonNull final InternalFilesViewHolder holder, int position) {
        holder.nameTextView.setText(files.get(position).getName());
        holder.sizeTextView.setText(files.get(position).getSize());
        holder.dateTextView.setText(files.get(position).getDate());
        holder.setSelectedItemPos(selectedItemPos);
        holder.itemView.setSelected(selectedItemPos == position);
    }

    public void setFileNameList(List<InternalFile> files) {
        this.files = files;
        notifyDataSetChanged();
    }

    public void setSelectedItem(int position) {
        int oldSelected = selectedItemPos;
        selectedItemPos = position;

        // update view of unselected item
        notifyItemChanged(oldSelected);

        // update view of just selected item
        notifyItemChanged(selectedItemPos);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public String getSelectedFileName() {
        return selectedItemPos >= 0 ? files.get(selectedItemPos).getName() : null;
    }

}