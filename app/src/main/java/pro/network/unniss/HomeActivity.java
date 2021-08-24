package pro.network.unniss;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.rubensousa.gravitysnaphelper.GravitySnapRecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.network.moeidbannerlibrary.banner.BannerBean;
import com.network.moeidbannerlibrary.banner.BannerLayout;
import com.network.moeidbannerlibrary.banner.BaseBannerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.network.unniss.app.AppConfig;
import pro.network.unniss.app.AppController;
import pro.network.unniss.app.BaseActivity;
import pro.network.unniss.app.DbCart;
import pro.network.unniss.cart.CartActivity;
import pro.network.unniss.chip.ChipBean;
import pro.network.unniss.chip.OnChip;
import pro.network.unniss.orders.MyOrderPage;
import pro.network.unniss.product.ProductActivity;
import pro.network.unniss.product.ProductItemClick;
import pro.network.unniss.product.ProductListAdapter;
import pro.network.unniss.product.ProductListBean;
import pro.network.unniss.random.OnRandom;
import pro.network.unniss.random.RandomBeen;
import pro.network.unniss.random.RandomAdapter;
import pro.network.unniss.wallet.WalletActivity;

import static pro.network.unniss.app.AppConfig.CATEGORIES_GET_ALL;

public class HomeActivity extends BaseActivity implements ProductItemClick,OnRandom, OnChip {

    RecyclerView recycler_product;
    CartActivity.OnCartItemChange onCartItemChange;
    BannerLayout banner;
    ProgressBar categoryProgress;
    ArrayList<ChipBean> chipBeans = new ArrayList<>();
    ArrayList<RandomBeen> chipBean = new ArrayList<>();
    ProductListAdapter productListAdapter;
    RandomAdapter randomAdapter;
    SharedPreferences sharedpreferences;
    TextView  title;
    private final String TAG = getClass().getSimpleName();
    private DbCart db;
   // private List<CategoryBeen> categoryList = new ArrayList<>();
    private List<ProductListBean> productList = new ArrayList<>();
    private TextView cart_badge;
   // private CategoryAdapter categoryAdapter;
    private final ArrayList<ChipBean> category = new ArrayList<>();
    private FloatingActionButton phone;
    TextView basketCount;
    private ImageView search,setting;

    @Override
    protected void startDemo() {
        setContentView(R.layout.activity_home);
        sharedpreferences = getApplicationContext().getSharedPreferences(AppConfig.mypreference, Context.MODE_PRIVATE);

        if (!sharedpreferences.contains(AppConfig.isLogin) || !sharedpreferences.getBoolean(AppConfig.isLogin, false)) {
        }


        if (sharedpreferences.contains(AppConfig.usernameKey)) {
         //   getSupportActionBar().setSubtitle(sharedpreferences.getString(AppConfig.usernameKey, ""));
        }
        db = new DbCart(getApplicationContext());
        title = findViewById(R.id.title);

        recycler_product = findViewById(R.id.recycler_product);
        categoryProgress = findViewById(R.id.categoryProgress);
        db = new DbCart(getApplicationContext());
      //  categoryList = new ArrayList<>();

        setting = findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AllProductActivity.class);
                intent.putExtra("isSearch", true);
                startActivity(intent);
            }
        });
        ImageView cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
            }
        });
        basketCount = findViewById(R.id.basketCount);
        updateCart();
        fetchBanner();
        showProduct();
        showCategories();

        phone = findViewById(R.id.floting_whatsapp);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=919841778659"
                            + "&text=" + "Hi Unniss"));
                    intent.setPackage("com.whatsapp.w4b");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=919841778659"
                            + "&text=" + "Hi Unnish"));
                    intent.setPackage("com.whatsapp");
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Whatsapp Not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
        banner = findViewById(R.id.Banner);

    }

    private void showCategories() {
        GravitySnapRecyclerView categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        final GridLayoutManager addManager1 = new GridLayoutManager(getApplication(), 3);
        categoryRecyclerView.setLayoutManager(addManager1);
        categoryRecyclerView.setHasFixedSize(true);
        randomAdapter = new RandomAdapter(getApplication(), chipBean, (OnRandom) this, "");
        categoryRecyclerView.setAdapter(randomAdapter);
        getAllCategories();

    }
    private void showProduct() {
        GravitySnapRecyclerView recent_product = findViewById(R.id.product_getAll);
        final GridLayoutManager addManager1 = new GridLayoutManager(HomeActivity.this, 2);
        recent_product.setLayoutManager(addManager1);
        recent_product.setAdapter(productListAdapter);
        fetchProductList("");
    }
    private void updateCart() {
        DbCart helperYalu = new DbCart(this);
        if (basketCount != null) {
            if (helperYalu.getCartCountYalu(sharedpreferences.getString(AppConfig.userId, "")) == 0) {
                basketCount.setVisibility(View.GONE);
            } else {
                basketCount.setVisibility(View.VISIBLE);
            }
            basketCount.setText(helperYalu.getCartCountYalu(sharedpreferences.getString(AppConfig.userId, "")) + "");
        }
    }

    private void getAllCategories() {
        String tag_string_req = "req_register";
        categoryProgress.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                CATEGORIES_GET_ALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                categoryProgress.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        chipBean = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            chipBean.add(new RandomBeen(jsonArray.getJSONObject(i)
                                    .getString("title"),
                                    jsonArray.getJSONObject(i).getString("image")));
                        }
                        randomAdapter.notifyData(chipBean);
                    }
                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                categoryProgress.setVisibility(View.GONE);
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getFromServer(final String searchKey) {
        String tag_string_req = "req_register";
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.PRODUCT_GET_ALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        productList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ProductListBean productListBean = new ProductListBean();
                            productListBean.setId(jsonObject.getString("id"));
                            productListBean.setBrand(jsonObject.getString("brand"));
                            productListBean.setPrice(jsonObject.getString("price"));
                            productListBean.setRam(jsonObject.getString("ram"));
                            productListBean.setRom(jsonObject.getString("rom"));
                            productListBean.setModel(jsonObject.getString("model"));
                            productListBean.setImage(jsonObject.getString("image"));
                            productListBean.setDescription(jsonObject.getString("description"));
                            productListBean.setStock_update(jsonObject.getString("stock_update"));
                            productList.add(productListBean);
                        }

                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("searchKey", searchKey);
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void fetchBanner() {
        String tag_string_req = "req_register";
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.BANNERS_GET_ALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        List<BannerBean> bannerBeans = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            BannerBean bannerBean = new BannerBean();
                            bannerBean.setImages(jsonObject.getString("banner"));
                            bannerBean.setId(jsonObject.getString("id"));
                            bannerBean.setDescription(jsonObject.getString("description"));
                            bannerBeans.add(bannerBean);
                        }
                        BaseBannerAdapter webBannerAdapter = new BaseBannerAdapter(getApplicationContext(), bannerBeans);
                        banner.setAdapter(webBannerAdapter);


                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void addtocart(ProductListBean productListBean) {
        productListBean.setQty("1");
        db.insertProductInCart(productListBean, sharedpreferences.getString(AppConfig.user_id, ""));
        Toast.makeText(getApplication(), "Item added successfully", Toast.LENGTH_SHORT).show();
        if (onCartItemChange != null) {
            onCartItemChange.onCartChange();
        }
        setupBadge();
        productListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onProductClick(ProductListBean productListBean) {

        Intent intent = new Intent(getApplication(), ProductActivity.class);
        intent.putExtra("data", productListBean);
        startActivity(intent);
    }

    @Override
    public void OnQuantityChange(int position, int qty) {

    }

    @Override
    public void onCartClick(ProductListBean productListBean) {

        addtocart(productListBean);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.front_menu, menu);


        final MenuItem menuItem = menu.findItem(R.id.cart);

        View actionView = menuItem.getActionView();
        cart_badge = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            case R.id.settings:
                Intent intenti = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intenti);
                return true;


            case R.id.cart:
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
                return true;
            case R.id.wallet:
                Intent intentj = new Intent(HomeActivity.this, WalletActivity.class);
                startActivity(intentj);
                return true;

            case R.id.order:
                Intent intentk = new Intent(HomeActivity.this, MyOrderPage.class);
                startActivity(intentk);

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {
        int cartCountYalu = db.getCartCountYalu(sharedpreferences.getString(AppConfig.user_id, ""));
        if (cart_badge != null) {
            if (cartCountYalu == 0) {
                if (cart_badge.getVisibility() != View.GONE) {
                    cart_badge.setVisibility(View.GONE);
                }
            } else {
                cart_badge.setText(String.valueOf(Math.min(cartCountYalu, 99)));
                if (cart_badge.getVisibility() != View.VISIBLE) {
                    cart_badge.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void fetchProductList(final String searchKey) {
        String tag_string_req = "req_register";
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.PRODUCT_GET_ALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        productList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ProductListBean productListBean = new ProductListBean();
                            productListBean.setId(jsonObject.getString("id"));
                            productListBean.setBrand(jsonObject.getString("brand"));
                            productListBean.setPrice(jsonObject.getString("price"));
                            productListBean.setRam(jsonObject.getString("ram"));
                            productListBean.setRom(jsonObject.getString("rom"));
                            productListBean.setModel(jsonObject.getString("model"));
                            productListBean.setImage(jsonObject.getString("image"));
                            productListBean.setDescription(jsonObject.getString("description"));
                            productListBean.setStock_update(jsonObject.getString("stock_update"));
                            productList.add(productListBean);
                        }
                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("searchKey", searchKey);
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*   setupBadge();*/
        fetchProductList("");
        try {
            if (sharedpreferences.contains(AppConfig.usernameKey)) {
                getSupportActionBar().setSubtitle(sharedpreferences.getString(AppConfig.usernameKey, ""));
            }
            setupBadge();
        } catch (Exception e) {

        }
    }

    private void startAllProduct(String view) {
        Intent intent = new Intent(HomeActivity.this, AllProductActivity.class);
        intent.putExtra("type", view);
        startActivity(intent);
    }


    @Override
    public void onItemClick(String type) {
        startAllProduct(type);
    }

}
