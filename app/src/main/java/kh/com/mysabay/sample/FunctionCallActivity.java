package kh.com.mysabay.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mysabay.sdk.Checkout_getPaymentServiceProviderForProductQuery;
import com.mysabay.sdk.GetProductsByServiceCodeQuery;
import com.mysabay.sdk.LoginWithPhoneMutation;
import com.mysabay.sdk.UserProfileQuery;
import com.mysabay.sdk.VerifyOtpCodMutation;

import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.callback.DataCallback;
import kh.com.mysabay.sdk.utils.LogUtil;

public class FunctionCallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_call);

        MySabaySDK.getInstance().loginWithPhoneNumber("85589970429", new DataCallback<LoginWithPhoneMutation.Sso_loginPhone>() {
            @Override
            public void onSuccess(LoginWithPhoneMutation.Sso_loginPhone response) {
                LogUtil.info("Success", response.toString());
            }

            @Override
            public void onFailed(Object error) {
                LogUtil.info("Error", error.toString());
            }
        });

        MySabaySDK.getInstance().verifyOTPCode("85512808080", "111111", new DataCallback<VerifyOtpCodMutation.Sso_verifyOTP>() {
            @Override
            public void onSuccess(VerifyOtpCodMutation.Sso_verifyOTP response) {
                LogUtil.info("Success", response.toString());
            }

            @Override
            public void onFailed(Object error) {
                LogUtil.info("Error", error.toString());
            }
        });

        MySabaySDK.getInstance().getStoreFromServer("aog1", "", new DataCallback<GetProductsByServiceCodeQuery.Store_listProduct>() {
            @Override
            public void onSuccess(GetProductsByServiceCodeQuery.Store_listProduct response) {
                LogUtil.info("Success", response.toString());
            }

            @Override
            public void onFailed(Object error) {
                LogUtil.info("Error", error.toString());
            }
        });

        MySabaySDK.getInstance().getMySabayCheckout("", new DataCallback<Checkout_getPaymentServiceProviderForProductQuery.Checkout_getPaymentServiceProviderForProduct>() {
            @Override
            public void onSuccess(Checkout_getPaymentServiceProviderForProductQuery.Checkout_getPaymentServiceProviderForProduct response) {
                LogUtil.info("Success", response.toString());
            }

            @Override
            public void onFailed(Object error) {
                LogUtil.info("Error", error.toString());
            }
        });

        MySabaySDK.getInstance().getUserInfo("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiYzJjMjM2NDAtMDEzZS0xMWViLTg2ZDctODk4MDg5ZTkxNTY2IiwiZnJvbSI6bnVsbCwiYXVkIjoidXNlciIsInRva2VuX2lkIjoiMTRjNDJhZmUtNDkzNy00N2E0LWE1NjgtMTlkMDhmZGU0YjJkIiwibXlzYWJheV91c2VyX2lkIjozNzgzODAyMCwiaWF0IjoxNjExMjk4ODM3LCJleHAiOjE2MTEzNDIwMzd9.eSijNwUGjQQLolGBwd5WetHrCn-Bpspl3iW-jsu49VEuYjFvxDgAcNTrpaYcVNXHVLYsUPA6p975c4iAv7McYRP-R5FRtlhqm8XGhNj4kFDHiaGsw73e0z4q7vzsRSW1ioX0mfRnUWh8IJxMZfMDvfJOFiCtltugG7OkUgdFB_ZDDOS8Pmm8egDM1oU-HZO2Ecwgk-gwIqHuSN69ndizapiqSsnAZrnySQ93dpVQhPG2YTLa10djQs9WHRNtE15JtUQWObX8sRjsQGQlupKbE3kirIkZ97RxyWbUi5j7Xgk2fIQ1CLALnVh1JtUUj6UwoIV6EYJVB5ZdaW3YZ7Z_TTEURCoLxcONMGwCGqoUzkh13uRywF7LZYR8FrS59gr0LbeAAl94HV7j9KuNB7IcYB-mpF-OBdsv9F9Og_zKSXQAFrOhnkGRVeyX3bkCkS60OqY-q5Vle5zAjwRTfOuX2M3glMRsVx_scGp0VCy7SyQJ6d_qFRwJIIVuMlVvtUsT4YSTIFCrIuMLtTzpYeok-pL5vEHUXV4nRLtUH1iDImbLgB7b70piLB5liA3RMG8O-61bixR02QpExDu6g733QNe3ucfYtJLYak6oCUy_TDnhLaXtGurg7rThsK-zBj3QNrQjpBJjg1l6eyrRPbWakrbX8970h2LLFgspGwQ8sqQ",
                new DataCallback<UserProfileQuery.Sso_userProfile>() {
                    @Override
                    public void onSuccess(UserProfileQuery.Sso_userProfile response) {
                        LogUtil.info("Success", response.toString());
                    }

                    @Override
                    public void onFailed(Object error) {
                        LogUtil.info("Error", error.toString());
                    }
                });

    }
}
