package pro.network.unniss.product;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import pro.network.unniss.R;
import pro.network.unniss.app.AppConfig;
import pro.network.unniss.app.DbCart;


public class ProductAdapterGird extends RecyclerView.Adapter<ProductAdapterGird.MyViewHolder> {

    private final Context mainActivityUser;
    public ProductItemClick productItemClick;
    int selectedPosition = 0;
    DbCart databaseHelper;
    SharedPreferences sharedpreferences;
    private List<ProductListBean> productBeans;

    public ProductAdapterGird(Context mainActivityUser, List<ProductListBean> productBeans, ProductItemClick productItemClick, SharedPreferences sharedPreferences) {
        this.mainActivityUser = mainActivityUser;
        this.productBeans = productBeans;
        this.productItemClick = productItemClick;
        sharedpreferences = mainActivityUser.getSharedPreferences(AppConfig.mypreference, Context.MODE_PRIVATE);
        databaseHelper = new DbCart(mainActivityUser);
    }

    public void notifyData(List<ProductListBean> productBeans) {
        this.productBeans = productBeans;
        notifyDataSetChanged();
    }

    public void notifyData(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public ProductAdapterGird.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_img_grid, parent, false);

        return new ProductAdapterGird.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ProductListBean productBean = productBeans.get(position);

        holder.product_name.setText(productBean.getModel());
        holder.brand.setText(Html.fromHtml(productBean.getBrand()));
        ArrayList<String> urls = new Gson().fromJson(productBean.image, (Type) List.class);

        Glide.with(mainActivityUser)
                .load(AppConfig.getResizedImage(urls.get(0), true))

                .into(holder.product_image);

        if (urls.size() > 1) {
            Glide.with(mainActivityUser)
                    .load(AppConfig.getResizedImage(urls.get(1), true))

                    .into(holder.image_girdone);
        }
        if (urls.size() > 2) {
            Glide.with(mainActivityUser)
                    .load(AppConfig.getResizedImage(urls.get(2), true))

                    .into(holder.image_girdtwo);
        }


        if (productBean.getStock_update().equalsIgnoreCase("Currently Unavailable")) {
            holder.linearPro.setBackgroundColor(Color.parseColor("#b0afaf"));
            holder.linearPro.setAlpha(0.5f);
        } else {
            holder.linearPro.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.linearPro.setAlpha(1f);
        }


        holder.product_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!productBean.getStock_update().equalsIgnoreCase("Currently Unavailable")) {
                    productItemClick.onProductClick(productBean);
                }
            }
        });


    }



    public int getItemCount() {
        return productBeans.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout product_card;
        private final ImageView product_image;
        private final TextView product_name;
        private final TextView product_rupee_final;
        private final TextView brand;
        private final RelativeLayout linearPro;
        private final ImageView image_girdone;
        private final ImageView image_girdtwo;

        public MyViewHolder(View view) {
            super((view));
            product_card = view.findViewById(R.id.product_card);
            product_image = view.findViewById(R.id.product_image);
            image_girdone = view.findViewById(R.id.image_gridone);
            image_girdtwo = view.findViewById(R.id.image_girdtwo);
            product_name = view.findViewById(R.id.product_name);
            brand = view.findViewById(R.id.brand);
            linearPro = view.findViewById(R.id.linearPro);

            product_rupee_final = view.findViewById(R.id.product_rupee_final);
        }
    }

}


