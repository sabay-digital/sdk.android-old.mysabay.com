# Official MySabay SDK for Android

This is the official MySabay SDK for native Android application. To use this SDK, you can follow the guides below or download the test with the example project we have in this repository.

## Version
-Latest: 
    - Change from rest api to graphQL gateway
    - Implement user process with graphQL gateway
    - Implement store process with graphQL gateway
    - Implement tracking with Piwik
    - Implement register with Mysabay from login
    - Implement Login with Mysabay UI

- Latest: 1.0.18  
    - Wing payment  
    - Check application install manually via APK or Google Play store.  
    - Display message if user input wrong verify code.  
    - Display loading if In-App Purchase Payment is not yet completed.  
    - Change login logo and update checkout screen.  

## Create your application

Create your MySabay application if you don't have one yet at [MySabay App Dashboard](https://kh.mysabay.com:8443/index.html) and copy your `appId` 
and `appSecret` for the integration. 

## Workflow
The login and payment workflow is described with the following diagram for communication between CP app, server, mySabay SDK and mySabay API.

### Login flow
<img src="https://git.sabay.com/mysabay/sdk/app.android.sdk.mysabay.com.public/-/raw/master/Images/user-login-flow.png">

### Payment flow
There are 2 different payment flows in the SDK: 1). payment with google In App Billing 2). payment with mySabay Wallet which includes different payment options such as Telco, Sabay Coin, and the list will continue as we are working to add more payment service providers such as banks. You will have to implement the SDK payment following the 2 flows below

#### 1). Payment with In-App Billing:
<img src="https://github.com/sabay-digital/app.android.sdk.mysabay.com.public/raw/master/Images/payment-flow-iap.png">

#### 2). Payment with mySabay Wallet:
<img src="https://github.com/sabay-digital/app.android.sdk.mysabay.com.public/raw/master/Images/payment-flow-ssn.png">

Refer to the API document below for payment receipt validation of both payments.

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
    implementation 'com.github.sabay-digital:sdk.android-old.mysabay.com:1.0.4-o'
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
                        "55", // mysabay app Id
                        "SDK sample", //mysabay  app name
                        "9c85c50a4362f687cd4507771ba81db5cf50eaa0b3008f4f943f77ba3ac6386b", //MySabay App Secret
                        "", // license key
                        "") // merchant id
                        .setSdkTheme(SdkTheme.Light)
                        .setToUseSandBox(true).build();
                MySabaySDK.Impl.setDefaultInstanceConfiguration(this, configuration);
    }
}
```
> NOTE: MySabaySdk is need configuration

## Integration

> Note that in order to use the store and checkout function, the user must login first.
> Follow the guide below for each functions provided by the SDK:

## With UI

*  **Login**

```java
    import kh.com.mysabay.sdk.MySabaySDK;

    MySabaySDK.getInstance().showLoginView(new LoginListener() {
        @Override
        public void loginSuccess(String accessToken) {
            MessageUtil.displayToast(v.getContext(), "accessToken = " + accessToken);
        }

        @Override
        public void loginFailed(Object error) {
            MessageUtil.displayToast(v.getContext(), "error = " + error);
        }
    });
``` 

* **Show user profile**

```java
    import kh.com.mysabay.sdk.MySabaySDK;

     MySabaySDK.getInstance().getUserProfile(info -> {
        UserProfileItem userProfile = new Gson().fromJson(info, UserProfileItem.class);
        LogUtil.info("Profile uuid", userProfile.data.uuid);
        LogUtil.info("Profile mySabayUserId", userProfile.data.mysabayUserId.toString());
        LogUtil.info("Profile serviceUserId", userProfile.data.serviceUserId);
        LogUtil.info("Profile lastLogin", userProfile.data.lastLogin);
        LogUtil.info("Profile enableLocalPay", userProfile.data.enableLocalPay.toString());
        LogUtil.info("Profile Info", userProfile.data.createdAt);
        LogUtil.info("Profile balance coin", userProfile.data.balance.coin.toString());
        LogUtil.info("Profile balance gold", userProfile.data.balance.gold.toString());
    });
```

* **Store and checkout**

```java
    import kh.com.mysabay.sdk.MySabaySDK;

    MySabaySDK.getInstance().showStoreView(new PaymentListener() {
        @Override
        public void purchaseSuccess(SubscribePayment data) {
            if (data.getType().equals(Globals.APP_IN_PURCHASE)) {
                GoogleVerifyBody receipt = (GoogleVerifyBody) data.data;
                LogUtil.info("data", receipt.receipt.data.toString());
                LogUtil.info("signature", receipt.receipt.signature.toString());
            } else if (data.getType().equals(Globals.MY_SABAY)) {
                PaymentResponseItem dataPayment = (PaymentResponseItem) data.data;
                LogUtil.info("satus",  dataPayment.status.toString());
                LogUtil.info("amount",  dataPayment.amount);
                LogUtil.info("hash",  dataPayment.hash);
                LogUtil.info("PackageId",  dataPayment.packageId);
                LogUtil.info("message",  dataPayment.message);
                LogUtil.info("pspAssetCode",  dataPayment.pspAssetCode);
                LogUtil.info("bonus",  dataPayment.bonus);
            } else {
                Data dataPayment = (Data) data.data;
                LogUtil.info(data.getType(), new Gson().toJson(data.data));
                LogUtil.info("hash",  dataPayment.hash);
                LogUtil.info("amount",  dataPayment.amount);
                LogUtil.info("packageId",  dataPayment.packageId);
                LogUtil.info("assetCode", dataPayment.assetCode);
            }
        }
        @Override
        public void purchaseFailed(Object dataError) {
            // hanlde error
        }
    });
```

>Sample Response - IN APP PURCHASE
```json
    {
        "receipt":{
            "data":{
                "orderId":"GPA.3392-7464-8332-69588",
                "packageName":"kh.com.sabay.aog",
                "productId":"kh.com.sabay.aog.iap.2_usd",
                "purchaseState":1,
                "purchaseTime":1601024909913,
                "purchaseToken":"ggllldpkefafhjjpogdipjbc.AO-J1Oz-XRD2126HN0qMc6_Nc4J17D4yDCMiXyb__FIiJ2ehkYYwwTqsTqqL4eHbWRmywPT13RIw8_PE2bDrvTe6XrNdO81zARTRPCsvtD6R1nPm0PjiCe3xaralXOMoD7TRLW1DeOex"
            },
            "signature":"Mixpc6bAdCNOqYfBzqNwbV7rJYTWrwufw2l0fik53WIlWOSSKgmnjHRUf+29gjSLRs8R0lL7tecBtG0Gt+xrxHjgQIaRCwQzDB2aU2O+Etsh7JAE9qhaub+GmKirPTWvg/lJimKxCuKet60ps7UP5JamgVWlhj9/h9ecv682YOt1P9Inw1t9hKW6marYDoYhICGPHafxpD5/n2lBKshbbMIEjJ4y0chk/QvHPV0BJdGnd+9X1uulGfFssKGVCq3VvtdpKrN7BArJQmlbF2ZgKMvEZ93Qk6++YyE82OklTv0s0XyDTWcxvInzyBfE4CePmO9Kqu/n7toJ4ROWOGcwYQ=="
        }
    }
```
>Sample Response - MYSABAY
    
```json
    {
        "amount":"80.0",
        "hash":"d15052dfb870306b0d55a785e815852729da2bb1a71e11041f7c090c1551a850",
        "label":"+12 Diamonds",
        "message":"Payment Completed.",
        "package_id":"kh.com.sabay.aog.local.2_usd",
        "psp_asset_code":"sc",
        "status":200
    }
```

* **Refresh token** 

```java
    import kh.com.mysabay.sdk.MySabaySDK;
    MySabaySDK.getInstance().refreshToken(new RefreshTokenListener() {
        @Override
        public void refreshSuccess(String token) {
              LogUtil.info("token", token);
        }
    
        @Override
        public void refreshFailed(Throwable error) {
            //handle error here
        }
    });
```

* **Get current token**

```java
    import kh.com.mysabay.sdk.MySabaySDK;

    MySabaySDK.getInstance().currentToken();
```

* **Check valid token**

```java
    import kh.com.mysabay.sdk.MySabaySDK;

    MySabaySDK.getInstance().isTokenValid();
```

* **Logout**

To logout user session from the app use the following method:

``` java
    import kh.com.mysabay.sdk.MySabaySDK;

    MySabaySDK.getInstance().logout();
```

## Without UI
### Login

There are three login functions offered by the SDK
#### Login with phone number
- If your phone number don't link with MySabay account it use OTP to verify
- If your phone number linked with MySabay account it use verify MySabay instead of OTP

**Functions**
- function: `loginWithPhoneNumber(Context context, String phoneNumber, String dialCode)`
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `phoneNumber`: phone number that you use to login
    - `dialCode`: telephone number prefix
- Example 

#### Login with MySabay account
**Function**
- function: `loginWithMySabayAccount(Context context, String username, String password)`
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `username`: username you input
    - `password`: password you input
- Example 

#### Login with facebook
**Function**
- function: `loginWithFacebook(Activity context, String token)`
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `token`: token then you get from facebook after register a callback with facebook login button
- Example
    
### Register
#### Create MySabay account without phone number
- we can create MySabay account by entering username and password

**Function**
- function: `createMySabayAccount(Context context, String username, String password)`
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `username`: username that you input
    - `password`: password that you input
- Example

#### Create MySabay account with phone number
- If you create MySabay account with phone number first you need to input username and password by using function `createMySabayWithPhoneOTP(Context context, String phoneNumber)` and pass username and password to verify screen then verify otp code by using function `createMySabayLoginWithPhone(Context context, String username, String password, String phoneNumber, String otpCode)`

**Function**
- function: `createMySabayWithPhoneOTP(Context context, String phoneNumber)`
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `phoneNumber`: phoneNumber you use to login
- Example

- function: `createMySabayLoginWithPhone(Context context, String username, String password, String phoneNumber, String otpCode)`
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `username`: username that you input
    - `password`: password that you input
    - `phoneNumber`: phoneNumber you use to login
    - `otpCode`: otp code that message sent to verify your phone number
- Example

### Verify  

### Verify otp code
- function: `verifyOTPCode(Context context, int otpCode)`
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `otpCode`: otp code that message sent to verify your phone number
- Example

### Verify MySabay
- function: `verifyMySabayAccount(Context context, String username, String password)`
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `username`: MySabay account username
    - `password`: MySabay account password
- Example

### Resend otp code
- function: `resendOTP(Context context, String phoneNumber)`
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `phoneNumber`: phoneNumber you use to login
- Example

### Get user profile
- function: `getUserProfile(Context context)`
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
- Example

## Tracking

### Functions
There are three functions offered by the SDK that the client can call.
#### trackPageView
- function: `trackPageView(Context context, String path, String title)`
- Description: This function can be called on screen that triggers when the user visits.
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `path`: screen path
    - `title`: The title of the action being tracked. It is possible to use slashes / to set one or several categories for this action.
- Example
```java
    import kh.com.mysabay.sdk.MySabaySDK;

    MySabaySDK.getInstance().trackPageView(getContext(), "/activity_main", "/activity_main");
```
#### trackEvent

- Function: `trackEvent(Context context, String category, String action, String name)`
- Description: This function can be called to track events triggered by the user's action or any processes of the app.
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `category - String`: defines a category that the event should be in.
    - `action - String`: defines what kind of action triggers this event.
    - `name - String`: defines the name of the event.
- Example:
```java
    import kh.com.mysabay.sdk.MySabaySDK;

    MySabaySDK.getInstance().trackEvents(getContext(), "login", "tap", "register-mysabay");
```
#### setCustomUserId

- Function: `setCustomUserId(Context context, String userId)`
- Description: This function can be called to set a custom user ID in place of the default UUID created for each visitor.
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `userId - String`: defines a custom ID to be used to identify a user.
- Example:
```java
    import kh.com.mysabay.sdk.MySabaySDK;

    MySabaySDK.getInstance().setCustomUserId(getContext(), userId);
```

### Internal tracking within the SDK (**PRIVATE INFORMATION**)

We automatically track page views and events within the SDK. The following will outline where and how we track these data.

#### Tracking Screens

The following screens are automatically tracked on when the user visits.

`trackPageView()` automatically adds `[PLATFORM]` to the beginning of the array. In this android SDK, it adds `android`. This properly defines structure for the data to be viewed later on the Matomo dashboard.

| Screen                                 | Activity/Fragment               | Code                                                                         |
| -------------------------------------- | ------------------------------- | ---------------------------------------------------------------------------- |
| Main Login Screen                      | `LoginFragment`                 | `MySabaySDK.getInstance().trackPageView(getContext(), "/sdk/login-screen", "/sdk/login-screen");`            |
| Verify OTP Screen                      | `VerifiedFragment`              | `MySabaySDK.getInstance().trackPageView(getContext(), "/sdk/otp-screen", "/sdk/otp-screen");`              |
| Verify Existing MySabay Account Screen | `MySabayLoginConfirmFragment`   | `MySabaySDK.getInstance().trackPageView(getContext(), "/sdk/verify-mysabay-screen", "/sdk/verify-mysabay-screen");`   |
| Register for a MySabay Account Screen  | `MySabayCreateFragment`         | `MySabaySDK.getInstance().trackPageView(getContext(), "/sdk/register-mysabay-screen", "/sdk/register-mysabay-scree");` |
| Store Screen                           | `ShopsFragment`                 | `MySabaySDK.getInstance().trackPageView(getContext(), "/sdk/product-screen", "/sdk/product-screen");`          |

#### Tracking Events

There are many places in the SDK that we use` trackEvent()`. Mainly, we use it to track user's interaction and any important processes of the app.

The function takes the following arguments and standards are defined for these arguments as stated below:
  - `category - String`: the format for category is `[PLATFORM]-sdk-[SCOPE]`. Platform can be either `ios` or `android`, where there are 3 defined Scopes for the SDK, `sso`, `store` and `payment`.
    - `sso`: used when the event triggered anywhere within the login/register scope.
    - `store`: used when the event triggered anywhere within the store scope.
    - `payment`: used when the event triggered anywhere within the payment scope.
  - `action - String`: there are currently two actions for the SDK, `tap` and `process`.
    - `tap`: used when the user taps on something on the screen.
    - `process`: used whenever there's a process to be completed within the app. For example, you can use this event to track whether the payment is successful or not. Please refer to the code example below for more details.
  - `name - String`: used to identify the event and it should be given a meaningful name. It should follow kebab-case convention. Avoid using abbreviations.

You should implement `input` tracking for all buttons in the SDK. For example,

```java
    import kh.com.mysabay.sdk.MySabaySDK;

   mViewBinding.btnLogin.setOnClickListener(v -> {
        MySabaySDK.getInstance().trackEvents(getContext(), "sdk-" + Constant.sso, Constant.tap, "login-with-phone-number");
   });            
```

You should implement `process` tracking for any important API call. This will allow us to track whether the call is successful or not.

```java
apolloClient.mutate(loginWithPhoneMutation).enqueue(new ApolloCall.Callback<LoginWithPhoneMutation.Data>() {
    @Override
    public void onResponse(@NotNull Response<LoginWithPhoneMutation.Data> response) {
        if (response.getData() != null) {
            MySabaySDK.getInstance().trackEvents(context, "sdk-" + Constant.sso, Constant.process, "login-with-phone-number-success");
        } else {
            MySabaySDK.getInstance().trackEvents(context, "sdk-" + Constant.sso, Constant.process, "login-with-phone-number-failed");
        } 
    }

    @Override
    public void onFailure(@NotNull ApolloException e) {
        LogUtil.info("err", e.getMessage());
        MySabaySDK.getInstance().trackEvents(context, "sdk-" + Constant.sso, Constant.process, "login-with-phone-number-failed");
    });
}
```

## mySabay API
### Server side validation
In order for the CP server to validate the user access token received from the client as valid, mySabay also hosts pulic user api for fetching user profile and validating token. The API document is available [here](https://api-reference.mysabay.com/).

### Note 
* This SDK supports Android with  minSdkVersion 21 only.


