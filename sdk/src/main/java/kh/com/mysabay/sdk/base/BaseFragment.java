package kh.com.mysabay.sdk.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import android.databinding.DataBindingUtil;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.R;
import kh.com.mysabay.sdk.pojo.NetworkState;
import kh.com.mysabay.sdk.utils.IdlingResourceHelper;
import kh.com.mysabay.sdk.utils.MessageUtil;
import kh.com.mysabay.sdk.utils.SdkTheme;

/**
 * Created by Tan Phirum on 3/4/20
 * Gmail phirumtan@gmail.com
 */
public abstract class BaseFragment<D extends ViewDataBinding, V extends ViewModel> extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @LayoutRes
    public abstract int getLayoutId();

    public abstract void initializeObjects(View v, Bundle args);

    public abstract void assignValues();

    public abstract void addListeners();

    public abstract View assignProgressView();

    public abstract View assignEmptyView();

    protected abstract Class<V> getViewModel();

    protected D mViewBinding;

    protected V viewModel;

    /**
     * This method will when online button was click
     */
    protected abstract void onOnlineCallback();

    public Gson gson;

    protected IdlingResourceHelper mIdlingResourceHelper;

    private boolean mIsNetworkRegistered;
    private BroadcastReceiver mNetworkReceiver;

    public boolean isHasLoadedOnce = false;

    public BaseFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getViewModel() != null)
            viewModel = ViewModelProviders.of(this, viewModelFactory).get(getViewModel());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(getTitleResId());
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        gson = new Gson();
        mViewBinding = DataBindingUtil.bind(view);
        initializeObjects(view, savedInstanceState);

     /*   if (view.findViewById(R.id.btn_retry) != null)
            view.findViewById(R.id.btn_retry).setOnClickListener(this::onRetryFetchItem);*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assignValues();
        addListeners();
    }

    @Override
    public void onResume() {
        if (!mIsNetworkRegistered)
            onRegisterReceiver();
        /*if (getActivity() != null)
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mNetworkReceiver,
                    new IntentFilter(NetworkRequestUtils.ACTION_REQUEST_INTERNET_RECONNECTION));*/

        super.onResume();
    }

    @Override
    public void onStop() {
        if (mIsNetworkRegistered && getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mNetworkReceiver);
            mIsNetworkRegistered = false;
        }
        super.onStop();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (isVisibleToUser && !isHasLoadedOnce) {
                isHasLoadedOnce = true;
                onFragmentVisibility();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //init method to receive internet behavior
    private void onRegisterReceiver() {
        mIsNetworkRegistered = true;
        mNetworkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    /*if (StringUtils.equalsIgnoreCase(intent.getAction(),
                            NetworkRequestUtils.ACTION_REQUEST_INTERNET_RECONNECTION))
                        onOnlineCallback();*/
                }
            }
        };
    }

    public void onFragmentVisibility() {

    }

    /**
     * implement this method for handle empty button click
     *
     * @param v button
     */
    public void onRetryFetchItem(View v) {
        MessageUtil.displayToast(getContext(), "retry click hye");
    }

    /**
     * Only called from test, creates and returns a new {@link IdlingResourceHelper}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResourceHelper == null)
            mIdlingResourceHelper = new IdlingResourceHelper();
        return mIdlingResourceHelper;
    }

    protected void showProgressState(@NotNull NetworkState initialLoadState) {
        switch (initialLoadState.status()) {
            case NetworkState.Status.SUCCESS:
                showProgress(false);
                break;
            case NetworkState.Status.ERROR:
                showProgress(false);
                break;
            case NetworkState.Status.LOADING:
            default:
                showProgress(true);
                break;
        }
    }

    private void showProgress(boolean isShow) {
        if (assignProgressView() == null)
            throw new IllegalArgumentException("view progress can't be null");
        assignProgressView().setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void showEmptyView(boolean isShow) {
        if (assignEmptyView() == null)
            throw new IllegalArgumentException("view Empty can't be null");
        assignEmptyView().setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public int textColorCode() {
        int colorCode;
        if (MySabaySDK.getInstance().getSdkConfiguration().sdkTheme == SdkTheme.Light) {
            colorCode = 0xFFFFFFFF;
        } else
            colorCode = 0xFF000000;
        return colorCode;
    }

    public int colorCodeBackground() {
        int colorCode;
        if (MySabaySDK.getInstance().getSdkConfiguration().sdkTheme == SdkTheme.Light) {
            colorCode = R.color.colorWhite;
        } else
            colorCode = R.color.colorBackground;
        return colorCode;
    }
}
