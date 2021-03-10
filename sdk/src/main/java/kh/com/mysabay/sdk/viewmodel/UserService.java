package kh.com.mysabay.sdk.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import android.os.Looper;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.request.RequestHeaders;
import com.mysabay.sdk.CheckExistingLoginQuery;
import com.mysabay.sdk.CreateMySabayLoginMutation;
import com.mysabay.sdk.CreateMySabayLoginWithPhoneMutation;
import com.mysabay.sdk.GetMatomoTrackingIdQuery;
import com.mysabay.sdk.LoginWithFacebookMutation;
import com.mysabay.sdk.LoginWithMySabayMutation;
import com.mysabay.sdk.LoginWithPhoneMutation;
import com.mysabay.sdk.SendCreateMySabayWithPhoneOTPMutation;
import com.mysabay.sdk.UserProfileQuery;
import com.mysabay.sdk.VerifyMySabayMutation;
import com.mysabay.sdk.VerifyOtpCodMutation;
import com.mysabay.sdk.type.Sso_LoginProviders;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import kh.com.mysabay.sdk.callback.DataCallback;
import kh.com.mysabay.sdk.utils.LogUtil;
import kh.com.mysabay.sdk.utils.RSA;

public class UserService extends ViewModel {

    private static final String TAG = UserApiVM.class.getSimpleName();
    private ApolloClient apolloClient;

    @Inject
    public UserService(ApolloClient apolloClient) {
        this.apolloClient = apolloClient;
    }

    public void loginWithPhoneNumber(String phoneNumber, DataCallback<LoginWithPhoneMutation.Sso_loginPhone> dataCallback) {
        LoginWithPhoneMutation loginWithPhoneMutation = new LoginWithPhoneMutation(phoneNumber);
        apolloClient.mutate(loginWithPhoneMutation).enqueue(new ApolloCall.Callback<LoginWithPhoneMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<LoginWithPhoneMutation.Data> response) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.getData() != null) {
                            dataCallback.onSuccess(response.getData().sso_loginPhone());
                        } else {
                            dataCallback.onFailed("Login Failed");
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                dataCallback.onFailed(e);
            }
        });
    }

    public void verifyOTPCode(String phoneNumber, String code, DataCallback<VerifyOtpCodMutation.Sso_verifyOTP> dataCallback) {
        LogUtil.info("Phone number", phoneNumber + " Otpcode " + code);
        apolloClient.mutate(new VerifyOtpCodMutation(phoneNumber, code)).enqueue(new ApolloCall.Callback<VerifyOtpCodMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<VerifyOtpCodMutation.Data> response) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.getErrors() != null) {
                            dataCallback.onFailed(response.getErrors().get(0).getMessage());
                        } else {
                            if (response.getData() != null) {
                                dataCallback.onSuccess(response.getData().sso_verifyOTP());
                            } else {
                                dataCallback.onFailed("Verify OTP Data response empty");
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                LogUtil.info("error", e.toString());
                dataCallback.onFailed(e);
            }

        });
    }

    public void loginWithFacebook(String token, DataCallback<LoginWithFacebookMutation.Sso_loginFacebook> dataCallback) {
        apolloClient.mutate(new LoginWithFacebookMutation(token)).enqueue(new ApolloCall.Callback<LoginWithFacebookMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<LoginWithFacebookMutation.Data> response) {
                if (response.getData() != null) {
                    dataCallback.onSuccess(response.getData().sso_loginFacebook());
                } else {
                    dataCallback.onFailed("Login with facebook failed");
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                dataCallback.onFailed(e);
            }
        });
    }

    public void loginWithMySabayAccount(String username, String password, DataCallback<LoginWithMySabayMutation.Sso_loginMySabay> dataCallback) {
        apolloClient.mutate(new LoginWithMySabayMutation(username, RSA.sha256String(password))).enqueue(new ApolloCall.Callback<LoginWithMySabayMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<LoginWithMySabayMutation.Data> response) {
                if (response.getData() != null) {
                    dataCallback.onSuccess(response.getData().sso_loginMySabay());
                } else {
                    dataCallback.onFailed("Login with MySabay account failed");
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                dataCallback.onFailed(e);
            }
        });
    }

    public void verifyMySabay(String username, String password, DataCallback<VerifyMySabayMutation.Sso_verifyMySabay> dataCallback) {
        apolloClient.mutate(new VerifyMySabayMutation(username, RSA.sha256String(password))).enqueue(new ApolloCall.Callback<VerifyMySabayMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<VerifyMySabayMutation.Data> response) {
                if (response.getData() != null) {
                    dataCallback.onSuccess(response.getData().sso_verifyMySabay());
                } else {
                    dataCallback.onFailed("Verify MySabay account failed");
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                dataCallback.onFailed(e);
            }
        });
    }

    public void resendOTP(String phoneNumber, DataCallback<LoginWithPhoneMutation.Sso_loginPhone> dataCallback) {
        apolloClient.mutate(new LoginWithPhoneMutation(phoneNumber)).enqueue(new ApolloCall.Callback<LoginWithPhoneMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<LoginWithPhoneMutation.Data> response) {
                if (response.getData() != null) {
                    dataCallback.onSuccess(response.getData().sso_loginPhone());
                } else {
                    dataCallback.onFailed("Resend OTP failed");
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                dataCallback.onFailed(e);
            }
        });
    }

    public void getUserProfile(String token, DataCallback<UserProfileQuery.Sso_userProfile> dataCallback) {
        apolloClient.query(new UserProfileQuery())
                .toBuilder()
                .requestHeaders(RequestHeaders.builder()
                        .addHeader("Authorization", "Bearer " + token).build())
                .build()
                .enqueue(new ApolloCall.Callback<UserProfileQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<UserProfileQuery.Data> response) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.getErrors() != null) {
                            dataCallback.onFailed(response.getErrors().get(0).getMessage());
                        } else {
                            if (response.getData() != null) {
                                dataCallback.onSuccess(response.getData().sso_userProfile());
                            } else {
                                dataCallback.onFailed("Get userProfile failed");
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                dataCallback.onFailed(e);
            }
        });
    }

    public void createMySabayAccount(String username, String password, DataCallback<CreateMySabayLoginMutation.Sso_createMySabayLogin> dataCallback) {
        apolloClient.mutate(new CreateMySabayLoginMutation(username, RSA.sha256String(password))).enqueue(new ApolloCall.Callback<CreateMySabayLoginMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<CreateMySabayLoginMutation.Data> response) {
                if (response.getData() != null) {
                    dataCallback.onSuccess(response.getData().sso_createMySabayLogin());
                } else {
                    dataCallback.onFailed("Create MySabay account failed");
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                dataCallback.onFailed(e);
            }
        });
    }

    public void createMySabayWithPhoneOTP(String phoneNumber, DataCallback<SendCreateMySabayWithPhoneOTPMutation.Sso_sendCreateMySabayWithPhoneOTP> dataCallback) {
        apolloClient.mutate(new SendCreateMySabayWithPhoneOTPMutation(phoneNumber)).enqueue(new ApolloCall.Callback<SendCreateMySabayWithPhoneOTPMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<SendCreateMySabayWithPhoneOTPMutation.Data> response) {
                if (response.getData() != null) {
                    dataCallback.onSuccess(response.getData().sso_sendCreateMySabayWithPhoneOTP());
                } else {
                    dataCallback.onFailed("Create MySabay account failed");
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                dataCallback.onFailed("Create MySabay account failed");
            }
        });
    }

    public void createMySabayLoginWithPhone(String username, String password, String phoneNumber, String otpCode, DataCallback<CreateMySabayLoginWithPhoneMutation.Sso_createMySabayLoginWithPhone> dataCallback) {
        apolloClient.mutate(new CreateMySabayLoginWithPhoneMutation(username, password, phoneNumber, otpCode)).enqueue(new ApolloCall.Callback<CreateMySabayLoginWithPhoneMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<CreateMySabayLoginWithPhoneMutation.Data> response) {
                if (response.getData() != null) {
                    dataCallback.onSuccess(response.getData().sso_createMySabayLoginWithPhone());
                } else {
                    dataCallback.onFailed("Create MySabay account failed");
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                dataCallback.onFailed("Create MySabay account failed");
            }
        });
    }

    public void checkExistingMySabayUsername(String username, DataCallback<CheckExistingLoginQuery.Data> dataCallback) {
        apolloClient.query(new CheckExistingLoginQuery(username, Sso_LoginProviders.SABAY)).enqueue(new ApolloCall.Callback<CheckExistingLoginQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<CheckExistingLoginQuery.Data> response) {
                if (response.getData() != null) {
                    dataCallback.onSuccess(response.getData());
                } else {
                    dataCallback.onFailed("check existing MySabay username failed");
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                dataCallback.onFailed(e);
            }
        });
    }

    public void getTrackingID(String serviceCode, DataCallback<GetMatomoTrackingIdQuery.Sso_service> dataCallback) {
        apolloClient.query(new GetMatomoTrackingIdQuery(serviceCode)).enqueue(new ApolloCall.Callback<GetMatomoTrackingIdQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetMatomoTrackingIdQuery.Data> response) {
                if(response.getErrors() == null) {
                    dataCallback.onSuccess(response.getData().sso_service().get(0));
                } else {
                    dataCallback.onFailed("Get Matomo Tracking Id failed");
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                dataCallback.onFailed(e);
            }
        });
    }
}
