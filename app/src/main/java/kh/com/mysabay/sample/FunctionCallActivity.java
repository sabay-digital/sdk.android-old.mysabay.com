package kh.com.mysabay.sample;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.gson.Gson;
import com.mysabay.sdk.Checkout_getPaymentServiceProviderForProductQuery;
import com.mysabay.sdk.CreateMySabayLoginMutation;
import com.mysabay.sdk.CreateMySabayLoginWithPhoneMutation;
import com.mysabay.sdk.GetInvoiceByIdQuery;
import com.mysabay.sdk.GetProductsByServiceCodeQuery;
import com.mysabay.sdk.LoginGuestMutation;
import com.mysabay.sdk.LoginWithFacebookMutation;
import com.mysabay.sdk.LoginWithMySabayMutation;
import com.mysabay.sdk.LoginWithPhoneMutation;
import com.mysabay.sdk.SendCreateMySabayWithPhoneOTPMutation;
import com.mysabay.sdk.UserProfileQuery;
import com.mysabay.sdk.VerifyMySabayMutation;
import com.mysabay.sdk.VerifyOtpCodMutation;

import org.apache.commons.lang3.StringUtils;
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
import kh.com.mysabay.sdk.pojo.googleVerify.DataBody;
import kh.com.mysabay.sdk.pojo.googleVerify.GoogleVerifyBody;
import kh.com.mysabay.sdk.pojo.googleVerify.ReceiptBody;
import kh.com.mysabay.sdk.pojo.mysabay.MySabayItemResponse;
import kh.com.mysabay.sdk.pojo.mysabay.ProviderResponse;
import kh.com.mysabay.sdk.pojo.shop.ShopItem;
import kh.com.mysabay.sdk.pojo.thirdParty.payment.Data;
import kh.com.mysabay.sdk.utils.FontUtils;
import kh.com.mysabay.sdk.utils.LogUtil;
import kh.com.mysabay.sdk.utils.MessageUtil;
import static com.android.billingclient.api.BillingClient.SkuType.INAPP;

public class FunctionCallActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private ActivityFunctionCallBinding mViewBinding;
    private MaterialDialog dialogBank;
    private MaterialDialog dialogShop;
    private GridLayoutManager mLayoutManager;

    List<ShopItem> shopItems;
    List<MySabayItemResponse> mySabayItemResponses;
    private ShopItem shopItem;
    private String mysabayUsername;
    private BillingClient billingClient;
    private static String PURCHASE_ID = "";
    GoogleVerifyBody googleVerifyBody = new GoogleVerifyBody();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_function_call);
        onBillingSetupFinished();
        mViewBinding.viewPb.setVisibility(View.GONE);

        mViewBinding.btnLoginGuest.setOnClickListener(v -> {
            MySabaySDK.getInstance().loginGuest(new DataCallback<LoginGuestMutation.Sso_loginGuest>() {
                @Override
                public void onSuccess(LoginGuestMutation.Sso_loginGuest response) {
                    AppItem appItem = new AppItem(response.accessToken(), response.refreshToken(), response.expire());
                    String encrypted = new Gson().toJson(appItem);
                    MySabaySDK.getInstance().saveAppItem(encrypted);
                    MessageUtil.displayDialog(v.getContext(), new Gson().toJson(response.toString()));
                }

                @Override
                public void onFailed(Object error) {
                    LogUtil.info("Error", error.toString());
                }
            });
        });

        mViewBinding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySabaySDK.getInstance().logout();
            }
        });

        mViewBinding.loginView.setOnClickListener(v -> {
            if (mViewBinding.loginGroup.getVisibility() == View.VISIBLE) {
                mViewBinding.loginGroup.setVisibility(View.GONE);
            } else {
                mViewBinding.loginGroup.setVisibility(View.VISIBLE);
            }
        });

        mViewBinding.verifyView.setOnClickListener(v -> {
            if (mViewBinding.verifyGroup.getVisibility() == View.VISIBLE) {
                mViewBinding.verifyGroup.setVisibility(View.GONE);
            } else {
                mViewBinding.verifyGroup.setVisibility(View.VISIBLE);
            }
        });

        mViewBinding.loginMysabayView.setOnClickListener(v -> {
            if (mViewBinding.loginMysabayGroup.getVisibility() == View.VISIBLE) {
                mViewBinding.loginMysabayGroup.setVisibility(View.GONE);
            } else {
                mViewBinding.loginMysabayGroup.setVisibility(View.VISIBLE);
            }
        });

        mViewBinding.createMysabayView.setOnClickListener(v -> {
            if (mViewBinding.createMysabayGroup.getVisibility() == View.VISIBLE) {
                mViewBinding.createMysabayGroup.setVisibility(View.GONE);
            } else {
                mViewBinding.createMysabayGroup.setVisibility(View.VISIBLE);
            }
        });

        mViewBinding.confirmMysabayView.setOnClickListener(v -> {
            mViewBinding.edtConfirmUsername.setText(mysabayUsername);
            if (mViewBinding.confrimMysabayGroup.getVisibility() == View.VISIBLE) {
                mViewBinding.confrimMysabayGroup.setVisibility(View.GONE);
            } else {
                mViewBinding.confrimMysabayGroup.setVisibility(View.VISIBLE);
            }
        });

        mViewBinding.btnRequestNewMysabayView.setOnClickListener(v -> {
            if (mViewBinding.requestNewMysabayGroup.getVisibility() == View.VISIBLE) {
                mViewBinding.requestNewMysabayGroup.setVisibility(View.GONE);
            } else {
                mViewBinding.requestNewMysabayGroup.setVisibility(View.VISIBLE);
            }
        });

        mViewBinding.edtMysabayUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    MySabaySDK.getInstance().checkExistingMySabayUsername(mViewBinding.edtMysabayUsername.getText().toString(), new DataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            if (response) {
                                MessageUtil.displayDialog(v.getContext(), "Username is already exist");
                            }
                        }

                        @Override
                        public void onFailed(Object error) {
                            MessageUtil.displayDialog(v.getContext(), error.toString());
                        }
                    });
                }
            }
        });

        mViewBinding.btnLoginFb.setOnClickListener(v -> {
            MySabaySDK.getInstance().loginWithFacebook("", new DataCallback<LoginWithFacebookMutation.Sso_loginFacebook>() {
                @Override
                public void onSuccess(LoginWithFacebookMutation.Sso_loginFacebook response) {

                }

                @Override
                public void onFailed(Object error) {

                }
            });
        });

        mViewBinding.btnLogin.setOnClickListener(v -> {
            String phoneNumber = mViewBinding.edtPhone.getText().toString();
            if (!phoneNumber.isEmpty()) {
                mViewBinding.viewPb.setVisibility(View.VISIBLE);
                MySabaySDK.getInstance().loginWithPhone(phoneNumber, new DataCallback<LoginWithPhoneMutation.Sso_loginPhone>() {
                    @Override
                    public void onSuccess(LoginWithPhoneMutation.Sso_loginPhone response) {
                        mViewBinding.viewPb.setVisibility(View.GONE);
                        if (response.verifyMySabay()) {
                            mysabayUsername = response.mySabayUsername();
                            MessageUtil.displayDialog(v.getContext(), "You need to confirm mysabay account");
                        } else {
                            MessageUtil.displayDialog(v.getContext(), "You need to verify otp code");
                        }
                    }

                    @Override
                    public void onFailed(Object error) {
                        mViewBinding.viewPb.setVisibility(View.GONE);
                        MessageUtil.displayDialog(v.getContext(), error.toString());
                    }
                });
            } else {
                MessageUtil.displayDialog(v.getContext(), "Please input phone number");
            }
        });

        mViewBinding.btnVerify.setOnClickListener(v -> {
            String phoneNumber = mViewBinding.edtPhone.getText().toString();
            String otpCode = mViewBinding.edtVerifyCode.getText().toString();
            if (!otpCode.isEmpty()) {
                mViewBinding.viewPb.setVisibility(View.VISIBLE);
                MySabaySDK.getInstance().verifyOtp(phoneNumber, otpCode, new DataCallback<VerifyOtpCodMutation.Sso_verifyOTP>() {
                    @Override
                    public void onSuccess(VerifyOtpCodMutation.Sso_verifyOTP response) {
                        mViewBinding.viewPb.setVisibility(View.GONE);
                        AppItem appItem = new AppItem(response.accessToken(), response.refreshToken(), response.expire());
                        String encrypted = new Gson().toJson(appItem);
                        MySabaySDK.getInstance().saveAppItem(encrypted);
                        MessageUtil.displayDialog(v.getContext(), "Login success");
                    }

                    @Override
                    public void onFailed(Object error) {
                        mViewBinding.viewPb.setVisibility(View.GONE);
                        LogUtil.info("Otp verification failed", error.toString());
                    }
                });
            } else {
                MessageUtil.displayDialog(v.getContext(), "Please input opt code");
            }
        });

        mViewBinding.btnConfirmMysabay.setOnClickListener(v -> {
            String password = mViewBinding.edtConfirmPassword.getText().toString();
            if (!password.isEmpty()) {
                mViewBinding.viewPb.setVisibility(View.VISIBLE);
                MySabaySDK.getInstance().verifyMySabay(mysabayUsername, password, new DataCallback<VerifyMySabayMutation.Sso_verifyMySabay>() {
                    @Override
                    public void onSuccess(VerifyMySabayMutation.Sso_verifyMySabay response) {
                        mViewBinding.viewPb.setVisibility(View.GONE);
                        AppItem appItem = new AppItem(response.accessToken(), response.refreshToken(), response.expire());
                        String encrypted = new Gson().toJson(appItem);
                        MySabaySDK.getInstance().saveAppItem(encrypted);
                        MessageUtil.displayDialog(v.getContext(), "Confirm MySabay success");
                    }

                    @Override
                    public void onFailed(Object error) {
                        mViewBinding.viewPb.setVisibility(View.GONE);
                        MessageUtil.displayDialog(v.getContext(), error.toString());
                    }
                });
            } else {
                MessageUtil.displayDialog(v.getContext(), "Please input data");
            }
        });

        mViewBinding.btnRequestMysabayWithPhone.setOnClickListener(v -> {
            String phoneNumber = mViewBinding.edtPhone.getText().toString();
            String username = mViewBinding.edtRequestNewUsername.getText().toString();
            String password = mViewBinding.edtRequestNewPassword.getText().toString();
            String confirmPassword = mViewBinding.edtConfirmRequestNewPassword.getText().toString();

            if (!username.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
                if (StringUtils.equals(password, confirmPassword)) {
                    mViewBinding.viewPb.setVisibility(View.VISIBLE);
                    MySabaySDK.getInstance().requestCreatingMySabayWithPhone(phoneNumber, new DataCallback<SendCreateMySabayWithPhoneOTPMutation.Sso_sendCreateMySabayWithPhoneOTP>() {
                        @Override
                        public void onSuccess(SendCreateMySabayWithPhoneOTPMutation.Sso_sendCreateMySabayWithPhoneOTP response) {
                            mViewBinding.viewPb.setVisibility(View.GONE);
                            mViewBinding.createMysabayWithPhoneVerifyGroup.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFailed(Object error) {
                            mViewBinding.viewPb.setVisibility(View.GONE);
                            MessageUtil.displayDialog(v.getContext(), error.toString());
                        }
                    });
                } else {
                    MessageUtil.displayDialog(v.getContext(), "Confirm Password don't match");
                }
            } else {
                MessageUtil.displayDialog(v.getContext(), "Please input data");
            }
        });

        mViewBinding.btnCreateMysabayWithPhone.setOnClickListener(v -> {
            String username = mViewBinding.edtRequestNewUsername.getText().toString();
            String password = mViewBinding.edtRequestNewPassword.getText().toString();
            String phoneNumber = mViewBinding.edtPhone.getText().toString();
            String otpCode = mViewBinding.edtMysabayWithPhoneVerifyCode.getText().toString();
            if(!otpCode.isEmpty()) {
                mViewBinding.viewPb.setVisibility(View.VISIBLE);
                LogUtil.info("Info", username + " " + password + " " + phoneNumber + " " + otpCode);
                MySabaySDK.getInstance().createMySabayWithPhone(username, password, phoneNumber, otpCode, new DataCallback<CreateMySabayLoginWithPhoneMutation.Sso_createMySabayLoginWithPhone>() {
                    @Override
                    public void onSuccess(CreateMySabayLoginWithPhoneMutation.Sso_createMySabayLoginWithPhone response) {
                        mViewBinding.viewPb.setVisibility(View.GONE);
                        AppItem appItem = new AppItem(response.accessToken(), response.refreshToken(), response.expire());
                        String encrypted = new Gson().toJson(appItem);
                        MySabaySDK.getInstance().saveAppItem(encrypted);
                        MessageUtil.displayDialog(v.getContext(), "Create new MySabay success");
                    }

                    @Override
                    public void onFailed(Object error) {
                        mViewBinding.viewPb.setVisibility(View.GONE);
                        MessageUtil.displayDialog(v.getContext(), error.toString());
                    }
                });
            } else {
                MessageUtil.displayDialog(v.getContext(), "Please input data");
            }
        });

        mViewBinding.btnLoginMysabay.setOnClickListener(v-> {
            String username = mViewBinding.edtUsername.getText().toString();
            String password = mViewBinding.edtPassword.getText().toString();
            if (!username.isEmpty() && !password.isEmpty()) {
                    mViewBinding.viewPb.setVisibility(View.VISIBLE);
                    MySabaySDK.getInstance().loginWithMySabay(username, password, new DataCallback<LoginWithMySabayMutation.Sso_loginMySabay>() {
                        @Override
                        public void onSuccess(LoginWithMySabayMutation.Sso_loginMySabay response) {
                            mViewBinding.viewPb.setVisibility(View.GONE);
                            AppItem appItem = new AppItem(response.accessToken(), response.refreshToken(), response.expire());
                            String encrypted = new Gson().toJson(appItem);
                            MySabaySDK.getInstance().saveAppItem(encrypted);
                            MessageUtil.displayDialog(v.getContext(), "Login with mysabay success");
                        }

                        @Override
                        public void onFailed(Object error) {
                            mViewBinding.viewPb.setVisibility(View.GONE);
                            MessageUtil.displayDialog(v.getContext(), "Login with mysbay failed");
                        }
                    });
            } else {
                MessageUtil.displayDialog(v.getContext(), "Please input data");
            }
        });

        mViewBinding.btnCreateMysabay.setOnClickListener(v -> {
            String username = mViewBinding.edtMysabayUsername.getText().toString();
            String password = mViewBinding.edtMysabayPassword.getText().toString();
            String confirmPassword = mViewBinding.edtMysabayConfirmPassword.getText().toString();
            if (!username.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
                if (StringUtils.equals(password, confirmPassword)) {
                    mViewBinding.viewPb.setVisibility(View.VISIBLE);
                    MySabaySDK.getInstance().registerMySabayAccount(username, password, new DataCallback<CreateMySabayLoginMutation.Sso_createMySabayLogin>() {
                        @Override
                        public void onSuccess(CreateMySabayLoginMutation.Sso_createMySabayLogin response) {
                            mViewBinding.viewPb.setVisibility(View.GONE);
                            AppItem appItem = new AppItem(response.accessToken(), response.refreshToken(), response.expire());
                            String encrypted = new Gson().toJson(appItem);
                            MySabaySDK.getInstance().saveAppItem(encrypted);
                            MessageUtil.displayDialog(v.getContext(), "Create mysabay success");
                        }

                        @Override
                        public void onFailed(Object error) {
                            mViewBinding.viewPb.setVisibility(View.GONE);
                            MessageUtil.displayDialog(v.getContext(), error.toString());
                        }
                    });
                } else {
                    MessageUtil.displayDialog(v.getContext(), "Confirm Password don't match");
                }
            } else {
                MessageUtil.displayDialog(v.getContext(), "Please input data");
            }
        });

        mViewBinding.btnUserProfile.setOnClickListener(v -> {
            if (MySabaySDK.getInstance().isLogIn()) {
                MySabaySDK.getInstance().getUserInfo(new DataCallback<UserProfileQuery.Sso_userProfile>() {
                    @Override
                    public void onSuccess(UserProfileQuery.Sso_userProfile response) {
                        MessageUtil.displayDialog(v.getContext(), response.toString());
                    }

                    @Override
                    public void onFailed(Object error) {
                        LogUtil.info("Error", error.toString());
                    }
                });
            } else {
                MessageUtil.displayDialog(v.getContext(), "You need to login");
            }
        });

        mViewBinding.btnStore.setOnClickListener(v -> {
            MySabaySDK.getInstance().getStoreProducts(new DataCallback<GetProductsByServiceCodeQuery.Store_listProduct>() {
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
            MySabaySDK.getInstance().getPaymentServiceProvidersByProduct(shopItem.id, new DataCallback<Checkout_getPaymentServiceProviderForProductQuery.Checkout_getPaymentServiceProviderForProduct>() {
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
                        ProviderResponse psp = MySabaySDK.getInstance().getInAppPurchaseProvider("play_store");
                        LogUtil.info("Bonus", psp.label);
                        mViewBinding.btnInAppPurchase.setVisibility(View.VISIBLE);
                        mViewBinding.lblInAppPurchase.setText(psp.label);
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
            mViewBinding.btnMysabay.setBackgroundResource(kh.com.mysabay.sdk.R.color.colorWhite);
            mViewBinding.btnThirdBankProvider.setBackgroundResource(kh.com.mysabay.sdk.R.color.colorYellow);
            mViewBinding.btnInAppPurchase.setBackgroundResource(kh.com.mysabay.sdk.R.color.colorWhite);
           showBankProviders(v.getContext(), get3PartyCheckout(mySabayItemResponses));
        });

        mViewBinding.btnMysabay.setOnClickListener(v -> {
            mViewBinding.btnMysabay.setBackgroundResource(kh.com.mysabay.sdk.R.color.colorYellow);
            mViewBinding.btnThirdBankProvider.setBackgroundResource(kh.com.mysabay.sdk.R.color.colorWhite);
            mViewBinding.btnInAppPurchase.setBackgroundResource(kh.com.mysabay.sdk.R.color.colorWhite);
            mViewBinding.sabayProvider.setVisibility(View.VISIBLE);
            for (ProviderResponse provider: MySabaySDK.getInstance().getMySabayProviders()) {
               if (StringUtils.equals(provider.code, "sabay_coin")) {
                   mViewBinding.btnSabayCoin.setVisibility(View.VISIBLE);
                   mViewBinding.btnSabayCoin.setText("SC");
                   mViewBinding.scBonus.setText(provider.label);
               }
                if (StringUtils.equals(provider.code, "sabay_gold")) {
                    mViewBinding.btnSabayGold.setVisibility(View.VISIBLE);
                    mViewBinding.btnSabayGold.setText("SG");
                    mViewBinding.sgBonus.setText(provider.label);
                }
            }

        });

        mViewBinding.btnSabayCoin.setOnClickListener(v -> {
            checkedId[0] = v.getId();
            mViewBinding.btnSabayCoin.setBackgroundResource(kh.com.mysabay.sdk.R.color.colorYellow);
            mViewBinding.btnSabayGold.setBackgroundResource(kh.com.mysabay.sdk.R.color.colorWhite);
        });

        mViewBinding.btnSabayGold.setOnClickListener(v -> {
            checkedId[0] = v.getId();
            mViewBinding.btnSabayCoin.setBackgroundResource(kh.com.mysabay.sdk.R.color.colorWhite);
            mViewBinding.btnSabayGold.setBackgroundResource(kh.com.mysabay.sdk.R.color.colorYellow);
        });

        mViewBinding.btnInAppPurchase.setOnClickListener(v -> {
            checkedId[0] = v.getId();
            mViewBinding.btnMysabay.setBackgroundResource(kh.com.mysabay.sdk.R.color.colorWhite);
            mViewBinding.btnThirdBankProvider.setBackgroundResource(kh.com.mysabay.sdk.R.color.colorWhite);
            mViewBinding.btnInAppPurchase.setBackgroundResource(kh.com.mysabay.sdk.R.color.colorYellow);
        });

        mViewBinding.btnPay.setOnClickListener(v -> {
            if (checkedId[0] == R.id.btn_sabay_coin) {
                mViewBinding.viewPb.setVisibility(View.VISIBLE);
                ProviderResponse provider = MySabaySDK.getInstance().getMySabayProvider("sabay_coin");
                payment(v.getContext(), shopItem, provider, "pre-auth");
            }
            if ((checkedId[0] == R.id.btn_sabay_gold)) {
                mViewBinding.viewPb.setVisibility(View.VISIBLE);
                ProviderResponse provider = MySabaySDK.getInstance().getMySabayProvider("sabay_gold");
                payment(v.getContext(), shopItem, provider, "pre-auth");
            }

            if ((checkedId[0] == R.id.btn_in_app_purchase)) {
                ProviderResponse provider = MySabaySDK.getInstance().getInAppPurchaseProvider("play_store");
                PURCHASE_ID = "kh.com.sabay.aog.iap.2_usd";
                purchase(v.getContext(), PURCHASE_ID);
            }

        });
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
                payment(context, shopItem, provider, "onetime");
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


    public void payment(Context context, ShopItem shopItem, ProviderResponse provider, String type) {
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
        paymentProcess(context, items, provider.id, shopItem.salePrice, provider.issueCurrencies.get(0), type);

    }

    public void paymentProcess(Context context, List<Object> items, String pspId, double amount, String currency, String type) {
        MySabaySDK.getInstance().createPaymentDetail(pspId, items, amount, currency, new DataCallback<Object>() {
            @Override
            public void onSuccess(Object response) {
                LogUtil.info("Response-Purchase", new Gson().toJson(response));
                Data data = new Gson().fromJson(new Gson().toJson(response), Data.class);
                if (type == "pre-auth") {
                    MySabaySDK.getInstance().postToChargePreAuth(data, new DataCallback<Object>() {
                        @Override
                        public void onSuccess(Object response) {
                            mViewBinding.viewPb.setVisibility(View.GONE);
                            LogUtil.info("PreAuth-Purchase", "Success");
                            MySabaySDK.getInstance().checkPaymentStatus(data.invoiceId, new DataCallback<GetInvoiceByIdQuery.Invoice_getInvoiceById>() {
                                @Override
                                public void onSuccess(GetInvoiceByIdQuery.Invoice_getInvoiceById response) {
                                    LogUtil.info("response", response.toString());
                                    if (!StringUtils.isEmpty(response.ssnTxHash())) {
                                        MessageUtil.displayDialog(context, "Payment with pre-auth is success");
                                    } else {
                                        MessageUtil.displayDialog(context, "Payment with pre-auth failed" + " ssnTxHash is Empty");
                                    }
                                }

                                @Override
                                public void onFailed(Object error) {
                                    MessageUtil.displayDialog(context, error.toString());
                                }
                            });
                        }

                        @Override
                        public void onFailed(Object error) {
                            mViewBinding.viewPb.setVisibility(View.GONE);
                            MessageUtil.displayDialog(context, error.toString());
                        }
                    });
                } else if (type == "iap"){
                    MySabaySDK.getInstance().verifyInAppPurcahse(data, googleVerifyBody, new DataCallback<Object>() {
                        @Override
                        public void onSuccess(Object response) {
                            mViewBinding.viewPb.setVisibility(View.GONE);
                            MySabaySDK.getInstance().checkPaymentStatus(data.invoiceId, new DataCallback<GetInvoiceByIdQuery.Invoice_getInvoiceById>() {
                                @Override
                                public void onSuccess(GetInvoiceByIdQuery.Invoice_getInvoiceById response) {
                                    if (!StringUtils.isEmpty(response.ssnTxHash())) {
                                        MessageUtil.displayDialog(context, "Payment with iap is success");
                                    } else {
                                        MessageUtil.displayDialog(context, "Payment with iap failed" + " ssnTxHash is Empty");
                                    }
                                }

                                @Override
                                public void onFailed(Object error) {
                                    MessageUtil.displayDialog(context, error.toString());
                                }
                            });
                        }

                        @Override
                        public void onFailed(Object error) {
                            mViewBinding.viewPb.setVisibility(View.GONE);
                            MessageUtil.displayDialog(context, error.toString());
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

    public List<ProviderResponse> get3PartyCheckout(List<MySabayItemResponse> mySabayItemResponses) {
        List<MySabayItemResponse> mySabayItem = mySabayItemResponses;
        List<ProviderResponse> result = new ArrayList<>();

        for (MySabayItemResponse item : mySabayItem) {
            if (item.type.equals(Globals.ONE_TIME_PROVIDER)) {
                for (ProviderResponse providerResponse: item.providers) {
                    result.add(providerResponse);
                }
            }
        }
        return result;
    }

    public void purchase(Context context, String productId) {
        //check if service is already connected
        if (billingClient.isReady()) {
            initiatePurchase(context, productId);
        }
        //else reconnect service
        else{
            billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase(context, productId);
                    } else {
                        MessageUtil.displayToast(context,"Error "+billingResult.getDebugMessage());
                    }
                }
                @Override
                public void onBillingServiceDisconnected() {
                }
            });
        }
    }

    public void onBillingSetupFinished() {
        billingClient = BillingClient.newBuilder(getApplicationContext()).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if(billingResult.getResponseCode()== BillingClient.BillingResponseCode.OK){
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(INAPP);
                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();
                    if(queryPurchases!=null && queryPurchases.size()>0){
                        handlePurchases(queryPurchases);
                    }
                    else{
                        LogUtil.info("Billing cilent", "");
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                MessageUtil.displayDialog(getApplicationContext(), "Service Disconnect");
            }
        });
    }

    private void initiatePurchase(Context context, String productId) {
        List<String> skuList = new ArrayList<>();
        skuList.add(productId);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<com.android.billingclient.api.SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (skuDetailsList != null && skuDetailsList.size() > 0) {
                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetailsList.get(0))
                                        .build();
                                billingClient.launchBillingFlow(FunctionCallActivity.this, flowParams);
                            }
                            else{
                                MessageUtil.displayToast(context,"Purchase Item not Found");
                            }
                        } else {
                            MessageUtil.displayToast(context, billingResult.getDebugMessage());
                        }
                    }

                });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        //if item newly purchased
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases);
        }
        //if item already purchased then check and reflect changes
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(INAPP);
            List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if(alreadyPurchases!=null){
                handlePurchases(alreadyPurchases);
            }
        }
        //if purchase cancelled
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            MessageUtil.displayToast(getApplicationContext(),"Purchase Canceled");
        }
        // Handle any other error msgs
        else {
            MessageUtil.displayToast(getApplicationContext(),"Error "+billingResult.getDebugMessage());
        }
    }

    void handlePurchases(List<Purchase>  purchases) {
        for(Purchase purchase:purchases) {
            if (!MySabaySDK.getInstance().verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                MessageUtil.displayDialog(getApplicationContext(),"Purchase is invalid");
                return;
            }

            if (!purchase.isAcknowledged()) {
                handlePurchase(purchase);
            }

            if (purchase != null && StringUtils.equals(purchase.getSku(), PURCHASE_ID)) {
                try {
                    ReceiptBody receiptBody = new ReceiptBody();
                    receiptBody.withSignature(purchase.getSignature());
                    DataBody dataBody = new DataBody(purchase.getOrderId(), purchase.getPackageName(), purchase.getSku(),
                            purchase.getPurchaseTime(), purchase.getPurchaseState(), purchase.getPurchaseToken());
                    receiptBody.withData(dataBody);
                    googleVerifyBody.withReceipt(receiptBody);
                    ProviderResponse provider = MySabaySDK.getInstance().getInAppPurchaseProvider("play_store");

                    mViewBinding.viewPb.setVisibility(View.VISIBLE);
                    payment(FunctionCallActivity.this, shopItem, provider, "iap");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    void handlePurchase(Purchase purchase) {
        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    LogUtil.info("Consume Purchase", "consume");
                }
            }
        };

        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
        billingClient.consumeAsync(consumeParams, listener);
    }

}
