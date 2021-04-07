# Tracking API for MySabay Android SDK

MySabay SDK provides functions for tracking in your app.

## Functions

### Tracking Screens

This function can be called on screen that triggers when the user visits.

```java
    MySabaySDK.getInstance().trackPageView(context, "/activity_main", "/activity_main");
```

- function: `trackPageView(Context context, String path, String title)`
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `path`: screen path
    - `title`: The title of the action being tracked. It is possible to use slashes / to set one or several categories for this action.

### Tracking Event

This function can be called to track events triggered by the user's action or any processes of the app.

```java
    MySabaySDK.getInstance().trackEvents(getContext(), "login", "tap", "register-mysabay");
```

- Function: `trackEvent(Context context, String category, String action, String name)`
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `category - String`: defines a category that the event should be in.
    - `action - String`: defines what kind of action triggers this event.
    - `name - String`: defines the name of the event.

### Tracking Order

This function can be called to track when purchase is made in the app.

```java
    MySabaySDK.getInstance().trackOrder(context, rackingOrder);
```

- Function: `trackOrder(Context context, TrackingOrder trackingOrder)`
- Arguments:
    - `context`: the context which is linked to the Activity from which is called
	- `trackingOrder` 
	    - `id: String`: defines a order id
		- `items: [EcommerceItems]`: defines the array of items of order
		- `grandTotal: Integer`: define total amount
		- `subTotal: Integer`: defines the sub total of order
		- `tax: Integer`: defines the tax of order
		- `shipping: Integer`: defines the shipping cost of order
		- `discount: Integer`: defines the discount of order

### Custom User ID

This function can be called to set a custom user ID in place of the default UUID created for each visitor.

```java 
    MySabaySDK.getInstance().setCustomUserId(context, userId);
```

- Arguments:
    - `context`: the context which is linked to the Activity from which is called
    - `userId - String`: defines a custom ID to be used to identify a user.

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
    MySabaySDK.getInstance().loginWithPhone(phoneNumber, new DataCallback<LoginWithPhoneMutation.Sso_loginPhone>() {
        @Override
        public void onSuccess(LoginWithPhoneMutation.Sso_loginPhone response) {
            if (response.getData() != null) {
                MySabaySDK.getInstance().trackEvents(context, "sdk-" + Constant.sso, Constant.process, "login-with-phone-number-success");
            } else {
                MySabaySDK.getInstance().trackEvents(context, "sdk-" + Constant.sso, Constant.process, "login-with-phone-number-failed");
            } 
        }

        @Override
        public void onFailed(Object error) {
            LogUtil.info("err", e.getMessage());
            MySabaySDK.getInstance().trackEvents(context, "sdk-" + Constant.sso, Constant.process, "login-with-phone-number-failed");
        });
    }
```
