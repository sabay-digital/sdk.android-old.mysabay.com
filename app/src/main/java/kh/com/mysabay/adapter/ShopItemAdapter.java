package kh.com.mysabay.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import kh.com.mysabay.sdk.R;
import kh.com.mysabay.sdk.callback.OnRcvItemClick;
import kh.com.mysabay.sdk.callback.ShopListener;
import kh.com.mysabay.sdk.databinding.PartialShopItemBinding;
import kh.com.mysabay.sdk.pojo.mysabay.Data;
import kh.com.mysabay.sdk.pojo.mysabay.ProviderResponse;
import kh.com.mysabay.sdk.pojo.shop.ShopItem;
import kh.com.mysabay.sdk.ui.holder.BankProviderVH;
import kh.com.mysabay.sdk.ui.holder.ShopItmVH;

public class ShopItemAdapter extends RecyclerView.Adapter<ShopItmVH> {

    private List<ShopItem> dataList;
    private LayoutInflater mInflater;
    private ShopListener mListener;
    private Context mContext;

    public ShopItemAdapter(Context context, List<ShopItem> dataList, ShopListener listener) {
        this.mContext = context;
        this.dataList = dataList;
        this.mInflater = LayoutInflater.from(context);
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ShopItmVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShopItmVH(LayoutInflater.from(mContext).inflate(R.layout.partial_shop_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopItmVH holder, int position) {
        ShopItem item = dataList.get(position);
        holder.viewBinding.setItem(item);
        holder.viewBinding.card.setOnClickListener(v -> {
            if (mListener != null) mListener.shopInfo(item);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

