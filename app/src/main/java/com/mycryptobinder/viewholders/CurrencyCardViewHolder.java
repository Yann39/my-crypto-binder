package com.mycryptobinder.viewholders;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mycryptobinder.R;
import com.mycryptobinder.activities.EditCurrencyActivity;
import com.mycryptobinder.adapters.CurrencyCardAdapter;
import com.mycryptobinder.entities.Currency;
import com.mycryptobinder.managers.CurrencyManager;
import com.mycryptobinder.viewmodels.CurrencyListViewModel;

import java.util.List;

/**
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item,
 * and you provide access to all the views for a data item in a view holder
 * <p>
 * Created by Yann on 26/05/2017
 */

public class CurrencyCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView currency_name_textView;
    public TextView currency_iso_code_textView;
    public TextView currency_symbol_textView;
    public List<Currency> currencies;
    private Context context;
    Currency currency;

    CurrencyListener listener;

    public interface CurrencyListener{
        void onDeleteClicked(Currency item);
    }

    public void setItem(Currency item) {
        this.currency = currency;
    }

    public CurrencyCardViewHolder(View view, final CurrencyListener listener) {
        super(view);
        view.setOnClickListener(this);

        currency_name_textView = view.findViewById(R.id.currency_card_currency_name);
        currency_iso_code_textView = view.findViewById(R.id.currency_card_currency_iso_code);
        currency_symbol_textView = view.findViewById(R.id.currency_card_currency_symbol);
        ImageView currency_delete_imageButton = view.findViewById(R.id.currency_card_btn_delete);

        context = view.getContext();

        // delete button click
        currency_delete_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onDeleteClicked(currency);

                // position of the clicked item
                final int position = getAdapterPosition();
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

                        String item = currencies.get(position).getIsoCode();

                        // delete from the database
                        CurrencyManager cm = new CurrencyManager(context);
                        cm.open();
                        cm.delete(item);

                        // remove the item from the data set
                        currencies.remove(position);

                        // notify any registered observers that the item previously located at position
                        // has been removed from the data set. The items previously located at and
                        // after position may now be found at oldPosition - 1.
                        adapter.notifyItemRemoved(position);

                        // notify any registered observers that the itemCount items starting at
                        // position positionStart have changed.
                        adapter.notifyItemRangeChanged(position, currencies.size());

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

    }

    @Override
    public void onClick(View view) {
        // get element values
        String id = currency_id_textView.getText().toString();
        String name = currency_name_textView.getText().toString();
        String isoCode = currency_iso_code_textView.getText().toString();
        String symbol = currency_symbol_textView.getText().toString();

        // store element values in the intent so we can access them later
        Intent edit_cur = new Intent(view.getContext(), EditCurrencyActivity.class);
        edit_cur.putExtra("id", id);
        edit_cur.putExtra("name", name);
        edit_cur.putExtra("isoCode", isoCode);
        edit_cur.putExtra("symbol", symbol);
        view.getContext().startActivity(edit_cur);
    }
}