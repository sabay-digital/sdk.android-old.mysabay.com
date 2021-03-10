package kh.com.mysabay.sdk;

public class SdkConfiguration {
    public String mySabayAppName;

    private SdkConfiguration(String mySabayAppName) {
        this.mySabayAppName = mySabayAppName;
    }

    public static class Builder {
        private final String mySabayAppName;

        public Builder(String mySabayAppName) {
            this.mySabayAppName = mySabayAppName;
        }

        public SdkConfiguration build() {
            return new SdkConfiguration(mySabayAppName);
        }
    }
}

