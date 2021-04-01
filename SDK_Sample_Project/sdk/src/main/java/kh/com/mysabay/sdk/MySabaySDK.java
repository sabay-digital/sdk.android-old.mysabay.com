package kh.com.mysabay.sdk;

import android.app.Application;;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import kh.com.mysabay.sdk.callback.LoginListener;
import kh.com.mysabay.sdk.screen.LoginActivity;

public class MySabaySDK {

    public Application mAppContext;
    private static MySabaySDK mySabaySDK;
    private SdkConfiguration mSdkConfiguration;

    public MySabaySDK(Application application, SdkConfiguration configuration) {
        Log.i("Init SDK", "Completed");
        mySabaySDK = this;
        this.mAppContext = application;
        mSdkConfiguration = configuration;
    }

    public static class Impl {
        public static synchronized void setDefaultInstanceConfiguration(Application application, SdkConfiguration configuration) {
            new MySabaySDK(application, configuration);
        }
    }

    public static MySabaySDK getInstance() {
        if (mySabaySDK == null)
            throw new NullPointerException("initialize mysabaySdk in application");
        if (mySabaySDK.mAppContext == null)
            throw new NullPointerException("Please provide application context");
        if (mySabaySDK.mSdkConfiguration == null)
            throw new RuntimeException("This sdk is need SdkConfiguration");
        return mySabaySDK;
    }

    public void showLoginView() {
        mAppContext.startActivity(getOpenFacebookIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public Intent getOpenFacebookIntent() {
        try {
            mAppContext.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/426253597411506"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/appetizerandroid"));
        }
    }

}
