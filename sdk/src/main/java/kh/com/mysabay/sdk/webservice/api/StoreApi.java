package kh.com.mysabay.sdk.webservice.api;

import io.reactivex.Observable;
import kh.com.mysabay.sdk.pojo.googleVerify.GoogleVerifyBody;
import kh.com.mysabay.sdk.pojo.googleVerify.GoogleVerifyResponse;
import kh.com.mysabay.sdk.pojo.payment.PaymentResponseItem;
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
import retrofit2.http.Url;

/**
 * Created by Tan Phirum on 3/13/20
 * Gmail phirumtan@gmail.com
 */
public interface StoreApi {

    @GET("api/v1.7/cashier")
    Observable<ThirdPartyItem> get3PartyCheckout(@Header("app_secret") String appSecret, @Header("Authorization") String token, @Query("uuid") String uuid);

    @FormUrlEncoded
    @POST
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Observable<GoogleVerifyResponse> postToVerifyGoogle(@Url String url,
                                                        @Header("Authorization") String token,
                                                        @Field("hash") String hash,
                                                        @Field("signature") String signature,
                                                        @Field("public_key") String publicKey,
                                                        @Field("receipt") String receipt);

    @FormUrlEncoded
    @POST
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Observable<PaymentResponseItem> postToPaid(@Url String url,
                                               @Header("Authorization") String token,
                                               @Field("hash") String hash,
                                               @Field("signature") String signature,
                                               @Field("public_key") String publicKey,
                                               @Field("payment_address") String payment);

    @FormUrlEncoded
    @POST
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Observable<ResponseItem> postToChargeOneTime(@Url String url,
                                                 @Header("Authorization") String token,
                                                 @Field("hash") String hash,
                                                 @Field("signature") String signature,
                                                 @Field("public_key") String publicKey,
                                                 @Field("payment_address") String payment);


}
