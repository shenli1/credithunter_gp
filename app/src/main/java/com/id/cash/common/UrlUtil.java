package com.id.cash.common;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by linchen on 2018/4/29.
 */

public class UrlUtil {
    public static final String GP_URL_PREFIX = "https://play.google.com/store/apps/details";
    public static final String GP_MARKET_PREFIX = "market://details";

    public static String getQueryString(String url) {
        int queryStringStart = url.indexOf("?");
        String ret = "";
        if (queryStringStart != -1) {
            ret = url.substring(queryStringStart + 1);
        }
        return ret;
    }

    public static boolean isGooglePlayLink(String url) {
        return url != null && (url.contains(GP_URL_PREFIX) || url.contains(GP_MARKET_PREFIX));
    }

    public static String qsStringify(Map<String, Object> map) {
        String qs = "";
        if (map == null) {
            return qs;
        }

        ArrayList<String> lines = new ArrayList<>();
        try {
            for (Map.Entry<String, Object> parameter : map.entrySet()) {
                final String encodedKey = URLEncoder.encode(parameter.getKey(), "UTF-8");
                final String encodedValue = URLEncoder.encode(parameter.getValue().toString(), "UTF-8");

                lines.add(encodedKey + "=" + encodedValue);
            }
        } catch (Exception ex) {
            LogUtil.e(ex);
        }

        return StringUtil.concat(lines, "&");
    }
}
