package com.id.cash;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import com.id.cash.common.Preference;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

import com.appsflyer.AppsFlyerLib;
import com.appsflyer.AppsFlyerConversionListener;

import java.util.Map;

public class MainApplication extends MultiDexApplication {
    private static Context context;
    private static final String AF_DEV_KEY = "6MFL7LAW3VuhJDePrnFtbY";

    @Override
    public void onCreate() {
        super.onCreate();

        CrashReport.initCrashReport(getApplicationContext(), BuildConfig.BUGGLY_APP_ID, false);

        context = getApplicationContext();
        Preference.createInstance(this);

        AppsFlyerConversionListener conversionDataListener =
            new AppsFlyerConversionListener() {
                @Override
                    public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
                        for (String attrName : conversionData.keySet()) {
                            Log.d(AppsFlyerLib.LOG_TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
                        }
                    }
                    @Override
                    public void onInstallConversionFailure(String errorMessage) {
                        Log.d(AppsFlyerLib.LOG_TAG, "error getting conversion data: " + errorMessage);
                    }
                    @Override
                    public void onAppOpenAttribution(Map<String, String> conversionData) {
                    }
                    @Override
                    public void onAttributionFailure(String errorMessage) {
                        Log.d(AppsFlyerLib.LOG_TAG, "error onAttributionFailure : " + errorMessage);
                    }
            };

        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionDataListener, getApplicationContext());
        AppsFlyerLib.getInstance().startTracking(this);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    public static Context getContext() {
        return context;
    }
}
