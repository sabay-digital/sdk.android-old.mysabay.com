package kh.com.mysabay.sample;

import android.databinding.DataBindingUtil;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.mysabay.sdk.GetInvoiceByIdQuery;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import kh.com.mysabay.sample.databinding.ActivityWebViewBinding;
import kh.com.mysabay.sdk.BuildConfig;
import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.callback.DataCallback;
import kh.com.mysabay.sdk.pojo.onetime.OneTime;
import kh.com.mysabay.sdk.pojo.thirdParty.payment.Data;
import kh.com.mysabay.sdk.utils.LogUtil;
import kh.com.mysabay.sdk.utils.MessageUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebViewActivity extends AppCompatActivity {

    private ActivityWebViewBinding mViewBinding;

    private Data mPaymentResponseItem;
    private boolean isFinished = false;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;

    private static final long START_TIME_IN_MILLIS = 120000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private long mEndTime;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);
        if(getIntent().getExtras() != null) {
            mPaymentResponseItem = (Data) getIntent().getParcelableExtra("DATA");
            LogUtil.info("mPaymentResponseItem", mPaymentResponseItem.paymentAddress);
        }

        startTimer();
        scheduledCheckPaymentStatus(handler, mPaymentResponseItem.invoiceId, mTimeLeftInMillis, delay);
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
            mViewBinding.wv.setWebViewClient(new WebViewClient());

            LogUtil.info("request url", mPaymentResponseItem.requestUrl + mPaymentResponseItem.paymentAddress);

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
                    .url(mPaymentResponseItem.requestUrl + mPaymentResponseItem.paymentAddress)
                    .post(formBody)
                    .build();

            OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @NotNull
                @Override
                public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
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
                            mViewBinding.wv.clearCache(true);
                            mViewBinding.wv.loadDataWithBaseURL(mPaymentResponseItem.requestUrl, htmlString, "text/html", "utf-8", null);
                        }
                    });
                }
            });

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
            mViewBinding.tvTimer.setText("This payment window will be closed in " + mTimeLeftInMillis / 1000 + "s");
        }
    }

    private void updateButtons() {
        if (!mTimerRunning) {
            MessageUtil.displayDialog(this, getString(R.string.sorry_we_were_unable_to_process_your_payment), R.color.colorWhite, getString(kh.com.mysabay.sdk.R.string.ok), (dialog, which) -> {
                finish();
            });
        }

    }

    public void scheduledCheckPaymentStatus(Handler handler, String invoiceId, long interval, long repeat) {
        MySabaySDK.getInstance().scheduledCheckPaymentStatus(handler, invoiceId, interval, repeat, new DataCallback<GetInvoiceByIdQuery.Invoice_getInvoiceById>() {
            @Override
            public void onSuccess(GetInvoiceByIdQuery.Invoice_getInvoiceById response) {
                LogUtil.info("Invoice-Response", response.toString());
                if(!response.ssnTxHash().isEmpty()) {
                    mCountDownTimer.cancel();
                    handler.removeCallbacksAndMessages(null);
                    finish();
                }
            }

            @Override
            public void onFailed(Object error) {
                MessageUtil.displayDialog(WebViewActivity.this, error.toString());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
