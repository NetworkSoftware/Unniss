package pro.network.unniss.coupon;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pro.network.unniss.R;
import pro.network.unniss.app.AppConfig;
import pro.network.unniss.app.AppController;

public class CouponsActivity extends AppCompatActivity implements OnCoupon {

    ArrayList<Coupon> coupons = new ArrayList<>();
    CouponAdapter couponAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_coupon);

        RecyclerView recycler_product = findViewById(R.id.recycler_product);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CouponsActivity.this,
                LinearLayoutManager.VERTICAL, false);
        recycler_product.setLayoutManager(layoutManager);
        couponAdapter = new CouponAdapter(coupons, this);
        recycler_product.setAdapter(couponAdapter);

        Drawable backArrow = getResources().getDrawable(R.drawable.ic_round_arrow_back_24);
        backArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        fetchCoupons();

    }

    @Override
    public void onCouponSelected(int position) {

        Intent intent = new Intent();
        intent.putExtra("data", coupons.get(position));
        setResult(1, intent);
        finish();
    }

    @Override
    public void onCouponLongClick(int position) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("coupon", coupons.get(position).getCoupon());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(CouponsActivity.this, "Coupon code copied", Toast.LENGTH_SHORT).show();
    }

    private void fetchCoupons() {
        String tag_string_req = "req_register";
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.COUPON_GET_ALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    coupons = new ArrayList<>();
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Coupon coupon = new Coupon();
                            coupon.setId(jsonObject.getString("id"));
                            coupon.setCoupon(jsonObject.getString("code"));
                            coupon.setAmt(jsonObject.getString("amount"));
                            // banner.setStatus(jsonObject.getString("status"));
                            coupon.setDescription(jsonObject.getString("description"));
                            coupon.setIsPercent(jsonObject.getString("isPercent"));
                            coupon.setPercentage(jsonObject.getString("percentage"));
                            coupon.setMinimumOrder(jsonObject.getString("minimum_amount"));
                            coupons.add(coupon);
                        }
                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    couponAdapter.notifyData(coupons);
                    getSupportActionBar().setSubtitle(coupons.size() + " Coupons");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
