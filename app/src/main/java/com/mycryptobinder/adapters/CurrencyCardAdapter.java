package com.mycryptobinder.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.activities.AddCurrencyActivity;
import com.mycryptobinder.activities.EditCurrencyActivity;
import com.mycryptobinder.managers.CurrencyManager;
import com.mycryptobinder.models.Currency;
import com.mycryptobinder.viewholders.CurrencyCardViewHolder;

import java.util.List;

/**
 * Adapter class for currency cards rendering
 * It acts as a bridge between an AdapterView and the underlying data for that view
 * <p>
 * Created by Yann on 25/05/2017
 */

public class CurrencyCardAdapter extends RecyclerView.Adapter<CurrencyCardViewHolder> {

    private List<Currency> currencies;
    private Context context;

    public CurrencyCardAdapter(Context context, List<Currency> currencies) {
        this.currencies = currencies;
        this.context = context;
    }

    @Override
    public CurrencyCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.card_currency, parent, false);
        return new CurrencyCardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CurrencyCardViewHolder holder, int position) {
        // get text from the data set at this position and replace it in the view
        holder.currency_id_textView.setText((String.valueOf(currencies.get(holder.getAdapterPosition()).getId())));
        holder.currency_name_textView.setText(currencies.get(holder.getAdapterPosition()).getName());
        holder.currency_isocode_textView.setText(currencies.get(holder.getAdapterPosition()).getIsoCode());
        holder.currency_symbol_textView.setText(currencies.get(holder.getAdapterPosition()).getSymbol());

        // delete button click
        holder.currency_delete_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // position of the clicked item
                final int position = holder.getAdapterPosition();
                final String itemName = currencies.get(position).getName();

                // show a confirm dialog
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(context.getResources().getString(R.string.lbl_confirmation));
                // because Html.fromHtml is deprecated in last Android versions
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    alert.setMessage(Html.fromHtml(context.getResources().getString(R.string.dialog_delete_currency_message, " <b>" + itemName + "</b>"), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    alert.setMessage(Html.fromHtml(context.getResources().getString(R.string.dialog_delete_currency_message, " <b>" + itemName + "</b>")));
                }
                alert.setPositiveButton(context.getResources().getString(R.string.lbl_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        long itemId = currencies.get(position).getId();

                        // delete from the database
                        CurrencyManager cm = new CurrencyManager(context);
                        cm.open();
                        cm.delete(itemId);

                        // remove the item from the data set
                        currencies.remove(position);

                        // notify any registered observers that the item previously located at position
                        // has been removed from the data set. The items previously located at and
                        // after position may now be found at oldPosition - 1.
                        notifyItemRemoved(position);

                        // notify any registered observers that the itemCount items starting at
                        // position positionStart have changed.
                        notifyItemRangeChanged(position, currencies.size());

                        // show a notification about the removed item
                        Toast.makeText(context, context.getResources().getString(R.string.msg_currency_removed, itemName), Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton(context.getResources().getString(R.string.lbl_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        // card click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get view elements
                TextView idTextView = (TextView) v.findViewById(R.id.currency_card_currency_id);
                TextView nameTextView = (TextView) v.findViewById(R.id.currency_card_currency_name);
                TextView isoCodeTextView = (TextView) v.findViewById(R.id.currency_card_currency_iso_code);
                TextView symbolTextView = (TextView) v.findViewById(R.id.currency_card_currency_symbol);

                // get element values
                String id = idTextView.getText().toString();
                String name = nameTextView.getText().toString();
                String isoCode = isoCodeTextView.getText().toString();
                String symbol = symbolTextView.getText().toString();

                // store element values in the intent so we can access them later
                Intent edit_cur = new Intent(v.getContext(), EditCurrencyActivity.class);
                edit_cur.putExtra("id", id);
                edit_cur.putExtra("name", name);
                edit_cur.putExtra("isoCode", isoCode);
                edit_cur.putExtra("symbol", symbol);
                v.getContext().startActivity(edit_cur);
            }
        });
    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

}
