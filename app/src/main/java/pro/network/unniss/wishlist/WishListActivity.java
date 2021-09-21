package pro.network.unniss.wishlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pro.network.unniss.R;
import pro.network.unniss.app.AppConfig;
import pro.network.unniss.app.BaseActivity;
import pro.network.unniss.app.DbCart;
import pro.network.unniss.app.DbWishList;
import pro.network.unniss.cart.CartActivity;
import pro.network.unniss.product.ProductListBean;

import static pro.network.unniss.app.AppConfig.decimalFormat;
import static pro.network.unniss.app.AppConfig.mypreference;

public class WishListActivity extends BaseActivity implements WishListClick {

    RecyclerView cart_list;
    private List<ProductListBean> productList = new ArrayList<>();
    WishListAdapter wishListAdapter;
    ProgressDialog pDialog;
    private String TAG = getClass().getSimpleName();
    private DbCart db;
    private DbWishList dbWishList;
    Button order;
    CartActivity.OnCartItemChange onCartItemChange;
    NestedScrollView nestedScrollView;
    CardView continueCard;
    LinearLayout empty_product;
    TextView emptyText;
    SharedPreferences sharedpreferences;

    @Override
    protected void startDemo() {
        setContentView(R.layout.activity_cart);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new DbCart(this);
        dbWishList = new DbWishList(this);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        Drawable backArrow = getResources().getDrawable(R.drawable.ic_round_arrow_back_24);
        backArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        cart_list = findViewById(R.id.cart_list);
        wishListAdapter = new WishListAdapter(this, productList, this);
        final LinearLayoutManager addManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cart_list.setLayoutManager(addManager1);
        cart_list.setAdapter(wishListAdapter);

        order = findViewById(R.id.order);
        order.setVisibility(View.GONE);

        nestedScrollView = findViewById(R.id.nested);
        continueCard = findViewById(R.id.continueCard);
        empty_product = findViewById(R.id.empty_product);
        emptyText = findViewById(R.id.emptyText);
        emptyText.setText("No Items in WishList");
        //  onCartItemChange = (CartActivity.OnCartItemChange) this;
        getAllCart();
    }


    private void getAllCart() {
        productList.clear();
        productList = dbWishList.getAllWishList(sharedpreferences.getString(AppConfig.user_id, ""));
        float grandTotal = 0f;
        for (int i = 0; i < productList.size(); i++) {
            String qty = productList.get(i).qty;
            if (qty == null || qty.length() <= 0) {
                qty = "1";
            }
            float startValue = Float.parseFloat(productList.get(i).price) * Integer.parseInt(qty);
            grandTotal = grandTotal + startValue;
        }
        ((TextView) findViewById(R.id.subtotal)).setText("₹" + decimalFormat.format(grandTotal) + ".00");
        ((TextView) findViewById(R.id.grandtotal)).setText("₹" + decimalFormat.format(grandTotal) + ".00");
        ((TextView) findViewById(R.id.total)).setText("₹" + decimalFormat.format(grandTotal) + ".00");
        cart_list.setAdapter(wishListAdapter);
        wishListAdapter.notifyData(productList);
        if (productList.size() == 0) {
            empty_product.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.GONE);
            continueCard.setVisibility(View.GONE);
        } else {
            empty_product.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.VISIBLE);
            continueCard.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onMoveToBag(int position) {
        dbWishList.deleteWishList(productList.get(position), sharedpreferences.getString(AppConfig.user_id, ""));
        ProductListBean productListBean = productList.get(position);
        productListBean.setQty("1");
        db.insertProductInCart(productListBean, sharedpreferences.getString(AppConfig.user_id, ""));
        if (onCartItemChange != null) {
            onCartItemChange.onCartChange();
        }
        getAllCart();
    }

    @Override
    public void ondeleteClick(int position) {
        dbWishList.deleteWishList(productList.get(position), sharedpreferences.getString(AppConfig.user_id, ""));
        if (onCartItemChange != null) {
            onCartItemChange.onCartChange();
        }
        getAllCart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}