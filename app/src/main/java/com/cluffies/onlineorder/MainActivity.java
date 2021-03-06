package com.cluffies.onlineorder;

import android.accounts.Account;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.preference.PreferenceManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v3.order.Order;
import com.clover.sdk.v3.order.OrderConnector;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity implements OrdersFragment.OrderFragmentListener {

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

    private OrdersFragment mReceivedOrdersFragment;
    private OrdersFragment mAcceptedOrdersFragment;
    private OrdersFragment mCompletedOrdersFragment;
    private OrdersFragment mRejectedOrdersFragment;

    private Account mAccount;
    private OrderConnector mOrderConnector;
    private OrderReceiver mOrderReceiver;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private static final String RECEIVED_ORDERS_PREF_KEY = "received_orders";
    private static final String ACCEPTED_ORDERS_PREF_KEY = "accepted_orders";
    private static final String COMPLETED_ORDERS_PREF_KEY = "completed_orders";
    private static final String REJECTED_ORDERS_PREF_KEY = "rejected_orders";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the four
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        // TODO: REMOVE OR IMPLEMENT BEFORE RELEASE
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mReceivedOrders = new LinkedHashMap<String, Order>();
        mAcceptedOrders = new LinkedHashMap<String, Order>();
        mCompletedOrders = new LinkedHashMap<String, Order>();
        mRejectedOrders = new LinkedHashMap<String, Order>();

        mReceivedOrdersFragment = OrdersFragment.newInstance(new ArrayList<Order>(mReceivedOrders.values()));
        mAcceptedOrdersFragment = OrdersFragment.newInstance(new ArrayList<Order>(mAcceptedOrders.values()));
        mCompletedOrdersFragment = OrdersFragment.newInstance(new ArrayList<Order>(mCompletedOrders.values()));
        mRejectedOrdersFragment = OrdersFragment.newInstance(new ArrayList<Order>(mRejectedOrders.values()));

        mAccount = CloverAccount.getAccount(this);

        if (mAccount == null) {
            throw new RuntimeException("Cannot get Clover Account");
        }

        mOrderConnector = new OrderConnector(this, mAccount,null);

        mOrderReceiver = new OrderReceiver(this, new OrderReceiver.OrderReceiverListener() {
            @Override
            public void onOrderReceive(String orderId) {
                try {
                    final Order order = mOrderConnector.getOrder(orderId);

                    if (order != null) {
                        if (!mReceivedOrders.containsKey(order.getId())
                                && !mAcceptedOrders.containsKey(order.getId())
                                && !mCompletedOrders.containsKey(order.getId())
                                && !mRejectedOrders.containsKey(order.getId())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(findViewById(R.id.main_content), "Online Order Received: "
                                            + order.getId(), Snackbar.LENGTH_SHORT).show();

                                    mReceivedOrders.put(order.getId(), new Order(order));
                                    mReceivedOrdersFragment.addOrder(order);
                                    saveOrders(RECEIVED_ORDERS_PREF_KEY);

                                    applyOrders();
                                }
                            });
                        }
                    }
                }

                catch (RemoteException | ClientException | ServiceException | BindingException e) {
                    e.printStackTrace();
                }
            }
        });

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        new LoadOrdersAsyncTask().execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mOrderReceiver.unregister();
    }

    /**
     * TODO: REMOVE OR IMPLEMENT BEFORE RELEASE
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * TODO: REMOVE OR IMPLEMENT BEFORE RELEASE
     */
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
    public void onOrderClick(int position, Order order) {
        if (mReceivedOrders.containsKey(order.getId())) {
            Toast.makeText(getBaseContext(), "Accepted Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mReceivedOrders.remove(order.getId());
            mReceivedOrdersFragment.removeOrderAtPosition(position);
            saveOrders(RECEIVED_ORDERS_PREF_KEY);

            mAcceptedOrders.put(order.getId(), new Order(order));
            mAcceptedOrdersFragment.addOrder(order);
            saveOrders(ACCEPTED_ORDERS_PREF_KEY);

            applyOrders();
        }

        else if (mAcceptedOrders.containsKey(order.getId())) {
            Toast.makeText(getBaseContext(), "Completed Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mAcceptedOrders.remove(order.getId());
            mAcceptedOrdersFragment.removeOrderAtPosition(position);
            saveOrders(ACCEPTED_ORDERS_PREF_KEY);

            mCompletedOrders.put(order.getId(), new Order(order));
            mCompletedOrdersFragment.addOrder(order);
            saveOrders(COMPLETED_ORDERS_PREF_KEY);

            applyOrders();
        }

        else if (mCompletedOrders.containsKey(order.getId())) {
            Toast.makeText(getBaseContext(), "Undo Completed Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mCompletedOrders.remove(order.getId());
            mCompletedOrdersFragment.removeOrderAtPosition(position);
            saveOrders(COMPLETED_ORDERS_PREF_KEY);

            mAcceptedOrders.put(order.getId(), new Order(order));
            mAcceptedOrdersFragment.addOrder(order);
            saveOrders(ACCEPTED_ORDERS_PREF_KEY);

            applyOrders();
        }

        else if (mRejectedOrders.containsKey(order.getId())) {
            Toast.makeText(getBaseContext(), "Undo Rejected Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mRejectedOrders.remove(order.getId());
            mRejectedOrdersFragment.removeOrderAtPosition(position);
            saveOrders(REJECTED_ORDERS_PREF_KEY);

            mReceivedOrders.put(order.getId(), new Order(order));
            mReceivedOrdersFragment.addOrder(order);
            saveOrders(RECEIVED_ORDERS_PREF_KEY);

            applyOrders();
        }
    }

    @Override
    public void onOrderLongClick(int position, Order order) {
        if (mReceivedOrders.containsKey(order.getId())) {
            Toast.makeText(getBaseContext(), "Rejected Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mReceivedOrders.remove(order.getId());
            mReceivedOrdersFragment.removeOrderAtPosition(position);
            saveOrders(RECEIVED_ORDERS_PREF_KEY);

            mRejectedOrders.put(order.getId(), new Order(order));
            mRejectedOrdersFragment.addOrder(order);
            saveOrders(REJECTED_ORDERS_PREF_KEY);

            applyOrders();
        }

        else if (mAcceptedOrders.containsKey(order.getId())) {
            Toast.makeText(getBaseContext(), "Undo Accepted Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mAcceptedOrders.remove(order.getId());
            mAcceptedOrdersFragment.removeOrderAtPosition(position);
            saveOrders(ACCEPTED_ORDERS_PREF_KEY);

            mReceivedOrders.put(order.getId(), new Order(order));
            mReceivedOrdersFragment.addOrder(order);
            saveOrders(RECEIVED_ORDERS_PREF_KEY);

            applyOrders();
        }

        else if (mCompletedOrders.containsKey(order.getId())) {
            Toast.makeText(getBaseContext(), "Deleted Completed Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mCompletedOrders.remove(order.getId());
            mCompletedOrdersFragment.removeOrderAtPosition(position);
            saveOrders(COMPLETED_ORDERS_PREF_KEY);

            applyOrders();
        }

        else if (mRejectedOrders.containsKey(order.getId())) {
            Toast.makeText(getBaseContext(), "Deleted Rejected Order: " + order.getId(), Toast.LENGTH_SHORT).show();

            mRejectedOrders.remove(order.getId());
            mRejectedOrdersFragment.removeOrderAtPosition(position);
            saveOrders(REJECTED_ORDERS_PREF_KEY);

            applyOrders();
        }
    }

    /**
     * Saves the list of orders specified by ordersPrefKey to SharedPreferences, but does not apply
     * them to memory.
     * @param ordersPrefKey The key to specify which list of orders to save.
     */
    private void saveOrders(String ordersPrefKey) {
        if (ordersPrefKey.equals(RECEIVED_ORDERS_PREF_KEY)) {
            String receivedOrders = jsonifyOrders(mReceivedOrders);
            mEditor.putString(RECEIVED_ORDERS_PREF_KEY, receivedOrders);
        }

        else if (ordersPrefKey.equals(ACCEPTED_ORDERS_PREF_KEY)) {
            String acceptedOrders = jsonifyOrders(mAcceptedOrders);
            mEditor.putString(ACCEPTED_ORDERS_PREF_KEY, acceptedOrders);
        }

        else if (ordersPrefKey.equals(COMPLETED_ORDERS_PREF_KEY)) {
            String completedOrders = jsonifyOrders(mCompletedOrders);
            mEditor.putString(COMPLETED_ORDERS_PREF_KEY, completedOrders);
        }

        else if (ordersPrefKey.equals(REJECTED_ORDERS_PREF_KEY)) {
            String rejectedOrders = jsonifyOrders(mRejectedOrders);
            mEditor.putString(REJECTED_ORDERS_PREF_KEY, rejectedOrders);
        }
    }

    /**
     * Applies the orders saved in SharedPreferences to memory.
     */
    private void applyOrders() {
        mEditor.apply();
    }

    private String jsonifyOrders(LinkedHashMap<String, Order> orders) {
        JSONArray jsonOrders = new JSONArray();

        for (Order order : orders.values()) {
            jsonOrders.put(order.getId());
        }

        return jsonOrders.toString();
    }

    private LinkedHashMap<String, Order> unjsonifyOrders(String jsonOrders) {
        LinkedHashMap<String, Order> orders = new LinkedHashMap<String, Order>();

        try {
            JSONArray orderIds = new JSONArray(jsonOrders);

            for (int i = 0; i < orderIds.length(); i++) {
                Order order = mOrderConnector.getOrder(orderIds.getString(i));

                if (order != null) {
                    orders.put(order.getId(), order);
                }
            }
        }

        catch (JSONException | RemoteException | ClientException | ServiceException | BindingException e) {
            e.printStackTrace();
        }

        return orders;
    }

    private class LoadOrdersAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String receivedOrders = mPreferences.getString(RECEIVED_ORDERS_PREF_KEY, null);
            String acceptedOrders = mPreferences.getString(ACCEPTED_ORDERS_PREF_KEY, null);
            String completedOrders = mPreferences.getString(COMPLETED_ORDERS_PREF_KEY, null);
            String rejectedOrders = mPreferences.getString(REJECTED_ORDERS_PREF_KEY, null);

            if (receivedOrders != null) {
                mReceivedOrders = unjsonifyOrders(receivedOrders);
            }

            if (acceptedOrders != null) {
                mAcceptedOrders = unjsonifyOrders(acceptedOrders);
            }

            if (completedOrders != null) {
                mCompletedOrders = unjsonifyOrders(completedOrders);
            }

            if (rejectedOrders != null) {
                mRejectedOrders = unjsonifyOrders(rejectedOrders);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mReceivedOrdersFragment.addOrders(new ArrayList<Order>(mReceivedOrders.values()));
                    mAcceptedOrdersFragment.addOrders(new ArrayList<Order>(mAcceptedOrders.values()));
                    mCompletedOrdersFragment.addOrders(new ArrayList<Order>(mCompletedOrders.values()));
                    mRejectedOrdersFragment.addOrders(new ArrayList<Order>(mRejectedOrders.values()));
                }
            });

            return null;
        }
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
                    return mReceivedOrdersFragment;
                case 1:
                    return mAcceptedOrdersFragment;
                case 2:
                    return mCompletedOrdersFragment;
                case 3:
                    return mRejectedOrdersFragment;
                default:
                    return mReceivedOrdersFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }
}
