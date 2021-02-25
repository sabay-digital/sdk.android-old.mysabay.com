package kh.com.mysabay.sdk.viewmodel;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.exception.ApolloNetworkException;
import com.apollographql.apollo.request.RequestHeaders;
import com.google.gson.Gson;
import com.mysabay.sdk.Checkout_getPaymentServiceProviderForProductQuery;
import com.mysabay.sdk.CreateInvoiceMutation;
import com.mysabay.sdk.GetInvoiceByIdQuery;
import com.mysabay.sdk.GetPaymentDetailQuery;
import com.mysabay.sdk.GetProductsByServiceCodeQuery;
import com.mysabay.sdk.type.Invoice_CreateInvoiceInput;
import com.mysabay.sdk.type.Store_PagerInput;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import kh.com.mysabay.sdk.Globals;
import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.R;
import kh.com.mysabay.sdk.SdkConfiguration;
import kh.com.mysabay.sdk.pojo.AppItem;
import kh.com.mysabay.sdk.pojo.NetworkState;
import kh.com.mysabay.sdk.pojo.googleVerify.GoogleVerifyBody;
import kh.com.mysabay.sdk.pojo.googleVerify.GoogleVerifyResponse;
import kh.com.mysabay.sdk.pojo.mysabay.MySabayItemResponse;
import kh.com.mysabay.sdk.pojo.mysabay.ProviderResponse;
import kh.com.mysabay.sdk.pojo.payment.PaymentResponseItem;
import kh.com.mysabay.sdk.pojo.payment.SubscribePayment;
import kh.com.mysabay.sdk.pojo.shop.ShopItem;
import kh.com.mysabay.sdk.pojo.thirdParty.payment.Data;
import kh.com.mysabay.sdk.pojo.thirdParty.payment.ResponseItem;
import kh.com.mysabay.sdk.repository.StoreRepo;
import kh.com.mysabay.sdk.ui.activity.StoreActivity;
import kh.com.mysabay.sdk.utils.AppRxSchedulers;
import kh.com.mysabay.sdk.utils.LogUtil;
import kh.com.mysabay.sdk.utils.MessageUtil;
import kh.com.mysabay.sdk.webservice.AbstractDisposableObs;
import kh.com.mysabay.sdk.webservice.Constant;

/**
 * Created by Tan Phirum on 3/8/20
 * Gmail phirumtan@gmail.com
 */
public class StoreApiVM extends ViewModel {

    private static final String TAG = StoreApiVM.class.getSimpleName();

    private final StoreRepo storeRepo;
    private final SdkConfiguration sdkConfiguration;

    ApolloClient apolloClient;

    @Inject
    AppRxSchedulers appRxSchedulers;
    @Inject
    Gson gson;

    private final MediatorLiveData<String> _msgError = new MediatorLiveData<>();
    private final MediatorLiveData<NetworkState> _networkState;
    private final MediatorLiveData<List<ShopItem>> _shopItem;
    private final CompositeDisposable mCompos;
    private final MediatorLiveData<ShopItem> mDataSelected;
    private final MediatorLiveData<List<MySabayItemResponse>> mySabayItemMediatorLiveData;
    public final MediatorLiveData<List<ProviderResponse>> _thirdPartyItemMediatorLiveData;
    private final MediatorLiveData<String> invoiceId;


    @Inject
    public StoreApiVM(ApolloClient apolloClient, StoreRepo storeRepo) {
        this.apolloClient = apolloClient;
        this.storeRepo = storeRepo;
        this._networkState = new MediatorLiveData<>();
        this._shopItem = new MediatorLiveData<>();
        this.mCompos = new CompositeDisposable();
        this.mDataSelected = new MediatorLiveData<>();
        this.mySabayItemMediatorLiveData = new MediatorLiveData<>();
        this._thirdPartyItemMediatorLiveData = new MediatorLiveData<>();
        this.sdkConfiguration = MySabaySDK.getInstance().getSdkConfiguration();
        this.invoiceId = new MediatorLiveData<>();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mCompos != null) {
            mCompos.dispose();
            mCompos.clear();
        }
    }

    public void getShopFromServerGraphQL(@NotNull Context context) {
        AppItem appItem = gson.fromJson(MySabaySDK.getInstance().getAppItem(), AppItem.class);
        _networkState.setValue(new NetworkState(NetworkState.Status.LOADING));
        Store_PagerInput pager = Store_PagerInput.builder().page(1).limit(20).build();
        List<ShopItem> shopItems = new ArrayList<ShopItem>();
        apolloClient.query(new GetProductsByServiceCodeQuery("aog", new Input<>(pager, true))).toBuilder()
                .requestHeaders(RequestHeaders.builder()
                        .addHeader("Authorization", "Bearer " + appItem.token).build())
                .build()
                .enqueue(new ApolloCall.Callback<GetProductsByServiceCodeQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetProductsByServiceCodeQuery.Data> response) {
                        if (response.getErrors() != null) {
                            showErrorMsg(context, "Get product failed");
                        } else {
                            if (response.getData() != null) {
                                List<GetProductsByServiceCodeQuery.Product> products =  response.getData().store_listProduct().products();
                                for (GetProductsByServiceCodeQuery.Product product: products) {
                                    ShopItem shopItem = gson.fromJson(gson.toJson(product), ShopItem.class);
                                    shopItems.add(shopItem);
                                    LogUtil.info("Shop-Item", shopItem.toString());
                                }
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        _networkState.setValue(new NetworkState(NetworkState.Status.SUCCESS));
                                        _shopItem.setValue(shopItems);
                                        MySabaySDK.getInstance().trackEvents(context, "sdk-" + Constant.store, Constant.process, "get-store-success");
                                    }
                                });
                            } else {
                                showErrorMsg(context, "Data is empty");
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        showErrorMsg(e, context, "Get shop failed");
                        MySabaySDK.getInstance().trackEvents(context, "sdk-" + Constant.store, Constant.process, "get-store-failed");
                    }
                });
    }

    public LiveData<List<ShopItem>> getShopItem() {
        return _shopItem;
    }

    public LiveData<NetworkState> getNetworkState() {
        return _networkState;
    }

    public LiveData<List<ProviderResponse>> getThirdPartyProviders() {
        return _thirdPartyItemMediatorLiveData;
    }

    public LiveData<List<MySabayItemResponse>> getMySabayProvider() {
        return mySabayItemMediatorLiveData;
    }

    public void setShopItemSelected(ShopItem data) {
        _networkState.setValue(new NetworkState(NetworkState.Status.SUCCESS));
        this.mDataSelected.setValue(data);
    }

    public LiveData<String> getInvoiceId() {
        return this.invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId.setValue(invoiceId);
    }

    public LiveData<ShopItem> getItemSelected() {
        return this.mDataSelected;
    }

    public void  getMySabayCheckoutWithGraphQL(@NotNull Context context, String itemId) {
        _networkState.setValue(new NetworkState(NetworkState.Status.LOADING));
        apolloClient.query(new Checkout_getPaymentServiceProviderForProductQuery(itemId)).enqueue(new ApolloCall.Callback<Checkout_getPaymentServiceProviderForProductQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<Checkout_getPaymentServiceProviderForProductQuery.Data> response) {
                if (response.getErrors() != null) {
                    showErrorMsg(context, "Get Mysabay checkout failed");
                } else {
                    if (response.getData() != null) {
                       if (response.getData().checkout_getPaymentServiceProviderForProduct().paymentServiceProviders() != null) {
                            List<Checkout_getPaymentServiceProviderForProductQuery.PaymentServiceProvider> providers = response.getData().checkout_getPaymentServiceProviderForProduct().paymentServiceProviders();
                            List<MySabayItemResponse> mySabayItemResponses = new ArrayList<>();
                            for (Checkout_getPaymentServiceProviderForProductQuery.PaymentServiceProvider payment : providers) {
                                MySabayItemResponse paymentProvider = gson.fromJson(gson.toJson(payment), MySabayItemResponse.class);
                                mySabayItemResponses.add(paymentProvider);
                            }

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        mySabayItemMediatorLiveData.setValue(mySabayItemResponses);
                                    } catch (Exception e) {
                                        MessageUtil.displayToast(context, "Get mysabay checkout failed");
                                    }
                                    _networkState.setValue(new NetworkState(NetworkState.Status.SUCCESS));
                                }
                            });

                       } else {
                           showErrorMsg(context, "Data is empty");
                       }
                    } else {
                        showErrorMsg(context, context.getString(R.string.msg_can_not_connect_server));
                    }
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                showErrorMsg(e, context, "Get payment service provider failed");
            }
        });
    }

    /**
     * @param context
     * @param shopItem
     */
    public void createInvoice(Context context, ShopItem shopItem) {
        AppItem appItem = gson.fromJson(MySabaySDK.getInstance().getAppItem(), AppItem.class);

        Object item = new ShopItem(shopItem.id, shopItem.properties);
        List<Object> items = new ArrayList<>();
        String json = new Gson().toJson(item);
        JSONParser parser = new JSONParser();
        try {
            items.add(parser.parse(json));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Invoice_CreateInvoiceInput obj = Invoice_CreateInvoiceInput.builder()
                .items(items)
                .amount(100)
                .currency("USD")
                .notes("this is invoice")
                .ssnTxHash("")
                .paymentProvider("")
                .build();
        Input<Invoice_CreateInvoiceInput> input = Input.fromNullable(obj);
        apolloClient.mutate(new CreateInvoiceMutation(input)).toBuilder()
                .requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + appItem.token).build())
                .build()
                .enqueue(new ApolloCall.Callback<CreateInvoiceMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<CreateInvoiceMutation.Data> response) {
                        if (response.getErrors() != null) {
                            showErrorMsg(context, "Create invoice failed");
                        } else {
                            if(response.getData() != null) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setInvoiceId(response.getData().invoice_createInvoice().invoice().id());
                                        _networkState.setValue(new NetworkState(NetworkState.Status.SUCCESS));
                                    }
                                });
                            } else {
                                showErrorMsg(context, "Invoice is empty");
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        showErrorMsg(e, context, "Create invoice failed");
                    }
                });
    }

    public void getPaymentDetail(StoreActivity context, String id, String invoiceId, ShopItem shopItem, String type) {
        String paymentAddress = MySabaySDK.getInstance().getPaymentAddress(invoiceId);
        AppItem appItem = gson.fromJson(MySabaySDK.getInstance().getAppItem(), AppItem.class);

        LogUtil.info("payment Address", paymentAddress);
        LogUtil.info("id", id);

        apolloClient.query(new GetPaymentDetailQuery(id, paymentAddress)).toBuilder()
                .requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + appItem.token).build())
                .build()
                .enqueue(new ApolloCall.Callback<GetPaymentDetailQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetPaymentDetailQuery.Data> response) {
                        if (response.getErrors() != null) {
                            showErrorMsg(context, "Get payment Detail failed");
                        } else {
                            if (response.getData().checkout_getPaymentServiceProviderDetailForPayment() != null) {
                                GetPaymentDetailQuery.Checkout_getPaymentServiceProviderDetailForPayment payment = response.getData().checkout_getPaymentServiceProviderDetailForPayment();

                                Data data = new Data();
                                data.withHash(payment.hash());
                                data.withSignature(payment.signature());
                                data.withPublicKey(payment.publicKey());
                                data.withRequestUrl(payment.requestUrl());
                                data.withAdditionalBody(payment.additionalBody());
                                data.withAdditionalHeader(payment.additionalHeader());

                                LogUtil.info("detail-data", data.toString());

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (type.equals("pre-auth")) {
                                            postToPaidWithMySabayProvider(context, (double) 0,  data, paymentAddress);
                                        } else {
                                            postToPaidWithBank(context, data, paymentAddress);
                                        }
                                        _networkState.setValue(new NetworkState(NetworkState.Status.SUCCESS));
                                    }
                                });
                            } else {
                                showErrorMsg(context, "Get Payment Detail is error");
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        showErrorMsg(e, context, "Get payment Detail failed");
                    }
                });
    }


    /**
     * show list all bank provider
     *
     * @param context
     */
    public void get3PartyCheckout(@NotNull Context context) {
        if (getMySabayProvider().getValue() == null) return;

        List<MySabayItemResponse> mySabayItem = getMySabayProvider().getValue();
        List<ProviderResponse> result = new ArrayList<>();

        for (MySabayItemResponse item : mySabayItem) {
            if (item.type.equals("onetime")) {
                for (ProviderResponse providerResponse: item.providers) {
                    result.add(providerResponse);
                }
            }
        }
        _thirdPartyItemMediatorLiveData.setValue(result);
    }

    /**
     * show list all bank provider
     *
     * @param context
     */
    public ProviderResponse getInAppPurchaseProvider(@NotNull Context context) {
        if (getMySabayProvider().getValue() == null) return new ProviderResponse();

        List<MySabayItemResponse> mySabayItem = getMySabayProvider().getValue();
        ProviderResponse provider  = new ProviderResponse();

        for (ProviderResponse item : mySabayItem.get(0).providers) {
            if (item.type.equals("iap")) {
                provider = item;
            }
        }
        return provider;
    }

    public ProviderResponse getMysabayProviderId(String type) {
        if (getMySabayProvider().getValue() == null) return new ProviderResponse();

        ProviderResponse provider  = new ProviderResponse();
        for (MySabayItemResponse item : getMySabayProvider().getValue()) {
            if (item.type.equals("pre-auth")) {
                for (ProviderResponse providerItem: item.providers) {
                    if (type.equals(providerItem.code)) {
                        provider = providerItem;
                    }
                }
            }
        }
    return provider;
    }

    public void postToVerifyAppInPurchase(@NotNull Context context, @NotNull GoogleVerifyBody body) {
        _networkState.setValue(new NetworkState(NetworkState.Status.LOADING));
        AppItem appItem = gson.fromJson(MySabaySDK.getInstance().getAppItem(), AppItem.class);
        mCompos.add(storeRepo.postToVerifyGoogle(MySabaySDK.getInstance().appSecret(), appItem.token, body).subscribeOn(appRxSchedulers.io())
                .observeOn(appRxSchedulers.mainThread()).subscribe(new Consumer<GoogleVerifyResponse>() {
                    @Override
                    public void accept(GoogleVerifyResponse googleVerifyResponse) throws Exception {
                        _networkState.setValue(new NetworkState(NetworkState.Status.SUCCESS));
                        EventBus.getDefault().post(new SubscribePayment(Globals.APP_IN_PURCHASE, body, null));
                        ((Activity) context).finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        _networkState.setValue(new NetworkState(NetworkState.Status.ERROR));
                        MessageUtil.displayDialog(context, "Error" + throwable.getMessage());
                    }
                }));
    }

    /**
     * This method is use to buy item with mysabay payment
     */
    public void postToPaidWithMySabayProvider(Context context, Double balanceGold, Data data, String paymentAddress) {
        AppItem appItem = gson.fromJson(MySabaySDK.getInstance().getAppItem(), AppItem.class);
        if (getMySabayProvider().getValue() == null) return;

        LogUtil.info("Payment-Address", paymentAddress);
        LogUtil.info("data", data.publicKey);


        List<ProviderResponse> listMySabayProvider = new ArrayList<>();
        for (MySabayItemResponse item : getMySabayProvider().getValue()) {
            if (item.type.equals("pre-auth")) {
                for (ProviderResponse provider: item.providers) {
                    if (provider.code.equals("sabay")) {
                        listMySabayProvider.add(0, provider);
                    } else {
                        listMySabayProvider.add(provider);
                    }
                }
            }
        }

        storeRepo.postToPaid(appItem.token, data.hash, data.signature, data.publicKey, paymentAddress, paymentAddress).subscribeOn(appRxSchedulers.io())
                .observeOn(appRxSchedulers.mainThread())
                .subscribe(new AbstractDisposableObs<PaymentResponseItem>(context, _networkState) {
                    @Override
                    protected void onSuccess(PaymentResponseItem item) {
                        EventBus.getDefault().post(new SubscribePayment(Globals.MY_SABAY, item, null));
                        ((Activity) context).finish();
                    }

                    @Override
                    protected void onErrors(Throwable error) {
                        LogUtil.info("Payment-Error",  error.getMessage());
                        EventBus.getDefault().post(new SubscribePayment(Globals.MY_SABAY, null, error.getMessage()));
                    }
                });
    }



    public void postToPaidWithBank(StoreActivity context, Data data, String paymentAddress) {
        AppItem appItem = gson.fromJson(MySabaySDK.getInstance().getAppItem(), AppItem.class);
        ShopItem shopItem = getItemSelected().getValue();

        LogUtil.info("Payment-Address", paymentAddress);
        LogUtil.info("data", data.toString());

        if (data != null && shopItem != null) {
            storeRepo.postToChargeOneTime(appItem.token, data.hash, data.signature, data.publicKey, paymentAddress, paymentAddress).subscribeOn(appRxSchedulers.io())
                    .observeOn(appRxSchedulers.mainThread())
                    .subscribe(new AbstractDisposableObs<ResponseItem>(context, _networkState) {
                        @Override
                        protected void onSuccess(ResponseItem response) {
                            if (response.status == 200) {
                                LogUtil.info("PaymentBody", response.toString());
//                                context.initAddFragment(BankVerifiedFm.newInstance(response.data, shopItem, data.code), PaymentFm.TAG, true);
                            } else
                                MessageUtil.displayDialog(context, gson.toJson(response));
                        }

                        @Override
                        protected void onErrors(Throwable error) {
                            MessageUtil.displayDialog(context, gson.toJson(error));
                            LogUtil.info(TAG, "error " + error.getLocalizedMessage());
                        }
                    });
        }
    }

    public void getInvoiceById(Context context, String id) {
        AppItem appItem = gson.fromJson(MySabaySDK.getInstance().getAppItem(), AppItem.class);
        if (id != null) {
            apolloClient.query(new GetInvoiceByIdQuery(id)).toBuilder()
                    .requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + appItem.token).build())
                    .build().enqueue(new ApolloCall.Callback<GetInvoiceByIdQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<GetInvoiceByIdQuery.Data> response) {

                }

                @Override
                public void onFailure(@NotNull ApolloException e) {

                }
            });
        }
    }

    public void  showErrorMsg(ApolloException e, Context context, String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (e != null) {
                    if (e instanceof ApolloNetworkException) {
                        _networkState.setValue(new NetworkState(NetworkState.Status.ERROR, context.getString(R.string.msg_can_not_connect_server)));
                    } else {
                        _networkState.setValue(new NetworkState(NetworkState.Status.ERROR));
                        MessageUtil.displayDialog(context, message);
                    }
                } else {
                    _networkState.setValue(new NetworkState(NetworkState.Status.ERROR));
                    MessageUtil.displayDialog(context, message);
                }
            }
        });
    }

    public void showErrorMsg(Context context, String message) {
        showErrorMsg(null, context, message);
    }


}

