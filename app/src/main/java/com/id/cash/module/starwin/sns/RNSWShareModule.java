//package com.id.cash.module.starwin.sns;
//
//import android.os.Handler;
//import android.os.Looper;
//
//import com.facebook.react.bridge.Callback;
//import com.facebook.react.bridge.ReactApplicationContext;
//import com.facebook.react.bridge.ReactContextBaseJavaModule;
//import com.facebook.react.bridge.ReactMethod;
//import com.hwangjr.rxbus.RxBus;
//import com.hwangjr.rxbus.annotation.Subscribe;
//import com.hwangjr.rxbus.annotation.Tag;
//import com.hwangjr.rxbus.thread.EventThread;
//
//import com.id.cash.common.ActivityManager;
//import com.id.cash.module.starwin.common.bus.BusAction;
//
///**
// * Created by wangfei on 17/8/28.
// */
//
//public class RNSWShareModule extends ReactContextBaseJavaModule {
//    private final int SUCCESS = 200;
//    private final int ERROR = 0;
//    private final int CANCEL = -1;
//    private static Handler mSDKHandler = new Handler(Looper.getMainLooper());
//
//    private Callback lastShareCallback;
//
//    public RNSWShareModule(ReactApplicationContext reactContext) {
//        super(reactContext);
//
//        // for @Subscribe
//        RxBus.get().register(this);
//    }
//
//    @Override
//    public String getName() {
//        return "RNSWShareModule";
//    }
//
//    private static void runOnMainThread(Runnable runnable) {
//        mSDKHandler.postDelayed(runnable, 0);
//    }
//
//    @ReactMethod
//    public void share(final String text, final String img, final String weburl, final String title, final int sharemedia, final Callback callback) {
//        lastShareCallback = callback;
//        // we need an activity hosting the share function
//        runOnMainThread(new Runnable() {
//            @Override
//            public void run() {
//                FBShareActivity.actionStart(getReactApplicationContext().getCurrentActivity(),
//                        new ShareContentDTO().setLink(weburl));
//            }
//        });
//    }
//
//    @Subscribe(
//            thread = EventThread.MAIN_THREAD,
//            tags = {@Tag(BusAction.SHARE_SUCCESS)})
//    public void onShareSuccess(ShareSuccess shareSuccess) {
//        if (lastShareCallback != null) {
//            lastShareCallback.invoke(SUCCESS, null);
//            lastShareCallback = null;
//        }
//    }
//
//    @Subscribe(
//            thread = EventThread.MAIN_THREAD,
//            tags = {@Tag(BusAction.SHARE_CANCEL)})
//    public void onShareCancel(ShareCancel shareCancel) {
//        if (lastShareCallback != null) {
//            lastShareCallback.invoke(CANCEL, null);
//            lastShareCallback = null;
//        }
//    }
//
//    @Subscribe(
//            thread = EventThread.MAIN_THREAD,
//            tags = {@Tag(BusAction.SHARE_ERROR)})
//    public void onShareError(ShareError shareError) {
//        if (lastShareCallback != null) {
//            lastShareCallback.invoke(ERROR, shareError.getMessage());
//            lastShareCallback = null;
//        }
//    }
//}
