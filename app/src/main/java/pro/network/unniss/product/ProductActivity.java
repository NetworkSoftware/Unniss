package pro.network.unniss.product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.shuhart.bubblepagerindicator.BubblePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.network.unniss.R;
import pro.network.unniss.app.AppConfig;
import pro.network.unniss.app.AppController;
import pro.network.unniss.app.BaseActivity;
import pro.network.unniss.app.DbCart;
import pro.network.unniss.app.DbWishList;
import pro.network.unniss.cart.CartActivity;
import pro.network.unniss.payment.PaymentActivity;

import static pro.network.unniss.app.AppConfig.PRODUCT_SIZE;
import static pro.network.unniss.app.AppConfig.mypreference;
import static pro.network.unniss.app.AppConfig.user_id;

public class ProductActivity extends BaseActivity implements ViewClick {
    SharedPreferences sharedpreferences;
    TextView product_price, product_descrpition, product_name, outOfStock;
    MaterialButton cart, buynow;
    int currentImage = 0;
    ArrayList<Size> sizes = new ArrayList<>();
    SizeAdapter sizeAdapter;
    BubblePageIndicator indicator;
    CardView bottomCard;
    LinearLayout stockupdate;
    ViewPager pager;
    LikeButton wish_button;
    TextView fabric, ideal, occasion, fit, color, closure, pocket, pattern, rise, origin, bestselling, pricedrop;
    ViewPagerAdapter adapter;
    private RecyclerView sizelist;
    private DbCart db;
    private PhotoView photoView;
    private ArrayList<String> urls = new ArrayList<>();
    private int selectedPosition = 0;
    DbWishList dbWishList;
    private ProductListBean productBean;

    @Override
    protected void startDemo() {
        setContentView(R.layout.activity_product_new);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        db = new DbCart(this);
        dbWishList = new DbWishList(this);
        sharedpreferences = this.getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        productBean = (ProductListBean) getIntent().getSerializableExtra("data");

        fabric = findViewById(R.id.fabric);
        ideal = findViewById(R.id.ideal);
        occasion = findViewById(R.id.occasion);
        fit = findViewById(R.id.fit);
        color = findViewById(R.id.color);
        sizelist = findViewById(R.id.sizelist);
        closure = findViewById(R.id.closure);
        pocket = findViewById(R.id.pocket);
        pattern = findViewById(R.id.pattern);
        rise = findViewById(R.id.rise);
        origin = findViewById(R.id.origin);
        product_name = findViewById(R.id.product_name);
        stockupdate = findViewById(R.id.stockupdate);
        bestselling = findViewById(R.id.bestselling);
        pricedrop = findViewById(R.id.pricedrop);
        wish_button = findViewById(R.id.wish_button);
        bottomCard = findViewById(R.id.bottomCard);
        product_price = findViewById(R.id.product_price);
        product_descrpition = findViewById(R.id.product_descrpition);
        outOfStock = findViewById(R.id.outOfStock);
        pager = findViewById(R.id.pager);
        indicator = findViewById(R.id.indicator);
        urls = new Gson().fromJson(productBean.image, (Type) List.class);

        adapter = new ViewPagerAdapter(ProductActivity.this, urls);
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);

        cart = findViewById(R.id.cart);

        sizelist = findViewById(R.id.sizelist);
        sizes = new ArrayList<>();
        sizeAdapter = new SizeAdapter(this, sizes, new ImageClick() {
            @Override
            public void onImageClick(int position) {
                selectedPosition = position;
                product_price.setText("Rs. " + sizes.get(selectedPosition).size_price +"/"+productBean.getRqty() + "" + productBean.getRqtyType());
                sizeAdapter.notifyData(position);
            }

            @Override
            public void onDeleteClick(int position) {
                sizes.remove(position);
                sizeAdapter.notifyData(sizes);
            }
        }, false);
        final LinearLayoutManager sizeManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        sizelist.setLayoutManager(sizeManager);
        sizelist.setAdapter(sizeAdapter);

        wish_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                productBean.setSize(sizes.get(selectedPosition).getSize());
                productBean.setPrice(sizes.get(selectedPosition).getSize_price());
                productBean.setVariationId(sizes.get(selectedPosition).getId());

                dbWishList.insertWishList(productBean, sharedpreferences.getString(AppConfig.user_id, ""));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                dbWishList.deleteWishList(productBean, sharedpreferences.getString(AppConfig.user_id, "")); }
        });
        wish_button.setLiked(dbWishList.isInWishList(productBean.id, sharedpreferences.getString(AppConfig.user_id, "")));



        /*final ArrayList<String> urls = new Gson().fromJson(productBean.image, (Type) List.class);
        photoView = findViewById(R.id.product_image);
        Glide.with(ProductActivity.this)
                .load(AppConfig.getResizedImage(urls.get(currentImage), false))
                .into(photoView);*/
        product_price.setText("Rs. " + productBean.getPrice() + "/" + productBean.getRqty() + "" + productBean.getRqtyType());
        product_name.setText(Html.fromHtml(productBean.getBrand()));
        fabric.setText(productBean.getFabric());
        ideal.setText(productBean.getIdeal());
        occasion.setText(productBean.getOccasion());
        fit.setText(productBean.getFit());
        color.setText(productBean.getColor());
        closure.setText(productBean.getClosure());
        pocket.setText(productBean.getPocket());
        pattern.setText(productBean.getPattern());
        rise.setText(productBean.getRise());
        origin.setText(productBean.getOrigin());
        bestselling.setText(productBean.getBestselling());
        pricedrop.setText(productBean.getPricedrop());
        if (productBean.getBestselling().equalsIgnoreCase("Best Selling")) {
            bestselling.setVisibility(View.VISIBLE);
        } else {
            bestselling.setVisibility(View.GONE);
        }
        if (productBean.getPricedrop().equalsIgnoreCase("Price Drop")) {
            pricedrop.setVisibility(View.VISIBLE);
        } else {
            pricedrop.setVisibility(View.GONE);
        }

        outOfStock.setText(productBean.getStock_update());

        if (productBean.getStock_update().equalsIgnoreCase("outofstock")) {
            outOfStock.setVisibility(View.VISIBLE);
            stockupdate.setVisibility(View.VISIBLE);
            bottomCard.setVisibility(View.GONE);
            cart.setVisibility(View.GONE);
        } else {
            outOfStock.setVisibility(View.GONE);
            cart.setVisibility(View.VISIBLE);
            bottomCard.setVisibility(View.VISIBLE);
            stockupdate.setVisibility(View.GONE);
        }

        product_descrpition.setText(Html.fromHtml(productBean.getDescription()));

        Drawable backArrow = getResources().getDrawable(R.drawable.ic_round_arrow_back_24);
        backArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);
        getSupportActionBar().setTitle(productBean.getModel());


        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db.isInCart(productBean.id, sharedpreferences.getString(AppConfig.user_id, ""))) {
                    startActivity(new Intent(ProductActivity.this, CartActivity.class));
                    finish();
                } else {
                    productBean.setQty("1");
                    productBean.setSize(sizes.get(selectedPosition).getSize());
                    productBean.setPrice(sizes.get(selectedPosition).getSize_price());
                    productBean.setVariationId(sizes.get(selectedPosition).getId());
                    db.insertProductInCart(productBean, sharedpreferences.getString(AppConfig.user_id, ""));
                    if (db.isInCart(productBean.id, sharedpreferences.getString(AppConfig.user_id, ""))) {
                        cart.setText("Go to Bag");
                    } else {
                        cart.setText("Add to Bag");
                    }
                }
            }
        });


        buynow = findViewById(R.id.buynow);
        buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                productBean.setQty("1");
                productBean.setSize(sizes.get(selectedPosition).getSize());
                productBean.setPrice(sizes.get(selectedPosition).getSize_price());
                productBean.setVariationId(sizes.get(selectedPosition).getId());
                db.insertProductInCart(productBean, sharedpreferences.getString(AppConfig.user_id, ""));

                if (sharedpreferences.getString(user_id, "guest").equals("guest")) {
                    Intent intent = new Intent(ProductActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ProductActivity.this, PaymentActivity.class);
                    startActivity(intent);
                }
            }
        });


        //  getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fetchSize();

    }

    @Override
    public void onViewClick(String s) {
        Glide.with(ProductActivity.this)
                .load(AppConfig.getResizedImage(s, false))
                .into(photoView);
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

    private class UploadFileToServer extends AsyncTask<String, Integer, Bitmap> {
        public long totalSize = 0;
        String filepath;

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pDialog.setMessage("Uploading..." + (progress[0]));
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            filepath = params[0];
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private Bitmap uploadFile() {
            pDialog.setMessage("Sending ...");
            String responseString = null;

            Uri bmpUri = null;
            try {
                URL url = new URL(filepath);

                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), "product_image" + ".png");
                file.getParentFile().mkdirs();
                FileOutputStream out = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                bmpUri = Uri.fromFile(file);
                return image;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            hideDialog();
            try {
                if (bitmap != null) {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, bitmap);
                    intent.putExtra(Intent.EXTRA_TEXT, "Share");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setType("image/png");
                    startActivity(intent);
                }
            } catch (Error | Exception e) {
                Toast.makeText(getApplicationContext(), "Image not Sharing", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(bitmap);
        }

    }

    private void fetchSize() {
        String tag_string_req = "req_register";
        pDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                PRODUCT_SIZE+"?id="+productBean.getId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d("Register Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        sizes = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Size product = new Size();
                            product.setId(jsonObject.getString("id"));
                            product.setSize(jsonObject.getString("size"));
                            product.setSize_price(jsonObject.getString("size_price"));
                            sizes.add(product);
                        }
                        sizeAdapter.notifyData(sizes);
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

}