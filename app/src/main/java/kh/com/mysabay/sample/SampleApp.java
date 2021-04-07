package kh.com.mysabay.sample;

import android.content.res.Configuration;
import android.support.annotation.NonNull;
import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.SdkConfiguration;
import kh.com.mysabay.sdk.SdkApplication;
import kh.com.mysabay.sdk.utils.SdkTheme;

/**
 * Created by Tan Phirum on 4/12/20
 * Gmail phirumtan@gmail.com
 */
public class SampleApp extends SdkApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        final SdkConfiguration configuration = new SdkConfiguration.Builder(
                "SDK SAMPLE",
                "sdk_sample",
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtvetByZxZJT4dqZ4LRM8wksF0+cx7xOB4qO4ZhDJHCeBMY9Biydq0vda24EKERP0Uv+Rr1SXDcP2mo4/GrbmFUHBHw9kiidXZCG7+RSoEu+BDNfMBHH8jSw2RbFN6Mox29qz4SrSNgGDOwW/REWkKSP8QkCUpeog85nw7cCcOoOKx7Fo46OwZLQBD744TAJlBeNK3FPVXaGlceDBPPjSrksGvnhKpdzl/6kFAX1Ufz0G+QJXQAJYAzRwYqt0SeMNKXv0vUjIbXtiJaDf9Ev8l5+OLZdFDaF6rxIiVs11X9r2m+D2ODEpQw1c0rM5JfVBkEr/Ty8yZh77cWTX9BdV6wIDAQAB", // license key
                "") // merchant id
                .setSdkTheme(SdkTheme.Dark)
                .setToUseSandBox(true).build();
        MySabaySDK.Impl.setDefaultInstanceConfiguration(this, configuration);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
