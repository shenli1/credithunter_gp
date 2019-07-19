//package com.id.cash.service.notification;
//
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;
//import com.hwangjr.rxbus.RxBus;
//
//import com.id.cash.common.LogUtil;
//import com.id.cash.module.starwin.common.bus.BusAction;
//import com.id.cash.module.starwin.common.bus.SetUserEvent;
//
///**
// * Created by linchen on 2018/5/23.
// */
//
//public class SWFirebaseInstanceIDService extends FirebaseInstanceIdService {
//    @Override
//    public void onTokenRefresh() {
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        LogUtil.i("FCM token refreshed: " + refreshedToken);
//
//        RxBus.get().post(BusAction.UPDATE_FCM_TOKEN, new SetUserEvent());
//    }
//}
