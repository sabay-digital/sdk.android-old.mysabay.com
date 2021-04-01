package kh.com.mysabay.sdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import kh.com.mysabay.sdk.R;
import kh.com.mysabay.sdk.callback.OnRcvItemClick;
import kh.com.mysabay.sdk.pojo.mysabay.Data;
import kh.com.mysabay.sdk.pojo.mysabay.ProviderResponse;
import kh.com.mysabay.sdk.ui.holder.BankProviderVH;

/**
 * Created by Tan Phirum on 4/1/20
 * Gmail phirumtan@gmail.com
 */
public class BankProviderAdapter extends RecyclerView.Adapter<BankProviderVH> {

    private List<ProviderResponse> dataList;
    private LayoutInflater mInflater;
    private OnRcvItemClick mListener;
    private Context mContext;

    public BankProviderAdapter(Context context, List<ProviderResponse> dataList, OnRcvItemClick listener) {
        this.mContext = context;
        this.dataList = dataList;
        this.mInflater = LayoutInflater.from(context);
        this.mListener = listener;
    }

    @NonNull
    @Override
    public BankProviderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BankProviderVH(mInflater.inflate(R.layout.partial_bank_provider_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BankProviderVH holder, int position) {
        ProviderResponse item = dataList.get(position);
        holder.setBankName(item.name);
        holder.showBankIcon(mContext, item.info.logo);
        holder.setBonus(item.label);
        holder.view.viewBankItem.setOnClickListener(v -> {
            if (mListener != null) mListener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
