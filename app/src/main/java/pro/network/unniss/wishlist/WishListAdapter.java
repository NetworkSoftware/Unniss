package pro.network.unniss.wishlist;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import pro.network.unniss.R;
import pro.network.unniss.app.AppConfig;
import pro.network.unniss.product.ProductListBean;

import static pro.network.unniss.app.AppConfig.decimalFormat;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.MyViewHolder> {

    private Context mainActivityUser;
    private List<ProductListBean> productBeans;
    public WishListClick wishListClick;
    int selectedPosition = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView product_image;
        private TextView product_name, product_price, product_total_price,
                removeWishList, moveTobag,size;

        public MyViewHolder(View view) {
            super((view));
            product_image = (ImageView) view.findViewById(R.id.product_image);
            product_name = (TextView) view.findViewById(R.id.product_name);
            product_price = view.findViewById(R.id.product_price);
            product_total_price = view.findViewById(R.id.product_total_price);
            removeWishList = view.findViewById(R.id.removeWishList);
            moveTobag = view.findViewById(R.id.moveTobag);
            size = view.findViewById(R.id.size);
        }
    }

    public WishListAdapter(Context mainActivityUser, List<ProductListBean> productBeans, WishListClick wishListClick) {
        this.mainActivityUser = mainActivityUser;
        this.productBeans = productBeans;
        this.wishListClick = wishListClick;
    }

    public void notifyData(List<ProductListBean> productBeans) {
        this.productBeans = productBeans;
        notifyDataSetChanged();
    }

    public void notifyData(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_wishlist_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ProductListBean productBean = productBeans.get(position);

        holder.product_name.setText((Html.fromHtml(productBean.getBrand())) + " - " + (Html.fromHtml(productBean.getModel())));
        holder.size.setText(productBean.getSize());
        try {
            ArrayList<String> urls = new Gson().fromJson(productBean.image, (Type) List.class);
            Glide.with(mainActivityUser)
                    .load(AppConfig.getResizedImage(urls.get(0), true))
                    .into(holder.product_image);
        } catch (Exception e) {
            Log.e("xxxxxxxxxxx", e.toString());
        }

        String qty = productBean.qty;
        if (qty == null || qty.length() <= 0) {
            qty = "1";
        }
        float total = Float.parseFloat(productBean.price) * Integer.parseInt(qty);
        holder.product_total_price.setText(qty + " * " + (productBean.price));
        holder.product_price.setText("₹" + decimalFormat.format(total) + ".00");
        holder.removeWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wishListClick.ondeleteClick(position);
            }
        });
        holder.moveTobag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wishListClick.onMoveToBag(position);
            }
        });
    }

    public int getItemCount() {
        return productBeans.size();
    }

}


