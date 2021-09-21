package pro.network.unniss;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

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
import pro.network.unniss.app.DbWishList;
import pro.network.unniss.cart.CartActivity;
import pro.network.unniss.chip.ChipAdapter;
import pro.network.unniss.chip.ChipBean;
import pro.network.unniss.chip.OnChip;
import pro.network.unniss.product.AllProductAdapter;
import pro.network.unniss.product.ProductActivity;
import pro.network.unniss.product.ProductItemClick;
import pro.network.unniss.product.ProductListBean;
import pro.network.unniss.product.Size;
import pro.network.unniss.wishlist.WishListActivity;

public class AllProductActivity extends BaseActivity implements ProductItemClick, OnChip {
    private final String TAG = getClass().getSimpleName();
    private final ArrayList<ChipBean> chipBeans = new ArrayList<>();
    ProgressDialog pDialog;
    RecyclerView recycler_product;
    AllProductAdapter allProductAdapter;
    SharedPreferences sharedpreferences;

    CartActivity.OnCartItemChange onCartItemChange;
    ChipAdapter chipAdapter;
    TextView wishListCount;
    ProgressBar allproductProgress;
    private DbCart db;
    private DbWishList dbWishList;
    private List<ProductListBean> productList = new ArrayList<>();
    private SearchView searchView;
    private TextView cart_badge;
    private String selectedType = "COMBO PACK";
    private String selectedTypeId = "COMBO PACK";

    @Override
    protected void startDemo() {
        setContentView(R.layout.activity_all_product);
        pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setCancelable(false);
        sharedpreferences = getApplicationContext().getSharedPreferences(AppConfig.mypreference, Context.MODE_PRIVATE);

        db = new DbCart(getApplicationContext());
        dbWishList = new DbWishList(this);
        recycler_product = findViewById(R.id.recycler_product);
        allproductProgress = findViewById(R.id.allproductProgress);
        productList = new ArrayList<>();
        allProductAdapter = new AllProductAdapter(getApplicationContext(), productList, this, sharedpreferences);
        final GridLayoutManager addManager1 = new GridLayoutManager(getApplication(), 2);
        recycler_product.setLayoutManager(addManager1);
        recycler_product.setAdapter(allProductAdapter);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String type = getIntent().getStringExtra("type");
        getSupportActionBar().setTitle(type != null ? type : Html.fromHtml("<font color='#000000'>UNNISS</font>"));
        selectedType = type;
        String id = getIntent().getStringExtra("id");
        getSupportActionBar().setTitle(type != null ? type : Html.fromHtml("<font color='#000000'>UNNISS</font>"));
        selectedTypeId = id != null ? id : "";

    }

    private void fetchProductList(final String searchKey) {
        String tag_string_req = "req_register";
        // showDialog();
        allproductProgress.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.PRODUCT_GET_ALL + "?searchKey=" + searchKey + "&category=" + selectedTypeId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                allproductProgress.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        productList = new ArrayList<>();
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ProductListBean productListBean = new ProductListBean();
                            productListBean.setId(jsonObject.getString("id"));
                            productListBean.setBrand(jsonObject.getString("subcategory"));
                            productListBean.setCategory(jsonObject.getString("category"));
                            productListBean.setModel(jsonObject.getString("name"));
                            if(!jsonObject.isNull("price")){
                                productListBean.setPrice(jsonObject.getString("price"));
                            }
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
                            productListBean.setVariation(jsonObject.getString("variation"));
                            productList.add(productListBean);
                        }
                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    allProductAdapter.notifyData(productList);
                    getSupportActionBar().setSubtitle(productList.size() + " Products");
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
                allproductProgress.setVisibility(View.GONE);
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("searchKey", searchKey);
                if (selectedType != null) {
                    localHashMap.put("category", selectedType);
                }
                if (sharedpreferences.contains("location")) {
                    localHashMap.put("locationSearch", sharedpreferences.getString("location", ""));
                }
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void addToCart(ProductListBean productListBean) {
        productListBean.setQty("1");
        ObjectMapper mapper = new ObjectMapper();
        Object listBeans = new Gson().fromJson(productListBean.getSize(),
                Object.class);
        ArrayList<Size> sizes = mapper.convertValue(
                listBeans,
                new TypeReference<ArrayList<Size>>() {
                }
        );
        productListBean.setSize(sizes.get(0).getSize());
        productListBean.setPrice(sizes.get(0).getSize_price());
        db.insertProductInCart(productListBean, sharedpreferences.getString(AppConfig.user_id, ""));
        Toast.makeText(getApplication(), "Item added successfully", Toast.LENGTH_SHORT).show();
        if (onCartItemChange != null) {
            onCartItemChange.onCartChange();
        }
        setupBadge();
        allProductAdapter.notifyDataSetChanged();
    }


    @Override
    public void onProductClick(ProductListBean productListBean) {
        try {
            Intent intent = new Intent(getApplication(), ProductActivity.class);
            intent.putExtra("data", productListBean);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please Wait, Try again", Toast.LENGTH_SHORT).show();
            Log.e("xxxxxxxxxxxxx", e.toString());
        }
    }

    @Override
    public void OnQuantityChange(int position, int qty) {
        if (qty <= 0) {
            db.deleteProductById(productList.get(position)
                    , sharedpreferences.getString(AppConfig.user_id, ""));
        } else {
            productList.get(position).setQty(qty + "");
            db.insertProductInCart(productList.get(position),
                    sharedpreferences.getString(AppConfig.user_id, ""));
            if (onCartItemChange != null) {
                onCartItemChange.onCartChange();
            }
        }
        setupBadge();
        allProductAdapter.notifyData(productList);
    }

    @Override
    public void onCartClick(ProductListBean productListBean) {
        addToCart(productListBean);
    }

    private void updateWishList() {
        int wishCountYalu = dbWishList.getWishListCount(sharedpreferences.getString(AppConfig.user_id, ""));
        if (wishListCount != null) {
            if (wishCountYalu == 0) {
                if (wishListCount.getVisibility() != View.GONE) {
                    wishListCount.setVisibility(View.GONE);
                }
            } else {
                wishListCount.setText(String.valueOf(Math.min(wishCountYalu, 99)));
                if (wishListCount.getVisibility() != View.VISIBLE) {
                    wishListCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onWishClick(ProductListBean productListBean) {
        updateWishList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.product_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();

        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                fetchProductList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                fetchProductList(query);
                return false;
            }
        });

        final MenuItem menuItem = menu.findItem(R.id.cart);

        View actionView = menuItem.getActionView();
        cart_badge = actionView.findViewById(R.id.cart_badge);
        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        final MenuItem fabmenu = menu.findItem(R.id.wishlist);
        View favView = fabmenu.getActionView();
        wishListCount = favView.findViewById(R.id.wishListCount);
        updateWishList();
        favView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(fabmenu);
            }
        });

        if (getIntent() != null && getIntent().getBooleanExtra("isSearch", false)) {
            searchView.setIconified(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.cart:
                Intent intent = new Intent(AllProductActivity.this, CartActivity.class);
                startActivity(intent);
                return true;
            case R.id.wishlist:
                Intent intent1 = new Intent(AllProductActivity.this, WishListActivity.class);
                startActivity(intent1);
                return true;
            case android.R.id.home:
                finish();
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

    @Override
    protected void onStart() {
        super.onStart();
        fetchProductList("");
        setupBadge();
        updateWishList();
    }

    @Override
    public void onItemClick(String type) {
        selectedType = type;
        getSupportActionBar().setTitle(selectedType);
        chipAdapter.notifyData(selectedType);
    }

}
