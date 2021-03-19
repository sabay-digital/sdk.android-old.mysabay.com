package kh.com.mysabay.sdk.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import javax.inject.Inject;
import kh.com.mysabay.sdk.BuildConfig;
import kh.com.mysabay.sdk.R;
import kh.com.mysabay.sdk.base.BaseFragment;
import kh.com.mysabay.sdk.callback.DataCallback;
import kh.com.mysabay.sdk.databinding.PartialBankProviderVerifiedBinding;
import kh.com.mysabay.sdk.pojo.invoice.InvoiceItemResponse;
import kh.com.mysabay.sdk.pojo.onetime.OneTime;
import kh.com.mysabay.sdk.pojo.shop.ShopItem;
import kh.com.mysabay.sdk.pojo.thirdParty.payment.Data;
import kh.com.mysabay.sdk.ui.activity.StoreActivity;
import kh.com.mysabay.sdk.utils.LogUtil;
import kh.com.mysabay.sdk.viewmodel.StoreApiVM;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Tan Phirum on 4/1/20
 * Gmail phirumtan@gmail.com
 */
public class BankVerifiedFm extends BaseFragment<PartialBankProviderVerifiedBinding, StoreApiVM> {

    public static final String TAG = BankVerifiedFm.class.getSimpleName();
    private static final String EXT_KEY_PaymentResponseItem = "PaymentResponseItem";
    public static final String EXT_KEY_DATA = "EXT_KEY_DATA";
    public static final String PSP_CODE = "EXT_PSP_CODE";
    public static final String PAYMENT_ADDRESS = "PAYMENT_ADDRESS";
    public static final String INVOICE_ID = "INVOICE_ID";

    private ShopItem mData;
    private Data mPaymentResponseItem;
    private String paymentAddress;
    private String invoiceId;
    private boolean isFinished = false;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;

    private static final long START_TIME_IN_MILLIS = 120000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private long mEndTime;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;
    @Inject
    Gson gson;
    private OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    @NotNull
    public static BankVerifiedFm newInstance(Data item, ShopItem shopItem, String paymentAddress, String invoiceId) {
        Bundle args = new Bundle();
        args.putParcelable(EXT_KEY_PaymentResponseItem, item);
        args.putParcelable(EXT_KEY_DATA, shopItem);
        args.putString(PAYMENT_ADDRESS, paymentAddress);
        args.putString(INVOICE_ID, invoiceId);
        BankVerifiedFm f = new BankVerifiedFm();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.info("Re-Work", "Bank Verify");
        if (getArguments() != null) {
            mPaymentResponseItem = getArguments().getParcelable(EXT_KEY_PaymentResponseItem);
            mData = getArguments().getParcelable(EXT_KEY_DATA);
            paymentAddress = getArguments().getString(PAYMENT_ADDRESS);
            invoiceId = getArguments().getString(INVOICE_ID);
        }
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public int getLayoutId() {
        return R.layout.partial_bank_provider_verified;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initializeObjects(View v, Bundle args) {
        viewModel.setShopItemSelected(mData);
        startTimer();
        checkInvoiceIdTimeout();
        if (args != null) {
            mViewBinding.wv.restoreState(args);
        } else {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
            mViewBinding.wv.getSettings().setJavaScriptEnabled(true);
            mViewBinding.wv.addJavascriptInterface(new OneTime(), "onetime");
            mViewBinding.wv.getSettings().setLoadsImagesAutomatically(true);
            mViewBinding.wv.getSettings().setLoadWithOverviewMode(true);
            mViewBinding.wv.getSettings().setUseWideViewPort(true);
            mViewBinding.wv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            mViewBinding.wv.getSettings().setSupportZoom(true);
            mViewBinding.wv.getSettings().setBuiltInZoomControls(true);
            mViewBinding.wv.getSettings().setDisplayZoomControls(false);
            mViewBinding.wv.getSettings().setDomStorageEnabled(true);
            mViewBinding.wv.getSettings().setMinimumFontSize(1);
            mViewBinding.wv.getSettings().setMinimumLogicalFontSize(1);
            mViewBinding.wv.clearHistory();
            mViewBinding.wv.clearCache(true);

            mViewBinding.viewWeb.setBackgroundResource(colorCodeBackground());
            mViewBinding.wv.setWebViewClient(new WebViewClient());

            FormBody.Builder formBuilder = new FormBody.Builder()
                    .add("hash", mPaymentResponseItem.hash)
                    .add("signature", mPaymentResponseItem.signature)
                    .add("public_key", mPaymentResponseItem.publicKey);

            if(mPaymentResponseItem.additionalBody != null) {
                try {
                    JSONObject jsonObject = new JSONObject(mPaymentResponseItem.additionalBody.toString());
                    Iterator x = jsonObject.keys();
                    while (x.hasNext()){
                        String key = (String) x.next();
                        formBuilder.add(key, jsonObject.get(key).toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .url(mPaymentResponseItem.requestUrl + paymentAddress)
                    .post(formBody)
                    .build();

            OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @NotNull
                @Override
                public Response intercept(@NotNull Chain chain) throws IOException {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    if (mPaymentResponseItem.additionalHeader != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(mPaymentResponseItem.additionalHeader.toString());
                            Iterator x = jsonObject.keys();
                            while (x.hasNext()){
                                String key = (String) x.next();
                                requestBuilder.addHeader(key, jsonObject.get(key).toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return chain.proceed(requestBuilder.build());
                }
            });


            okHttpClient.build().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.info("Error", e.getMessage());
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String htmlString = response.body().string();
                    LogUtil.info("Success", htmlString);
                    mViewBinding.wv.post(new Runnable() {
                        @Override
                        public void run() {
                            mViewBinding.waitingView.progressBar.setVisibility(View.GONE);
                            mViewBinding.wv.clearCache(true);
                            mViewBinding.wv.loadDataWithBaseURL(mPaymentResponseItem.requestUrl, htmlString, "text/html", "utf-8", null);
                        }
                    });
                }
            });

        }
    }

    @Override
    public void assignValues() {
        mViewBinding.waitingView.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void addListeners() {
        mViewBinding.btnBack.setOnClickListener(v -> {
            if (mViewBinding.wv.canGoBack())
                mViewBinding.wv.goBack();
            else {
                if (getActivity() != null)
                    getActivity().onBackPressed();
            }
        });

        mViewBinding.btnClose.setOnClickListener(v -> {
            if (getActivity() != null)
                getActivity().finish();
        });
    }

    @Override
    public View assignProgressView() {
        return null;
    }

    @Override
    public View assignEmptyView() {
        return null;
    }

    @Override
    protected Class getViewModel() {
        return StoreApiVM.class;
    }

    @Override
    protected void onOnlineCallback() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((StoreActivity) context).userComponent.inject(this);
        // Now you can access loginViewModel here and onCreateView too
        // (shared instance with the Activity and the other Fragment)
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mViewBinding.wv.saveState(outState);

    }

    public void getInvoiceId(String id) {
        viewModel.getInvoiceById(getContext(), id, new DataCallback<InvoiceItemResponse>() {
            @Override
            public void onSuccess(InvoiceItemResponse response) {
                LogUtil.info("Invoice Response", response.toString());
            }

            @Override
            public void onFailed(Object error) {
                LogUtil.info("Error", error.toString());
            }
        });
    }

    public void checkInvoiceIdTimeout() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                if(mTimerRunning) {
                    LogUtil.info("InvoiceId", invoiceId);
                    getInvoiceId(invoiceId);
                    handler.postDelayed(runnable, delay);
                } else {
                    handler.removeCallbacks(runnable);
                }
            }
        }, delay);
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateButtons();
            }
        }.start();
        mTimerRunning = true;
        updateButtons();
    }

    private void updateCountDownText() {
        if (mTimerRunning) {
            mViewBinding.tvTimer.setText("You have 1 minute to purchase " + mTimeLeftInMillis / 1000 + "s");
        } else {
            mViewBinding.tvTimer.setText("Didn’t get OTP code? request again in ");
        }
    }

    private void updateButtons() {
        if (!mTimerRunning) {
            getActivity().finish();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @NotNull
    @Contract(pure = true)
    private String scriptFormValidate(@NotNull Data item) {
        LogUtil.info("request url", item.requestUrl + paymentAddress);
        LogUtil.info("hash", item.hash);
        LogUtil.info("signature", item.signature);
        LogUtil.info("publicKey", item.publicKey);
        LogUtil.info("paymentAddress", paymentAddress);
        String additionalBody = "";
        try {
            JSONObject jsonObject = new JSONObject(item.additionalBody.toString());
            Iterator x = jsonObject.keys();
            while (x.hasNext()){
                String key = (String) x.next();
                if (jsonObject.get(key) instanceof JSONObject) {
                    JSONObject subJson =(JSONObject) jsonObject.get(key);
                    String inputValue = "{";
                    Iterator itr = subJson.keys();
                        while (itr.hasNext()){
                            String subFieldKey = (String) itr.next();
                            LogUtil.info("subFieldKey", itr.hasNext() + "");
                            String endTag = itr.hasNext() ? "&quot;," : "&quot;";
                            inputValue += "&quot;"+subFieldKey+"&quot;: &quot;" + subJson.get(subFieldKey).toString() + endTag;
                            if (!itr.hasNext()) {
                                inputValue += "}";
                            }
                        }
                    LogUtil.info("Form", inputValue);
                    additionalBody +=  " <input type=\"hidden\" name=\"" + key + "\" value=\"" + inputValue + "\">\n" ;
                } else {
                    additionalBody +=  " <input type=\"hidden\" name=\"" + key + "\" value=\"" + jsonObject.get(key).toString() + "\">\n" ;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bulma@0.8.0/css/bulma.min.css\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Please Wait</h1>\n" +
                "    <form id=\"frm\" action=\"" + item.requestUrl + paymentAddress + "\" method=\"post\">\n" +
                "        <input type=\"hidden\" name=\"hash\" value=\"" + item.hash + "\">\n" +
                "        <input type=\"hidden\" name=\"signature\" value=\"" + item.signature + "\">\n" +
                "        <input type=\"hidden\" name=\"public_key\" value=\"" + item.publicKey + "\">\n" +
                         additionalBody  +
                "    </form>\n" +
                "    <script>\n" +
                "       document.getElementById('frm').submit()\n" +
                "    </script>\n" +
                "\u200B\n" +
                "</body>\n" +
                "</html>";
    }
}