package com.id.cash.network;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import com.id.cash.bean.ApiReturn;
import com.id.cash.bean.GatewayBean;

/**
 * Created by linchen on 2018/5/21.
 */

public interface GatewayApi {
    @GET("gateway")
    Observable<ApiReturn<GatewayBean>> getApiSets();
}
