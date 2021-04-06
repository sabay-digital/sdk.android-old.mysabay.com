package kh.com.mysabay.sdk;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.request.RequestHeaders;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.mysabay.sdk.Checkout_getPaymentServiceProviderForProductQuery;
import com.mysabay.sdk.CreateMySabayLoginMutation;
import com.mysabay.sdk.CreateMySabayLoginWithPhoneMutation;
import com.mysabay.sdk.DeleteTokenMutation;
import com.mysabay.sdk.GetExchangeRateQuery;
import com.mysabay.sdk.GetInvoiceByIdQuery;
import com.mysabay.sdk.GetMatomoTrackingIdQuery;
import com.mysabay.sdk.GetProductsByServiceCodeQuery;
import com.mysabay.sdk.LoginGuestMutation;
import com.mysabay.sdk.LoginWithFacebookMutation;
import com.mysabay.sdk.LoginWithMySabayMutation;
import com.mysabay.sdk.LoginWithPhoneMutation;
import com.mysabay.sdk.RefreshTokenMutation;
import com.mysabay.sdk.SendCreateMySabayWithPhoneOTPMutation;
import com.mysabay.sdk.UserProfileQuery;
import com.mysabay.sdk.VerifyMySabayMutation;
import com.mysabay.sdk.VerifyOtpCodMutation;
import com.mysabay.sdk.VerifyTokenQuery;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.matomo.sdk.Tracker;
import org.matomo.sdk.extra.EcommerceItems;
import org.matomo.sdk.extra.MatomoApplication;
import org.matomo.sdk.extra.TrackHelper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import kh.com.mysabay.sdk.callback.DataCallback;
import kh.com.mysabay.sdk.callback.LoginListener;
import kh.com.mysabay.sdk.callback.PaymentListener;
import kh.com.mysabay.sdk.callback.RefreshTokenListener;
import kh.com.mysabay.sdk.callback.UserInfoListener;
import kh.com.mysabay.sdk.di.BaseAppComponent;
import kh.com.mysabay.sdk.di.DaggerBaseAppComponent;
import kh.com.mysabay.sdk.pojo.AppItem;
import kh.com.mysabay.sdk.pojo.NetworkState;
import kh.com.mysabay.sdk.pojo.TrackingOrder.TrackingOrder;
import kh.com.mysabay.sdk.pojo.googleVerify.GoogleVerifyBody;
import kh.com.mysabay.sdk.pojo.login.SubscribeLogin;
import kh.com.mysabay.sdk.pojo.mysabay.ProviderResponse;
import kh.com.mysabay.sdk.pojo.payment.SubscribePayment;
import kh.com.mysabay.sdk.pojo.thirdParty.payment.Data;
import kh.com.mysabay.sdk.ui.activity.LoginActivity;
import kh.com.mysabay.sdk.ui.activity.StoreActivity;
import kh.com.mysabay.sdk.utils.AppRxSchedulers;
import kh.com.mysabay.sdk.utils.LogUtil;
import kh.com.mysabay.sdk.utils.MessageUtil;
import kh.com.mysabay.sdk.viewmodel.StoreService;
import kh.com.mysabay.sdk.viewmodel.UserService;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Tan Phirum on 3/11/20
 * Gmail phirumtan@gmail.com
 */
@Singleton
public class MySabaySDK {

    private static final String TAG = MySabaySDK.class.getSimpleName();

    @Inject
    ApolloClient apolloClient;
    @Inject
    Gson gson;
    @Inject
    AppRxSchedulers appRxSchedulers;

    @Inject
    UserService userService;
    @Inject
    StoreService storeService;

    private SharedPreferences mPreferences;
    public BaseAppComponent mComponent;
    public Application mAppContext;

    private static MySabaySDK mySabaySDK;
    private SdkConfiguration mSdkConfiguration;

    private LoginListener loginListner;
    private PaymentListener mPaymentListener;
    private final MediatorLiveData<NetworkState> _networkState;

    @Inject
    public MySabaySDK(Application application, SdkConfiguration configuration) {
        LogUtil.debug(TAG, "init MySabaySDK");
        mySabaySDK = this;
        this.mAppContext = application;
        this.mComponent = DaggerBaseAppComponent.create();
        this._networkState = new MediatorLiveData<>();
        mSdkConfiguration = configuration;
        this.mComponent.inject(this);
        EventBus.getDefault().register(this);
    }

    public static class Impl {
        public static synchronized void setDefaultInstanceConfiguration(Application application, SdkConfiguration configuration) {
            new MySabaySDK(application, configuration);
        }
    }

    @Contract(pure = true)
    public static MySabaySDK getInstance() {
        if (mySabaySDK == null)
            throw new NullPointerException("initialize mysabaySdk in application");
        if (mySabaySDK.mAppContext == null)
            throw new NullPointerException("Please provide application context");
        if (mySabaySDK.mSdkConfiguration == null)
            throw new RuntimeException("This sdk is need SdkConfiguration");
        return mySabaySDK;
    }

    /**
     * Show the login screen
     *
     * @param listener return token when login success, failed message if login failed
     */
    public void showLoginView(LoginListener listener) {
        if (listener != null)
            this.loginListner = listener;
        AppItem item = gson.fromJson(getAppItem(), AppItem.class);
        if (item != null) {
            _networkState.setValue(new NetworkState(NetworkState.Status.LOADING));
            apolloClient.query(new VerifyTokenQuery(item.token)).enqueue(new ApolloCall.Callback<VerifyTokenQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<VerifyTokenQuery.Data> response) {
                    apolloClient.mutate(new RefreshTokenMutation(item.refreshToken)).enqueue(new ApolloCall.Callback<RefreshTokenMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<RefreshTokenMutation.Data> response) {
                            if (response.getData() != null) {
                                item.withToken(response.getData().sso_refreshToken().accessToken());
                                item.withExpired(response.getData().sso_refreshToken().expire());
                                item.withRefreshToken(response.getData().sso_refreshToken().refreshToken());
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        _networkState.setValue(new NetworkState(NetworkState.Status.SUCCESS));
                                        MySabaySDK.getInstance().saveAppItem(gson.toJson(item));
                                        EventBus.getDefault().post(new SubscribeLogin(item.token, null));
                                    }
                                });
                            } else {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        _networkState.setValue(new NetworkState(NetworkState.Status.SUCCESS));
                                        LogUtil.info("Data is null", "Error");
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            _networkState.setValue(new NetworkState(NetworkState.Status.ERROR));
                            EventBus.getDefault().post(new SubscribeLogin(null, e));
                            LogUtil.info(TAG, e.getMessage());
                        }
                    });
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    apolloClient.mutate(new RefreshTokenMutation(item.refreshToken)).enqueue(new ApolloCall.Callback<RefreshTokenMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<RefreshTokenMutation.Data> response) {
                            item.withToken(response.getData().sso_refreshToken().accessToken());
                            item.withExpired(response.getData().sso_refreshToken().expire());
                            item.withRefreshToken(response.getData().sso_refreshToken().refreshToken());
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    _networkState.setValue(new NetworkState(NetworkState.Status.SUCCESS));
                                    MySabaySDK.getInstance().saveAppItem(gson.toJson(item));
                                    EventBus.getDefault().post(new SubscribeLogin(item.token, null));
                                }
                            });
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            LogUtil.info(TAG, e.getMessage());
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    _networkState.setValue(new NetworkState(NetworkState.Status.ERROR));
                                    mAppContext.startActivity(new Intent(mAppContext, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                }
                            });
                        }
                    });
                }
            });
        } else {
            mAppContext.startActivity(new Intent(mAppContext, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    /**
     * validate if user login
     *
     * @return true has logged , false otherwise
     */
    public boolean isLogIn() {
        return !StringUtils.isBlank(MySabaySDK.getInstance().getAppItem());
    }

    public void logout() {
        AppItem item = gson.fromJson(getAppItem(), AppItem.class);
        if (item != null) {
          logoutWithGraphQl(item.refreshToken);
        }
    }

    public void logoutWithGraphQl(String refreshToken) {
        _networkState.setValue(new NetworkState(NetworkState.Status.LOADING));
            apolloClient.mutate(new DeleteTokenMutation(refreshToken)).enqueue(new ApolloCall.Callback<DeleteTokenMutation.Data>() {
                @Override
                public void onResponse(@NotNull Response<DeleteTokenMutation.Data> response) {
                    if (response.getData() != null) {
                        clearAppItem();
                        if (LoginManager.getInstance() != null) {
                            LogUtil.info("Facebook", "Logout");
                            LoginManager.getInstance().logOut();
                        }
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                _networkState.setValue(new NetworkState(NetworkState.Status.SUCCESS));
                            }
                        });
                    } else {
                        clearAppItem();
                        LogUtil.info("Logout", "null");
                    }
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    LogUtil.info("OnError", e.toString());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            _networkState.setValue(new NetworkState(NetworkState.Status.ERROR));
                        }
                    });
                }
            });
    }

    /**
     * Get user profile
     *
     * @param listener
     */
    public void getUserProfile(UserInfoListener listener) {
        AppItem item = gson.fromJson(getAppItem(), AppItem.class);
        apolloClient.query(new UserProfileQuery()).toBuilder()
                .requestHeaders(RequestHeaders.builder()
                        .addHeader("Authorization", "Bearer " + item.token).build())
                .build()
                .enqueue(new ApolloCall.Callback<UserProfileQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<UserProfileQuery.Data> response) {
                if (response.getData() != null) {
                    if (listener != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                MySabaySDK.getInstance().saveAppItem(gson.toJson(item));
                                listener.userInfo(gson.toJson(response.getData().sso_userProfile()));
                            }
                        });
                    } else {
                        onFailure(new ApolloException("UserInfoListener required!!!"));
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                           listener.userInfo(null);
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                LogUtil.info("Error", e.getMessage());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        _networkState.setValue(new NetworkState(NetworkState.Status.ERROR));
                    }
                });
            }
        });
    }

    /**
     * Show the shop item
     *
     * @param listener return with item purchase transaction or failed message
     */
    public void showStoreView(PaymentListener listener) {
        if (listener == null) return;

        this.mPaymentListener = listener;
        AppItem appItem = gson.fromJson(MySabaySDK.getInstance().getAppItem(), AppItem.class);
        if (appItem == null || StringUtils.isBlank(appItem.token)) {
            MessageUtil.displayToast(mAppContext, "You need to login first");
            return;
        }

        apolloClient.query(new VerifyTokenQuery(appItem.token)).enqueue(new ApolloCall.Callback<VerifyTokenQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<VerifyTokenQuery.Data> response) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        _networkState.setValue(new NetworkState(NetworkState.Status.SUCCESS));
                        mAppContext.startActivity(new Intent(mAppContext, StoreActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        _networkState.setValue(new NetworkState(NetworkState.Status.ERROR));
                        MessageUtil.displayToast(mAppContext, "Token is invalid");
                    }
                });
            }
        });
    }

    /**
     * @param listener
     */
    public void refreshToken(RefreshTokenListener listener) {
        AppItem item = gson.fromJson(getAppItem(), AppItem.class);

        apolloClient.mutate(new RefreshTokenMutation(item.refreshToken)).enqueue(new ApolloCall.Callback<RefreshTokenMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<RefreshTokenMutation.Data> response) {
                LogUtil.info("Success", response.getData().toString());
                if (listener != null) {
                    item.withToken(response.getData().sso_refreshToken().accessToken());
                    item.withExpired(response.getData().sso_refreshToken().expire());
                    item.withRefreshToken(response.getData().sso_refreshToken().refreshToken());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            _networkState.setValue(new NetworkState(NetworkState.Status.SUCCESS));
                            MySabaySDK.getInstance().saveAppItem(gson.toJson(item));
                            listener.refreshSuccess(response.getData().sso_refreshToken().refreshToken());
                        }
                    });
                } else {
                    onFailure(new ApolloException("RefreshTokenListener required!!!"));
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                LogUtil.info("OnError", e.toString());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        _networkState.setValue(new NetworkState(NetworkState.Status.ERROR));
                        if (listener != null) listener.refreshFailed(e);
                    }
                });
            }
        });
    }

    /**
     * @return with token that valid
     */
    public String currentToken() {
        AppItem item = gson.fromJson(getAppItem(), AppItem.class);
        return item.token;
    }

    /**
     * @return true if token is valid, false otherwise
     */
    public boolean isTokenValid() {
        AppItem item = gson.fromJson(getAppItem(), AppItem.class);
        if (System.currentTimeMillis() == item.expire)
            return false;
        else return true;
    }


    @Subscribe
    public void onLoginEvent(SubscribeLogin event) {
        LogUtil.info("Subscribe", "Login");
        if (loginListner != null) {
            if (!StringUtils.isBlank(event.accessToken)) {
                loginListner.loginSuccess(event.accessToken);
            } else
                loginListner.loginFailed(event.error);
        } else {
            LogUtil.debug(TAG, "loginListerner null " + gson.toJson(event));
        }
    }

    @Subscribe
    public void onPaymentEvent(SubscribePayment event) {
        if (mPaymentListener != null) {
            if (event.data != null) {
                mPaymentListener.purchaseSuccess(event);
            } else {
                mPaymentListener.purchaseFailed(event.error);
            }
        } else
            LogUtil.debug(TAG, "loginListerner null " + gson.toJson(event));
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
        loginListner = null;
        mPaymentListener = null;
        mySabaySDK = null;
        mAppContext = null;
    }

    public SharedPreferences getPreferences(Activity context) {
        if (mPreferences == null)
            mPreferences = context.getSharedPreferences(Globals.PREF_NAME, MODE_PRIVATE);
        return mPreferences;
    }

    public SharedPreferences getPreferences() {
        if (mPreferences == null)
            mPreferences = mAppContext.getSharedPreferences(Globals.PREF_NAME, MODE_PRIVATE);
        return mPreferences;
    }

    public void saveAppItem(String item) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(Globals.EXT_KEY_APP_ITEM, item);
        editor.apply();
    }

    public void clearAppItem() {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.remove(Globals.EXT_KEY_APP_ITEM);
        editor.commit();
    }

    public String getAppItem() {
        return getPreferences().getString(Globals.EXT_KEY_APP_ITEM, null);
    }

    public void saveMethodSelected(String item) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(Globals.EXT_KEY_PAYMENT_METHOD, item);
        editor.apply();
    }

    public String getMethodSelected() {
        return getPreferences().getString(Globals.EXT_KEY_PAYMENT_METHOD, "");
    }

    public SdkConfiguration getSdkConfiguration() {
        return mSdkConfiguration;
    }

    /**
     *  Create Tracker instance
     */
    private Tracker getTracker(Context context) {
        return ((MatomoApplication) context.getApplicationContext()).getTracker();
    }

    /**
     * track screen views
     */
    public void trackPageView(Context context, String path, String title) {
        TrackHelper.track().screen("android" + path).title("android" + title).with(getTracker(context));
    }

    /**
     * track events
     */
    public void trackEvents(Context context, String category, String action, String name) {
        TrackHelper.track().event("android-" + category, action).name(name).with(getTracker(context));
    }

    public void setCustomUserId(Context context, String userId) {
        getTracker(context).setUserId(userId);
    }

    public void trackOrder(Context context, TrackingOrder trackingOrder) {
        TrackHelper.track().order(trackingOrder.orderId, trackingOrder.grandTotal).subTotal(trackingOrder.subTotal)
                .tax(trackingOrder.tax).shipping(trackingOrder.shipping).discount(trackingOrder.discount)
                .items(trackingOrder.ecommerceItems).with(getTracker(context));
    }

    public String appSecret() {
        return mSdkConfiguration.isSandBox ? "9c85c50a4362f687cd4507771ba81db5cf50eaa0b3008f4f943f77ba3ac6386b" : "d41faee946f531794d18a152eafeb5fd8fc81ce4de520e97fcfe41fefdd0381c";
    }

    public String userApiUrl() {
        return mSdkConfiguration.isSandBox ? "http://gateway.master.sabay.com/graphql/" : "http://gateway.master.sabay.com/graphql/";
    }

    public String storeApiUrl() {
        return mSdkConfiguration.isSandBox ? "http://pp.master.mysabay.com/" : "http://pp.master.mysabay.com/";
//        return mSdkConfiguration.isSandBox ? "https://demo-pp.testing.ssn.digital/": "https://demo-pp.testing.ssn.digital/";
    }

    public String getPaymentAddress(String invoiceId) {
        return mSdkConfiguration.isSandBox ? invoiceId + "*invoice.master.sabay.com" : invoiceId + "*invoice.sabay.com";
    }

    public String serviceCode() {
        return mSdkConfiguration.serviceCode;
    }

    // provided function

    public void loginGuest(DataCallback<LoginGuestMutation.Sso_loginGuest> dataCallback) {
        userService.loginAsGuest(dataCallback);
    }

    public void loginWithPhone(String phoneNumber, DataCallback<LoginWithPhoneMutation.Sso_loginPhone> dataCallback) {
        userService.loginWithPhoneNumber(phoneNumber, dataCallback);
    }

    public void verifyOtp(String phoneNumber, String otpCode, DataCallback<VerifyOtpCodMutation.Sso_verifyOTP> dataCallback) {
        userService.verifyOTPCode(phoneNumber, otpCode, dataCallback);
    }

    public void loginWithFacebook(String token, DataCallback<LoginWithFacebookMutation.Sso_loginFacebook> dataCallback) {
        userService.loginWithFacebook(token, dataCallback);
    }

    public void getUserInfo(DataCallback<UserProfileQuery.Sso_userProfile> dataCallback) {
        userService.getUserProfile(dataCallback);
    }

    public void loginWithMySabay(String username, String password, DataCallback<LoginWithMySabayMutation.Sso_loginMySabay> dataCallback) {
        userService.loginWithMySabayAccount(username, password, dataCallback);
    }

    public void verifyMySabay(String username, String password, DataCallback<VerifyMySabayMutation.Sso_verifyMySabay> dataCallback) {
        userService.verifyMySabay(username, password, dataCallback);
    }

    public void registerMySabayAccount(String username, String password, DataCallback<CreateMySabayLoginMutation.Sso_createMySabayLogin> dataCallback) {
        userService.createMySabayAccount(username, password, dataCallback);
    }

    public void createMySabayWithPhone(String username, String password, String phoneNumber, String otpCode, DataCallback<CreateMySabayLoginWithPhoneMutation.Sso_createMySabayLoginWithPhone> dataCallback) {
        userService.createMySabayLoginWithPhone(username, password, phoneNumber, otpCode, dataCallback);
    }

    public void requestCreatingMySabayWithPhone(String phoneNumber, DataCallback<SendCreateMySabayWithPhoneOTPMutation.Sso_sendCreateMySabayWithPhoneOTP> dataCallback) {
        userService.createMySabayWithPhoneOTP(phoneNumber, dataCallback);
    }

    public void checkExistingMySabayUsername(String username, DataCallback<Boolean> dataCallback) {
        userService.checkExistingMySabayUsername(username, dataCallback);
    }

    public void getMatomoTrackingId(String serviceCode, DataCallback<GetMatomoTrackingIdQuery.Sso_service> dataCallback) {
        userService.getTrackingID(serviceCode, dataCallback);
    }

    public void getStoreProducts(DataCallback<GetProductsByServiceCodeQuery.Store_listProduct> dataCallback) {
        storeService.getStoreProducts(dataCallback);
    }

    public void getPaymentServiceProvidersByProduct(String productId, DataCallback<Checkout_getPaymentServiceProviderForProductQuery.Checkout_getPaymentServiceProviderForProduct> callbackData) {
        storeService.getPaymentServiceProvidersByProduct(productId, callbackData);
    }

    public void scheduledCheckPaymentStatus(Handler handler, String invoiceId, long interval, long repeat, DataCallback<GetInvoiceByIdQuery.Invoice_getInvoiceById> dataCallback) {
        storeService.scheduledCheckPaymentStatus(handler, invoiceId, interval, repeat, dataCallback);
    }

    public void checkPaymentStatus(String invoiceId, DataCallback<GetInvoiceByIdQuery.Invoice_getInvoiceById> dataCallback) {
        storeService.getInvoiceById(invoiceId, dataCallback);
    }

    public void getExchangeRate(DataCallback<List<GetExchangeRateQuery.Sso_service>> callback) {
        storeService.getExchangeRate(callback);
    }

    public void createPaymentDetail(String pspId, List<Object> items,double amount, String currency, DataCallback<Object> callbackData) {
        storeService.createPaymentProcess(pspId, items, amount, currency, callbackData);
    }

    public void postToChargePreAuth(Data data, DataCallback<Object> callback) {
        storeService.postToChargePreAuth(data.requestUrl + data.paymentAddress, data.hash, data.signature, data.publicKey, data.paymentAddress, callback);
    }

    public void verifyInAppPurcahse(Data data, GoogleVerifyBody body, DataCallback<Object> callback) {
        storeService.postToChargeInAppPurchase(data, body, callback);
    }

    public ProviderResponse getMySabayProvider(String type) {
        return  storeService.getMySabayProviderId(type);
    }

    public List<ProviderResponse> getMySabayProviders() {
        return  storeService.getMySabayProviders();
    }

    public ProviderResponse getInAppPurchaseProvider(String type) {
        return  storeService.getInAppPurchaseProvider(type);
    }

    public boolean verifyValidSignature(String signedData, String signature) {
        return storeService.verifyValidSignature(signedData, signature);
    }

    public void postToChargeWithOneTime(Data dataPayment, DataCallback<String> callback) {
        storeService.postToChargeWithOneTime(dataPayment, callback);
    }
}
