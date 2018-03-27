package com.cluffies.onlineorder;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clover.sdk.v1.app.AppNotificationReceiver;
import com.clover.sdk.v1.app.AppNotification;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderReceiver extends AppNotificationReceiver {
    private static final String ONLINE_ORDER_EVENT = "online_order";
    private OrderReceiverListener mListener;

    public OrderReceiver(Context context, OrderReceiverListener listener) {
        super.register(context);

        mListener = listener;
    }

    @Override
    public void onReceive(Context context, AppNotification notification) {
        Log.d("NOTIFICATION", "Notification Received: " + notification.appEvent + " " + notification.payload);

        if (notification.appEvent.equals(ONLINE_ORDER_EVENT)) {
            new AsyncTask<AppNotification, Void, Void>() {
                @Override
                protected Void doInBackground(AppNotification... params) {
                    try {
                        AppNotification notification = params[0];
                        JSONObject data = new JSONObject(notification.payload);
                        JSONObject orderData = data.getJSONObject("order");
                        String orderId = orderData.getString("id");

                        mListener.onOrderReceive(orderId);
                    }

                    catch (JSONException e) {
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
        void onOrderReceive(String orderId);
    }
}
