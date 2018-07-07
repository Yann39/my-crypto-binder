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

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.activities.EditExchangeActivity;
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.viewholders.ExchangeCardViewHolder;

import java.util.List;

public class ExchangeCardAdapter extends RecyclerView.Adapter<ExchangeCardViewHolder> {

    private List<Exchange> exchanges;
    private Context context;
    private View.OnClickListener clickListener;

    public ExchangeCardAdapter(List<Exchange> exchanges, Context context, View.OnClickListener clickListener) {
        this.exchanges = exchanges;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ExchangeCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.card_exchange, parent, false);
        return new ExchangeCardViewHolder(v);
    }

    public void addItems(List<Exchange> exchanges) {
        this.exchanges = exchanges;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final ExchangeCardViewHolder holder, int position) {
        // get text from the data set at this position and replace it in the view
        Exchange exchange = exchanges.get(holder.getAdapterPosition());
        holder.exchange_name_textView.setText(exchange.getName());
        holder.exchange_link_textView.setText(exchange.getLink());
        holder.exchange_description_textView.setText(exchange.getDescription());
        holder.exchange_publicApiKey_textView.setText(exchange.getPublicApiKey());
        holder.exchange_privateApiKey_textView.setText(exchange.getPrivateApiKey());

        // delete button click
        holder.exchange_delete_imageButton.setOnClickListener(clickListener);

        // set exchange as tag so we can get it in the click listener later
        holder.exchange_delete_imageButton.setTag(exchange);

        // card click
        holder.itemView.setOnClickListener(v -> {
            // get view elements
            TextView nameTextView = v.findViewById(R.id.exchange_card_exchange_name);
            TextView linkTextView = v.findViewById(R.id.exchange_card_exchange_link);
            TextView descriptionTextView = v.findViewById(R.id.exchange_card_exchange_description);
            TextView publicApiKeyTextView = v.findViewById(R.id.exchange_card_exchange_publicApiKey);
            TextView privateApiKeyTextView = v.findViewById(R.id.exchange_card_exchange_privateApiKey);

            // get element values
            String name = nameTextView.getText().toString();
            String link = linkTextView.getText().toString();
            String description = descriptionTextView.getText().toString();
            String publicApiKey = publicApiKeyTextView.getText().toString();
            String privateApiKey = privateApiKeyTextView.getText().toString();

            // store element values in the intent so we can access them later
            Intent edit_exc = new Intent(v.getContext(), EditExchangeActivity.class);
            edit_exc.putExtra("name", name);
            edit_exc.putExtra("link", link);
            edit_exc.putExtra("description", description);
            edit_exc.putExtra("publicApiKey", publicApiKey);
            edit_exc.putExtra("privateApiKey", privateApiKey);
            v.getContext().startActivity(edit_exc);
        });
    }

    @Override
    public int getItemCount() {
        return exchanges.size();
    }

}