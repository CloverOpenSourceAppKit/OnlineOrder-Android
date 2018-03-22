package com.cluffies.onlineorder;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import com.clover.sdk.v3.order.LineItem;
import com.clover.sdk.v3.order.Order;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OrdersFragment.OnListFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private LinkedHashMap<String, Order> mReceivedOrders;
    private LinkedHashMap<String, Order> mAcceptedOrders;
    private LinkedHashMap<String, Order> mCompletedOrders;
    private LinkedHashMap<String, Order> mRejectedOrders;

    private OrdersFragment receivedOrdersFragment;
    private OrdersFragment acceptedOrdersFragment;
    private OrdersFragment completedOrdersFragment;
    private OrdersFragment rejectedOrdersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mReceivedOrders = new LinkedHashMap<String, Order>();
        mReceivedOrders = generatePlaceholderOrders();

        mAcceptedOrders = new LinkedHashMap<String, Order>();
        mCompletedOrders = new LinkedHashMap<String, Order>();
        mRejectedOrders = new LinkedHashMap<String, Order>();

        receivedOrdersFragment = OrdersFragment.newInstance(new ArrayList<Order>(mReceivedOrders.values()));
        acceptedOrdersFragment = OrdersFragment.newInstance(new ArrayList<Order>(mAcceptedOrders.values()));
        completedOrdersFragment = OrdersFragment.newInstance(new ArrayList<Order>(mCompletedOrders.values()));
        rejectedOrdersFragment = OrdersFragment.newInstance(new ArrayList<Order>(mRejectedOrders.values()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOrderClick(Order order) {
        if (mReceivedOrders.containsKey(order.getId())) {
            Log.d("ORDER_ACCEPTED", "Accepted Order: " + order.getId());
            Toast.makeText(getBaseContext(), "Accepted Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mReceivedOrders.remove(order.getId());
            mAcceptedOrders.put(order.getId(), new Order(order));
            acceptedOrdersFragment.addOrder(order);
        }

        else if (mAcceptedOrders.containsKey(order.getId())) {
            Log.d("ORDER_COMPLETED", "Completed Order: " + order.getId());
            Toast.makeText(getBaseContext(), "Completed Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mAcceptedOrders.remove(order.getId());
            mCompletedOrders.put(order.getId(), new Order(order));
            completedOrdersFragment.addOrder(order);
        }

        else if (mCompletedOrders.containsKey(order.getId())) {
            Log.d("ORDER_UNDO_COMPLETED", "Undo Completed Order: " + order.getId());
            Toast.makeText(getBaseContext(), "Undo Completed Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mCompletedOrders.remove(order.getId());
            mAcceptedOrders.put(order.getId(), new Order(order));
            acceptedOrdersFragment.addOrder(order);
        }

        else if (mRejectedOrders.containsKey(order.getId())) {
            Log.d("ORDER_UNDO_REJECTED", "Undo Rejected Order: " + order.getId());
            Toast.makeText(getBaseContext(), "Undo Rejected Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mRejectedOrders.remove(order.getId());
            mReceivedOrders.put(order.getId(), new Order(order));
            receivedOrdersFragment.addOrder(order);
        }
    }

    @Override
    public void onOrderLongClick(Order order) {
        if (mReceivedOrders.containsKey(order.getId())) {
            Log.d("ORDER_REJECTED", "Rejected Order: " + order.getId());
            Toast.makeText(getBaseContext(), "Rejected Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mReceivedOrders.remove(order.getId());
            mRejectedOrders.put(order.getId(), new Order(order));
            rejectedOrdersFragment.addOrder(order);
        }

        else if (mAcceptedOrders.containsKey(order.getId())) {
            Log.d("ORDER_UNDO_ACCEPTED", "Undo Accepted Order: " + order.getId());
            Toast.makeText(getBaseContext(), "Undo Accepted Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mAcceptedOrders.remove(order.getId());
            mReceivedOrders.put(order.getId(), new Order(order));
            receivedOrdersFragment.addOrder(order);
        }

        else if (mCompletedOrders.containsKey(order.getId())) {
            Log.d("COMPLETED_ORDER_DELETED", "Deleted Completed Order: " + order.getId());
            Toast.makeText(getBaseContext(), "Deleted Completed Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mCompletedOrders.remove(order.getId());
        }

        else if (mRejectedOrders.containsKey(order.getId())) {
            Log.d("REJECTED_ORDER_DELETED", "Deleted Rejected Order: " + order.getId());
            Toast.makeText(getBaseContext(), "Deleted Rejected Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mRejectedOrders.remove(order.getId());
        }
    }

    /**
     * Generates a list of placeholder orders.
     * TODO: REMOVE BEFORE RELEASE
     */
    public LinkedHashMap<String, Order> generatePlaceholderOrders() {
        LinkedHashMap<String, Order> orders = new LinkedHashMap<String, Order>();
        List<LineItem> lineItems = new ArrayList<LineItem>();

        Order order1 = new Order();
        order1.setId("ANEGC14WQSTFR");
        LineItem potato = new LineItem();
        potato.setName("potato");
        potato.setPrice((long)50);
        lineItems.add(potato);

        order1.setLineItems(lineItems);
        orders.put(order1.getId(), order1);

        Order order2 = new Order();
        order2.setId("102R8PMJB3GSA");
        LineItem tomato = new LineItem();
        tomato.setName("tomato");
        tomato.setPrice((long)100);
        lineItems.add(tomato);

        order2.setLineItems(lineItems);
        orders.put(order2.getId(), order2);

        Order order3 = new Order();
        order3.setId("FKV22M33R00H0");
        LineItem ricatto = new LineItem();
        ricatto.setName("ricatto");
        ricatto.setPrice((long)200);
        lineItems.add(ricatto);

        order3.setLineItems(lineItems);
        orders.put(order3.getId(), order3);

        return orders;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Returns an OrdersFragment.

            switch(position) {
                case 0:
                    return receivedOrdersFragment;
                case 1:
                    return acceptedOrdersFragment;
                case 2:
                    return completedOrdersFragment;
                case 3:
                    return rejectedOrdersFragment;
                default:
                    Log.e("INVALID_FRAGMENT", "Cannot find Fragment at position: " + position + ", defaulting to position 0.");
                    return receivedOrdersFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }
}
