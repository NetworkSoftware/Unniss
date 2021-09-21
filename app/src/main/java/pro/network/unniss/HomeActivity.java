package pro.network.unniss;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import pro.network.unniss.app.PreferenceBean;
import pro.network.unniss.cart.CartActivity;
import pro.network.unniss.chip.ChipBean;
import pro.network.unniss.chip.OnChip;
import pro.network.unniss.orders.MyOrderPage;
import pro.network.unniss.product.ProductActivity;
import pro.network.unniss.product.ProductAdapterGird;
import pro.network.unniss.product.ProductAdaptertwo;
import pro.network.unniss.product.ProductItemClick;
import pro.network.unniss.product.ProductListBean;
import pro.network.unniss.product.SingleProductAdapter;
import pro.network.unniss.random.OnRandom;
import pro.network.unniss.random.RandomAdapter;
import pro.network.unniss.random.RandomBeen;
import pro.network.unniss.wallet.WalletActivity;

import static pro.network.unniss.app.AppConfig.CATEGORIES_GET_ALL;

public class HomeActivity extends BaseActivity implements ProductItemClick, OnRandom{

    private final String TAG = getClass().getSimpleName();
    private final ArrayList<ChipBean> category = new ArrayList<>();
    RecyclerView recycler_product;
    CartActivity.OnCartItemChange onCartItemChange;
    BannerLayout banner;
    ProgressBar categoryProgress, productProgress,womensproductProgress,gridproductProgress;
    ArrayList<RandomBeen> chipBean = new ArrayList<>();
    SingleProductAdapter productListAdapter;
    ProductAdaptertwo productAdaptertwo;
    ProductAdapterGird productAdapterGird;
    SharedPreferences sharedpreferences;
    TextView title;
    RandomAdapter randomAdapter;
    TextView basketCount;
    private DbCart db;
    private List<ProductListBean> productList = new ArrayList<>();
    private TextView cart_badge;
    private FloatingActionButton phone;
    private ImageView  setting,myorder;
    private TextView search;

    @Override
    protected void startDemo() {
        setContentView(R.layout.activity_home);
        sharedpreferences = getApplicationContext().getSharedPreferences(AppConfig.mypreference, Context.MODE_PRIVATE);

        db = new DbCart(getApplicationContext());
        title = findViewById(R.id.title);

        recycler_product = findViewById(R.id.recycler_product);
        categoryProgress = findViewById(R.id.categoryProgress);
        productProgress = findViewById(R.id.productProgress);
        womensproductProgress = findViewById(R.id.mensproductProgress);
        gridproductProgress = findViewById(R.id.kidsproductProgress);
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
        myorder = findViewById(R.id.myorder);
        myorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MyOrderPage.class);
                startActivity(intent);
            }
        });
        search = findViewById(R.id.searchhome);
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
        showWomensProduct();
        showKidsProduct();
        phone = findViewById(R.id.floting_whatsapp);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=917904274703"
                            + "&text=" + "Hi Unniss"));
                    intent.setPackage("com.whatsapp.w4b");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=917904274703"
                            + "&text=" + "Hi Unniss"));
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
        final GridLayoutManager addManager1 = new GridLayoutManager(HomeActivity.this, 3);
        categoryRecyclerView.setLayoutManager(addManager1);
        randomAdapter = new RandomAdapter(getApplication(), chipBean, this, "");
        categoryRecyclerView.setAdapter(randomAdapter);
        getAllCategories();

    }

    private void showProduct() {
        GravitySnapRecyclerView recent_product = findViewById(R.id.product_getAll);
        productListAdapter = new SingleProductAdapter(HomeActivity.this, productList,
                this, sharedpreferences);
        final GridLayoutManager addManager1 = new GridLayoutManager(HomeActivity.this, 2);
        recent_product.setLayoutManager(addManager1);
        recent_product.setAdapter(productListAdapter);
        fetchProductList();
    }
    private void showWomensProduct() {
        GravitySnapRecyclerView recent_productwomens = findViewById(R.id.product_getAll_womens);
        productAdaptertwo = new ProductAdaptertwo(HomeActivity.this, productList,
                this, sharedpreferences);
        final GridLayoutManager addManager2 = new GridLayoutManager(HomeActivity.this, 2);
        recent_productwomens.setLayoutManager(addManager2);
        recent_productwomens.setAdapter(productAdaptertwo);
        fetchallwomenswaer("");
    }
    private void showKidsProduct() {
        GravitySnapRecyclerView recent_productkids = findViewById(R.id.product_getAll_kids);
        productAdapterGird = new ProductAdapterGird(HomeActivity.this, productList,
                this, sharedpreferences);
        final GridLayoutManager addManager3 = new GridLayoutManager(HomeActivity.this, 2);
        recent_productkids.setLayoutManager(addManager3);
        recent_productkids.setAdapter(productAdapterGird);
        fetchallgird("");
    }

    private void updateCart() {
        basketCount=findViewById(R.id.basketCount);
        if (basketCount != null) {
            if (db.getCartCountYalu(sharedpreferences.getString(AppConfig.user_id, "")) == 0) {
                basketCount.setVisibility(View.GONE);
            } else {
                basketCount.setVisibility(View.VISIBLE);
            }
            basketCount.setText(db.getCartCountYalu(sharedpreferences.getString(AppConfig.user_id, "")) + "");
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
                                    jsonArray.getJSONObject(i).getString("image"),
                                    jsonArray.getJSONObject(i).getString("id")));
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

    private void fetchallwomenswaer(final String searchKey) {
            String tag_string_req = "req_register";
            womensproductProgress.setVisibility(View.VISIBLE);
            StringRequest strReq = new StringRequest(Request.Method.GET,
                    AppConfig.PRODUCT_GET_ALL+"?category=45", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Register Response: ", response);
                    womensproductProgress.setVisibility(View.GONE);
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
                                productListBean.setBrand(jsonObject.getString("subcategory"));
                                productListBean.setCategory(jsonObject.getString("category"));
                                productListBean.setPrice(jsonObject.getString("price"));
                                productListBean.setModel(jsonObject.getString("name"));
                                productListBean.setImage(jsonObject.getString("image"));
                                productListBean.setDescription(jsonObject.getString("description"));
                                productListBean.setRqtyType(jsonObject.getString("rqtyType"));
                                productListBean.setRqty(jsonObject.getString("rqty"));
                                productListBean.setFabric(jsonObject.getString("fabric"));
                                productListBean.setBestselling(jsonObject.getString("bestselling"));
                                productListBean.setPricedrop(jsonObject.getString("pricedrop"));
                                productListBean.setIdeal(jsonObject.getString("ideal"));
                                productListBean.setOccasion(jsonObject.getString("occasion"));
                                productListBean.setFit(jsonObject.getString("fit"));
                                productListBean.setColor(jsonObject.getString("color"));
                            //    productListBean.setSize(jsonObject.getString("size"));
                                productListBean.setClosure(jsonObject.getString("closure"));
                                productListBean.setPocket(jsonObject.getString("pocket"));
                                productListBean.setPattern(jsonObject.getString("pattern"));
                                productListBean.setRise(jsonObject.getString("rise"));
                                productListBean.setOrigin(jsonObject.getString("origin"));
                                productListBean.setStock_update(jsonObject.getString("stock_status"));
                                productList.add(productListBean);
                            }
                        } else {
                            Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("xxxxxxxxxxx", e.toString());
                        Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                    }
                    productAdaptertwo.notifyData(productList);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplication(),
                            "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                    womensproductProgress.setVisibility(View.GONE);
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

    private void fetchallgird(final String searchKey) {
        String tag_string_req = "req_register";
        gridproductProgress.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.PRODUCT_GET_ALL+"?category=15", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                gridproductProgress.setVisibility(View.GONE);
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
                            productListBean.setBrand(jsonObject.getString("subcategory"));
                            productListBean.setCategory(jsonObject.getString("category"));
                            productListBean.setPrice(jsonObject.getString("price"));
                            productListBean.setModel(jsonObject.getString("name"));
                            productListBean.setImage(jsonObject.getString("image"));
                            productListBean.setDescription(jsonObject.getString("description"));
                            productListBean.setRqtyType(jsonObject.getString("rqtyType"));
                            productListBean.setRqty(jsonObject.getString("rqty"));
                            productListBean.setFabric(jsonObject.getString("fabric"));
                            productListBean.setBestselling(jsonObject.getString("bestselling"));
                            productListBean.setPricedrop(jsonObject.getString("pricedrop"));
                            productListBean.setIdeal(jsonObject.getString("ideal"));
                            productListBean.setOccasion(jsonObject.getString("occasion"));
                            productListBean.setFit(jsonObject.getString("fit"));
                            productListBean.setColor(jsonObject.getString("color"));
                          //  productListBean.setSize(jsonObject.getString("size"));
                            productListBean.setClosure(jsonObject.getString("closure"));
                            productListBean.setPocket(jsonObject.getString("pocket"));
                            productListBean.setPattern(jsonObject.getString("pattern"));
                            productListBean.setRise(jsonObject.getString("rise"));
                            productListBean.setOrigin(jsonObject.getString("origin"));
                            productListBean.setStock_update(jsonObject.getString("stock_status"));
                            productList.add(productListBean);
                        }
                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }
                productAdapterGird.notifyData(productList);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                gridproductProgress.setVisibility(View.GONE);
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

                        ViewGroup.LayoutParams params= banner.getLayoutParams();
                        params.width = PreferenceBean.getInstance().getWidth();    //500px
                        params.height = 9 * PreferenceBean.getInstance().getWidth() / 16;
                        banner.requestLayout();

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
        updateCart();
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
    public void onWishClick(ProductListBean position) {

    }


    private void fetchProductList() {
        String tag_string_req = "req_register";
        productProgress.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.PRODUCT_GET_ALL+"?category=44", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                productProgress.setVisibility(View.GONE);
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
                            productListBean.setBrand(jsonObject.getString("subcategory"));
                            productListBean.setCategory(jsonObject.getString("category"));
                            productListBean.setPrice(jsonObject.getString("price"));
                            productListBean.setModel(jsonObject.getString("name"));
                            productListBean.setImage(jsonObject.getString("image"));
                            productListBean.setDescription(jsonObject.getString("description"));
                            productListBean.setRqtyType(jsonObject.getString("rqtyType"));
                            productListBean.setRqty(jsonObject.getString("rqty"));
                            productListBean.setFabric(jsonObject.getString("fabric"));
                            productListBean.setBestselling(jsonObject.getString("bestselling"));
                            productListBean.setPricedrop(jsonObject.getString("pricedrop"));
                            productListBean.setIdeal(jsonObject.getString("ideal"));
                            productListBean.setOccasion(jsonObject.getString("occasion"));
                            productListBean.setFit(jsonObject.getString("fit"));
                            productListBean.setColor(jsonObject.getString("color"));
                           // productListBean.setSize(jsonObject.getString("size"));
                            productListBean.setClosure(jsonObject.getString("closure"));
                            productListBean.setPocket(jsonObject.getString("pocket"));
                            productListBean.setPattern(jsonObject.getString("pattern"));
                            productListBean.setRise(jsonObject.getString("rise"));
                            productListBean.setOrigin(jsonObject.getString("origin"));
                            productListBean.setStock_update(jsonObject.getString("stock_status"));
                            productList.add(productListBean);
                        }
                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }
                productListAdapter.notifyData(productList);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                productProgress.setVisibility(View.GONE);
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
              //  localHashMap.put("searchKey", searchKey);
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateCart();
        fetchProductList();
        try {
            if (sharedpreferences.contains(AppConfig.usernameKey)) {
                getSupportActionBar().setSubtitle(sharedpreferences.getString(AppConfig.usernameKey, ""));
            }
        } catch (Exception e) {

        }
    }

    private void startAllProduct(String view,String id) {
        Intent intent = new Intent(HomeActivity.this, AllProductActivity.class);
        intent.putExtra("type", view);
        intent.putExtra("id",id);
        startActivity(intent);
    }


    @Override
    public void onItemClick(String type, String id) {
        startAllProduct(type,id);

    }

}
