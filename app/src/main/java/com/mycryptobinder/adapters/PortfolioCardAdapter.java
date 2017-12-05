package com.mycryptobinder.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mycryptobinder.R;
import com.mycryptobinder.models.HoldingData;
import com.mycryptobinder.viewholders.PortfolioCardViewHolder;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

/**
 * Adapter class for portfolio cards rendering
 * It acts as a bridge between an AdapterView and the underlying data for that view
 * <p>
 * Created by Yann on 08/11/2017
 */

public class PortfolioCardAdapter extends RecyclerView.Adapter<PortfolioCardViewHolder> {

    private List<HoldingData> holdingData;
    private Context context;
    private DecimalFormat df;

    public PortfolioCardAdapter(Context context, List<HoldingData> hdList) {
        this.holdingData = hdList;
        this.context = context;
        df = new DecimalFormat("#.##");
    }

    public void setItems(List<HoldingData> holdingData) {
        this.holdingData = holdingData;
        notifyDataSetChanged();
    }

    @Override
    public PortfolioCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the layout (create a new view)
        View v = LayoutInflater.from(context).inflate(R.layout.card_portfolio, parent, false);

        // get a random color from a predefined color array and apply it to the image view background drawable
        TypedArray ar = context.getResources().obtainTypedArray(R.array.currency_backgrounds);
        int len = ar.length();
        int[] picArray = new int[len];
        for (int i = 0; i < len; i++) {
            picArray[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();
        int randomAndroidColor = picArray[new Random().nextInt(picArray.length)];
        ImageView imageView = v.findViewById(R.id.portfolio_card_currency_logo);
        DrawableCompat.setTint(imageView.getBackground(), ContextCompat.getColor(context, randomAndroidColor));

        return new PortfolioCardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PortfolioCardViewHolder holder, int position) {
        HoldingData hd = holdingData.get(position);

        Double quantity = holdingData.get(position).getQuantity();
        Double currentPrice = holdingData.get(position).getCurrentPrice();

        holder.portfolio_currency_iso_code_textView.setText(hd.getIsoCode());
        holder.portfolio_holding_quantity_textView.setText(quantity != null ? df.format(quantity) : "0.00");
        holder.portfolio_card_price_24h_change.setText(df.format(8.56));
        holder.portfolio_holding_total_value_textView.setText((quantity != null && currentPrice != null) ? df.format(quantity * currentPrice) : "0.00");
        holder.portfolio_card_current_price.setText(currentPrice != null ? df.format(currentPrice) : "0.00");
    }

    @Override
    public int getItemCount() {
        return holdingData.size();
    }

}