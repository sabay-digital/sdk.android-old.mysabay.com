package kh.com.mysabay.sample;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;

import kh.com.mysabay.sample.databinding.ActivityMainBinding;
import kh.com.mysabay.sdk.Globals;
import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.callback.LoginListener;
import kh.com.mysabay.sdk.callback.PaymentListener;
import kh.com.mysabay.sdk.callback.RefreshTokenListener;
import kh.com.mysabay.sdk.pojo.googleVerify.GoogleVerifyBody;
import kh.com.mysabay.sdk.pojo.onetime.Data;
import kh.com.mysabay.sdk.pojo.payment.PaymentResponseItem;
import kh.com.mysabay.sdk.pojo.payment.SubscribePayment;
import kh.com.mysabay.sdk.pojo.profile.UserProfileItem;
import kh.com.mysabay.sdk.utils.MessageUtil;
import kh.com.mysabay.sdk.utils.LogUtil;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MySabaySDK.getInstance().trackPageView(this, "/home-screen", "/home-screen");
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewBinding.viewPb.setVisibility(View.GONE);
        findViewById(R.id.show_login_screen).setOnClickListener(v -> {
            mViewBinding.viewPb.setVisibility(View.VISIBLE);
                MySabaySDK.getInstance().showLoginView(new LoginListener() {
                    @Override
                    public void loginSuccess(String accessToken) {
                        MessageUtil.displayToast(v.getContext(), "accessToken = " + accessToken);
                        mViewBinding.viewPb.setVisibility(View.GONE);
                    }

                    @Override
                    public void loginFailed(Object error) {
                        MessageUtil.displayToast(v.getContext(), "error = " + error);
                    }
                });
        });

        mViewBinding.showPaymentPreAuth.setOnClickListener(v-> {
                MySabaySDK.getInstance().showStoreView(new PaymentListener() {
                    @Override
                    public void purchaseSuccess(SubscribePayment data) {
                        if(data.getType().equals(Globals.APP_IN_PURCHASE)) {
                            GoogleVerifyBody receipt = (GoogleVerifyBody) data.data;
                            LogUtil.info("Data from", new Gson().toJson(data.data));
                            LogUtil.info("data", receipt.receipt.data.toString());
                            LogUtil.info("signature", receipt.receipt.signature.toString());
                            LogUtil.info("Profile balance gold", new Gson().toJson(receipt));
                            MessageUtil.displayDialog(v.getContext(), new Gson().toJson(data.data));
                        } else if (data.getType().equals(Globals.MY_SABAY)) {
                            if (data.data != null) {
                                PaymentResponseItem dataPayment = (PaymentResponseItem) data.data;
                                LogUtil.info("data", new Gson().toJson(data.data));
                                MessageUtil.displayDialog(v.getContext(), new Gson().toJson(data.data));
                            } else {
                                LogUtil.info("Error", data.error.toString());
                            }
                        } else {
                            Data dataPayment = (Data) data.data;
                            LogUtil.info(data.getType(), new Gson().toJson(data.data));
                            LogUtil.info("hash",  dataPayment.hash);
                            LogUtil.info("amount",  dataPayment.amount);
                            LogUtil.info("packageId",  dataPayment.packageId);
                            LogUtil.info("assetCode", dataPayment.assetCode);
                            MessageUtil.displayDialog(v.getContext(), new Gson().toJson(data.data));
                        }
                    }

                    @Override
                    public void purchaseFailed(Object dataError) {
                        MessageUtil.displayToast(v.getContext(), dataError + "");
                    }
                });
        });

        mViewBinding.btnGetToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MySabaySDK.getInstance().isLogIn())
                    MessageUtil.displayToast(v.getContext(), "current token =" + MySabaySDK.getInstance().currentToken());
                else
                    MessageUtil.displayToast(v.getContext(), "Need user login");
            }
        });

        mViewBinding.btnRefreshToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MySabaySDK.getInstance().isLogIn()) {
                    MySabaySDK.getInstance().refreshToken(new RefreshTokenListener() {
                        @Override
                        public void refreshSuccess(String token) {
                            LogUtil.info("token", token);
                            MessageUtil.displayToast(v.getContext(), "refresh token = " + token);
                        }

                        @Override
                        public void refreshFailed(Throwable error) {
                            MessageUtil.displayToast(v.getContext(), "error in refresh token " + error.getLocalizedMessage());
                        }
                    });
                } else
                    MessageUtil.displayToast(v.getContext(), "Need user login");
            }
        });

        mViewBinding.btnValidateTokenExpired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MySabaySDK.getInstance().isLogIn())
                    MessageUtil.displayToast(v.getContext(), "Token is valid =" + MySabaySDK.getInstance().isTokenValid());
                else
                    MessageUtil.displayToast(v.getContext(), "Need user login");
            }
        });

        mViewBinding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySabaySDK.getInstance().logout();
            }
        });

        mViewBinding.btnUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MySabaySDK.getInstance().isLogIn()) {
                    MySabaySDK.getInstance().getUserProfile(info -> {
                        if (info != null) {
                            UserProfileItem userProfile = new Gson().fromJson(info, UserProfileItem.class);
                            LogUtil.info("Profile userId", userProfile.userID.toString());
                            LogUtil.info("Profile name", userProfile.givenName);
                            LogUtil.info("Profile localPayEnabled", userProfile.localPayEnabled.toString());
                            LogUtil.info("Profile persona", userProfile.persona.toString());
                            MessageUtil.displayDialog(v.getContext(), info);
                        } else {
                            MessageUtil.displayDialog(v.getContext(), getString(R.string.msg_can_not_connect_server));
                        }
                    });
                } else {
                    MessageUtil.displayToast(v.getContext(), "Need user login");
                }
            }
        });

        mViewBinding.btnCallActivity.setOnClickListener(v-> {
            Intent intent = new Intent(this, FunctionCallActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStop() {
        mViewBinding.viewPb.setVisibility(View.GONE);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MySabaySDK.getInstance().destroy();
    }

    @Override
    public void onBackPressed() {
        mViewBinding.viewPb.setVisibility(View.GONE);
        super.onBackPressed();
    }
}
