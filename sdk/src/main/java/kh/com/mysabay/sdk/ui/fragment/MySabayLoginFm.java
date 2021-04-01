package kh.com.mysabay.sdk.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.R;
import kh.com.mysabay.sdk.base.BaseFragment;
import kh.com.mysabay.sdk.databinding.FmMysabayLoginBinding;
import kh.com.mysabay.sdk.pojo.AppItem;
import kh.com.mysabay.sdk.ui.activity.LoginActivity;
import kh.com.mysabay.sdk.utils.LogUtil;
import kh.com.mysabay.sdk.viewmodel.UserApiVM;
import kh.com.mysabay.sdk.webservice.Constant;

/**
 * Created by Tan Phirum on 3/10/20
 * Gmail phirumtan@gmail.com
 */
public class MySabayLoginFm extends BaseFragment<FmMysabayLoginBinding, UserApiVM> {

    public static final String TAG = MySabayLoginFm.class.getSimpleName();
    private static final String EXT_KEY_DEEPLINK = "EXT_KEY_DEEPLINK";
    private String mDeepLink;

    public static MySabayLoginFm newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(EXT_KEY_DEEPLINK, url);
        MySabayLoginFm f = new MySabayLoginFm();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null)
            mDeepLink = getArguments().getString(EXT_KEY_DEEPLINK);

        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fm_mysabay_login;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initializeObjects(View v, Bundle args) {
        WebView.setWebContentsDebuggingEnabled(true);
        mViewBinding.wv.getSettings().setJavaScriptEnabled(true);
        mViewBinding.wv.getSettings().setLoadsImagesAutomatically(true);
        mViewBinding.wv.getSettings().setLoadWithOverviewMode(true);
        mViewBinding.wv.getSettings().setUseWideViewPort(true);
        mViewBinding.wv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mViewBinding.wv.getSettings().setSupportZoom(true);
        mViewBinding.wv.getSettings().setBuiltInZoomControls(true);
        mViewBinding.wv.getSettings().setDisplayZoomControls(false);
        mViewBinding.wv.clearHistory();
        mViewBinding.wv.clearCache(true);
        mViewBinding.wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 60)
                    mViewBinding.progressBar.setVisibility(View.GONE);
            }
        });
        mViewBinding.wv.setWebViewClient(new WebViewClient() {
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                LogUtil.info("Scheme URL", request.getUrl().toString());
                LogUtil.debug(TAG, "url =" + request.getUrl() + " token =" + request.getUrl().getQueryParameter("access_token"));
                String token = request.getUrl().getQueryParameter("access_token");
                String refreshToken = request.getUrl().getQueryParameter("refresh_token");
                String expire = request.getUrl().getQueryParameter("expire");

                if (!StringUtils.isBlank(token) && getActivity() != null) {
                    if (!StringUtils.isBlank(token) && !StringUtils.isBlank(refreshToken)) {
                        AppItem appItem = new AppItem(null, null, token, refreshToken, "", Long.parseLong(expire));
                        String encrypted = gson.toJson(appItem);
                        MySabaySDK.getInstance().saveAppItem(encrypted);
                    }
                    getActivity().runOnUiThread(() -> viewModel.getUserProfile(getActivity(), token));
                }

                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mViewBinding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtil.info("URL", url);
                mViewBinding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void assignValues() {
        if (StringUtils.isBlank(mDeepLink)) {
            Map<String, String> header = new HashMap<>();
            header.put("app_secret", MySabaySDK.getInstance().appSecret());
            mViewBinding.wv.loadUrl(MySabaySDK.getInstance().userApiUrl() + Constant.mySabayUrl, header);
        } else
            mViewBinding.wv.loadUrl(mDeepLink);
    }

    @Override
    public void addListeners() {

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
        return UserApiVM.class;
    }

    @Override
    protected void onOnlineCallback() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((LoginActivity) context).userComponent.inject(this);
        // Now you can access loginViewModel here and onCreateView too
        // (shared instance with the Activity and the other Fragment)
    }
}