package com.mycryptobinder.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycryptobinder.R;
import com.mycryptobinder.activities.EditExchangeActivity;
import com.mycryptobinder.entities.Exchange;
import com.mycryptobinder.viewholders.ExchangeCardViewHolder;

import java.util.List;


/**
 * Adapter class for exchange cards rendering
 * It acts as a bridge between an AdapterView and the underlying data for that view
 * <p>
 * Created by Yann on 02/05/2017
 */

public class ExchangeCardAdapter extends RecyclerView.Adapter<ExchangeCardViewHolder> {

    private List<Exchange> exchanges;
    private Context context;

    public ExchangeCardAdapter(List<Exchange> exchanges, Context context) {
        this.exchanges = exchanges;
        this.context = context;
    }

    @Override
    public ExchangeCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.card_exchange, parent, false);
        return new ExchangeCardViewHolder(v);
    }

    public void addItems(List<Exchange> exchanges) {
        this.exchanges = exchanges;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ExchangeCardViewHolder holder, int position) {
        // get text from the data set at this position and replace it in the view
        holder.exchange_name_textView.setText(exchanges.get(holder.getAdapterPosition()).getName());
        holder.exchange_link_textView.setText(exchanges.get(holder.getAdapterPosition()).getLink());
        holder.exchange_description_textView.setText(exchanges.get(holder.getAdapterPosition()).getDescription());

        // delete button click
        holder.exchange_delete_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // position of the clicked item
                final int position = holder.getAdapterPosition();
                final String itemName = exchanges.get(position).getName();

                // show a confirm dialog
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(context.getResources().getString(R.string.lbl_confirmation));
                // because Html.fromHtml is deprecated in last Android versions
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    alert.setMessage(Html.fromHtml(context.getResources().getString(R.string.dialog_delete_exchange_message, " <b>" + itemName + "</b>"), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    alert.setMessage(Html.fromHtml(context.getResources().getString(R.string.dialog_delete_exchange_message, " <b>" + itemName + "</b>")));
                }
                alert.setPositiveButton(context.getResources().getString(R.string.lbl_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        /*String item = exchanges.get(position).getName();

                        // delete from the database
                        ExchangeManager em = new ExchangeManager(context);
                        em.open();
                        em.delete(item);

                        // remove the item from the data set
                        exchanges.remove(position);

                        // notify any registered observers that the item previously located at position
                        // has been removed from the data set. The items previously located at and
                        // after position may now be found at oldPosition - 1.
                        notifyItemRemoved(position);

                        // notify any registered observers that the itemCount items starting at
                        // position positionStart have changed.
                        notifyItemRangeChanged(position, exchanges.size());

                        // show a notification about the removed item
                        Toast.makeText(context, context.getResources().getString(R.string.msg_exchange_removed, itemName), Toast.LENGTH_SHORT).show();
*/
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
                TextView idTextView = v.findViewById(R.id.exchange_card_exchange_id);
                TextView nameTextView = v.findViewById(R.id.exchange_card_exchange_name);
                TextView linkTextView = v.findViewById(R.id.exchange_card_exchange_link);
                TextView descriptionTextView = v.findViewById(R.id.exchange_card_exchange_description);

                // get element values
                String id = idTextView.getText().toString();
                String name = nameTextView.getText().toString();
                String link = linkTextView.getText().toString();
                String description = descriptionTextView.getText().toString();

                // store element values in the intent so we can access them later
                Intent edit_exc = new Intent(v.getContext(), EditExchangeActivity.class);
                edit_exc.putExtra("id", id);
                edit_exc.putExtra("name", name);
                edit_exc.putExtra("link", link);
                edit_exc.putExtra("description", description);
                v.getContext().startActivity(edit_exc);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exchanges.size();
    }

}
