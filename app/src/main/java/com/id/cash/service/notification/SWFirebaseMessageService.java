//package com.id.cash.service.notification;
//
//import android.app.Notification;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.os.Build;
//import android.text.TextUtils;
//
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import java.util.Map;
//
//import com.id.cash.R;
//import com.id.cash.common.LocalNotificationHelper;
//import com.id.cash.module.starwin.common.BroadcastConstants;
//
///**
// * Created by linchen on 2018/5/23.
// */
//
//// sample
//// https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/MainActivity.java
//public class SWFirebaseMessageService extends FirebaseMessagingService {
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        String title = null;
//        String body = null;
//        String actionType = null;
//        String productId = null;
//        String linkTitle = null;
//        String link = null;
//
//        if (remoteMessage.getNotification() != null) {
//            RemoteMessage.Notification notification = remoteMessage.getNotification();
//
//            title = notification.getTitle();
//            body = notification.getBody();
//        }
//
//        if (remoteMessage.getData().size() > 0) {
//            Map<String, String> data = remoteMessage.getData();
//            // for local notification
//            String dataTitle = data.get("title");
//            String dataBody = data.get("body");
//            if (!TextUtils.isEmpty(dataTitle)) {
//                title = dataTitle;
//            }
//            if (!TextUtils.isEmpty(dataBody)) {
//                body = dataBody;
//            }
//
//            // the switcher value
//            actionType = data.get("action_type");
//
//            // parameters
//            productId = data.get("product_id");
//            linkTitle = data.get("link_title");
//            link = data.get("link");
//        }
//
//        sendLocalNotification(title, body, actionType, productId, linkTitle, link);
//    }
//
//    private void sendLocalNotification(String title, String body, String actionType, String productId, String linkTitle, String link) {
//        Intent intent = new Intent(this, NotificationBroadcastReceiver.class);
//        intent.setAction(BroadcastConstants.PUSH_NOTIFICATION_RECEIVED);
//        intent.putExtra("action_type", actionType);
//        intent.putExtra("product_id", productId);
//        intent.putExtra("link_title", linkTitle);
//        intent.putExtra("link", link);
//
//        // send notification to system bar
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
//        LocalNotificationHelper helper = new LocalNotificationHelper(this, 1);
//
//        int icon = getApplicationInfo().icon;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            icon = R.drawable.ic_notification;
//        }
//        helper.send(pendingIntent, icon, body, title, body,
//                true, true, false);
//    }
//}
