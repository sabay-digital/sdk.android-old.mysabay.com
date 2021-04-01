# Installation for MySabay Android SDK

This is the quick guide to install MySabay Android SDK in your app.

## Service Code

MySabay SDK distinguishes the service based on service code. You can create your service at ....

## Installation

1. Add Jitpack to your project build.gralde file

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. Then add this dependency to your app build.gradle file.

```gradle
dependencies {
    implementation 'com.github.sabay-digital:sdk.android-old.mysabay.com:2.0.0'
}
```

Add dataBinding to gradle
```gradle
android {
    ...
    dataBinding {
        enabled = true
    }
}
```

Add compileOptions
```grale    
android {
    ...
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
        sourceCompatibility JavaVersion.VERSION_1_8
}
```

Add thease dependencies if you use android appcompat
```gradle
dependencies {
    implementation 'com.android.support:design:28.0.0'
    implementation 'com.afollestad.material-dialogs:core:0.9.6.0'
    implementation 'com.google.code.gson:gson:2.8.6'
    implementation 'com.googlecode.json-simple:json-simple:1.1'
    implementation 'org.apache.commons:commons-lang3:3.8.1'
}
```

3. Declare Permissions in AndroidManifest.xml
```java
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
```

4. Initialize SDK

MysabaySdk needs to be initialized. You should only do this 1 time, so placing the initialization in your Application is a good idea. An example for this would be:

```java
[MyApplication.java]

import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.SdkConfiguration;
import kh.com.mysabay.sdk.utils.SdkTheme;
import kh.com.mysabay.sdk.SdkApplication;

public class MyApplication extends SdkApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //MySabaySDK has default configuration with dark theme and sandbox url.
        final SdkConfiguration configuration = new SdkConfiguration.Builder(
                "ARENA OF GLORY", //mysabay  app name
                "sdk_sample", // service code
                "", // license key
                "") // merchant id
                .setSdkTheme(SdkTheme.Dark)
                .setToUseSandBox(true).build();
                MySabaySDK.Impl.setDefaultInstanceConfiguration(this, configuration);
    }
}
```
> NOTE: MySabaySdk is need configuration
