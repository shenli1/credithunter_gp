package com.id.cash.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import com.id.cash.BuildConfig;
import com.id.cash.bean.ApiEndPointSetBean;
import com.id.cash.bean.TokenBean;
import com.id.cash.bean.UserBean;

/**
 * Created by linchen on 2018/5/2.
 */

public class Preference {
    private final static String name = "uuang_pref";
    private static Preference instance = null;
    private final String INSTALL_REFERRER = "INSTALL_REFERRER";
    private final String TOKEN = "TOKEN";
    private final String USER = "USER";
    private final String UDID = "UDID";
    private final String SEARCH_HISTORY = "SEARCH_HISTORY";
    private final String LAST_UPGRADE_PROMPT_VERSION = "LAST_UPGRADE_PROMPT_VERSION";
    private final String PRIVACYPOLICY_ACCEPTED = "PRIVACYPOLICY_ACCEPTED";
    private final String PRIVACYPOLICY_SHOWN_FLAG = "PRIVACYPOLICY_SHOWN_FLAG";
    private final String GATEWAYS = "GATEWAYS";
    private final String APISETS = "APISETS";
    private final String CURRENT_APISET = "CURRENT_APISET";

    // on first launch, get the referrer & push to server
    private final String FIRST_LAUNCH_CALLED = "FIRST_LAUNCH_CALLED";

    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;

    public static void createInstance(Context context) {
        instance = new Preference();
        instance.sharedPreferences = context.getSharedPreferences(name, 0);
        instance.editor = instance.sharedPreferences.edit();
    }

    public static Preference getInstance() {
        return instance;
    }

    private static Object fromString(String str) {
        try {
            return new ObjectInputStream(
                    new ByteArrayInputStream(
                            Base64.decode(str, Base64.NO_WRAP)))
                    .readObject();
        } catch (Exception e) {
            LogUtil.e(e);
            return null;
        }
    }

    private static String convertToString(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(byteArrayOutputStream).writeObject(obj);
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP);
        } catch (Exception e) {
            LogUtil.e(e);
            return null;
        }
    }

    public boolean getIsPrivacyPolicyRejected() {
        String flag = sharedPreferences.getString(PRIVACYPOLICY_ACCEPTED, null);
        return "0".equals(flag);
    }

    public boolean getIsPrivacyPolicyAccepted() {
        return isVersionFlagEqual(PRIVACYPOLICY_ACCEPTED);
    }

    public void setIsPrivacyPolicyAccepted(boolean isAccept) {
        setVersionFlag(PRIVACYPOLICY_ACCEPTED, isAccept);
        setPrivacyPolicyShownFlag();
    }

    public boolean getIsPrivacyPolicyShown() {
        return isVersionFlagSet(PRIVACYPOLICY_SHOWN_FLAG);
    }

    public void setPrivacyPolicyShownFlag() {
        setVersionFlag(PRIVACYPOLICY_SHOWN_FLAG, true);
    }

    @SuppressWarnings("unchecked")
    public void addToSearchHistory(String text) {
        // text && text.trim is not null
        if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(text.trim())) {
            String str = sharedPreferences.getString(SEARCH_HISTORY, null);
            LinkedHashSet<String> historyHashList = null;
            if (!TextUtils.isEmpty(str)) {
                historyHashList = JsonUtil.fromString(str, LinkedHashSet.class);
            }
            if (historyHashList == null) {
                historyHashList = new LinkedHashSet<>();
            }

            historyHashList.remove(text);
            historyHashList.add(text);
            String json = JsonUtil.stringify(historyHashList);

            LogUtil.d("set search history: " + json);
            editor.putString(SEARCH_HISTORY, json);
            editor.commit();
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getSearchHistory() {
        String json = sharedPreferences.getString(SEARCH_HISTORY, null);
        LinkedHashSet<String> historyHashList = JsonUtil.fromString(json, LinkedHashSet.class);
        ArrayList<String> arrayList = new ArrayList<>();

        if (historyHashList != null) {
            arrayList.addAll(historyHashList);
        }
        Collections.reverse(arrayList);
        return arrayList;
    }

    public void clearSearchHistory() {
        editor.putString(SEARCH_HISTORY, null);
        editor.commit();
    }

    public String getInstallReferrer() {
        return sharedPreferences.getString(INSTALL_REFERRER, "");
    }

    // TODO: remove me (after verifying install referrer api
    public void setInstallReferrer(String referrer) {
        LogUtil.i("set referrer: " + referrer);
        editor.putString(INSTALL_REFERRER, referrer);
        editor.commit();
    }

    public void setFirstLaunchCalled() {
        LogUtil.i("setFirstLaunchCalled");
        editor.putString(FIRST_LAUNCH_CALLED, FIRST_LAUNCH_CALLED);
        editor.commit();
    }

    public boolean getIsFirstLaunchCalled() {
        boolean ret = !TextUtils.isEmpty(sharedPreferences.getString(FIRST_LAUNCH_CALLED, null));
        LogUtil.i("getIsFirstLaunchCalled: " + ret);
        return ret;
    }

    public TokenBean getToken() {
        String str = sharedPreferences.getString(TOKEN, null);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        LogUtil.d("TokenBean: " + str);
        return (TokenBean) fromString(str);
    }

    public void setToken(TokenBean tokenBean) {
        if (tokenBean != null) {
            String str = Preference.convertToString(tokenBean);
            LogUtil.d("set TokenBean: " + str);
            editor.putString(TOKEN, str);
            editor.commit();
        } else {
            LogUtil.i("remove token");
            editor.remove(TOKEN);
            editor.commit();
        }
    }

    public UserBean getUser() {
        String str = sharedPreferences.getString(USER, null);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        LogUtil.d("UserBean: " + str);
        return (UserBean) fromString(str);
    }

    public void setUser(UserBean userBean) {
        if (userBean != null) {
            String str = Preference.convertToString(userBean);
            LogUtil.d("set UserBean: " + str);
            editor.putString(USER, str);
            editor.commit();
        } else {
            LogUtil.i("remove user");
            editor.remove(USER);
            editor.commit();
        }
    }

    public synchronized int getLastUpgradePromptVersion() {
        int versionCode = sharedPreferences.getInt(LAST_UPGRADE_PROMPT_VERSION, 0);
        LogUtil.d("getLastUpgradePromptVersion: " + versionCode);
        return versionCode;
    }

    public synchronized void setLastUpgradePromptVersion(int version) {
        LogUtil.d("setLastUpgradePromptVersion: " + version);
        editor.putInt(LAST_UPGRADE_PROMPT_VERSION, version);
        editor.commit();
    }

    public String getUDID() {
        String str = sharedPreferences.getString(UDID, null);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        LogUtil.d("get UDID: " + str);
        return str;
    }

    public synchronized void setUDID(String str) {
        if (!TextUtils.isEmpty(str)) {
            LogUtil.d("set UDID: " + str);
            editor.putString(UDID, str);
            editor.commit();
        } else {
            LogUtil.d("remove UDID");
            editor.remove(UDID);
            editor.commit();
        }
    }

    public synchronized void setGateways(String[] gateways) {
        if (gateways != null && gateways.length > 0) {
            LogUtil.d("set GATEWAYS: " + gateways);

            LinkedHashSet<String> set = new LinkedHashSet<>(Arrays.asList(gateways));
            String json = JsonUtil.stringify(set);

            editor.putString(GATEWAYS, json);
        } else {
            LogUtil.d("remove GATEWAYS");
            editor.remove(GATEWAYS);
            editor.commit();
        }
        editor.commit();
    }

    public List<String> getGateways() {
        String json = sharedPreferences.getString(GATEWAYS, null);
        LinkedHashSet<String> list = JsonUtil.fromString(json, LinkedHashSet.class);
        ArrayList<String> arrayList = new ArrayList<>();

        if (list != null) {
            arrayList.addAll(list);
        }
        Collections.reverse(arrayList);
        return arrayList;
    }

    public synchronized void setApiEndPointSets(ApiEndPointSetBean[] apiSets) {
        if (apiSets != null && apiSets.length > 0) {
            String str = Preference.convertToString(apiSets);
            LogUtil.d("set APISETS: " + str);
            editor.putString(APISETS, str);
            editor.commit();
        } else {
            LogUtil.i("remove APISETS");
            editor.remove(APISETS);
            editor.commit();
        }
    }

    public ApiEndPointSetBean[] getApiEndPointSets() {
        String str = sharedPreferences.getString(APISETS, null);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        LogUtil.d("APISETS: " + str);
        return (ApiEndPointSetBean[]) fromString(str);
    }

    public synchronized void setCurrentEndpointSet(ApiEndPointSetBean apiEndPointSetBean) {
        if (apiEndPointSetBean != null) {
            String str = Preference.convertToString(apiEndPointSetBean);
            LogUtil.d("set CURRENT_APISET: " + str);
            editor.putString(CURRENT_APISET, str);
            editor.commit();
        } else {
            LogUtil.i("remove CURRENT_APISET");
            editor.remove(CURRENT_APISET);
            editor.commit();
        }
    }

    public ApiEndPointSetBean getCurrentEndpointSet() {
        String str = sharedPreferences.getString(APISETS, null);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        LogUtil.d("APISETS: " + str);
        return (ApiEndPointSetBean) fromString(str);
    }

    public void setTestTokenBean() {
        editor.putString(TOKEN, "rO0ABXNyACR1dWFuZy5waW5qYW1hbi5ydXBpYWguYmVhbi5Ub2tlbkJlYW7YkJqc7QxSxAIAAkwABXRva2VudAASTGphdmEvbGFuZy9TdHJpbmc7TAAGdXNlcklkcQB+AAF4cHQA1GV5SmhiR2NpT2lKSVV6VXhNaUo5LmV5SnpkV0lpT2lJeE1EQTBNQ0lzSW1WNGNDSTZNVFV6TURRek1qSXdOQ3dpZFdScFpDSTZJak5oTldJM1lUWTRMVGN5T1RNdE5HUm1OUzA0WWpoakxUSXdPV0UzTlRnNU9UWmxZaUo5LktkdGloVm9teGkyNXl2V2ZvNUJKS0J6T1U3RHZaZHEyd19RN2xBMEVrcFJjdW1Kb0laTXY5aW1BSmFrV2dsSHJFU2JLLVNQbGY5T20wRF9CMkJRa2h3dAAFMTAwNDA=");
        editor.commit();
    }

    private void setVersionFlag(String key, boolean accepted) {
        editor.putString(key, accepted ? String.valueOf(BuildConfig.VERSION_CODE) : "0");
        editor.commit();
    }

    private boolean isVersionFlagSet(String key) {
        return !TextUtils.isEmpty(sharedPreferences.getString(key, null));
    }

    private boolean isVersionFlagEqual(String key) {
        String flag = sharedPreferences.getString(key, null);
        return String.valueOf(BuildConfig.VERSION_CODE).equals(flag);
    }
}
