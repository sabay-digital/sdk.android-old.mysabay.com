package kh.com.mysabay.sdk.repository;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import kh.com.mysabay.sdk.pojo.googleVerify.GoogleVerifyBody;
import kh.com.mysabay.sdk.pojo.googleVerify.GoogleVerifyResponse;
import kh.com.mysabay.sdk.pojo.mysabay.MySabayItem;
import kh.com.mysabay.sdk.pojo.payment.PaymentBody;
import kh.com.mysabay.sdk.pojo.payment.PaymentBodyPreAuth;
import kh.com.mysabay.sdk.pojo.payment.PaymentResponseItem;
import kh.com.mysabay.sdk.pojo.shop.ShopItem;
import kh.com.mysabay.sdk.pojo.thirdParty.ThirdPartyItem;
import kh.com.mysabay.sdk.pojo.thirdParty.payment.ResponseItem;
import kh.com.mysabay.sdk.webservice.api.StoreApi;

/**
 * Created by Tan Phirum on 3/13/20
 * Gmail phirumtan@gmail.com
 */
@Singleton
public class StoreRepo implements StoreApi {

    private final StoreApi storeApi;

    @Inject
    public StoreRepo(StoreApi storeApi) {
        this.storeApi = storeApi;
    }

    @Override
    public Observable<ShopItem> getShopItem(String appSecret, String token) {
        return storeApi.getShopItem(appSecret, "Bearer " + token);
    }

//    @Override
//    public Observable<MySabayItem> getMySabayCheckout(String appSecret, String token, String uuid) {
//        return this.storeApi.getMySabayCheckout(appSecret, "Bearer " + token, uuid);
//    }

    @Override
    public Observable<MySabayItem> getMySabayCheckout(String appSecret, String token, String packageCode) {
        return this.storeApi.getMySabayCheckout(appSecret, "Bearer " + token, packageCode);
    }

    @Override
    public Observable<ThirdPartyItem> get3PartyCheckout(String appSecret, String token, String uuid) {
        return this.storeApi.get3PartyCheckout(appSecret, "Bearer " + token, uuid);
    }

    @Override
    public Observable<GoogleVerifyResponse> postToVerifyGoogle(String appSecret, String token, GoogleVerifyBody body) {
        return this.storeApi.postToVerifyGoogle(appSecret, "Bearer " + token, body);
    }

    @Override
    public Observable<PaymentResponseItem> postToPaid(String token, String hash, String signature, String publicKey, String payment, String paymentAddress) {
        return this.storeApi.postToPaid("Bearer " + token, hash, signature, publicKey, payment , paymentAddress);
    }

    @Override
    public Observable<ResponseItem> postToChargeOneTime(String token, String hash, String signature, String publicKey, String payment, String paymentAddress) {
        return this.storeApi.postToChargeOneTime("Bearer " + token, hash, signature, publicKey, payment , paymentAddress);
    }
}
