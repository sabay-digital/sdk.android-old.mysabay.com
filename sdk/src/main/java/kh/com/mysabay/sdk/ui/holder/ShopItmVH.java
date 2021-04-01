package kh.com.mysabay.sdk.ui.holder;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import kh.com.mysabay.sdk.MySabaySDK;
import kh.com.mysabay.sdk.R;
import kh.com.mysabay.sdk.callback.ShopListener;
import kh.com.mysabay.sdk.databinding.PartialShopItemBinding;
import kh.com.mysabay.sdk.pojo.AppItem;
import kh.com.mysabay.sdk.ui.activity.StoreActivity;
import kh.com.mysabay.sdk.ui.fragment.PaymentFm;
import kh.com.mysabay.sdk.utils.LogUtil;
import kh.com.mysabay.sdk.utils.SdkTheme;

/**
 * Created by Tan Phirum on 3/13/20
 * Gmail phirumtan@gmail.com
 */
public class ShopItmVH extends RecyclerView.ViewHolder implements View.OnClickListener {

    public PartialShopItemBinding viewBinding;
    private ShopListener mListener;

    public ShopItmVH(@NonNull View itemView, ShopListener sListener) {
        super(itemView);
        this.viewBinding = DataBindingUtil.bind(itemView);
        this.mListener = sListener;
        if (this.viewBinding != null) {
            this.viewBinding.card.setBackgroundResource(MySabaySDK.getInstance().getSdkConfiguration().sdkTheme == SdkTheme.Dark ?
                    R.color.colorBackground : R.color.colorWhite);
            this.viewBinding.card.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(@NotNull View v) {
        if (v.getId() == R.id.card) {
            if (v.getContext() instanceof StoreActivity) {
                AppItem item = new Gson().fromJson(MySabaySDK.getInstance().getAppItem(), AppItem.class);
                LogUtil.info("enableLocalPay", item.enableLocalPay + "");
                if (item.enableLocalPay)
                    ((StoreActivity) v.getContext()).initAddFragment(PaymentFm.newInstance(viewBinding.getItem()), PaymentFm.TAG, true);
                else
                    mListener.shopInfo(viewBinding.getItem());
            }
        }
    }
}
