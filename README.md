# OnlineOrder-Android
An example Online Ordering Android App for Clover App Market that guarantees notifications, allows for kitchen order printing and management.

Here is an example body of a POST request to [CreateMerchantAppNotification](https://www.clover.com/api_docs/#!/notifications/CreateMerchantAppNotification) to send a notification to this app.

```
{
	"event": "online_order",
	"data": "{ \"order\": { \"id\": \"H2XGJQN8FGDT2\" } }"
}
```

Built using Android Studio 3.2
