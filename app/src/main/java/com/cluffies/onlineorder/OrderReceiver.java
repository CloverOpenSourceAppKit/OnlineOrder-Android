package com.cluffies.onlineorder;

import android.accounts.Account;
import android.content.Context;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v1.app.AppNotificationReceiver;
import com.clover.sdk.v1.app.AppNotification;
import com.clover.sdk.v3.order.Order;
import com.clover.sdk.v3.order.OrderConnector;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderReceiver extends AppNotificationReceiver {
    private static final String ONLINE_ORDER_EVENT = "online_order";
    private Account mAccount;
    private OrderConnector mOrderConnector;
    private OrderReceiverListener mListener;

    public OrderReceiver() {
    }

    public OrderReceiver(Context context, OrderReceiverListener listener) {
        super.register(context);

        mAccount = CloverAccount.getAccount(context);

        if (mAccount == null) {
            throw new RuntimeException("Cannot get Clover Account");
        }

        mOrderConnector = new OrderConnector(context, mAccount,null);

        mListener = listener;
    }

    @Override
    public void onReceive(Context context, AppNotification notification) {
        if (notification.appEvent.equals(ONLINE_ORDER_EVENT)) {
            new AsyncTask<AppNotification, Void, Void>() {
                @Override
                protected Void doInBackground(AppNotification... params) {
                    try {
                        AppNotification notification = params[0];
                        JSONObject data = new JSONObject(notification.payload);
                        JSONObject orderData = data.getJSONObject("order");
                        String orderId = orderData.getString("id");

                        Order order = mOrderConnector.getOrder(orderId);

                        if (order != null) {
                            mListener.onOrderReceive(order);
                        }
                    }

                    catch (JSONException | RemoteException | ClientException | ServiceException | BindingException e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, notification);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * OrderReceiver to be notified when an order is received.
     */
    public interface OrderReceiverListener {
        void onOrderReceive(Order order);
    }
}
