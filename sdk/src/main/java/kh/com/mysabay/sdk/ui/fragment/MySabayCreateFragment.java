package kh.com.mysabay.sdk.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mysabay.sdk.CheckExistingLoginQuery;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.R;
import kh.com.mysabay.sdk.base.BaseFragment;
import kh.com.mysabay.sdk.callback.DataCallback;
import kh.com.mysabay.sdk.databinding.FmCreateMysabayBinding;
import kh.com.mysabay.sdk.pojo.NetworkState;
import kh.com.mysabay.sdk.ui.activity.LoginActivity;
import kh.com.mysabay.sdk.utils.KeyboardUtils;
import kh.com.mysabay.sdk.utils.LogUtil;
import kh.com.mysabay.sdk.utils.MessageUtil;
import kh.com.mysabay.sdk.viewmodel.UserApiVM;
import kh.com.mysabay.sdk.webservice.Constant;

public class MySabayCreateFragment extends BaseFragment<FmCreateMysabayBinding, UserApiVM> {

    public static final String TAG = MySabayCreateFragment.class.getSimpleName();
    public static final String EXT_KEY_DATA = "EXT_KEY_DATA";
    private String mData;

    @NotNull
    @Contract(" -> new")
    public static MySabayCreateFragment newInstance(String pathFrom) {
        Bundle args = new Bundle();
        args.putString(EXT_KEY_DATA, pathFrom);
        MySabayCreateFragment f = new MySabayCreateFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null)
            mData = getArguments().getString(EXT_KEY_DATA);

        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fm_create_mysabay;
    }

    @Override
    public void initializeObjects(View v, Bundle args) {
        mViewBinding.viewMainRegister.setBackgroundResource(colorCodeBackground());
        this.viewModel = LoginActivity.loginActivity.viewModel;

        MySabaySDK.getInstance().trackPageView(getContext(), "/sdk/register-mysabay-screen", "/sdk/register-mysabay-screen");
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

        mViewBinding.edtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    isExistingLogin(v.getContext(), mViewBinding.edtUsername.getText().toString());
                }
            }
        });

        mViewBinding.btnCreateMysabay.setOnClickListener(v -> {
            MySabaySDK.getInstance().trackEvents(v.getContext(),"sdk-" + Constant.sso, Constant.tap, "register-mysabay");
            KeyboardUtils.hideKeyboard(getContext(), v);
            String username = mViewBinding.edtUsername.getText().toString();
            String password = mViewBinding.edtPassword.getText().toString();
            String confirmPassword = mViewBinding.edtConfirmPassword.getText().toString();
            if (StringUtils.isAnyBlank(username)) {
                showCheckFields(mViewBinding.edtUsername, R.string.msg_input_username);
            } else if (StringUtils.isAnyBlank(password)) {
                showCheckFields(mViewBinding.edtPassword, R.string.msg_input_password);
            } else if (StringUtils.isAnyBlank(confirmPassword)) {
                showCheckFields(mViewBinding.edtConfirmPassword, R.string.msg_input_confirm_password);
            } else if (!password.equals(confirmPassword)) {
                showCheckFields(mViewBinding.edtConfirmPassword, R.string.msg_confirm_password_not_match);
            }
            else {
                viewModel.checkExistingLogin(username, new DataCallback<CheckExistingLoginQuery.Data>() {
                    @Override
                    public void onSuccess(CheckExistingLoginQuery.Data response) {
                        if(response.sso_existingLogin()) {
                            showCheckFields(getContext(), mViewBinding.edtUsername, R.string.msg_username_already_exist);
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mData != null) {
                                        if (mData.equals(MySabayLoginConfirmFragment.TAG)) {
                                            viewModel.createMySabayWithPhoneOTP(v.getContext(), username, password);
                                            LogUtil.info("mdata", "not null");
                                        }
                                    } else {
                                        viewModel.createMySabayAccount(v.getContext(), username, password);
                                        LogUtil.info("mdata", "null");
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailed(Object error) {
                        MessageUtil.displayDialog(getContext(), "Check Existing Login Failed");
                    }
                });
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

    private void showCheckFields(Context context, AppCompatEditText view, int msg) {
        if (view != null) {
            YoYo.with(Techniques.Shake).duration(600).playOn(view);
        }
        if (this.isAdded())
            MessageUtil.displayToast(context, getString(msg));
    }

    private void isExistingLogin(Context context, String login) {
        viewModel.checkExistingLogin(login).enqueue(new ApolloCall.Callback<CheckExistingLoginQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<CheckExistingLoginQuery.Data> response) {
                if (response.getData() != null) {
                    if (response.getData().sso_existingLogin()) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                showCheckFields(context, mViewBinding.edtUsername, R.string.msg_username_already_exist);
                            }
                        });
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            MessageUtil.displayDialog(context, "Can't communicate with sersver");
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        MessageUtil.displayDialog(getContext(), "Check Existing Login Failed");
                    }
                });
            }
        });
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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((LoginActivity) context).userComponent.inject(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected Class<UserApiVM> getViewModel() {
        return null;
    }

    @Override
    protected void onOnlineCallback() {

    }
}
