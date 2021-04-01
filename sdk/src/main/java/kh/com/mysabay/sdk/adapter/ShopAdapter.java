package kh.com.mysabay.sdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.LinkedList;
import java.util.List;

import kh.com.mysabay.sdk.R;
import kh.com.mysabay.sdk.callback.ShopListener;
import kh.com.mysabay.sdk.pojo.shop.Data;
import kh.com.mysabay.sdk.pojo.shop.ShopItem;
import kh.com.mysabay.sdk.ui.holder.ShopItmVH;

/**
 * Created by Tan Phirum on 3/13/20
 * Gmail phirumtan@gmail.com
 */
public class ShopAdapter extends RecyclerView.Adapter<ShopItmVH> {

    private static final String TAG = ShopAdapter.class.getSimpleName();

    private Context context;
    private LinkedList<ShopItem> shopItems;
    private ShopListener mListener;

    public ShopAdapter(Context context, ShopListener listener) {
        this.context = context;
        this.mListener = listener;
        this.shopItems = new LinkedList<>();
    }

    @NonNull
    @Override
    public ShopItmVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShopItmVH(LayoutInflater.from(context).inflate(R.layout.partial_shop_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopItmVH holder, int position) {
        holder.viewBinding.setItem(getItem(position));
    }

    @Override
    public int getItemCount() {
        return shopItems.size();
    }

    public void insert(ShopItem item) {
        shopItems.add(item);
        //notifyItemInserted(shopItems.size());
    }

    public void insert(List<ShopItem> items) {
        shopItems.addAll(items);
        notifyDataSetChanged();
    }

    public ShopItem getItem(int pos) {
        return shopItems.get(pos);
    }

    public void clear() {
        shopItems.clear();
    }
}
