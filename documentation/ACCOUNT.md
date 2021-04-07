# Account API for MySabay Android SDK

MySabay SDK provides UI support and functionalities for your app to access account authentication.

## UI Support

MySabay SDK built-in with ui support for authentication for account. This will make you easy without making your own ui form to handle all functionalities for authentication process. Here we will handle for login with phone and one-time password, MySabay account and Facebook, registering MySabay account, verifying and creating MySabay account related to a phone number.

### Login Process

Call this function if you want to use ui support, all you will do is to wait for result. 

```java
    MySabaySDK.getInstance().openLoginScreen(new LoginListener() {
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

Using ui support you dont to care about session for user, MySabay SDK will handle it for you.

## Functionalities

### Login Guest

If you want user to try your app without login with account, MySabay SDK provides function to login as guest.

```java
    MySabaySDK.getInstance().loginGuest(new DataCallback<LoginGuestMutation.Sso_loginGuest>() {
        @Override
        public void onSuccess(LoginGuestMutation.Sso_loginGuest response) {
            LogUtil.info("Login as guest", response.toString());
        }

        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", error.toString());
        }
        });
    });
```

### Login Phone

Sending phone number to login and waiting for one-time password to verify

```java
    MySabaySDK.getInstance().loginWithPhone(phoneNumber, new DataCallback<LoginWithPhoneMutation.Sso_loginPhone>() {
        @Override
        public void onSuccess(LoginWithPhoneMutation.Sso_loginPhone response) {
            LogUtil.info("Login with phone", response.toString());
        }

        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", error.toString());
        }
    });
```

This will return a promise with turple

- `phoneNumber: String` phone number that you want to login.

- `verifyMySabay: Bool` this can be `true` or `false`

    - `true`: It indicates that this phone number is related to any MySabay account. You will get `mySabayUserName` value in return. This requires user to have option to confirm their MySabay account my input password that is matched `mySabayUserName` with [verifyMySabay](#verify-mysabay) function or user can request to create another MySabay account by calling [requestCreatingMySabayWithPhone](#request-creating-mysabay-with-phone) function.
    - `false`: It indicated that this phone number is not related to any MySabay account, you can go to one-time password verification.

- `mySabayUserName: String` this can be empty or with value based on `verifyMySabay`.

### Verify OTP code

```java
    MySabaySDK.getInstance().verifyOtp(phoneNumber, otpCode, new DataCallback<VerifyOtpCodMutation.Sso_verifyOTP>() {
    	@Override
    	public void onSuccess(VerifyOtpCodMutation.Sso_verifyOTP response) {
            LogUtil.info("Verify otp", response.toString());     
        }

        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", error.toString());            
        }
    });
```

### Login Facebook

MySabay SDK allows user to login with Facebook. Here you have to get Facebook App ID from us to integrate in your app. After login process with Facebook SDK is successfully, send token with function below to login.

```java
    MySabaySDK.getInstance().loginWithFacebook(token, new DataCallback<LoginWithFacebookMutation.Sso_loginFacebook>() {
        @Override
        public void onSuccess(LoginWithFacebookMutation.Sso_loginFacebook response) {
            LogUtil.info("Login with facebook", response.toString());     
        }

		@Override
		public void onFailed(Object error) {
			LogUtil.info("Error", error.toString());     
		}
	});	
```

### Login MySabay

Login with MySabay is for user who has MySabay account. Call this function in your app to login.

```java 
    MySabaySDK.getInstance().loginWithMySabay(username, password, new DataCallback<LoginWithMySabayMutation.Sso_loginMySabay>() {
        @Override
        public void onSuccess(LoginWithMySabayMutation.Sso_loginMySabay response) {
            LogUtil.info("Login with MySabay", response.toString()); 
        }

        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", response.toString()); 
	    }
    });
```

### Register MySabay

To register as new MySabay user, call function below.

```java
    MySabaySDK.getInstance().registerMySabayAccount(username, password, new DataCallback<CreateMySabayLoginMutation.Sso_createMySabayLogin>() {
        @Override
        public void onSuccess(CreateMySabayLoginMutation.Sso_createMySabayLogin response) {
            LogUtil.info("Register MySabay account", response.toString());        
        }

        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", error.toString());                
        }
    });
```

### Check MySabay

Before calling [Register MySabay](#register-mysabay) account, you can check if username is already exist in MySabay system by calling function below.

```java
    MySabaySDK.getInstance().checkExistingMySabayUsername(username, new DataCallback<Boolean>() {
    	@Override
        public void onSuccess(Boolean response) {
           LogUtil.info("Check existing Mysabay username", response.toString());
        }

        @Override
        public void onFailed(Object error) {
			LogUtil.info("Error", error.toString());        
        }
    });
```

### Verify MySabay

This function is described in [Login Phone](#login-phone)

```java
    MySabaySDK.getInstance().verifyMySabay(mysabayUsername, password, new DataCallback<VerifyMySabayMutation.Sso_verifyMySabay>() {
        @Override
        public void onSuccess(VerifyMySabayMutation.Sso_verifyMySabay response) {
            LogUtil.info("Verify Mysabay", response.toString());  
        }

        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", error.toString());                 
        }
    });
```

### Request Creating MySabay With Phone

This function is described in [Login Phone](#login-phone)

```java 
    MySabaySDK.getInstance().requestCreatingMySabayWithPhone(phoneNumber, new DataCallback<SendCreateMySabayWithPhoneOTPMutation.Sso_sendCreateMySabayWithPhoneOTP>() {
        @Override
        public void onSuccess(SendCreateMySabayWithPhoneOTPMutation.Sso_sendCreateMySabayWithPhoneOTP response) {
           LogUtil.info("Verify Mysabay", response.toString());                   
        }

        @Override
        public void onFailed(Object error) {
			LogUtil.info("Error", error.toString());   
        }
    });
```

### Create MySabay Account with Phone 

This is finalizing the process with [requestCreatingMySabayWithPhone](#request-creating-mysabay-with-phone) after getting one-time password.

```java
    MySabaySDK.getInstance().createMySabayWithPhone(username, password, phoneNumber, otpCode, new DataCallback<CreateMySabayLoginWithPhoneMutation.Sso_createMySabayLoginWithPhone>() {
        @Override
        public void onSuccess(CreateMySabayLoginWithPhoneMutation.Sso_createMySabayLoginWithPhone response) {
            LogUtil.info("Create Mysabay with phone", response.toString());     
        }

        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", response.toString());        
        }
    });
```

### Get Profile

This is to get user's profile.

```java
    import kh.com.mysabay.sdk.MySabaySDK;

     MySabaySDK.getInstance().getUserProfile(info -> {
        if (info != null) {
            UserProfileItem userProfile = new Gson().fromJson(info, UserProfileItem.class);
            LogUtil.info("Profile userId", userProfile.userID.toString());
            LogUtil.info("Profile name", userProfile.givenName);
            LogUtil.info("Profile localPayEnabled", userProfile.localPayEnabled.toString());
            LogUtil.info("Profile persona", userProfile.persona.toString());
            MessageUtil.displayDialog(v.getContext(), info);
        }
    });
```

### Refresh Tokens

Call this function to refresh login's tokens.

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

### Logout

To logout user, call this function

```java
    MySabaySDK.getInstance().logout();
```

### Tokens Management

MySabay SDK will manage tokens when login in with UI support. For functionalities, you have to manage tokens manually for later use.

```java
    AppItem appItem = new AppItem(accessToken, refreshToken, expire);
    String item = new Gson().toJson(appItem);
    MySabaySDK.getInstance().saveAppItem(item);
```

#### Example

```java 
  	MySabaySDK.getInstance().verifyOTPCode(phoneNumber, otpCode, new DataCallback<VerifyOtpCodMutation.Sso_verifyOTP>() {
    	@Override
    	public void onSuccess(VerifyOtpCodMutation.Sso_verifyOTP response) {
            AppItem appItem = new AppItem(response.accessToken(), response.refreshToken(), response.expire());
            String item = new Gson().toJson(appItem);
            MySabaySDK.getInstance().saveAppItem(item);   
        }

        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", error.toString());            
        }
    });
```


