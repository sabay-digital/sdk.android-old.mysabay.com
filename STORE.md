# Store API for MySabay Android SDK

MySabay SDK provides UI support and functionalities for your app to access store process.

## UI Support

MySabay SDK built-in with ui support for access store. This will make you easy without making your own ui show store products or handle payment process.

### Store Process

Call this function if you want to use ui support for store, all you will do is to wait for result.

```java
    MySabaySDK.getInstance().showStoreView(new PaymentListener() {
        @Override
        public void purchaseSuccess(SubscribePayment data) {
            if (data.getType().equals(Globals.APP_IN_PURCHASE)) {
                GoogleVerifyBody receipt = (GoogleVerifyBody) data.data;
                 LogUtil.info("iap",  receipt.toString());
            } else if (data.getType().equals(Globals.MY_SABAY)) {
                PaymentResponseItem dataPayment = (PaymentResponseItem) data.data;
                LogUtil.info("mysabay",  dataPayment.toString());
            } else {
                Data dataPayment = (Data) data.data;
                LogUtil.info("pre-auth", new Gson().toJson(data.data));
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

## Funtionalities

## Store Products

To get all store products for your service, call this function. 

```java
    MySabaySDK.getInstance().getStoreProducts(new DataCallback<GetProductsByServiceCodeQuery.Store_listProduct>() {
        @Override
        public void onSuccess(GetProductsByServiceCodeQuery.Store_listProduct response) {
            LogUtil.info("Store Product", response.toString());
        }

        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", error.toString());
        }
    });
```

Each store product has it own supported providers. To get providers for specific product, call this function.

```java
    MySabaySDK.getInstance().getPaymentServiceProvidersByProduct(productId, new DataCallback<Checkout_getPaymentServiceProviderForProductQuery.Checkout_getPaymentServiceProviderForProduct>() {
        @Override
        public void onSuccess(Checkout_getPaymentServiceProviderForProductQuery.Checkout_getPaymentServiceProviderForProduct response) {
            LogUtil.info("Product Payment Provider", response.toString());    
        }

        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", error.toString());
        }
    });
```

## Payment Detail

Every purchase with MySabay SDK has to be recorded in our network. Call function below before doing any purchase

```java
    MySabaySDK.getInstance().createPaymentDetail(pspId, items, amount, currency, new DataCallback<Object>() {
        @Override
        public void onSuccess(Object response) {
            LogUtil.info("Create PaymentDetail", response.toString());
        }

        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", error.toString());
        }
```

- Arguments:
    - `pspId - String`: Payment service provide id
    - `items - List<Object>)`: is list of product item
    - `pspId - double`: Amount of product
    - `currency`: is currency of product

## In App Purchase

To purchase with android in-app purchase, call below function. 

```java 
    MySabaySDK.getInstance().verifyInAppPurcahse(data, googleVerifyBody, new DataCallback<Object>() {
        @Override
        public void onSuccess(Object response) {
            LogUtil.info("Verify In app purchase", response.toString());
        }
        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", error.toString());
        }
```

- Arguments:
    - `data - Data`: data that get from CreatePaymentDetail
    - `googleVerifyBody - GoogleVerifyBody)`: get For handlePurchase of IAP android Library

## Pre Authorization Purchase 

To make purchase with pre-authorization provider, call this function 

```java 
    MySabaySDK.getInstance().postToChargePreAuth(data, new DataCallback<Object>() {
        @Override
        public void onSuccess(Object response) {
            LogUtil.info("purchase with pre-auth", response.toString());
        }
        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", error.toString());
        }
    }); 
```

- Arguments:
    - `data - Data`: data that get from CreatePaymentDetail

## One Time Purchase

To make purchase with one-time provider, you have to create [Payment Detail](#payment-detail). For detail instruction to process one-time payment, you can read [One-Time Instruction](). 

## Payment Status

When purchase is finished, you have to check status for invoice that you have created for payment. This is the final step to make sure that purchase is paid in our system. To check payment status, call this function.

```java 
   MySabaySDK.getInstance().checkPaymentStatus(data.invoiceId, new DataCallback<GetInvoiceByIdQuery.Invoice_getInvoiceById>() {
        @Override
        public void onSuccess(GetInvoiceByIdQuery.Invoice_getInvoiceById response) {
            LogUtil.info("Check Payment Status", response.toString());
        }
        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", error.toString());
        }
    }); 
```

Invoice status will take some time to update. To check payment status schedully, call this function.

```java 
  MySabaySDK.getInstance().scheduledCheckPaymentStatus(handler, invoiceId, interval, repeat, new DataCallback<GetInvoiceByIdQuery.Invoice_getInvoiceById>() {
            @Override
            public void onSuccess(GetInvoiceByIdQuery.Invoice_getInvoiceById response) {
            LogUtil.info("Check Payment Status", response.toString());
        }
        @Override
        public void onFailed(Object error) {
            LogUtil.info("Error", error.toString());
        }
    }); 
```

- Arguments:
    - `handler - Handler`: A Handler allows communicating back with UI thread from other background thread
    - `invoiceId - String`: is invoice id
    - `interval - long`: The number of milliseconds 
    - `repeat - long`: will execute handler every repeat time

>These will return a promise with invoice object with `paid` status that means purchase is successful.

## How Can I Implement Purchase With MySabay SDK?

To implement purchase with MySabay SDK, it will take some steps based on payment provider:

### In-app purchase

    1. Create payment detail
    2. Purchase with Google Play (Implement with BillingClient android)
    4. Verify purchase
    5. Check payment status

### Pre-authorization purchase

    1. Create payment detail
    2. Create pre-auth payment
    3. Check payment status

### One-time purchase

    1. Create payment detail
    2. Compose html template with payment detail
    3. Process payment on web
    4. Check payment status


