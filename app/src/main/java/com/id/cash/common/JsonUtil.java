package com.id.cash.common;

import android.text.TextUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by linchen on 2018/5/21.
 */

public class JsonUtil {
    public static String stringify(Object obj) {
        String json = null;
        try {
            json = new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            LogUtil.e(ex);
        }
        return json;
    }

    public static <T> T fromString(String json, Class<T> classOfT) {
        if (!TextUtils.isEmpty(json)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(json, classOfT);
            } catch (Exception ex) {
                LogUtil.e(ex);
            }
        }
        return null;
    }
}
