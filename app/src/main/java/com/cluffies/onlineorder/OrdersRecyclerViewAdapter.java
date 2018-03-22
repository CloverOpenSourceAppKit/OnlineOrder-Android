package com.cluffies.onlineorder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clover.sdk.v3.order.LineItem;
import com.clover.sdk.v3.order.Order;
import com.cluffies.onlineorder.OrdersFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Order} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<OrdersRecyclerViewAdapter.ViewHolder> {

    private List<Order> mOrders;
    private final OnListFragmentInteractionListener mListener;

    public OrdersRecyclerViewAdapter(List<Order> orders, OnListFragmentInteractionListener listener) {
        mOrders = new ArrayList<Order>();

        for (Order order : orders) {
            mOrders.add(new Order(order));
        }

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
                    mListener.onOrderClick(holder.mOrder);
                    removeOrderAtPosition(holder.getAdapterPosition());
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onOrderLongClick(holder.mOrder);
                    removeOrderAtPosition(holder.getAdapterPosition());
                }

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    public void addOrder(Order order) {
        if (order == null) {
            Log.e("NULL_ORDER", "Cannot add a null order to this " + getClass().getSimpleName());
            return;
        }

        int position = getItemCount();
        mOrders.add(new Order(order));

        notifyItemInserted(position);
    }

    public void removeOrderAtPosition(int position) {
        if (position >= getItemCount()) {
            Log.e("POSITION_OUT_OF_BOUNDS", "No order at position " + position + "; it is out of bounds.");
            return;
        }

        mOrders.remove(position);
        notifyItemRemoved(position);
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
