package com.cluffies.onlineorder;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clover.sdk.v3.order.Order;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A fragment representing a list of Orders.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class OrdersFragment extends Fragment {

    private static final String ARG_ORDERS = "orders";
    private LinkedHashMap<String, Order> mOrders;
    private OrdersRecyclerViewAdapter ordersRecyclerViewAdapter;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OrdersFragment() {
    }

    public static OrdersFragment newInstance(List<Order> orders) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();

        args.putParcelableArrayList(ARG_ORDERS, (ArrayList<Order>) orders);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ArrayList<Order> argOrders = getArguments().<Order>getParcelableArrayList(ARG_ORDERS);

            mOrders = new LinkedHashMap<String, Order>();

            for (Order order : argOrders) {
                mOrders.put(order.getId(), new Order(order));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();

            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            ordersRecyclerViewAdapter = new OrdersRecyclerViewAdapter(new ArrayList<Order>(mOrders.values()), mListener);

            recyclerView.setAdapter(ordersRecyclerViewAdapter);
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void addOrder(Order order) {
        if (order == null) {
            Log.e("NULL_ORDER", "Cannot add a null order to " + getClass().getSimpleName());
            return;
        }

        ordersRecyclerViewAdapter.addOrder(order);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onOrderClick(Order order);
        void onOrderLongClick(Order order);
    }
}
