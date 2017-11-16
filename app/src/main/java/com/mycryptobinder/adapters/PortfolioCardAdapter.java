package com.mycryptobinder.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mycryptobinder.R;
import com.mycryptobinder.managers.TransactionManager;
import com.mycryptobinder.models.Currency;
import com.mycryptobinder.models.HoldingData;
import com.mycryptobinder.viewholders.PortfolioCardViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

/**
 * Adapter class for portfolio cards rendering
 * It acts as a bridge between an AdapterView and the underlying data for that view
 * <p>
 * Created by Yann on 08/11/2017
 */

public class PortfolioCardAdapter extends RecyclerView.Adapter<PortfolioCardViewHolder> {

    private List<Currency> currencies;
    private List<HoldingData> hdList;
    private Context context;
    private String isoCode;
    private Double currPrice;
    private DecimalFormat df;
    private CursorAdapter mCursorAdapter;

    //public PortfolioCardAdapter(Context context, List<Currency> currencies) {
    public PortfolioCardAdapter(Context context, /*Cursor c*/ List<HoldingData> hdList) {
        //this.currencies = currencies;
        this.hdList = hdList;
        this.context = context;
        df = new DecimalFormat("#.##");

        /*mCursorAdapter = new CursorAdapter(context, c, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                // Inflate the view here
                return LayoutInflater.from(context).inflate(R.layout.card_portfolio, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                // Binding operations
            }
        };*/
    }

    @Override
    public PortfolioCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.card_portfolio, parent, false);
        //return new PortfolioCardViewHolder(v);
        /*View v = mCursorAdapter.newView(context, mCursorAdapter.getCursor(), parent);*/

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

        /*mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.itemView, context, mCursorAdapter.getCursor());
        holder.portfolio_currency_iso_code_textView.setText(mCursorAdapter.getCursor().getString(0));
        holder.portfolio_holding_quantity_textView.setText(df.format(mCursorAdapter.getCursor().getDouble(1)));

        final Double quantity = mCursorAdapter.getCursor().getDouble(1);*/

        isoCode = hdList.get(holder.getAdapterPosition()).getIsoCode();
        Double quantity = hdList.get(holder.getAdapterPosition()).getQuantity();
        Double currentPrice = hdList.get(holder.getAdapterPosition()).getCurrentPrice();
        Double currentValue = hdList.get(holder.getAdapterPosition()).getCurrentValue();

        holder.portfolio_currency_iso_code_textView.setText(isoCode);
        holder.portfolio_holding_quantity_textView.setText(currentPrice != null ? df.format(quantity) : "0.00");
        holder.portfolio_card_price_24h_change.setText(currentPrice != null ? df.format(8.56) : "0.00");
        holder.portfolio_holding_total_value_textView.setText(currentPrice != null ? df.format(quantity*currentPrice) : "0.00");
        holder.portfolio_card_current_price.setText(currentPrice != null ? df.format(currentPrice) : "0.00");

        //holder.portfolio_card_current_price.setText(df.format(currPrice));
        //holder.portfolio_holding_total_value_textView.setText(df.format(currPrice));

        /*TransactionManager tm = new TransactionManager(context);
        tm.open();

        final Double quantity = tm.getCurrencyQuantity(isoCode);

        // get text from the data set at this position and replace it in the view
        //holder.currency_id_textView.setText((String.valueOf(currencies.get(holder.getAdapterPosition()).getId())));
        isoCode = currencies.get(holder.getAdapterPosition()).getIsoCode();
        holder.portfolio_currency_iso_code_textView.setText(isoCode);
        holder.portfolio_holding_quantity_textView.setText(df.format(quantity));

        AsyncHttpClient client = new AsyncHttpClient();
        String params = "?fsym=" + isoCode + "&tsyms=EUR&extraParams=your_app_name";
        client.get(properties.getProperty("CRYPTOCOMPARE_API_BASE_URL") + "price" + params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String val = response.getString("EUR");
                    Double res;
                    try {
                        res = Double.parseDouble(val);
                    } catch (NumberFormatException e) {
                        res = null;
                    }
                    currPrice = res;
                    holder.portfolio_card_current_price.setText(df.format(currPrice));
                    holder.portfolio_holding_total_value_textView.setText(df.format(quantity * currPrice));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                System.out.println("Error " + statusCode);
            }
        });


        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, -1);
        long time = c.getTime().getTime();
        params = "?fsym=" + isoCode + "&tsyms=EUR&ts=" + time + "&extraParams=your_app_name";
        client.get(properties.getProperty("CRYPTOCOMPARE_API_BASE_URL") + "pricehistorical" + params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject json_data = response.getJSONObject(isoCode);
                    Double res = Double.parseDouble(json_data.getString("EUR"));
                    if (res < currPrice) {
                        Double percent = (res * 100f) / currPrice;
                        holder.portfolio_card_price_24h_change.setText(df.format(percent));
                        holder.portfolio_card_price_24h_change.setTextColor(Color.GREEN);
                    } else {
                        Double percent = (currPrice * 100f) / res;
                        holder.portfolio_card_price_24h_change.setText(df.format(percent));
                        holder.portfolio_card_price_24h_change.setTextColor(Color.RED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                System.out.println("Error " + statusCode);
            }
        });

        // card click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/



    }

    @Override
    public int getItemCount() {
        //return currencies.size();
        //return mCursorAdapter.getCount();
        return hdList.size();
    }

}
