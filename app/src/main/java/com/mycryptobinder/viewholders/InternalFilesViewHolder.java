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

package com.mycryptobinder.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.adapters.InternalFilesListAdapter;

public class InternalFilesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView nameTextView;
    public TextView dateTextView;
    public TextView sizeTextView;
    private int selectedItemPos = -1;
    private InternalFilesListAdapter internalFilesListAdapter;

    public InternalFilesViewHolder(View view, InternalFilesListAdapter internalFilesListAdapter) {
        super(view);
        view.setOnClickListener(this);
        nameTextView = view.findViewById(R.id.fileName_textView);
        dateTextView = view.findViewById(R.id.fileDate_textView);
        sizeTextView = view.findViewById(R.id.fileSize_textView);
        this.internalFilesListAdapter = internalFilesListAdapter;
    }

    public void setSelectedItemPos(int selectedItem) {
        this.selectedItemPos = selectedItem;
    }

    @Override
    public void onClick(View v) {
        //if the clicked item is already selected, unselect it (new one will be -1, representing no selection)
        int newSelectedItem = getAdapterPosition() == selectedItemPos ? -1 : getAdapterPosition();

        //apply changes on adapter
        internalFilesListAdapter.setSelectedItem(newSelectedItem);
    }

}