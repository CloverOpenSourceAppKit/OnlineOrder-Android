package com.cluffies.onlineorder.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clover.sdk.v3.order.Order;
import com.cluffies.onlineorder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Orders received.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class OrdersReceivedFragment extends Fragment {

    private static final String ARG_ORDERS = "orders";
    private List<Order> mOrders;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OrdersReceivedFragment() {
    }

    public static OrdersReceivedFragment newInstance(List<Order> orders) {
        OrdersReceivedFragment fragment = new OrdersReceivedFragment();
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

            mOrders = new ArrayList<Order>();

            for (Order order : argOrders) {
                mOrders.add(new Order(order));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_received_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();

            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new OrdersReceivedRecyclerViewAdapter(mOrders, mListener));
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onOrderAccepted(Order order);
        void onOrderRejected(Order order);
    }
}
