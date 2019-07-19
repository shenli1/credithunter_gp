package com.id.cash.network;

import com.id.cash.BuildConfig;

public class H5Urls {
    public static String getQA() {
        return getH5Link(BuildConfig.POINT_QA_PATH);
    }

    public static String getPrivacyPolicy() {
        return getH5Link(BuildConfig.PRIVACY_POLICY_PATH);
    }

    public static String getUserPolicy() {
        return getH5Link(BuildConfig.USER_POLICY_PATH);
    }

    private static String getH5Link(String path) {
        return BuildConfig.H5_BASE_URL + path;
    }
}
