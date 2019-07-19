package com.id.cash.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import com.id.cash.common.ConfigUtil;

/**
 * Created by linchen on 2018/5/21.
 */

public class OkHttpHelper {
    private final String TAG = this.getClass().getSimpleName();
    private final int TIME_OUT = 20; //unit: seconds

    private OkHttpClient okHttpClient;
    private HttpLoggingInterceptor httpLoggingInterceptor;
    private CookieJar cookieJar;
    private Interceptor httpParamsInterceptor;

    public OkHttpHelper(Interceptor httpParamsInterceptor) {
        this.httpParamsInterceptor = httpParamsInterceptor;
        initInterceptor();
        initCookieJar();
        initOkHttpClient();
    }

    private void initInterceptor() {
        httpLoggingInterceptor = new HttpLoggingInterceptor();
        if (ConfigUtil.isApkInDebug()) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }
    }

    /**
     * cookie init
     */
    private void initCookieJar() {
        cookieJar = new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        };
    }


    /**
     * OkHttpClient init:
     * 1. add request params by @httpParamsInterceptor
     * 2. add logger by @httpLoggingInterceptor
     * 3. add debug in chrome developer tools by @StethoInterceptor
     */
    private void initOkHttpClient() {
        if (cookieJar != null && httpLoggingInterceptor != null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (httpParamsInterceptor != null) {
                builder = builder.addInterceptor(httpParamsInterceptor);
            }

            okHttpClient = builder
//                    .addNetworkInterceptor(new StethoInterceptor())
                    .addNetworkInterceptor(httpLoggingInterceptor)
                    .cookieJar(cookieJar)
                    .retryOnConnectionFailure(false)
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .build();
        }
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
