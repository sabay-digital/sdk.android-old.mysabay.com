package kh.com.mysabay.sample;

import android.content.Context;
import android.content.Intent;
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
import com.mysabay.sdk.CreateMySabayLoginMutation;
import com.mysabay.sdk.GetProductsByServiceCodeQuery;
import com.mysabay.sdk.LoginGuestMutation;
import com.mysabay.sdk.LoginWithMySabayMutation;
import com.mysabay.sdk.LoginWithPhoneMutation;
import com.mysabay.sdk.UserProfileQuery;
import com.mysabay.sdk.VerifyOtpCodMutation;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

import kh.com.mysabay.adapter.ShopItemAdapter;
import kh.com.mysabay.sample.databinding.ActivityFunctionCallBinding;
import kh.com.mysabay.sdk.Globals;
import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.adapter.BankProviderAdapter;
import kh.com.mysabay.sdk.callback.DataCallback;
import kh.com.mysabay.sdk.databinding.PartialBankProviderBinding;
import kh.com.mysabay.sdk.databinding.PartialShopProviderBinding;
import kh.com.mysabay.sdk.pojo.AppItem;
import kh.com.mysabay.sdk.pojo.googleVerify.GoogleVerifyBody;
import kh.com.mysabay.sdk.pojo.mysabay.MySabayItemResponse;
import kh.com.mysabay.sdk.pojo.mysabay.ProviderResponse;
import kh.com.mysabay.sdk.pojo.shop.ShopItem;
import kh.com.mysabay.sdk.pojo.thirdParty.payment.Data;
import kh.com.mysabay.sdk.utils.FontUtils;
import kh.com.mysabay.sdk.utils.LogUtil;
import kh.com.mysabay.sdk.utils.MessageUtil;

public class FunctionCallActivity extends AppCompatActivity {

    private ActivityFunctionCallBinding mViewBinding;
    private MaterialDialog dialogBank;
    private MaterialDialog dialogShop;
    private GridLayoutManager mLayoutManager;

    List<ShopItem> shopItems;
    List<MySabayItemResponse> mySabayItemResponses;
    private ShopItem shopItem;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_function_call);
        mViewBinding.viewPb.setVisibility(View.GONE);

        mViewBinding.btnLoginGuest.setOnClickListener(v -> {
            MySabaySDK.getInstance().loginAsGuest(new DataCallback<LoginGuestMutation.Sso_loginGuest>() {
                @Override
                public void onSuccess(LoginGuestMutation.Sso_loginGuest response) {
                    MessageUtil.displayDialog(v.getContext(), new Gson().toJson(response.toString()));
                }

                @Override
                public void onFailed(Object error) {
                    LogUtil.info("Error", error.toString());
                }
            });
        });

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

        mViewBinding.btnLoginMysabay.setOnClickListener(v-> {
            MySabaySDK.getInstance().loginWithMySabay("test_pro5", "11111111", new DataCallback<LoginWithMySabayMutation.Sso_loginMySabay>() {
                @Override
                public void onSuccess(LoginWithMySabayMutation.Sso_loginMySabay response) {
                    MessageUtil.displayDialog(v.getContext(), "Login with mysabay success");
                }

                @Override
                public void onFailed(Object error) {
                    MessageUtil.displayDialog(v.getContext(), "Login with mysbay failed");
                }
            });
        });

        mViewBinding.btnCreateMysabay.setOnClickListener(v -> {
            MySabaySDK.getInstance().createMySabayAccount("test_pro6", "11111111", new DataCallback<CreateMySabayLoginMutation.Sso_createMySabayLogin>() {
                @Override
                public void onSuccess(CreateMySabayLoginMutation.Sso_createMySabayLogin response) {
                    MessageUtil.displayDialog(v.getContext(), "Create mysabay success");
                }

                @Override
                public void onFailed(Object error) {
                    MessageUtil.displayDialog(v.getContext(), error.toString());
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
            if (shopItem != null) {
            MySabaySDK.getInstance().getMySabayCheckout(shopItem.id, new DataCallback<Checkout_getPaymentServiceProviderForProductQuery.Checkout_getPaymentServiceProviderForProduct>() {
                @Override
                public void onSuccess(Checkout_getPaymentServiceProviderForProductQuery.Checkout_getPaymentServiceProviderForProduct response) {
                    List<Checkout_getPaymentServiceProviderForProductQuery.PaymentServiceProvider> providers = response.paymentServiceProviders();
                    mySabayItemResponses = new ArrayList<>();
                    for (Checkout_getPaymentServiceProviderForProductQuery.PaymentServiceProvider payment : providers) {
                        MySabayItemResponse paymentProvider = new Gson().fromJson(new Gson().toJson(payment), MySabayItemResponse.class);
                        mySabayItemResponses.add(paymentProvider);
                    }
                    mViewBinding.btnGroup.setVisibility(View.VISIBLE);
                    if (checkMySabayProvider(mySabayItemResponses)) {
                        mViewBinding.btnMysabay.setVisibility(View.VISIBLE);
                    } else {
                        mViewBinding.btnMysabay.setVisibility(View.GONE);
                    }
                    if (checkOneTimeProvider(mySabayItemResponses)) {
                        mViewBinding.btnThirdBankProvider.setVisibility(View.VISIBLE);
                    } else {
                        mViewBinding.btnThirdBankProvider.setVisibility(View.GONE);
                    }
                    if (checkIapProvider(mySabayItemResponses)) {
                        mViewBinding.btnInAppPurchase.setVisibility(View.VISIBLE);
                    } else {
                        mViewBinding.btnInAppPurchase.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailed(Object error) {
                    LogUtil.info("Error", error.toString());
                }
            });

            } else {
                MessageUtil.displayDialog(v.getContext(), "Please select item first");
            }
        });

        final int[] checkedId = new int[1];

        mViewBinding.btnThirdBankProvider.setOnClickListener(v -> {
            checkedId[0] = v.getId();
           showBankProviders(v.getContext(), get3PartyCheckout(v.getContext(), mySabayItemResponses));
        });

        mViewBinding.btnMysabay.setOnClickListener(v -> {
            checkedId[0] = v.getId();
            mViewBinding.btnMysabay.setBackgroundResource(kh.com.mysabay.sdk.R.color.colorYellow);
        });

        mViewBinding.btnInAppPurchase.setOnClickListener(v -> {
            checkedId[0] = v.getId();
        });

        mViewBinding.btnPay.setOnClickListener(v -> {
            LogUtil.info("Test", "Test");
            if (checkedId[0] == R.id.btn_mysabay) {
                ProviderResponse provider = getMySabayProvider(mySabayItemResponses, "sabay_coin");
                LogUtil.info("Provider", provider.toString());
                payment(v.getContext(), MySabaySDK.getInstance().currentToken(), shopItem, provider, "pre-auth");
            }
        });
    }

    public List<ProviderResponse> get3PartyCheckout(Context context, List<MySabayItemResponse> mySabayItemResponses) {
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
                payment(context, MySabaySDK.getInstance().currentToken(), shopItem, provider, "onetime");
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
        ShopItemAdapter adapter = new ShopItemAdapter(context, data, item -> {
           if (item != null) {
               shopItem = item;
               mViewBinding.shopItem.setText(shopItem.properties.displayName);
           }
           if (dialogShop != null)
                dialogShop.dismiss();
            dialogShop = null;
        });

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

    public boolean checkMySabayProvider(List<MySabayItemResponse> mySabayItemResponses) {
        boolean isFound = false;
        for (MySabayItemResponse mySabayItemResponse: mySabayItemResponses) {
            if (mySabayItemResponse.type.equals(Globals.MY_SABAY_PROVIDER)) {
                isFound = true;
            }
        }
        return  isFound;
    }

    public boolean checkOneTimeProvider(List<MySabayItemResponse> mySabayItemResponses) {
        boolean isFound = false;
        for (MySabayItemResponse mySabayItemResponse: mySabayItemResponses) {
            if (mySabayItemResponse.type.equals(Globals.ONE_TIME_PROVIDER)) {
                isFound = true;
            }
        }
        return  isFound;
    }

    public boolean checkIapProvider(List<MySabayItemResponse> mySabayItemResponses) {
        boolean isFound = false;
        for (MySabayItemResponse mySabayItemResponse: mySabayItemResponses) {
            if (mySabayItemResponse.type.equals(Globals.IAP_PROVIDER)) {
                isFound = true;
            }
        }
        return  isFound;
    }


    public void payment(Context context, String token, ShopItem shopItem, ProviderResponse provider, String type) {
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

        LogUtil.info("Currency", provider.issueCurrencies.get(0));

        paymentProcess(context, token, items, provider, shopItem.salePrice, provider.issueCurrencies.get(0), type);

    }

    public void paymentProcess(Context context, String token, List<Object> items, ProviderResponse provider, double amount, String currency, String type) {
        MySabaySDK.getInstance().createPaymentProcess(token, items, provider, amount, currency, new DataCallback<Object>() {
            @Override
            public void onSuccess(Object response) {
                LogUtil.info("Response-Purchase", response.toString());
                Data data = new Gson().fromJson(new Gson().toJson(response), Data.class);
                if (type == "pre-auth") {
                    MySabaySDK.getInstance().postToChargePreAuth(data.requestUrl + data.paymentAddress, token, data.hash, data.signature, data.publicKey, data.paymentAddress, new DataCallback<Object>() {
                        @Override
                        public void onSuccess(Object response) {
                            LogUtil.info("PreAuth-Purchase", "Success");
                        }

                        @Override
                        public void onFailed(Object error) {
                            LogUtil.info("PreAuth-Purchase", "Failed");
                        }
                    });
                } else if (type == "iap"){
                    GoogleVerifyBody body = new GoogleVerifyBody();
                    MySabaySDK.getInstance().postToChargeInAppPurchase(data.requestUrl + data.paymentAddress, token, body, new DataCallback<Object>() {
                        @Override
                        public void onSuccess(Object response) {
                            LogUtil.info("IAP-Purchase", "Success");
                        }

                        @Override
                        public void onFailed(Object error) {
                            LogUtil.info("IAP-Purchase", "Failed");
                        }
                    });
                } else {
                    LogUtil.info("onetime", data.invoiceId);
                    LogUtil.info("pay", data.paymentAddress);

                    Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
                    intent.putExtra("DATA", data);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailed(Object error) {
                MessageUtil.displayDialog(context, error.toString());
                LogUtil.info("Error", error.toString());
            }
        });
    }

    public ProviderResponse getMySabayProvider(List<MySabayItemResponse> mySabayItemResponses, String type) {
        ProviderResponse provider  = null;

        for (MySabayItemResponse item : mySabayItemResponses) {
            if (item.type.equals(Globals.MY_SABAY_PROVIDER)) {
                for (ProviderResponse providerItem: item.providers) {
                    if (type.equals(providerItem.code)) {
                        provider = providerItem;
                    }
                }
            }
        }
        return provider;
    }
}
