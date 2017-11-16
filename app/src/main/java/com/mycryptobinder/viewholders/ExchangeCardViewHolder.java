package com.mycryptobinder.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mycryptobinder.R;

/**
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item,
 * and you provide access to all the views for a data item in a view holder
 * <p>
 * Created by Yann on 02/05/2017
 */

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