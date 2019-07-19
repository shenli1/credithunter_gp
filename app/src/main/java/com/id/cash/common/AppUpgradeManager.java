package com.id.cash.common;

import com.hwangjr.rxbus.RxBus;

import com.id.cash.bean.AppVersionBean;
import com.id.cash.module.starwin.common.bus.BusAction;

/**
 * Created by linchen on 2018/6/5.
 * upgrade is handled by RestApiSubscriber at 2 places:
 * 1. bad return: handleKnownError: force update
 * 2. good return: onResult: optional update
 */

public class AppUpgradeManager {
    private static AppUpgradeManager instance;

    interface AppUpdateType {
        int NO_UPDATE = -1;
        int OPTIONAL_UPDATE = 0;
        int FORCE_UPDATE = 1;
    }

    private AppUpgradeManager() {
    }

    public synchronized static AppUpgradeManager getInstance() {
        if (instance == null) {
            instance = new AppUpgradeManager();
        }
        return instance;
    }

    public int check(AppVersionBean appVersionBean) {
        if (appVersionBean == null) {
            return AppUpdateType.NO_UPDATE;
        }

        int updateType = appVersionBean.getUpdateType();
        boolean shouldPrompt = false;
        if (updateType == AppUpdateType.OPTIONAL_UPDATE) {
            int versionCode = Preference.getInstance().getLastUpgradePromptVersion();
            if (appVersionBean.getVersionCode() > versionCode) {
                // posting notification to main for handling
                shouldPrompt = true;
            }
        } else if (updateType == AppUpdateType.FORCE_UPDATE) {
            shouldPrompt = true;
        }

        if (shouldPrompt) {
            RxBus.get().post(BusAction.APP_UPGRADE, appVersionBean);
        }

        return updateType;
    }
}
