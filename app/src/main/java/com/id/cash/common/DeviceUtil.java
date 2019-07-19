package com.id.cash.common;

import java.util.HashMap;

import com.id.cash.MainApplication;
import com.id.cash.module.starwin.deviceinfo.DeviceInfo;

/**
 * Created by linchen on 2018/5/30.
 */

public class DeviceUtil {
    private static final DeviceInfo deviceInfo;

    static {
        deviceInfo = new DeviceInfo(MainApplication.getContext(), null);
    }

    public static HashMap<String, Object> getDevice() {
        HashMap<String, Object> deviceIds = new HashMap<>();
        deviceInfo.injectDeviceIds(deviceIds);
        return deviceIds;
    }

    public static HashMap<String, Object> getDeviceProperties() {
        HashMap<String, Object> deviceProps = new HashMap<>();
        deviceInfo.injectDeviceProperties(deviceProps);
        return deviceProps;
    }
}
