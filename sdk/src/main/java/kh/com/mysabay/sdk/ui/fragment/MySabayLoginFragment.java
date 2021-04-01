package kh.com.mysabay.sdk.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import kh.com.mysabay.sdk.Globals;
import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.R;
import kh.com.mysabay.sdk.base.BaseFragment;
import kh.com.mysabay.sdk.databinding.FmLoginMysabayBinding;
import kh.com.mysabay.sdk.pojo.NetworkState;
import kh.com.mysabay.sdk.ui.activity.LoginActivity;
import kh.com.mysabay.sdk.utils.KeyboardUtils;
import kh.com.mysabay.sdk.utils.MessageUtil;
import kh.com.mysabay.sdk.viewmodel.UserApiVM;
import kh.com.mysabay.sdk.webservice.Constant;

public class MySabayLoginFragment extends BaseFragment<FmLoginMysabayBinding, UserApiVM> {

    public static final String TAG = MySabayLoginFragment.class.getSimpleName();
    private FragmentManager mManager;

    @NotNull
    @Contract(" -> new")
    public static MySabayLoginFragment newInstance() {
        return new MySabayLoginFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fm_login_mysabay;
    }

    @Override
    public void initializeObjects(View v, Bundle args) {
        mViewBinding.viewMainLogin.setBackgroundResource(colorCodeBackground());
        this.viewModel = LoginActivity.loginActivity.viewModel;
        mManager = getFragmentManager();

        MySabaySDK.getInstance().trackPageView(getContext(), "/sdk/login-mysabay-screen", "/sdk/login-mysabay-screen");
    }

    @Override
    public void assignValues() {
        new Handler().postDelayed(() -> showProgressState(new NetworkState(NetworkState.Status.SUCCESS)), 500);
    }

    @Override
    public void addListeners() {
        viewModel.liveNetworkState.observe(this, this::showProgressState);

        mViewBinding.btnClose.setOnClickListener(v -> {
            if (v.getContext() instanceof LoginActivity)
                getActivity().finish();
        });

        mViewBinding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });
        mViewBinding.tvRegisterAccount.setOnClickListener(v -> {
            initAddFragment(new MySabayCreateFragment(), MySabayCreateFragment.TAG, true);
        });
        mViewBinding.btnLogin.setOnClickListener(v -> {
            MySabaySDK.getInstance().trackEvents(v.getContext(), "sdk-" + Constant.sso, Constant.tap, "login-with-mysabay");
            KeyboardUtils.hideKeyboard(getContext(), v);
            String username = mViewBinding.edtUsername.getText().toString();
            String password = mViewBinding.edtPassword.getText().toString();
            if (StringUtils.isAnyBlank(username)) {
                showCheckFields(mViewBinding.edtUsername, R.string.msg_input_username);
            } else if (StringUtils.isAnyBlank(password)) {
                showCheckFields(mViewBinding.edtPassword, R.string.msg_input_password);
            } else {
                viewModel.loginWithMySabayAccount(v.getContext(), username, password);
            }
        });
    }

    private void showCheckFields(AppCompatEditText view, int msg) {
        if (view != null) {
            YoYo.with(Techniques.Shake).duration(600).playOn(view);
            view.requestFocus();
        }
        MessageUtil.displayToast(getContext(), getString(msg));
    }

    public void initAddFragment(Fragment f, String tag, boolean isBack) {
        Globals.initAddFragment(mManager, f, tag, isBack);
    }

    @Override
    public View assignProgressView() {
        return mViewBinding.viewEmpty.progressBar;
    }

    @Override
    public View assignEmptyView() {
        return mViewBinding.viewEmpty.viewRetry;
    }

    @Override
    protected Class<UserApiVM> getViewModel() {
        return null;
    }

    @Override
    protected void onOnlineCallback() {

    }
}
