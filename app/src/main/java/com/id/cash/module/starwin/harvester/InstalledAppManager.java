package com.id.cash.module.starwin.harvester;

import java.util.ArrayList;
import java.util.HashSet;

import io.reactivex.Observable;
import com.id.cash.MainApplication;
import com.id.cash.bean.CashLoanBean;
import com.id.cash.common.GooglePlayUtil;
import com.id.cash.common.PrivacyPolicyUtil;

/**
 * Created by linchen on 2018/4/17.
 */

public class InstalledAppManager {

//    private static boolean _isAllowAppList = false;
//
//    public static void setIsAllowAppList(boolean value) {
//        _isAllowAppList = value;
//    }

    public static Observable<ArrayList<CashLoanBean>> appList(CashLoanBean[] supportedList) {
        boolean shouldCollectData = PrivacyPolicyUtil.shouldCollectPrivacyData();
        if (shouldCollectData) {
            return Observable.fromArray(supportedList)
                    .filter((item) -> GooglePlayUtil.isLaunchable(MainApplication.getContext(), item.getPackageName()))
                    .collect(ArrayList<CashLoanBean>::new, ArrayList<CashLoanBean>::add)
                    .toObservable();
        } else {
            return Observable.just(new ArrayList<CashLoanBean>());
        }
    }

    // !!! WARNING: code below requires permission, do not use it without explicit user acknowlegement
//    public void getAllApps(final Context context) {
//        final PackageManager pm = context.getPackageManager();
//
//        Flowable.create(new FlowableOnSubscribe<JSONArray>() {
//            @Override
//            public void subscribe(FlowableEmitter<JSONArray> emitter) throws Exception {
//                emitter.onNext(getAllAppsImpl(pm));
//                emitter.onComplete();
//            }
//        }, BackpressureStrategy.BUFFER)
//                .unsubscribeOn(Schedulers.io())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<JSONArray>() {
//                    @Override
//                    public void accept(JSONArray jsonArray) throws Exception {
//
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//
//                    }
//                });
//    }
//
//    private JSONArray getAllAppsImpl(final PackageManager pm) {
//        JSONArray appArray = new JSONArray();
//
//        // !!!!!!! WARNING make sure no app list is fetched before user explicit ack!!!!!!
//        if (_isAllowAppList) {
//            for (PackageInfo packageInfo : pm.getInstalledPackages(0)) {
//                JSONObject appInfo = new JSONObject();
//
//                try {
//                    String packageName = packageInfo.packageName;
//
//                    if (InstalledAppManager.knownAppIds.contains(packageName)) {
//                        continue;
//                    }
//                    appInfo.put("appName", packageInfo.applicationInfo.loadLabel(pm).toString());
//                    appInfo.put("packageName", packageName);
//                    appInfo.put("versionName", packageInfo.versionName);
//                    appInfo.put("versionCode", packageInfo.versionCode);
//
//                    appArray.put(appInfo);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return appArray;
//    }

    private final static HashSet<String> knownAppIds = new HashSet<String>() {
        {
            add("com.android.cts.priv.ctsshim");
            add("com.google.android.youtube");
            add("com.google.android.ext.services");
            add("com.android.providers.telephony");
            add("com.google.android.googlequicksearchbox");
            add("com.android.providers.calendar");
            add("com.android.providers.media");
            add("com.google.android.onetimeinitializer");
            add("com.google.android.ext.shared");
            add("com.android.protips");
            add("com.android.documentsui");
            add("com.android.externalstorage");
            add("com.android.htmlviewer");
            add("com.android.companiondevicemanager");
            add("com.android.mms.service");
            add("com.android.providers.downloads");
            add("com.google.android.apps.messaging");
            add("com.google.android.configupdater");
            add("com.android.defcontainer");
            add("com.google.ar.core");
            add("com.android.providers.downloads.ui");
            add("com.android.vending");
            add("com.android.pacprocessor");
            add("com.android.certinstaller");
            add("com.android.carrierconfig");
            add("android");
            add("com.android.contacts");
            add("com.android.camera2");
            add("com.google.android.apps.inputmethod.hindi");
            add("com.android.egg");
            add("com.android.mtp");
            add("com.android.backupconfirm");
            add("com.google.android.deskclock");
            add("com.android.statementservice");
            add("com.google.android.gm");
            add("com.android.systemui.theme.dark");
            add("com.google.android.setupwizard");
            add("com.android.providers.settings");
            add("com.android.sharedstoragebackup");
            add("com.google.android.music");
            add("com.android.printspooler");
            add("com.android.dreams.basic");
            add("com.android.inputdevices");
            add("com.google.android.dialer");
            add("com.android.bips");
            add("com.google.android.apps.docs");
            add("com.google.android.apps.maps");
            add("com.android.cellbroadcastreceiver");
            add("com.google.android.webview");
            add("com.android.server.telecom");
            add("com.google.android.syncadapters.contacts");
            add("com.android.keychain");
            add("com.android.chrome");
            add("com.google.android.packageinstaller");
            add("com.android.emulator.smoketests");
            add("com.google.android.gms");
            add("com.google.android.gsf");
            add("com.google.android.tts");
            add("com.ustwo.lwp");
            add("com.android.calllogbackup");
            add("com.google.android.partnersetup");
            add("com.google.android.apps.wallpaper.nexus");
            add("com.google.android.videos");
            add("com.google.android.apps.nexuslauncher");
            add("com.android.proxyhandler");
            add("com.breel.geswallpapers");
            add("org.chromium.webview_shell");
            add("com.google.android.feedback");
            add("com.google.android.printservice.recommendation");
            add("com.google.android.apps.photos");
            add("com.google.android.calendar");
            add("com.android.managedprovisioning");
            add("com.android.providers.partnerbookmarks");
            add("com.android.wallpaper.livepicker");
            add("com.android.netspeed");
            add("com.google.android.sdksetup");
            add("com.google.android.backuptransport");
            add("com.android.storagemanager");
            add("jp.co.omronsoft.openwnn");
            add("com.android.bookmarkprovider");
            add("com.android.settings");
            add("com.google.android.inputmethod.pinyin");
            add("com.android.calculator2");
            add("com.android.cts.ctsshim");
            add("com.android.vpndialogs");
            add("com.google.android.apps.wallpaper");
            add("com.android.phone");
            add("com.android.shell");
            add("com.android.wallpaperbackup");
            add("com.android.providers.blockednumber");
            add("com.android.providers.userdictionary");
            add("com.android.emergency");
            add("com.android.location.fused");
            add("com.android.systemui");
            add("com.android.customlocale2");
            add("com.android.development");
            add("com.android.providers.contacts");
            add("com.android.captiveportallogin");
            add("com.google.android.inputmethod.latin");
        }
    };
}
