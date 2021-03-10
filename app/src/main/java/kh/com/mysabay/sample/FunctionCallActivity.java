package kh.com.mysabay.sample;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.mysabay.sdk.Checkout_getPaymentServiceProviderForProductQuery;
import com.mysabay.sdk.GetProductsByServiceCodeQuery;
import com.mysabay.sdk.LoginWithPhoneMutation;
import com.mysabay.sdk.UserProfileQuery;
import com.mysabay.sdk.VerifyOtpCodMutation;

import java.util.ArrayList;
import java.util.List;

import kh.com.mysabay.sample.databinding.ActivityFunctionCallBinding;
import kh.com.mysabay.sdk.Globals;
import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.adapter.BankProviderAdapter;
import kh.com.mysabay.sdk.adapter.ShopAdapter;
import kh.com.mysabay.sdk.callback.DataCallback;
import kh.com.mysabay.sdk.databinding.PartialBankProviderBinding;
import kh.com.mysabay.sdk.databinding.PartialShopItemBinding;
import kh.com.mysabay.sdk.databinding.PartialShopProviderBinding;
import kh.com.mysabay.sdk.pojo.AppItem;
import kh.com.mysabay.sdk.pojo.mysabay.MySabayItemResponse;
import kh.com.mysabay.sdk.pojo.mysabay.ProviderResponse;
import kh.com.mysabay.sdk.pojo.shop.ShopItem;
import kh.com.mysabay.sdk.utils.FontUtils;
import kh.com.mysabay.sdk.utils.LogUtil;
import kh.com.mysabay.sdk.utils.MessageUtil;

public class FunctionCallActivity extends AppCompatActivity {

    private ActivityFunctionCallBinding mViewBinding;
    private MaterialDialog dialogBank;
    private MaterialDialog dialogShop;
    private GridLayoutManager mLayoutManager;

    List<ShopItem> shopItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_function_call);
        mViewBinding.viewPb.setVisibility(View.GONE);

        mViewBinding.btnLogin.setOnClickListener(v -> {
            MySabaySDK.getInstance().loginWithPhoneNumber("85512267670", new DataCallback<LoginWithPhoneMutation.Sso_loginPhone>() {
                @Override
                public void onSuccess(LoginWithPhoneMutation.Sso_loginPhone response) {
                    MessageUtil.displayDialog(v.getContext(), "You need to verify otp code");
                }

                @Override
                public void onFailed(Object error) {
                    MessageUtil.displayDialog(getApplicationContext(), "Login Error");
                }
            });
        });

        mViewBinding.btnVerify.setOnClickListener(v -> {
            MySabaySDK.getInstance().verifyOTPCode("85512267670", "111111", new DataCallback<VerifyOtpCodMutation.Sso_verifyOTP>() {
                @Override
                public void onSuccess(VerifyOtpCodMutation.Sso_verifyOTP response) {
                    AppItem appItem = new AppItem(response.accessToken(), response.refreshToken(), response.expire());
                    String encrypted = new Gson().toJson(appItem);
                    MySabaySDK.getInstance().saveAppItem(encrypted);
                    MessageUtil.displayDialog(v.getContext(), "Login success");
                }

                @Override
                public void onFailed(Object error) {
                    LogUtil.info("Otp verification failed", error.toString());
                }
            });
        });

        mViewBinding.btnUserProfile.setOnClickListener(v -> {
            MySabaySDK.getInstance().getUserInfo(MySabaySDK.getInstance().currentToken(),
                new DataCallback<UserProfileQuery.Sso_userProfile>() {
                    @Override
                    public void onSuccess(UserProfileQuery.Sso_userProfile response) {
                        MessageUtil.displayDialog(v.getContext(), response.toString());
                    }

                    @Override
                    public void onFailed(Object error) {
                        LogUtil.info("Error", error.toString());
                    }
                });
        });

        mViewBinding.btnStore.setOnClickListener(v -> {
            MySabaySDK.getInstance().getStoreFromServer("sdk_sample", MySabaySDK.getInstance().currentToken(), new DataCallback<GetProductsByServiceCodeQuery.Store_listProduct>() {
                @Override
                public void onSuccess(GetProductsByServiceCodeQuery.Store_listProduct response) {
                    shopItems = new ArrayList<ShopItem>();
                    List<GetProductsByServiceCodeQuery.Product> products =  response.products();
                    for (GetProductsByServiceCodeQuery.Product product: products) {
                        ShopItem shopItem = new Gson().fromJson(new Gson().toJson(product), ShopItem.class);
                        shopItems.add(shopItem);
                    }
                    showShopItem(v.getContext(), shopItems);
                }

                @Override
                public void onFailed(Object error) {
                    LogUtil.info("Get shop Error", error.toString());
                }
            });
        });

        mViewBinding.btnPaymentProvider.setOnClickListener(v-> {
            MySabaySDK.getInstance().getMySabayCheckout(shopItems.get(0).id, new DataCallback<Checkout_getPaymentServiceProviderForProductQuery.Checkout_getPaymentServiceProviderForProduct>() {
                @Override
                public void onSuccess(Checkout_getPaymentServiceProviderForProductQuery.Checkout_getPaymentServiceProviderForProduct response) {

                    List<Checkout_getPaymentServiceProviderForProductQuery.PaymentServiceProvider> providers = response.paymentServiceProviders();
                    List<MySabayItemResponse> mySabayItemResponses = new ArrayList<>();
                    for (Checkout_getPaymentServiceProviderForProductQuery.PaymentServiceProvider payment : providers) {
                        MySabayItemResponse paymentProvider = new Gson().fromJson(new Gson().toJson(payment), MySabayItemResponse.class);
                        mySabayItemResponses.add(paymentProvider);
                    }
                    showBankProviders(v.getContext(), get3PartyCheckout(v.getContext(), mySabayItemResponses));
                }

                @Override
                public void onFailed(Object error) {
                    LogUtil.info("Error", error.toString());
                }
            });
        });
    }

    public List<ProviderResponse> get3PartyCheckout(Context context, List<MySabayItemResponse> mySabayItemResponses) {
        if (mySabayItemResponses == null) return null;

        List<MySabayItemResponse> mySabayItem = mySabayItemResponses;
        List<ProviderResponse> result = new ArrayList<>();

        for (MySabayItemResponse item : mySabayItem) {
            if (item.type.equals(Globals.ONE_TIME_PROVIDER)) {
                for (ProviderResponse providerResponse: item.providers) {
                    result.add(providerResponse);
                }
            }
        }
        LogUtil.info("Result", result.toString());
        return result;
    }

    private void showBankProviders(Context context, List<ProviderResponse> data) {
        if (dialogBank != null) {
            dialogBank.dismiss();
        }
        PartialBankProviderBinding view = DataBindingUtil.inflate(LayoutInflater.from(context), kh.com.mysabay.sdk.R.layout.partial_bank_provider, null, false);
        RecyclerView rcv = view.bankRcv;
        BankProviderAdapter adapter = new BankProviderAdapter(context, data, item -> {
            ProviderResponse provider = (ProviderResponse) item;
            if (data != null) {
//                paymentProcess(getContext(), mData, Globals.ONE_TIME_PROVIDER, provider);
            }
            if (dialogBank != null)
                dialogBank.dismiss();
            dialogBank = null;
        });
        rcv.setLayoutManager(new LinearLayoutManager(context));
        rcv.setHasFixedSize(true);
        rcv.setAdapter(adapter);
        dialogBank = new MaterialDialog.Builder(context)
                .typeface(FontUtils.getTypefaceKhmerBold(context), FontUtils.getTypefaceKhmer(context))
                .customView(view.getRoot(), true)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .backgroundColorRes(kh.com.mysabay.sdk.R.color.colorWhite)
                .positiveColorRes(kh.com.mysabay.sdk.R.color.colorYellow)
                .positiveText(kh.com.mysabay.sdk.R.string.label_close).onPositive((dialog, which) -> {
                    dialog.dismiss();
                    dialogBank = null;
                }).build();
        dialogBank.show();
    }

    private void showShopItem(Context context, List<ShopItem> data) {
        LogUtil.info("data", data.size() + "");
        if (dialogShop != null) {
            dialogShop.dismiss();
        }
        ShopAdapter adapter = new ShopAdapter(context, item -> {
           if (item != null) {

           }
            if (dialogShop != null)
                dialogShop.dismiss();
            dialogShop = null;
        });

        adapter.clear();
        adapter.insert(data);
        adapter.notifyDataSetChanged();

        PartialShopProviderBinding view = DataBindingUtil.inflate(LayoutInflater.from(context), kh.com.mysabay.sdk.R.layout.partial_shop_provider, null, false);
        RecyclerView rcv = view.rcv;
        mLayoutManager = new GridLayoutManager(context, getResources().getInteger(kh.com.mysabay.sdk.R.integer.layout_size));
        mLayoutManager.setSpanCount(getResources().getInteger(kh.com.mysabay.sdk.R.integer.layout_size));
        rcv.setLayoutManager(mLayoutManager);
        rcv.setHasFixedSize(true);
        rcv.setAdapter(adapter);

        dialogShop = new MaterialDialog.Builder(context)
                .typeface(FontUtils.getTypefaceKhmerBold(context), FontUtils.getTypefaceKhmer(context))
                .customView(view.getRoot(), true)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .backgroundColorRes(kh.com.mysabay.sdk.R.color.colorWhite)
                .positiveColorRes(kh.com.mysabay.sdk.R.color.colorYellow)
                .positiveText(kh.com.mysabay.sdk.R.string.label_close).onPositive((dialog, which) -> {
                    dialog.dismiss();
                    dialogShop = null;
                }).build();
        dialogShop.show();
    }
}
