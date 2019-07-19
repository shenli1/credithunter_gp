package com.id.cash.module.starwin.deviceinfo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Pair;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.id.cash.BuildConfig;
import com.id.cash.common.LogUtil;
import com.id.cash.module.starwin.common.Constants;
import com.id.cash.module.starwin.common.StarWinFactory;
import com.id.cash.module.starwin.common.Util;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.id.cash.module.starwin.common.Constants.HIGH;
import static com.id.cash.module.starwin.common.Constants.LARGE;
import static com.id.cash.module.starwin.common.Constants.LONG;
import static com.id.cash.module.starwin.common.Constants.LOW;
import static com.id.cash.module.starwin.common.Constants.MEDIUM;
import static com.id.cash.module.starwin.common.Constants.NORMAL;
import static com.id.cash.module.starwin.common.Constants.SMALL;
import static com.id.cash.module.starwin.common.Constants.XLARGE;

/**
 * Created by pfms on 06/11/14.
 */
public class DeviceInfo {
    String playAdId;
    Boolean isTrackingEnabled;
    private boolean nonGoogleIdsRead = false;
//    String macSha1;
//    String macShortMd5;
//    String mac;
    String androidId;
    String fbAttributionId;
    String clientSdk;
    String packageName;
    String appVersion;
    String deviceType;
    String deviceName;
    String deviceManufacturer;
    String osName;
    String osVersion;
    String apiLevel;
    String language;
    String country;
    String screenSize;
    String screenFormat;
    String screenDensity;
    String displayWidth;
    String displayHeight;
    String hardwareName;
//    String serialNumber;
    String abi;
    String buildName;
    String vmInstructionSet;
    String appInstallTime;
    String appUpdateTime;

    int versionCode;
//    String imei;
    public final Boolean isEmulator;
    Map<String, String> pluginKeys;

    public DeviceInfo(Context context, String sdkPrefix) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        Locale locale = Util.getLocale(configuration);
        int screenLayout = configuration.screenLayout;
        ContentResolver contentResolver = context.getContentResolver();

        reloadDeviceIds(context);

        packageName = getPackageName(context);
        appVersion = getAppVersion(context);
        deviceType = getDeviceType(screenLayout);
        deviceName = getDeviceName();
        deviceManufacturer = getDeviceManufacturer();
        osName = getOsName();
        osVersion = getOsVersion();
        apiLevel = getApiLevel();
        language = getLanguage(locale);
        country = getCountry(locale);
        screenSize = getScreenSize(screenLayout);
        screenFormat = getScreenFormat(screenLayout);
        screenDensity = getScreenDensity(displayMetrics);
        displayWidth = getDisplayWidth(displayMetrics);
        displayHeight = getDisplayHeight(displayMetrics);
        clientSdk = getClientSdk(sdkPrefix);
        fbAttributionId = getFacebookAttributionId(context);
        pluginKeys = Util.getPluginKeys(context);
        hardwareName = getHardwareName();
//        serialNumber = getSerialNumber();
        abi = getABI();
        buildName = getBuildName();
        vmInstructionSet = getVmInstructionSet();
        appInstallTime = getAppInstallTime(context);
        appUpdateTime = getAppUpdateTime(context);
        isEmulator = isEmulator();

        versionCode = getVersionCode(context);
    }

    void reloadDeviceIds(Context context) {
        isTrackingEnabled = Util.isPlayTrackingEnabled(context);
        playAdId = Util.getPlayAdId(context);

        if (!nonGoogleIdsRead) {
            if (!Util.checkPermission(context, android.Manifest.permission.ACCESS_WIFI_STATE)) {
                StarWinFactory.getLogger().warn("Missing permission: ACCESS_WIFI_STATE");
            }
//            mac = Util.getMacAddress(context);
//            macSha1 = getMacSha1(mac);
//            macShortMd5 = getMacShortMd5(mac);
//            imei = Util.getIMEI(((TelephonyManager) context.getSystemService(TELEPHONY_SERVICE)));

            androidId = Util.getAndroidId(context);

            nonGoogleIdsRead = true;
        }
    }

    public void injectDeviceIds(HashMap<String, Object> hashMap) {
//        hashMap.put("playAdId", playAdId);
        hashMap.put("androidId", androidId);
//        hashMap.put("mac", mac);
//        hashMap.put("imei", imei);
//        hashMap.put("isTrackingEnabled", isTrackingEnabled);
    }

    public void injectAppInfo(HashMap<String, Object> hashMap) {
        hashMap.put("versionCode", versionCode);
        hashMap.put("channel", BuildConfig.CHANNEL);
        hashMap.put("packageName", BuildConfig.APPLICATION_ID);
        hashMap.put("os", "ANDROID");
    }

    public void injectDeviceProperties(HashMap<String, Object> hashMap) {
        hashMap.put("appVersion", appVersion);
        hashMap.put("deviceType", deviceType);
        hashMap.put("deviceName", deviceName);
        hashMap.put("deviceManufacturer", deviceManufacturer);
        hashMap.put("osVersion", osVersion);
        hashMap.put("apiLevel", apiLevel);
        hashMap.put("language", language);
        hashMap.put("country", country);
        hashMap.put("screenSize", screenSize);
        hashMap.put("screenFormat", screenFormat);
        hashMap.put("screenDensity", screenDensity);
        hashMap.put("displayWidth", displayWidth);
        hashMap.put("displayHeight", displayHeight);
        hashMap.put("hardwareName", hardwareName);
//        hashMap.put("serialNumber", serialNumber);
        hashMap.put("abi", abi);
        hashMap.put("buildName", buildName);
        hashMap.put("vmInstructionSet", vmInstructionSet);
        hashMap.put("appInstallTime", appInstallTime);
        hashMap.put("appUpdateTime", appUpdateTime);
        hashMap.put("isEmulator", isEmulator);
    }

    private String getMacAddress(Context context, boolean isGooglePlayServicesAvailable) {
        if (!isGooglePlayServicesAvailable) {
            if (!Util.checkPermission(context, android.Manifest.permission.ACCESS_WIFI_STATE)) {
                StarWinFactory.getLogger().warn("Missing permission: ACCESS_WIFI_STATE");
            }
            return Util.getMacAddress(context);
        } else {
            return null;
        }
    }

    private String getPackageName(Context context) {
        return context.getPackageName();
    }

    private String getAppVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            String name = context.getPackageName();
            PackageInfo info = packageManager.getPackageInfo(name, 0);
            return info.versionName;
        } catch (Exception e) {
            return null;
        }
    }

    private String getDeviceType(int screenLayout) {
        int screenSize = screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return "phone";
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
            case 4:
                return "tablet";
            default:
                return null;
        }
    }

    private int getVersionCode(Context context) {
        int versionCode = 0;

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (Exception e) {
            LogUtil.e(e.toString());
        }

        if (versionCode == 0) {
            versionCode = BuildConfig.VERSION_CODE;
        }

        return versionCode;
    }

    private String getDeviceName() {
        return Build.MODEL;
    }

    private String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    private String getOsName() {
        return "android";
    }

    private String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    private String getApiLevel() {
        return "" + Build.VERSION.SDK_INT;
    }

    private String getLanguage(Locale locale) {
        return locale.getLanguage();
    }

    private String getCountry(Locale locale) {
        return locale.getCountry();
    }

    private String getBuildName() {
        return Build.ID;
    }

    private String getHardwareName() {
        return Build.DISPLAY;
    }
    private String getSerialNumber() { return Build.SERIAL; }

    private String getScreenSize(int screenLayout) {
        int screenSize = screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return SMALL;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return NORMAL;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return LARGE;
            case 4:
                return XLARGE;
            default:
                return null;
        }
    }

    private String getScreenFormat(int screenLayout) {
        int screenFormat = screenLayout & Configuration.SCREENLAYOUT_LONG_MASK;

        switch (screenFormat) {
            case Configuration.SCREENLAYOUT_LONG_YES:
                return LONG;
            case Configuration.SCREENLAYOUT_LONG_NO:
                return NORMAL;
            default:
                return null;
        }
    }

    private String getScreenDensity(DisplayMetrics displayMetrics) {
        int density = displayMetrics.densityDpi;
        int low = (DisplayMetrics.DENSITY_MEDIUM + DisplayMetrics.DENSITY_LOW) / 2;
        int high = (DisplayMetrics.DENSITY_MEDIUM + DisplayMetrics.DENSITY_HIGH) / 2;

        if (density == 0) {
            return null;
        } else if (density < low) {
            return LOW;
        } else if (density > high) {
            return HIGH;
        }
        return MEDIUM;
    }

    private String getDisplayWidth(DisplayMetrics displayMetrics) {
        return String.valueOf(displayMetrics.widthPixels);
    }

    private String getDisplayHeight(DisplayMetrics displayMetrics) {
        return String.valueOf(displayMetrics.heightPixels);
    }

    private String getClientSdk(String sdkPrefix) {
        if (sdkPrefix == null) {
            return Constants.CLIENT_SDK;
        } else {
            return Util.formatString("%s@%s", sdkPrefix, Constants.CLIENT_SDK);
        }
    }

    private String getMacSha1(String macAddress) {
        if (macAddress == null) {
            return null;
        }
        String macSha1 = Util.sha1(macAddress);

        return macSha1;
    }

    private String getMacShortMd5(String macAddress) {
        if (macAddress == null) {
            return null;
        }
        String macShort = macAddress.replaceAll(":", "");
        String macShortMd5 = Util.md5(macShort);

        return macShortMd5;
    }

    private String getFacebookAttributionId(final Context context) {
        try {
            final ContentResolver contentResolver = context.getContentResolver();
            final Uri uri = Uri.parse("content://com.facebook.katana.provider.AttributionIdProvider");
            final String columnName = "aid";
            final String[] projection = {columnName};
            final Cursor cursor = contentResolver.query(uri, projection, null, null, null);

            if (cursor == null) {
                return null;
            }
            if (!cursor.moveToFirst()) {
                cursor.close();
                return null;
            }

            final String attributionId = cursor.getString(cursor.getColumnIndex(columnName));
            cursor.close();
            return attributionId;
        } catch (Exception e) {
            return null;
        }
    }

    private String getABI() {
        String[] SupportedABIS = Util.getSupportedAbis();

        // SUPPORTED_ABIS is only supported in API level 21
        // get CPU_ABI instead
        if (SupportedABIS == null || SupportedABIS.length == 0) {
            return Util.getCpuAbi();
        }

        return SupportedABIS[0];
    }

    private String getVmInstructionSet() {
        String instructionSet = Util.getVmInstructionSet();
        return instructionSet;
    }

    private String getAppInstallTime(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);

            String appInstallTime = Util.dateFormatter.format(new Date(packageInfo.firstInstallTime));

            return appInstallTime;
        } catch (Exception ex) {
            return null;
        }
    }

    private String getAppUpdateTime(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);

            String appInstallTime = Util.dateFormatter.format(new Date(packageInfo.lastUpdateTime));

            return appInstallTime;
        } catch (Exception ex) {
            return null;
        }
    }

    private Boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
}
