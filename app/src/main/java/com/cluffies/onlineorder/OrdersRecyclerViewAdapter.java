package com.cluffies.onlineorder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clover.sdk.v3.order.LineItem;
import com.clover.sdk.v3.order.Order;
import com.cluffies.onlineorder.OrdersFragment.OrderFragmentListener;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Order} and makes a call to the
 * specified {@link OrdersFragment.OrderFragmentListener}.
 */
public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<OrdersRecyclerViewAdapter.ViewHolder> {

    private List<Order> mOrders;
    private final OrderFragmentListener mListener;

    public OrdersRecyclerViewAdapter(List<Order> orders, OrderFragmentListener listener) {
        mOrders = orders;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mOrder = mOrders.get(position);
        holder.mIdView.setText(mOrders.get(position).getId());

        StringBuilder lineItems = new StringBuilder();
        ListIterator<LineItem> it = mOrders.get(position).getLineItems().listIterator();

        // Build a string containing all line items.
        while (it.hasNext()) {
            lineItems.append(it.next().getName());

            if (it.hasNext()) {
                lineItems.append(System.getProperty("line.separator"));
            }
        }

        holder.mContentView.setText(lineItems.toString());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onOrderClick(position, holder.mOrder);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onOrderLongClick(position, holder.mOrder);
                }

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Order mOrder;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.order_id);
            mContentView = (TextView) view.findViewById(R.id.line_items);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }
}
