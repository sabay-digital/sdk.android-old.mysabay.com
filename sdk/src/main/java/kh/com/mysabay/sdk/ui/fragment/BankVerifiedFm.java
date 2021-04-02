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

import org.apache.commons.lang3.StringUtils;
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
import kh.com.mysabay.sdk.utils.MessageUtil;
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
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public int getLayoutId() {
        return R.layout.partial_bank_provider_verified;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initializeObjects(View v, Bundle args) {
        viewModel.setShopItemSelected(mData);
        checkPaymentStatus(mTimeLeftInMillis, delay);
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

            LogUtil.info("request url", mPaymentResponseItem.requestUrl + paymentAddress);

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

    public void checkPaymentStatus(long interval, long repeat) {
        startTimer(interval);
        scheduledCheckPaymentStatus(getContext(),handler, invoiceId, interval, repeat);
    }

    public void scheduledCheckPaymentStatus(Context context, Handler handler, String invoiceId, long interval, long repeat) {
        viewModel.scheduledCheckPaymentStatus(context, handler, invoiceId, interval, repeat, new DataCallback<InvoiceItemResponse>() {
            @Override
            public void onSuccess(InvoiceItemResponse response) {
                LogUtil.info("InvoiceItemResponse", response.toString());
                if(!StringUtils.isEmpty(response.ssnTxHash)) {
                    mCountDownTimer.cancel();
                    handler.removeCallbacksAndMessages(null);
                    getActivity().finish();
                }
            }

            @Override
            public void onFailed(Object error) {
               LogUtil.info("Error", error.toString());
            }
        });
    }

    private void startTimer(long interval) {
        mEndTime = System.currentTimeMillis() + interval;
        mCountDownTimer = new CountDownTimer(interval, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                showFailDialog();
            }
        }.start();
        mTimerRunning = true;
        showFailDialog();
    }

    private void updateCountDownText() {
        if (mTimerRunning) {
            mViewBinding.tvTimer.setText("This payment window will be closed in " + mTimeLeftInMillis / 1000 + "s");
        }
    }

    private void showFailDialog() {
        if (!mTimerRunning) {
            MessageUtil.displayDialog(getContext(), "Sorry, we were unable to process your payment.", colorCodeBackground(), getString(R.string.ok), (dialog, which) -> {
                getActivity().finish();
            });
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
}