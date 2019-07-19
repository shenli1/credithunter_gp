package com.id.cash.network;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import com.id.cash.bean.ApiReturn;
import com.id.cash.bean.BankCardBean;
import com.id.cash.bean.BannerBean;
import com.id.cash.bean.BatchBean;
import com.id.cash.bean.BonusPointBean;
import com.id.cash.bean.BonusPointTaskBean;
import com.id.cash.bean.BonusPointTaskResultBean;
import com.id.cash.bean.CashLoanBean;
import com.id.cash.bean.CashLoanDetailBean;
import com.id.cash.bean.CashLoanProceduerBean;
import com.id.cash.bean.ClientFeedsBean;
import com.id.cash.bean.FeedsBannerBean;
import com.id.cash.bean.FeedsBean;
import com.id.cash.bean.FilterBean;
import com.id.cash.bean.IgnoredDataBean;
import com.id.cash.bean.InviteReferralBean;
import com.id.cash.bean.ListResultBean;
import com.id.cash.bean.RegisterDeviceResultBean;
import com.id.cash.bean.ShareInfoBean;
import com.id.cash.bean.SubmitWithdrawResultBean;
import com.id.cash.bean.TokenBean;
import com.id.cash.bean.UserBean;

/**
 * Created by linchen on 2018/5/21.
 */

public interface PortalApi {
    @GET("product/{loanId}")
    Observable<ApiReturn<CashLoanDetailBean>> getLoanDetail(@Path("loanId") String loanId);

    @GET("procedure/{loanId}")
    Observable<ApiReturn<CashLoanProceduerBean>> getLoanProcedure(@Path("loanId") String loanId);

    @FormUrlEncoded
    @POST("product/apply/")
    Observable<ApiReturn<IgnoredDataBean>> logApply(@Field("loanId") String loanId);

    @POST("user/instance-id")
    Observable<ApiReturn<IgnoredDataBean>> registerPushToken(@Query("instanceId") String instanceId);

    @GET("user/referrals")
    Observable<ApiReturn<InviteReferralBean[]>> getInviteList();

    @GET("client/hot-words")
    Observable<ApiReturn<String>> getHotWords();

    @GET("product/search")
    Observable<ApiReturn<ListResultBean<CashLoanBean>>> search(@Query("q") String keyword);

    @GET("filter/{filterId}/loan?descs=priority")
    Observable<ApiReturn<ListResultBean<CashLoanBean>>> filter(@Path("filterId") String filterId,
                                                               @Query("current") String current,
                                                               @Query("size") String size);

    @GET("point/bonus")
    Observable<ApiReturn<BonusPointBean[]>> getBonusPointList();

    @GET("sharing/info")
    Observable<ApiReturn<ShareInfoBean>> getShareInfo(@Query("channel") String channel, @Query("method") String method);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("device/")
    Observable<ApiReturn<RegisterDeviceResultBean>> registerDevice(@Body HashMap<String, Object> body);

    @FormUrlEncoded
    @POST("user/login")
    Observable<ApiReturn<TokenBean>> loginByAccountKitAuthCode(@Field("authorization_code") String authCode);

    @GET("user/current-user")
    Observable<ApiReturn<UserBean>> getUserInfo();

    @POST("user/logout/")
    Observable<ApiReturn<Boolean>> logout();

    @GET("ad/banner")
    Observable<ApiReturn<BannerBean[]>> getBanner();

    @GET("filter/")
    Observable<ApiReturn<ListResultBean<FilterBean>>> getFilters(@Query("current") int current,
                                                                 @Query("size") int pageSize,
                                                                 @Query("descs") String priority);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("product/customized_list/")
    Observable<ApiReturn<ListResultBean<CashLoanBean>>> getCustomizedCashLoanList(@Query("current") int current,
                                                                                  @Query("size") int pageSize,
                                                                                  @Query("descs") String priority,
                                                                                  @Body HashMap<String, Object> body);

    @GET("task/execute-status")
    Observable<ApiReturn<BonusPointTaskBean[]>> getBonusPointTaskExecuteStatus();

    @FormUrlEncoded
    @POST("task/point")
    Observable<ApiReturn<BonusPointTaskResultBean>> postTaskResult(@Field("taskTempletId") String taskTemplateId, @Field("method") String method);

    @FormUrlEncoded
    @POST("callback/referral/v2")
    Observable<ApiReturn<String>> postInstallReferrer(@Field("queries") String referrer);

    @GET("bankcard")
    Observable<ApiReturn<BankCardBean>> getGetBankCardInfo();

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("bankcard")
    Observable<ApiReturn<BankCardBean>> submitBankCardInfo(@Query("userName") String userName,
                                                           @Query("bankName") String bankName,
                                                           @Query("bankAccount") String bankAccount);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("withdraw")
    Observable<ApiReturn<SubmitWithdrawResultBean>> submitWithdraw(@Query("bankCardId") String bankCardId,
                                                                   @Query("point") String point);

    @GET("product/package/list")
    Observable<ApiReturn<CashLoanBean[]>> getPackageList();

    @GET("client/batch")
    Observable<ApiReturn<BatchBean[]>> getBatch();

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("client/sms")
    Observable<ApiReturn<Boolean>> postSmsBatch(@Query("infoBatchId") String infoBatchId, @Body String body);

    @POST("client/info-batch")
    Observable<ApiReturn<Boolean>> updateInfoBatchStatus(@Body HashMap<String, Object> body);

    @GET("user/records")
    Observable<ApiReturn<String[]>> getLatestEvents();

    @GET("feeds/list/?descs=id")
    Observable<ApiReturn<ListResultBean<FeedsBean>>> getFeedsList(@Query("current") int current,
                                                                     @Query("size") int pageSize);

    @GET("feeds/{feedsId}")
    Observable<ApiReturn<FeedsBean>> getFeeds(@Path("feedsId") String feedsId);

    @POST("feeds/{feedsId}/toggleLike")
    Observable<ApiReturn<ClientFeedsBean>> toggleLike(@Path("feedsId") String feedsId);

    @GET("feeds/{feedsId}/client-feeds")
    Observable<ApiReturn<ClientFeedsBean>> getClientFeeds(@Path("feedsId") String feedsId);

    @GET("feeds/banner")
    Observable<ApiReturn<FeedsBannerBean[]>> getFeedsBanner();

    @GET("sharing/feeds")
    Observable<ApiReturn<ShareInfoBean>> getFeedsShareInfo(@Query("channel") String channel,
                                                           @Query("method") String method,
                                                           @Query("feedId") String feedsId);

}
