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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import com.clover.sdk.v3.order.LineItem;
import com.clover.sdk.v3.order.Order;
import com.cluffies.onlineorder.tabs.OrdersReceivedFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OrdersReceivedFragment.OnListFragmentInteractionListener {

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

    private List<Order> mOrders;

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
    public void onOrderAccepted(Order order) {
        Log.d("ORDER_ACCEPTED", "Accepted Order: " + order.getId());
        Toast.makeText(getBaseContext(), "Accepted Order: " + order.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOrderRejected(Order order) {
        Log.d("ORDER_REJECTED", "Rejected Order: " + order.getId());
        Toast.makeText(getBaseContext(), "Rejected Order: " + order.getId(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Generates a list of placeholder orders.
     * TODO: REMOVE BEFORE RELEASE
     */
    public List<Order> generatePlaceholderOrders() {
        List<Order> orders = new ArrayList<>();
        List<LineItem> lineItems = new ArrayList<LineItem>();

        Order order1 = new Order();
        order1.setId("ANEGC14WQSTFR");
        LineItem potato = new LineItem();
        potato.setName("potato");
        potato.setPrice((long)50);
        lineItems.add(potato);

        order1.setLineItems(lineItems);
        orders.add(order1);

        Order order2 = new Order();
        order2.setId("102R8PMJB3GSA");
        LineItem tomato = new LineItem();
        tomato.setName("tomato");
        tomato.setPrice((long)100);
        lineItems.add(tomato);

        order2.setLineItems(lineItems);
        orders.add(order2);

        Order order3 = new Order();
        order3.setId("FKV22M33R00H0");
        LineItem ricatto = new LineItem();
        ricatto.setName("ricatto");
        ricatto.setPrice((long)200);
        lineItems.add(ricatto);

        order3.setLineItems(lineItems);
        orders.add(order3);

        // Generate more data to test vertical scrolling.
        for (int i = 0; i < 3; i++) {
            orders.add(order1);
            orders.add(order2);
            orders.add(order3);
        }

        return orders;
    }

    /**
     * A placeholder fragment containing a simple view.
     * TODO: REMOVE BEFORE RELEASE
     */
    public static class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_order_status, container, false);

            return rootView;
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
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return OrdersReceivedFragment.newInstance(generatePlaceholderOrders());
            }

            else {
                return new PlaceholderFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
