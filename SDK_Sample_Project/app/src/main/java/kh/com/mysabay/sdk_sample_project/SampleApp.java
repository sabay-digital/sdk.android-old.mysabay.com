package kh.com.mysabay.sdk_sample_project;

import android.app.Application;
import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.SdkConfiguration;

public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        final SdkConfiguration configuration = new SdkConfiguration.Builder("SDK").build();
        MySabaySDK.Impl.setDefaultInstanceConfiguration(this, configuration);
    }
}
