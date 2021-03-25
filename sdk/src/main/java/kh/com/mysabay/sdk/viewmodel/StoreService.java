package kh.com.mysabay.sdk.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import android.os.Looper;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.request.RequestHeaders;
import com.google.gson.Gson;
import com.mysabay.sdk.Checkout_getPaymentServiceProviderForProductQuery;
import com.mysabay.sdk.CreateInvoiceMutation;
import com.mysabay.sdk.GetExchangeRateQuery;
import com.mysabay.sdk.GetInvoiceByIdQuery;
import com.mysabay.sdk.GetPaymentDetailQuery;
import com.mysabay.sdk.GetProductsByServiceCodeQuery;
import com.mysabay.sdk.type.Invoice_CreateInvoiceInput;
import com.mysabay.sdk.type.Store_PagerInput;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.util.List;
import javax.inject.Inject;
import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.callback.DataCallback;
import kh.com.mysabay.sdk.pojo.googleVerify.GoogleVerifyBody;
import kh.com.mysabay.sdk.pojo.googleVerify.GoogleVerifyResponse;
import kh.com.mysabay.sdk.pojo.mysabay.ProviderResponse;
import kh.com.mysabay.sdk.pojo.payment.PaymentResponseItem;
import kh.com.mysabay.sdk.pojo.thirdParty.payment.Data;
import kh.com.mysabay.sdk.repository.StoreRepo;
import kh.com.mysabay.sdk.utils.AppRxSchedulers;
import kh.com.mysabay.sdk.utils.LogUtil;
import kh.com.mysabay.sdk.webservice.AbstractDisposableObs;

public class StoreService extends ViewModel {

    private static final String TAG = UserApiVM.class.getSimpleName();
    private ApolloClient apolloClient;
    private final StoreRepo storeRepo;

    @Inject
    AppRxSchedulers appRxSchedulers;

    @Inject
    public StoreService(ApolloClient apolloClient, StoreRepo storeRepo) {
        this.apolloClient = apolloClient;
        this.storeRepo = storeRepo;
    }

    public void getShopFromServerGraphQL(String serviceCode, String token, DataCallback<GetProductsByServiceCodeQuery.Store_listProduct> callbackData) {
        Store_PagerInput pager = Store_PagerInput.builder().page(1).limit(20).build();
        apolloClient.query(new GetProductsByServiceCodeQuery(serviceCode, new Input<>(pager, true))).toBuilder()
                .requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + token).build())
                .build()
                .enqueue(new ApolloCall.Callback<GetProductsByServiceCodeQuery.Data>() {

                    @Override
                    public void onResponse(@NotNull Response<GetProductsByServiceCodeQuery.Data> response) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.getErrors() != null) {
                                    callbackData.onFailed(response.getErrors().get(0).getMessage());
                                } else {
                                    if (response.getData() != null) {
                                        callbackData.onSuccess(response.getData().store_listProduct());
                                    } else {
                                        callbackData.onFailed("Get shop from server Failed");
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        callbackData.onFailed(e);
                    }
        });
    }

    public void getMySabayCheckout(String itemId, DataCallback<Checkout_getPaymentServiceProviderForProductQuery.Checkout_getPaymentServiceProviderForProduct> callbackData) {
        apolloClient.query(new Checkout_getPaymentServiceProviderForProductQuery(itemId)).enqueue(new ApolloCall.Callback<Checkout_getPaymentServiceProviderForProductQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<Checkout_getPaymentServiceProviderForProductQuery.Data> response) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.getErrors() != null) {
                            callbackData.onFailed(response.getErrors().get(0).getMessage());
                        } else {
                            if (response.getData() != null) {
                                callbackData.onSuccess(response.getData().checkout_getPaymentServiceProviderForProduct());
                            } else {
                                callbackData.onFailed("Get MySabay checkout failed");
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                callbackData.onFailed(e);
            }
        });
    }

    public void createPaymentProcess(String token, List<Object> items, ProviderResponse provider, double amount, String currency, DataCallback<Object> callbackData) {
        Input<String> serviceCode = Input.fromNullable(MySabaySDK.getInstance().serviceCode());
        apolloClient.query(new GetExchangeRateQuery(serviceCode)).toBuilder()
                .build()
                .enqueue(new ApolloCall.Callback<GetExchangeRateQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetExchangeRateQuery.Data> response) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.getErrors() != null) {
                                    callbackData.onFailed(response.getErrors().get(0).getMessage());
                                } else {
                                    if (response.getData() != null) {
                                        createInvoice(token, items, provider, amount, currency, callbackData);
                                    } else {
                                        callbackData.onFailed("Get Exchange rate failed");
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callbackData.onFailed(e);
                            }
                        });
                    }
                });
    }

    public void createInvoice(String token, List<Object> items, ProviderResponse provider, double amount, String currency, DataCallback<Object> callbackData) {
        Invoice_CreateInvoiceInput obj = Invoice_CreateInvoiceInput.builder()
                .items(items)
                .amount(amount)
                .currency(currency)
                .notes("")
                .ssnTxHash("")
                .paymentProvider("")
                .build();

        Input<Invoice_CreateInvoiceInput> input = Input.fromNullable(obj);
        apolloClient.mutate(new CreateInvoiceMutation(input)).toBuilder()
                .requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + token).build())
                .build()
                .enqueue(new ApolloCall.Callback<CreateInvoiceMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<CreateInvoiceMutation.Data> response) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.getErrors() != null) {
                                    callbackData.onFailed(response.getErrors().get(0).getMessage());
                                } else {
                                    if(response.getData() != null) {
                                        LogUtil.info("Create Invoice", "invoice");
                                        getPaymentDetail(token, provider.id, response.getData().invoice_createInvoice().invoice().id(), callbackData);
                                    } else {
                                        callbackData.onFailed("Create invoice failed");
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callbackData.onFailed("Create invoice failed");
                            }
                        });
                    }
                });
    }

    public void getPaymentDetail(String token, String paymentProviderId, String invoiceId, DataCallback<Object> callback) {
        String paymentAddress = MySabaySDK.getInstance().getPaymentAddress(invoiceId);

        LogUtil.info("Id", paymentProviderId);
        LogUtil.info("paymentAddress", paymentAddress);

        apolloClient.query(new GetPaymentDetailQuery(paymentProviderId, paymentAddress)).toBuilder()
                .requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + token).build())
                .build()
                .enqueue(new ApolloCall.Callback<GetPaymentDetailQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetPaymentDetailQuery.Data> response) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.getErrors() != null) {
                                    LogUtil.info("Getpaymentdetail", response.getErrors().get(0).getMessage());
                                    callback.onFailed(response.getErrors().get(0).getMessage());
                                } else {
                                    if (response.getData() != null) {
                                        LogUtil.info("Getpaymentdetail", "success");
                                        GetPaymentDetailQuery.Checkout_getPaymentServiceProviderDetailForPayment payment = response.getData().checkout_getPaymentServiceProviderDetailForPayment();

                                        Data data = new Data();
                                        data.withHash(payment.hash());
                                        data.withSignature(payment.signature());
                                        data.withPublicKey(payment.publicKey());
                                        data.withRequestUrl(payment.requestUrl());
                                        data.withAdditionalBody(payment.additionalBody());
                                        data.withAdditionalHeader(payment.additionalHeader());

                                        data.withPaymentAddress(paymentAddress);
                                        data.withInvoiceId(invoiceId);

                                        LogUtil.info("Payload", new Gson().toJson(data));
                                        callback.onSuccess(data);
                                    }
                                }
                            }
                        });
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailed(e);
                            }
                        });
                    }
                });

    }

    public void postToChargeInAppPurchase(String url, String token, GoogleVerifyBody body, DataCallback<Object> callback) {
        storeRepo.postToVerifyGoogle(url, token, body)
                .subscribeOn(appRxSchedulers.io())
                .observeOn(appRxSchedulers.mainThread())
                .subscribe(new AbstractDisposableObs<GoogleVerifyResponse>(null) {
                    @Override
                    protected void onSuccess(GoogleVerifyResponse googleVerifyResponse) {
                        if(googleVerifyResponse != null) {
                            callback.onSuccess(googleVerifyResponse);
                        } else {
                            callback.onSuccess("Purchase with iap failed");
                        }
                    }

                    @Override
                    protected void onErrors(Throwable error) {
                        callback.onFailed(error);
                    }
                });
    }

    public void postToChargePreAuth(String url, String token, String hash, String signature, String publicKey, String paymentAddress, DataCallback<Object> callback) {
        storeRepo.postToPaid(url, token, hash, signature, publicKey, paymentAddress).subscribeOn(appRxSchedulers.io())
                .observeOn(appRxSchedulers.mainThread())
                .subscribe(new AbstractDisposableObs<PaymentResponseItem>(null) {
                    @Override
                    protected void onSuccess(PaymentResponseItem paymentResponseItem) {
                        LogUtil.info("Pre-Auth", "Payment Success");
                        if (paymentResponseItem != null) {
                            callback.onSuccess(paymentResponseItem);
                        } else
                            callback.onFailed("Data response is empty");
                    }

                    @Override
                    protected void onErrors(Throwable error) {
                        callback.onFailed(error);
                    }
                });
    }

    public void getInvoiceById(String token, String invoiceId, DataCallback<GetInvoiceByIdQuery.Invoice_getInvoiceById> callback) {
        apolloClient.query(new GetInvoiceByIdQuery(invoiceId)).toBuilder()
                .requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + token).build())
                .build()
                .enqueue(new ApolloCall.Callback<GetInvoiceByIdQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetInvoiceByIdQuery.Data> response) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.getErrors() != null) {
                                    callback.onFailed(response.getErrors());
                                } else {
                                    if (response.getData() != null) {
                                        callback.onSuccess(response.getData().invoice_getInvoiceById());
                                    } else {
                                        callback.onFailed("Get Invoice Id Failed");
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailed(e);
                            }
                        });
                    }
                });

    }

    public void getExchangeRate(DataCallback<List<GetExchangeRateQuery.Sso_service>> callback) {
        Input<String> serviceCode = Input.fromNullable(MySabaySDK.getInstance().serviceCode());
        apolloClient.query(new GetExchangeRateQuery(serviceCode)).toBuilder()
                .build()
                .enqueue(new ApolloCall.Callback<GetExchangeRateQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetExchangeRateQuery.Data> response) {
                        if (response.getErrors() != null) {
                            callback.onFailed(response.getErrors().get(0).getMessage());
                        } else {
                            if (response.getData() != null) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onSuccess(response.getData().sso_service());
                                    }
                                });
                            } else {
                                callback.onFailed("Get Exchange rate failed");
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        callback.onFailed(e);
                    }
                });
    }
}
