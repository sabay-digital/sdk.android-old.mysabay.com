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
import kh.com.mysabay.sample.databinding.ActivityWebViewBinding;
import kh.com.mysabay.sdk.BuildConfig;
import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.callback.DataCallback;
import kh.com.mysabay.sdk.pojo.thirdParty.payment.Data;
import kh.com.mysabay.sdk.utils.LogUtil;
import kh.com.mysabay.sdk.utils.MessageUtil;
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

        MySabaySDK.getInstance().postToChargeWithOneTime(mPaymentResponseItem, new DataCallback<String>() {
            @Override
            public void onSuccess(String response) {
                mViewBinding.wv.post(new Runnable() {
                    @Override
                    public void run() {
                        mViewBinding.wv.clearCache(true);
                        mViewBinding.wv.loadDataWithBaseURL(mPaymentResponseItem.requestUrl, response, "text/html", "utf-8", null);
                    }
                });
            }

            @Override
            public void onFailed(Object error) {
                MessageUtil.displayDialog(WebViewActivity.this, error.toString());
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
