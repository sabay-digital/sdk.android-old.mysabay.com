package kh.com.mysabay.sdk.webservice.api;

import io.reactivex.Observable;
import kh.com.mysabay.sdk.pojo.googleVerify.GoogleVerifyBody;
import kh.com.mysabay.sdk.pojo.googleVerify.GoogleVerifyResponse;
import kh.com.mysabay.sdk.pojo.mysabay.MySabayItem;
import kh.com.mysabay.sdk.pojo.payment.PaymentResponseItem;
import kh.com.mysabay.sdk.pojo.shop.ShopItem;
import kh.com.mysabay.sdk.pojo.thirdParty.ThirdPartyItem;
import kh.com.mysabay.sdk.pojo.thirdParty.payment.ResponseItem;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Tan Phirum on 3/13/20
 * Gmail phirumtan@gmail.com
 */
public interface StoreApi {

    @GET("api/v1.7/store")
    Observable<ShopItem> getShopItem(@Header("app_secret") String appSecret, @Header("Authorization") String token);

    @GET("api/v1.7/checkout")
    Observable<MySabayItem> getMySabayCheckout(@Header("app_secret") String appSecret, @Header("Authorization") String token, @Query("package_code") String packageCode);

    @GET("api/v1.7/cashier")
    Observable<ThirdPartyItem> get3PartyCheckout(@Header("app_secret") String appSecret, @Header("Authorization") String token, @Query("uuid") String uuid);

    @POST("api/v1.7/verify_receipt/google")
    Observable<GoogleVerifyResponse> postToVerifyGoogle(@Header("app_secret") String appSecret, @Header("Authorization") String token,
                                                        @Body() GoogleVerifyBody body);

    @FormUrlEncoded
    @POST("v1/charge/auth/{payment_address}")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Observable<PaymentResponseItem> postToPaid(@Header("Authorization") String token,
                                               @Field("hash") String hash,
                                               @Field("signature") String signature,
                                               @Field("public_key") String publicKey,
                                               @Field("payment_address") String payment,
                                               @Path("payment_address") String paymentAddress);

    @FormUrlEncoded
    @POST("v1/charge/onetime/{payment_address}")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Observable<ResponseItem> postToChargeOneTime(@Header("Authorization") String token,
                                                 @Field("hash") String hash,
                                                 @Field("signature") String signature,
                                                 @Field("public_key") String publicKey,
                                                 @Field("payment_address") String payment,
                                                 @Path("payment_address") String paymentAddress);


}
