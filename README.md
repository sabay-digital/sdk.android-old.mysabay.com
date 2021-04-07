# Official MySabay SDK for Android

This is the official MySabay SDK for native Android application. To use this SDK, you can follow the guides below or download the test with the example project we have in this repository.

- [Installation & Configuration](/documentation/INSTALLATION.md)
- [Authentication & Account](/documentation/ACCOUNT.md)
- [Store & Purchase](/documentation/STORE.md)
- [Tracking](/documentation/TRACKING.md)

# SDK Functionalities

Currently, MySabay SDK both provides UI support and functionalities Support

## UI Support

There are 2 main functions offered by sdk to use built-in ui support 

### User

|         Function        |           Desciption              |
| ----------------------- | --------------------------------- |
| ```openLoginScreen(PaymentListener listener)``` | Open ui support for user process. In this function SDK handle many processes of user management as below <ul><li>Login with Phone number</li><li>Login with Mysabay</li><li>Login with Facebook</li><li>Register MySabay account</li><li>Register MySabay account</li><li>Create new MySabay account related to phone number</li><li>Logout</li></ul> |

### Store

|         Function        |           Desciption              |
| ----------------------- | --------------------------------- |
| ```openStoreScreen()``` | Open ui support for store process. In this function SDK handle many processes of store management as below <ul><li>Show store products</li><li>Checkout payment service provider</li><li>Purchase with three type of payment</li><ul><li>MySabay</li><li>In app purchase</li><li>Other Payment Options</li></ul></ul> | 

In ui support you can also use theses function too.
|         Function        |           Desciption              |
| ----------------------- | --------------------------------- |
| ```getUserProfile()``` | To get user's profile. |
| ```currentToken()``` | To get current accessToken|
| ```currentRefreshToken()``` | To get current refreshToken|
| ```refreshTokens()``` | To refresh login's tokens. |
| ```verifyTokens()``` | To check if token is verified |
| ```logout()``` | To logout user |

## Functionalities support

### user

|         Function        |           Desciption              |
| ----------------------- | --------------------------------- |
| ```loginGuest(String deviceId)``` | Login without providing credential but You need to provide your deviceId, MySabay SDK allow user to login as a guest. |
| ```loginWithPhone(String phoneNumber)``` | Login with phone number and waiting for one-time password to verify. |
| ```verifyOtp(String phoneNumber, String otp)``` | Verify phone number with one-time otp-code to server for verification. |
| ```loginWithFacebook(String token)``` | Login with Facebook by passing token form login with Facebook SDK. <br/><b>Note</b> You need to get Facebook app id from us |
| ```loginWithMySabay(String username, String password)``` | Login with MySabay if you have MySabay account |
| ```registerMySabayAccount(String username, String password)``` | Register new user MySabay account|
| ```verifyMySabay(String username, String password)``` | Confirm MySabay account in case you login with phone number that related to MySabay account So you need to confirm MySabay account to login without confirm with otp-code |
| ```requestCreatingMySabayWithPhone(String phoneNumber)``` | In case you cannot confirm MySabay account you can use this function to request new MySabay account that related to your phone number |
| ```createMySabayWithPhone(String username, String password, String phoneNumber, String otpCode)``` | After request new MySabay Account You need to use this function to create new MySabay account that related to your phone number|
| ```checkExistingMySabayUsername(String username)``` | Before calling `Register MySabay` account, you can use this function to check username is already exist or not in MySabay system.|
| ```getUserProfile()``` | To get user's profile. |
| ```refreshTokens()``` | To refresh login's tokens. |
| ```verifyTokens()``` | To check if token is verified |
| ```saveAppItem(String item``` | To store appItem `(accessToken, refreshToken, expire)` in local storage|
| ```currentToken()``` | To get current accessToken|
| ```currentRefreshToken()``` | To get current refreshToken|
| ```logout()``` | To logout user |


## Store 
|         Function        |           Desciption              |
| ----------------------- | --------------------------------- |
| ```getStoreProducts()``` | To get all store products |
| ```getPaymentServiceProvidersByProduct(String productId)``` | Each store product has it own supported providers. Call this function to get providers for specific product |
| ```getExchangeRate()``` | To get exchange rate from usd to Khmer riel |
| ```createPaymentDetail(String pspId, List<Object> items,double amount, String currency)``` | To create a purchase you need to call this function to get payment data |
| ```createPreAuthPayment(Data data)``` | To make purchase with pre-authorization provider |
| ```verifyInAppPurchase(Data data, GoogleVerifyBody body)``` | To make purchase with in-app purchase provider |
| ```createOneTimePayment(Data dataPayment)``` | To make purchase with other payment option provider |
| ```checkPaymentStatus()``` | The final step to check that purchase is paid or not|
| ```scheduledCheckPaymentStatus(Handler handler, String invoiceId, long interval, long repeat)``` | The final step to check that purchase is paid or not as schedule|

### Tracking

|         Function        |           Desciption              |
| ----------------------- | --------------------------------- |
| ```trackPageView(Context context, String path, String title)``` | Track screen where the user visit |
| ```trackEvents(Context context, String category, String action, String name)``` | Tracking events triggered by the user's action or any processes of the app |
| ```trackOrder(Context context, TrackingOrder trackingOrder)``` | Track when purchase is made in the app |
| ```setCustomUserId(Context context, String userId)``` | Set a custom user ID for user tracking |

### Note 
* This SDK supports Android from  minSdkVersion 21


