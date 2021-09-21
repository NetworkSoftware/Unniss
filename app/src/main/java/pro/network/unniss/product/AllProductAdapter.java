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

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import pro.network.unniss.R;
import pro.network.unniss.app.AppConfig;
import pro.network.unniss.app.DbCart;
import pro.network.unniss.app.DbWishList;


public class AllProductAdapter extends RecyclerView.Adapter<AllProductAdapter.MyViewHolder> {

    private final Context mainActivityUser;
    public ProductItemClick productItemClick;
    int selectedPosition = 0;
    DbCart databaseHelper;
    SharedPreferences sharedpreferences;
    private List<ProductListBean> productBeans;
    DbWishList dbWishList;


    public AllProductAdapter(Context mainActivityUser, List<ProductListBean> productBeans, ProductItemClick productItemClick, SharedPreferences sharedPreferences) {
        this.mainActivityUser = mainActivityUser;
        this.productBeans = productBeans;
        this.productItemClick = productItemClick;
        sharedpreferences = mainActivityUser.getSharedPreferences(AppConfig.mypreference, Context.MODE_PRIVATE);
        databaseHelper = new DbCart(mainActivityUser);
        dbWishList = new DbWishList(mainActivityUser);
    }

    public void notifyData(List<ProductListBean> productBeans) {
        this.productBeans = productBeans;
        notifyDataSetChanged();
    }

    public void notifyData(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public AllProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newsingle_product, parent, false);

        return new AllProductAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ProductListBean productBean = productBeans.get(position);

        holder.product_name.setText(productBean.getModel());
        holder.brand.setText(Html.fromHtml(productBean.getBrand()));
        //  holder.rqty.setText(productBean.getRqty() + "" + productBean.getRqtyType());
        holder.stock_cart.setVisibility(View.VISIBLE);
        if(productBean.getStock_update().equalsIgnoreCase("outofstock")){
            holder.stock_cart.setVisibility(View.GONE);
        }
        ArrayList<String> urls = new Gson().fromJson(productBean.image, (Type) List.class);
        Glide.with(mainActivityUser)
                .load(AppConfig.getResizedImage(urls.get(0), true))
                .into(holder.product_image);
          holder.product_rupee_final.setText("Rs." + productBean.getPrice()+" / "+productBean.getRqty() + "" + productBean.getRqtyType());


        holder.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productItemClick.onCartClick(productBean);
                holder.quantity.setText(productBean.getQty());
            }
        });


        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQuan = Integer.parseInt(holder.quantity.getText().toString()) - 1;
                doCallCartChange(newQuan, holder, position);
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQuan = Integer.parseInt(holder.quantity.getText().toString()) + 1;
                doCallCartChange(newQuan, holder, position);
            }
        });
        holder.wish_button.setVisibility(View.GONE);
        holder.wish_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
             /*   ObjectMapper mapper = new ObjectMapper();
                Object listBeans = new Gson().fromJson(productBean.getVariation(),
                        Object.class);
                ArrayList<Size> sizes = mapper.convertValue(
                        listBeans,
                        new TypeReference<ArrayList<Size>>() {
                        }
                );
                productBean.setSize(sizes.get(0).getSize_price());
                productBean.setPrice(sizes.get(0).getSize_price());
                productBean.setVariationId(sizes.get(0).getId());*/
                dbWishList.insertWishList(productBean, sharedpreferences.getString(AppConfig.user_id, ""));
                productItemClick.onWishClick(productBean);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                dbWishList.deleteWishList(productBean, sharedpreferences.getString(AppConfig.user_id, ""));
                productItemClick.onWishClick(productBean);
            }
        });
        holder.wish_button.setLiked(dbWishList.isInWishList(productBean.id, sharedpreferences.getString(AppConfig.user_id, "")));

        holder.linearPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productItemClick.onProductClick(productBean);
            }
        });
        /*
        if (productBean.getStock_update().equalsIgnoreCase("outofstock")) {
            holder.linearPro.setBackgroundColor(Color.parseColor("#000"));
            holder.linearPro.setAlpha(0.5f);
        } else {
            holder.linearPro.setBackgroundColor(Color.parseColor("#000"));
            holder.linearPro.setAlpha(1f);
        }*/
        if (productBean.getBestselling().equalsIgnoreCase("Best Selling")) {
            holder.bestselling.setVisibility(View.VISIBLE);
        } else {
            holder.bestselling.setVisibility(View.GONE);
        }
        if (databaseHelper.isInCart(productBean.id,
                sharedpreferences.getString(AppConfig.user_id, ""))) {
            holder.cart.setVisibility(View.GONE);
            holder.add_qty.setVisibility(View.GONE);
            holder.quantity.setText(databaseHelper.getQuantityInCart(productBean.id,
                    sharedpreferences.getString(AppConfig.user_id, "")));

        } else {
            holder.cart.setVisibility(View.GONE);
            holder.add_qty.setVisibility(View.GONE);
            holder.quantity.setText("0");
        }


       /* holder.product_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!productBean.getStock_update().equalsIgnoreCase("Currently Unavailable")) {
                    productItemClick.onProductClick(productBean);
                }
            }
        });*/


    }

    private void doCallCartChange(int newQuan, AllProductAdapter.MyViewHolder holder, int position) {
        holder.quantity.setText(newQuan + "");
        if (newQuan == 0) {
            holder.cart.setVisibility(View.GONE);
            holder.add_qty.setVisibility(View.GONE);
        } else {
            holder.cart.setVisibility(View.VISIBLE);
            holder.add_qty.setVisibility(View.GONE);
        }
        productItemClick.OnQuantityChange(position, newQuan);
    }

    public int getItemCount() {
        return productBeans.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView product_image;
        private final ImageView minus;
        private final ImageView plus;
        private final TextView product_name;
        private final TextView product_rupee_final;
        private final TextView brand;
        private final TextView quantity;
        TextView bestselling;
        private final LinearLayout add_qty;
        LinearLayout linearPro,stock_cart;
        LikeButton wish_button;
        ImageView cart;

        public MyViewHolder(View view) {
            super((view));
            product_image = view.findViewById(R.id.product_image);
            cart = view.findViewById(R.id.add_cart);
            product_name = view.findViewById(R.id.product_name);
            //  rqty = (TextView) view.findViewById(R.id.rqty);
            quantity = view.findViewById(R.id.quantity);
            brand = view.findViewById(R.id.brand);
            minus = view.findViewById(R.id.minus);
            plus = view.findViewById(R.id.plus);
            add_qty = view.findViewById(R.id.add_qty);
            linearPro = view.findViewById(R.id.linearPro);
            product_rupee_final = view.findViewById(R.id.product_rupee_final);
            wish_button = view.findViewById(R.id.wish_button);
            bestselling = view.findViewById(R.id.bestselling);
            stock_cart = view.findViewById(R.id.stock_cart);
        }
    }

}


