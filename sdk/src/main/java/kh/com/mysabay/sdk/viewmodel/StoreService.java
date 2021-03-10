package kh.com.mysabay.sdk.viewmodel;

import android.arch.lifecycle.ViewModel;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.request.RequestHeaders;
import com.google.gson.Gson;
import com.mysabay.sdk.Checkout_getPaymentServiceProviderForProductQuery;
import com.mysabay.sdk.CreateInvoiceMutation;
import com.mysabay.sdk.GetInvoiceByIdQuery;
import com.mysabay.sdk.GetPaymentDetailQuery;
import com.mysabay.sdk.GetProductsByServiceCodeQuery;
import com.mysabay.sdk.type.Invoice_CreateInvoiceInput;
import com.mysabay.sdk.type.Store_PagerInput;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import kh.com.mysabay.sdk.Globals;
import kh.com.mysabay.sdk.callback.DataCallback;
import kh.com.mysabay.sdk.pojo.mysabay.ProviderResponse;
import kh.com.mysabay.sdk.pojo.payment.PaymentResponseItem;
import kh.com.mysabay.sdk.pojo.shop.ShopItem;
import kh.com.mysabay.sdk.pojo.thirdParty.payment.Data;
import kh.com.mysabay.sdk.pojo.thirdParty.payment.ResponseItem;
import kh.com.mysabay.sdk.repository.StoreRepo;
import kh.com.mysabay.sdk.utils.AppRxSchedulers;
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
                        if(response.getData() == null) {
                            callbackData.onSuccess(response.getData().store_listProduct());
                        } else {
                            callbackData.onFailed("Get shop from server Failed");
                        }
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
                if(response.getData() != null) {
                    callbackData.onSuccess(response.getData().checkout_getPaymentServiceProviderForProduct());
                } else {
                    callbackData.onFailed("Get MySabay checkout failed");
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                callbackData.onFailed(e);
            }
        });
    }

    public void createPaymentProcess(String token, ShopItem shopItem, ProviderResponse provider, String type, int exChangeRate, DataCallback<Object> callbackData) {

        List<Object> items = new ArrayList<>();
        JSONObject jsonObject=new JSONObject();
        JSONParser parser = new JSONParser();
        try {
            jsonObject.put("package_id",provider.packageId);
            jsonObject.put("displayName", shopItem.properties.displayName);
            jsonObject.put("packageCode", shopItem.properties.packageCode);
            items.add(parser.parse(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        double amount = shopItem.salePrice * exChangeRate;
        double priceOfItem = shopItem.salePrice;

        if (type.equals(Globals.MY_SABAY_PROVIDER)) {
            if (provider.issueCurrencies.get(0).equals("SG")) {
                priceOfItem = Math.ceil(amount/100);
            } else if (provider.issueCurrencies.get(0).equals("SC")) {
                priceOfItem = Math.ceil(amount/100);
            }
        }
        Invoice_CreateInvoiceInput obj = Invoice_CreateInvoiceInput.builder()
                .items(items)
                .amount(priceOfItem)
                .currency(provider.issueCurrencies.get(0))
                .notes("this is invoice")
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
                        if (response.getErrors() != null) {
                            callbackData.onFailed("Create invoice failed");
                        } else {
                            if(response.getData() != null) {
                                getPaymentDetail(token, shopItem, provider.id, response.getData().invoice_createInvoice().invoice().id(), type, callbackData);
                            } else {
                                callbackData.onFailed("Create invoice failed");
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        callbackData.onFailed("Create invoice failed");
                    }
                });
    }

    public void createInvoice(String token, ShopItem shopItem, DataCallback<CreateInvoiceMutation.Invoice_createInvoice> callbackData) {
        Object shop = new ShopItem(shopItem.id, shopItem.salePrice, shopItem.currencyCode, shopItem.properties);
        List<Object> items = new ArrayList<>();
        String json = new Gson().toJson(shop);
        JSONParser parser = new JSONParser();
        try {
            items.add(parser.parse(json));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Invoice_CreateInvoiceInput obj = Invoice_CreateInvoiceInput.builder()
                .items(items)
                .amount(100)
                .notes("this is invoice")
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
                        if (response.getErrors() == null) {
                            callbackData.onSuccess(response.getData().invoice_createInvoice());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        callbackData.onFailed("Create invoice failed");
                    }
                });
    }

    public void getPaymentDetail(String token, ShopItem shopItem, String paymentProviderId, String paymentAddress, String type, DataCallback<Object> callback) {
        apolloClient.query(new GetPaymentDetailQuery(paymentProviderId, paymentAddress)).toBuilder()
                .requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + token).build())
                .build()
                .enqueue(new ApolloCall.Callback<GetPaymentDetailQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetPaymentDetailQuery.Data> response) {
                        if (response.getErrors() == null) {
                            if (response.getData() != null) {
                                GetPaymentDetailQuery.Checkout_getPaymentServiceProviderDetailForPayment payment = response.getData().checkout_getPaymentServiceProviderDetailForPayment();

                                Data data = new Data();
                                data.withHash(payment.hash());
                                data.withSignature(payment.signature());
                                data.withPublicKey(payment.publicKey());
                                data.withRequestUrl(payment.requestUrl());
                                data.withAdditionalBody(payment.additionalBody());
                                data.withAdditionalHeader(payment.additionalHeader());

                                if (type.equals(Globals.MY_SABAY_PROVIDER)) {
                                    postToChargePreAuth(data.requestUrl + paymentAddress, token, data.hash, data.signature, data.publicKey, paymentAddress, callback);
                                } else if (type.equals(Globals.MY_SABAY_PROVIDER)) {

                                }
                            } else {
                                callback.onFailed("Data response is empty");
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        callback.onFailed(e);
                    }
                });

    }

    public void postToChargeOneTime(String token, String url, String hash, String signature, String publicKey, String paymentAddress, DataCallback<ResponseItem> callback) {
        storeRepo.postToChargeOneTime(url, token, hash, signature, publicKey, paymentAddress)
                .subscribeOn(appRxSchedulers.io())
                .observeOn(appRxSchedulers.mainThread())
                .subscribe(new AbstractDisposableObs<ResponseItem>(null) {
                    @Override
                    protected void onSuccess(ResponseItem response) {
                        if (response.status == 200) {
                           callback.onSuccess(response);
                        } else
                            callback.onFailed("");
                    }

                    @Override
                    protected void onErrors(Throwable error) {
                       callback.onFailed("");
                    }
                });
    }

    public void postToChargePreAuth(String url, String token, String hash, String signature, String publicKey, String paymentAddress, DataCallback<Object> callback) {
        storeRepo.postToPaid(url, token, hash, signature, publicKey, paymentAddress).subscribeOn(appRxSchedulers.io())
                .observeOn(appRxSchedulers.mainThread())
                .subscribe(new AbstractDisposableObs<PaymentResponseItem>(null) {
                    @Override
                    protected void onSuccess(PaymentResponseItem paymentResponseItem) {
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

    public void getInvoiceById(String token, String invoiceId, DataCallback<GetInvoiceByIdQuery.Data> callback) {
        apolloClient.query(new GetInvoiceByIdQuery(invoiceId)).toBuilder()
                .requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + token).build())
                .build()
                .enqueue(new ApolloCall.Callback<GetInvoiceByIdQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetInvoiceByIdQuery.Data> response) {
                        if (response.getErrors() != null) {
                            callback.onFailed(response.getErrors());
                        } else {
                            if (response.getData() != null) {
                                callback.onSuccess(response.getData());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        callback.onFailed("Get invoice by id failed");
                    }
                });

    }
}
