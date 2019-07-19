package com.id.cash.network;

import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import com.id.cash.MainApplication;
import com.id.cash.bean.TokenBean;
import com.id.cash.common.LogUtil;
import com.id.cash.common.Preference;
import com.id.cash.common.UrlUtil;
import com.id.cash.module.starwin.deviceinfo.DeviceInfo;

/**
 * Created by linchen on 2018/5/21.
 */

public class SWInterceptor implements Interceptor {
    private final String TAG = this.getClass().getSimpleName();
    private final static String UDID_HEADER_TAG = "X-UDID";

    private final String deviceIdsString;
    private final String devicePropsString;
    // x-app for app update
    private final String appInfoString;

    public SWInterceptor() {
        // get device Ids
        DeviceInfo deviceInfo;
        HashMap<String, Object> deviceIds = new HashMap<>();
        deviceInfo = new DeviceInfo(MainApplication.getContext(), null);
        deviceInfo.injectDeviceIds(deviceIds);
        deviceIdsString = UrlUtil.qsStringify(deviceIds);

        // get device props
        HashMap<String, Object> deviceProps = new HashMap<>();
        deviceInfo.injectDeviceProperties(deviceProps);
        devicePropsString = UrlUtil.qsStringify(deviceProps);

        HashMap<String, Object> appInfo = new HashMap<>();
        deviceInfo.injectAppInfo(appInfo);
        appInfoString = UrlUtil.qsStringify(appInfo);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder newBuilder = originalRequest.newBuilder();

        // try fixing the sporadic connection failure problem
        // https://stackoverflow.com/questions/36452739/protocolexception-unexpected-end-of-stream
        if (originalRequest.header("Connection") == null) {
            newBuilder.header("Connection", "close");
        }

        if (!TextUtils.isEmpty(deviceIdsString)) {
            newBuilder.addHeader("X-DEVICE", deviceIdsString);
        }
        if (!TextUtils.isEmpty(devicePropsString)) {
            newBuilder.addHeader("X-DEVICE-PROPERTY", devicePropsString);
        }
        if (!TextUtils.isEmpty(appInfoString)) {
            newBuilder.addHeader("X-APP", appInfoString);
        }
        String udid = Preference.getInstance().getUDID();
        if (!TextUtils.isEmpty(udid)) {
            newBuilder.addHeader(UDID_HEADER_TAG, udid);
        }

        TokenBean tokenBean = Preference.getInstance().getToken();
        if (tokenBean != null &&
                !TextUtils.isEmpty(tokenBean.getToken()) && !TextUtils.isEmpty(tokenBean.getUserId())) {
            newBuilder.addHeader("X-USERID", tokenBean.getUserId());
            newBuilder.addHeader("X-TOKEN", tokenBean.getToken());
        }

        // TODO build request!
        //add User-Agent header
//        String uaHeader = "okhttp/3.3.0 clientid/" + APIID.LvJinClientId;
//        String version = MainApplication.getAppVersion();639061
//        if (version != null) {
//            uaHeader = "lvjinsuoandroid/" + version + " " + uaHeader;
//        }
//
//        if (!TextUtils.isEmpty(uaHeader) && (newBuilder != null)) {
//            newBuilder.addHeader("User-Agent", uaHeader);
//        }
//
//        //add Authorization header
//        String authHeader = UserManager.getInstance().getAuthHeader();
//        if (!TextUtils.isEmpty(authHeader) && (newBuilder != null)) {
//            newBuilder.addHeader("Authorization", authHeader);
//        }
//
//        //add uid header
//        String uidHeader = UserManager.getInstance().getUidHeader();
//        if (!TextUtils.isEmpty(uidHeader) && (newBuilder != null)) {
//            newBuilder.addHeader("UID", uidHeader);
//        }

        Response response = null;
        try {
            response = chain.proceed(newBuilder.build());
            if (response != null) {
                udid = response.headers().get(UDID_HEADER_TAG);
                if (!TextUtils.isEmpty(udid)) {
                    Preference.getInstance().setUDID(udid);
                }
//                final String ts = response.headers().get("X-TIMESTAMP");
//                if (!TextUtils.isEmpty(ts)){
//                    //1. update timestamp
//                    long serverTimestamp = Long.valueOf(ts); //in seconds
//                    long localTimestamp = (System.currentTimeMillis() / 1000);
//                    long timestampDelta = localTimestamp - serverTimestamp;
//
//                    LogUtil.i(TAG, "serverTimestamp = " + serverTimestamp +
//                            ", localTimestamp = " + localTimestamp +
//                            ", timestampDelta = " + timestampDelta);
//
//                    LvjinService.setTimeStampDeltaSeconds(timestampDelta);
//                }
            }
        } catch (Exception e) {
            LogUtil.e(e);
            throw e;
        }

        return response;
    }
}
