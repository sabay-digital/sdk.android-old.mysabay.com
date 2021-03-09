package kh.com.mysabay.sdk;

import kh.com.mysabay.sdk.utils.SdkLanguages;
import kh.com.mysabay.sdk.utils.SdkTheme;

/**
 * Created by Tan Phirum on 3/30/20
 * Gmail phirumtan@gmail.com
 */
public class SdkConfiguration {

    public String appId;
    public String appSecret;
    public String licenseKey;
    public String merchantId;
    public boolean isSandBox;
    public SdkTheme sdkTheme;
    public SdkLanguages sdkLanguages;
    public String mySabayAppName;

    private SdkConfiguration(String appId, String appSecret, String licenseKey, String merchantId,
                             boolean isSandBox, SdkTheme sdkTheme, SdkLanguages sdkLanguages, String mySabayAppName) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.licenseKey = licenseKey;
        this.merchantId = merchantId;
        this.isSandBox = isSandBox;
        this.sdkTheme = sdkTheme;
        this.sdkLanguages = sdkLanguages;
        this.mySabayAppName = mySabayAppName;
    }

    public static class Builder {
        private final String appId;
        private final String appSecret;
        private final String licenseKey;
        private final String merchantId;
        private final String mySabayAppName;
        private boolean isSandBox = true;
        private SdkTheme sdkTheme = SdkTheme.Dark;
        private SdkLanguages sdkLanguages = SdkLanguages.En;

        public Builder(String appId, String mySabayAppName, String appSecret, String licenseKey, String merchantId) {
            this.appId = appId;
            this.appSecret = appSecret;
            this.licenseKey = licenseKey;
            this.merchantId = merchantId;
            this.mySabayAppName = mySabayAppName;
        }

        public Builder setToUseSandBox(boolean isSandBox) {
            this.isSandBox = isSandBox;
            return this;
        }

        public Builder setSdkTheme(SdkTheme sdkTheme) {
            this.sdkTheme = sdkTheme;
            return this;
        }

        public Builder setLanguage(SdkLanguages sdkLanguages) {
            this.sdkLanguages = sdkLanguages;
            return this;
        }

        public SdkConfiguration build() {
            return new SdkConfiguration(appId, appSecret, licenseKey, merchantId, isSandBox, sdkTheme, sdkLanguages, mySabayAppName);
        }
    }
}

