package com.id.cash.common;

import android.content.Context;

//import com.android.installreferrer.api.InstallReferrerClient;
//import com.android.installreferrer.api.InstallReferrerStateListener;
//import com.android.installreferrer.api.ReferrerDetails;

public class InstallReferrelHelper {
    private OnInstallReferrerListener onInstallReferrerListener;

    public OnInstallReferrerListener getOnInstallReferrerListener() {
        return onInstallReferrerListener;
    }

    public void setOnInstallReferrerListener(OnInstallReferrerListener onInstallReferrerListener) {
        this.onInstallReferrerListener = onInstallReferrerListener;
    }

    public void getReferrerAsync(Context context) {
//        try {
//            final InstallReferrerClient installReferrerClient = InstallReferrerClient.newBuilder(context).build();
//            installReferrerClient.startConnection(new InstallReferrerStateListener() {
//                @Override
//                public void onInstallReferrerSetupFinished(int responseCode) {
//                    switch (responseCode) {
//                        case InstallReferrerClient.InstallReferrerResponse.OK:
//                            // Connection established, get referrer
//                            getInstallReferrerImpl(installReferrerClient);
//                            break;
//                        case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
//                            // API not available on the current Play Store app
//                            break;
//                        case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
//                            // Connection could not be established
//                            break;
//                    }
//                }
//
//                @Override
//                public void onInstallReferrerServiceDisconnected() {
//                    // Try to restart the connection on the next request to
//                    // Google Play by calling the startConnection() method.
//                }
//
//            });
//        } catch (Exception ex) {
//            LogUtil.e(ex);
//        }
    }

//    private void getInstallReferrerImpl(InstallReferrerClient installReferrerClient) {
//        if (installReferrerClient != null) {
//            try {
//                ReferrerDetails response = installReferrerClient.getInstallReferrer();
//                String referrer = response.getInstallReferrer();
//                if (onInstallReferrerListener != null) {
//                    onInstallReferrerListener.OnReferrerReceived(referrer);
//                }
//
//                installReferrerClient.endConnection();
//            } catch (Exception ex) {
//                LogUtil.e(ex);
//            }
//        }
//    }

    public interface OnInstallReferrerListener {
        void OnReferrerReceived(String referrer);
    }
}
