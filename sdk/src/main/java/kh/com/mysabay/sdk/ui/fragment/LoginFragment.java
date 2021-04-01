package kh.com.mysabay.sdk.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.apollographql.apollo.ApolloClient;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import kh.com.mysabay.sdk.BuildConfig;
import kh.com.mysabay.sdk.Globals;
import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.R;
import kh.com.mysabay.sdk.adapter.CountryAdapter;
import kh.com.mysabay.sdk.base.BaseFragment;
import kh.com.mysabay.sdk.databinding.FragmentLoginBinding;
import kh.com.mysabay.sdk.pojo.NetworkState;
import kh.com.mysabay.sdk.ui.activity.LoginActivity;
import kh.com.mysabay.sdk.ui.holder.CountryItem;
import kh.com.mysabay.sdk.utils.CountryUtils;
import kh.com.mysabay.sdk.utils.KeyboardUtils;
import kh.com.mysabay.sdk.utils.LogUtil;
import kh.com.mysabay.sdk.utils.MessageUtil;
import kh.com.mysabay.sdk.utils.PhoneNumberFormat;
import kh.com.mysabay.sdk.viewmodel.UserApiVM;
import kh.com.mysabay.sdk.webservice.Constant;

/**
 * Created by Tan Phirum on 3/7/20
 * Gmail phirumtan@gmail.com
 */
public class LoginFragment extends BaseFragment<FragmentLoginBinding, UserApiVM> {

    public static final String TAG = LoginFragment.class.getSimpleName();
    private ArrayList<CountryItem> mCountryList;
    private CountryAdapter mAdapter;
    String dialCode;
    private FragmentManager mManager;
    private CallbackManager callbackManager;

    @Inject
    ApolloClient apolloClient;

    @NotNull
    @Contract(" -> new")
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void initializeObjects(View v, Bundle args) {
        mViewBinding.viewMainLogin.setBackgroundResource(colorCodeBackground());
        mViewBinding.tvMySabayAppName.setText(MySabaySDK.getInstance().getSdkConfiguration().mySabayAppName);
        mViewBinding.btnLogin.setTextColor(textColorCode());
        mViewBinding.fb.setTextColor(textColorCode());
        mViewBinding.btnLoginMysabay.setTextColor(textColorCode());
        mViewBinding.edtPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        this.onTaskCompleted();
        this.viewModel = LoginActivity.loginActivity.viewModel;

        mManager = getFragmentManager();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            LogUtil.info("TAG", "Username is: " + Profile.getCurrentProfile().getName());
            LogUtil.info("TAG", "Username is: " + accessToken.getToken());
        }
        callbackManager = CallbackManager.Factory.create();

        MySabaySDK.getInstance().trackPageView(getContext(), "/sdk/login-screen", "/sdk/login-screen");
    }

    @Override
    public void assignValues() {
        if (BuildConfig.DEBUG) {
            mViewBinding.edtPhone.setText("012808080");
        }
//        mViewBinding.edtPhone.requestFocus();
        new Handler().postDelayed(() -> showProgressState(new NetworkState(NetworkState.Status.SUCCESS)), 500);
    }

    @Override
    public void addListeners() {
        viewModel.liveNetworkState.observe(this, this::showProgressState);

        viewModel.login.observe(this, phone -> mViewBinding.edtPhone.setText(phone));
        mViewBinding.fb.setOnClickListener(v-> {
            MySabaySDK.getInstance().trackEvents(v.getContext(), "sdk-" + Constant.sso, Constant.tap, "login-with-facebook");
            mViewBinding.btnLoginFb.performClick();
        });

        mViewBinding.btnLogin.setOnClickListener(v -> {
            if (mViewBinding.edtPhone.getText() == null) return;

            MySabaySDK.getInstance().trackEvents(v.getContext(), "sdk-" + Constant.sso, Constant.tap, "login-with-phone-number");
            KeyboardUtils.hideKeyboard(getContext(), v);
            String phoneNo = mViewBinding.edtPhone.getText().toString();
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            if (StringUtils.isAnyBlank(phoneNo)) {
                showCheckFields(mViewBinding.edtPhone, R.string.msg_input_phone);
            } else if (StringUtils.isAnyBlank(dialCode)) {
               MessageUtil.displayToast(getContext(), R.string.msg_input_phone);
            } else {
                if (phoneNo.length() == 1) {
                    showCheckFields(mViewBinding.edtPhone, R.string.msg_phone_incorrect);
                } else {
                    Phonenumber.PhoneNumber phoneNumber = PhoneNumberFormat.validatePhoneNumber(dialCode, phoneNo);
                    boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
                    if (isValid) {
                        String phNumber = String.valueOf(phoneNumber.getNationalNumber());
                        String dialCode = String.valueOf(phoneNumber.getCountryCode());
                        viewModel.loginWithPhoneNumber(v.getContext(), phNumber, dialCode);
                    } else {
                        showCheckFields(mViewBinding.edtPhone, R.string.msg_phone_incorrect);
                    }
                }
            }
        });

        mViewBinding.btnLoginMysabay.setOnClickListener(v ->
            initAddFragment(MySabayLoginFragment.newInstance(), MySabayLoginFragment.TAG, true));
        mViewBinding.btnClose.setOnClickListener(v -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });
        mViewBinding.btnLoginFb.setReadPermissions("email");
        mViewBinding.btnLoginFb.setFragment(this);

        mViewBinding.btnLoginFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LogUtil.info("OnSuccess", loginResult.getAccessToken().getToken());
                viewModel.loginWithFacebook(getActivity(), loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                LogUtil.info("OnCancel", "Canceled");
            }

            @Override
            public void onError(FacebookException error) {
                LogUtil.info("OnError", error.getMessage());
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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

    private void showCheckFields(AppCompatEditText view, int msg) {
        if (view != null) {
            YoYo.with(Techniques.Shake).duration(600).playOn(view);
            view.requestFocus();
        }
        MessageUtil.displayToast(getContext(), getString(msg));
    }

    private void initList() {
        mCountryList = new ArrayList<CountryItem>();
        String jsonFileString = CountryUtils.getJsonFromAssets(getContext(), "countries.json");
        Gson gson = new Gson();
        Type countryTypes = new TypeToken<List<CountryItem>>() { }.getType();

        List<CountryItem> country = gson.fromJson(jsonFileString, countryTypes);

        for (int i = 0; i < country.size(); i++) {
            mCountryList.add(country.get(i));
        }
    }

    public void onTaskCompleted() {
            initList();
            Spinner spinnerCountries = mViewBinding.spinnerCountries;
            mAdapter = new CountryAdapter(getContext(), mCountryList);
            spinnerCountries.setAdapter(mAdapter);
            for (int i =0; i < mCountryList.size(); i++) {
                if (mCountryList.get(i).getCode().equals("KH")) {
                    spinnerCountries.setSelection(i);
                    dialCode = mCountryList.get(i).getDial_code();
                }
            }
            spinnerCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CountryItem clickedItem = (CountryItem) parent.getItemAtPosition(position);
                    dialCode = clickedItem.getDial_code();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
    }
}