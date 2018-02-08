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
import android.widget.ImageButton;
import android.widget.TextView;

import com.mycryptobinder.R;

public class ExchangeCardViewHolder extends RecyclerView.ViewHolder {

    public TextView exchange_id_textView;
    public TextView exchange_name_textView;
    public TextView exchange_link_textView;
    public TextView exchange_description_textView;
    public ImageButton exchange_delete_imageButton;

    public ExchangeCardViewHolder(View v) {
        super(v);
        exchange_id_textView = v.findViewById(R.id.exchange_card_exchange_id);
        exchange_name_textView = v.findViewById(R.id.exchange_card_exchange_name);
        exchange_link_textView = v.findViewById(R.id.exchange_card_exchange_link);
        exchange_description_textView = v.findViewById(R.id.exchange_card_exchange_description);
        exchange_delete_imageButton = v.findViewById(R.id.exchange_card_btn_delete);
    }

}