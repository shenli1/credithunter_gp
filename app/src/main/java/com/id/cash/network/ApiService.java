package com.id.cash.network;

import com.id.cash.BuildConfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by linchen on 2018/5/21.
 */

public class ApiService {
    public static ApiService getInstance() {
        // do not re-use to avoid stale connections
        return new ApiService();
    }

    public PortalApi getPortalApi() {
        Retrofit retrofit = getRetrofit(BuildConfig.PORTAL_API_BASE);
        return retrofit.create(PortalApi.class);
    }

    private Retrofit getRetrofit(String baseUrl) {
        OkHttpClient okHttpClient = new OkHttpHelper(new SWInterceptor())
                .getOkHttpClient();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    /**
     * handle the exception: end of input at line 1 column 1 path $
     * when api response the empty body with 0-bytes, such as get investment sharing red packet api
     * reference from: https://github.com/square/retrofit/issues/1554
     */
    static class NullOnEmptyConverterFactory extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return body -> {
                if (body.contentLength() == 0) return null;
                return delegate.convert(body);
            };
        }
    }
}
