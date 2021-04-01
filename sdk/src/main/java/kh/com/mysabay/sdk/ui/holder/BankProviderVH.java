package kh.com.mysabay.sdk.ui.holder;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import kh.com.mysabay.sdk.R;
import kh.com.mysabay.sdk.databinding.PartialBankProviderItemBinding;

/**
 * Created by Tan Phirum on 4/1/20
 * Gmail phirumtan@gmail.com
 */
public class BankProviderVH extends RecyclerView.ViewHolder {

    public PartialBankProviderItemBinding view;

    public BankProviderVH(@NonNull View itemView) {
        super(itemView);
        view = DataBindingUtil.bind(itemView);
    }

    public void setBankName(String bankName) {
        view.bankName.setText(bankName);
    }

    public void setBonus(String bonus) {
        view.tvLabel.setText(bonus);
    }

    public void showBankIcon(Context context, String url) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.other_payment_option)
                .error(R.mipmap.other_payment_option)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(view.appCompatImageView2);
    }
}
