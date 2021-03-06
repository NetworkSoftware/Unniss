package pro.network.unniss.payment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.instamojo.android.Instamojo;
import com.instamojo.android.activities.PaymentDetailsActivity;
import com.instamojo.android.callbacks.OrderRequestCallback;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.models.Errors;
import com.instamojo.android.models.Order;
import com.razorpay.Checkout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.network.unniss.R;
import pro.network.unniss.app.AppConfig;
import pro.network.unniss.app.AppController;
import pro.network.unniss.app.DbCart;
import pro.network.unniss.app.PreferenceBean;
import pro.network.unniss.app.UniqueID;
import pro.network.unniss.coupon.Coupon;
import pro.network.unniss.coupon.CouponsActivity;
import pro.network.unniss.orders.MyOrderPage;
import pro.network.unniss.orders.MyorderBean;
import pro.network.unniss.orders.SingleOrderPage;
import pro.network.unniss.product.ProductListBean;

import static pro.network.unniss.app.AppConfig.decimalFormat;
import static pro.network.unniss.app.AppConfig.emailKey;
import static pro.network.unniss.app.AppConfig.mypreference;
import static pro.network.unniss.app.AppConfig.phone;
import static pro.network.unniss.app.AppConfig.usernameKey;

public class PaymentActivity extends AppCompatActivity implements AddressItemClick {
    final int UPI_PAYMENT = 90;
    TextView grandtotal, subtotal, shippingTotal,viewAllCoupon,couponTotal;
    ProgressDialog pDialog;
    SharedPreferences sharedpreferences;
    Button paynow;
    TextView orderDiabled;
    LinearLayout walletLinear;
    private EditText couponsId;
    TextView walletTotal;
    int walletAmtUser;
    ProgressBar walletProgress;
    private DbCart db;
    private List<ProductListBean> productList = new ArrayList<>();
    private RadioButton cod, online, gpay;
    private ArrayList<Address> addressArrayList = new ArrayList<>();
    private AddressListAdapter addressListAdapter;
    private ProgressBar addressProgress;
    private int selectedAddressItem = 0;
    private Button addAddressBtn;
    private EditText comments;
    private MaterialButton applyCoupon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Checkout.preload(getApplicationContext());

        orderDiabled = findViewById(R.id.orderDiabled);
        addressProgress = findViewById(R.id.addressProgress);
        walletLinear = findViewById(R.id.walletLinear);
        walletProgress = findViewById(R.id.walletProgress);
        walletTotal = findViewById(R.id.walletTotal);
        couponsId = findViewById(R.id.couponsId);
        applyCoupon = findViewById(R.id.applyCoupon);
        couponTotal = findViewById(R.id.couponTotal);
        viewAllCoupon = findViewById(R.id.viewAllCoupon);
        SpannableString text = new SpannableString("View all Coupons");
        text.setSpan(new UnderlineSpan(), 0, text.toString().length() - 1, 0);


        comments = findViewById(R.id.comments);
        addAddressBtn = findViewById(R.id.Add);
        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog(false, null);
            }
        });
        viewAllCoupon.setText(text);

        viewAllCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, CouponsActivity.class);
                startActivityForResult(intent, 21);// Activity is started with requestCode 2
            }
        });
        applyCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponsId.getText().toString().length() > 0) {
                    getCouponVal();
                    couponsId.setError(null);
                } else {
                    couponsId.setError("Enter valid Coupon");
                }
            }
        });
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new DbCart(this);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);


        grandtotal = findViewById(R.id.grandtotal);
        shippingTotal = findViewById(R.id.shippingTotal);
        subtotal = findViewById(R.id.subtotal);
        grandtotal.setText(getIntent().getStringExtra("total"));


        cod = findViewById(R.id.cod);
        online = findViewById(R.id.online);
        gpay = findViewById(R.id.gpay);

        online.setChecked(true);

        cod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cod.setChecked(true);
                    online.setChecked(false);
                    gpay.setChecked(false);
                } else {
                    cod.setChecked(false);
                }
            }
        });

        online.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cod.setChecked(false);
                    online.setChecked(true);
                    gpay.setChecked(false);
                } else {
                    online.setChecked(false);
                }
            }
        });

        gpay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cod.setChecked(false);
                    online.setChecked(false);
                    gpay.setChecked(true);
                } else {
                    gpay.setChecked(false);
                }
            }
        });


        paynow = findViewById(R.id.paynow);
        paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressArrayList.size() <= 0) {
                    Toast.makeText(PaymentActivity.this, "Enter valid address", Toast.LENGTH_SHORT).show();
                } else if (online.isChecked() || gpay.isChecked()) {
                    String emailVal = sharedpreferences.getString(AppConfig.emailKey, "");
                    if (AppConfig.emailValidator(emailVal)) {
                        createAccessToken();
                    } else {
                        showChangeEmailDialog();
                    }
                } else if (cod.isChecked()) {
                    createOrder(null);
                }
            }
        });


        RecyclerView address_list = findViewById(R.id.address_list);
        addressListAdapter = new AddressListAdapter(this, addressArrayList, this);
        final LinearLayoutManager addManager10 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        address_list.setLayoutManager(addManager10);
        address_list.setAdapter(addressListAdapter);
        Drawable backArrow = getResources().getDrawable(R.drawable.ic_round_arrow_back_24);
        backArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        couponsId.setText("");
        getAllCart(0, 0, "0");
        fetchAddressList();

        //getWalletAmount();
    }

    private void showChangeEmailDialog() {
        final RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(PaymentActivity.this);
        LayoutInflater inflater = PaymentActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.bottom_reviews_layout, null);

        final TextInputEditText review = dialogView.findViewById(R.id.review);
        TextInputLayout reviewTxt = dialogView.findViewById(R.id.reviewTxt);
        TextView title = dialogView.findViewById(R.id.title);
        final Button submit = dialogView.findViewById(R.id.submit);
        title.setText("Update Valid Mail");
        reviewTxt.setHint("Enter Email");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (review.getText().toString().length() <= 0 || !AppConfig.emailValidator(review.getText().toString())) {
                    Toast.makeText(PaymentActivity.this, "Enter Valid Email", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    changeEmail(review.getText().toString(), mBottomSheetDialog);
                }
            }
        });
        mBottomSheetDialog.setContentView(dialogView);
        review.requestFocus();
        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RoundedBottomSheetDialog d = (RoundedBottomSheetDialog) dialog;
                        FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }, 0);
            }
        });
        mBottomSheetDialog.show();
    }

    private void changeEmail(final String email, final RoundedBottomSheetDialog mBottomSheetDialog) {
        String tag_string_req = "req_register";
        pDialog.setMessage("Updating profile  ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.PUT, AppConfig.UPDATE_EMAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    String msg = jObj.getString("message");
                    if (success) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(AppConfig.emailKey, email);
                        editor.commit();
                        createAccessToken();
                        mBottomSheetDialog.hide();
                    }
                    Toast.makeText(PaymentActivity.this, msg, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e("xxxxxxxxxx", e.toString());
                }
                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this,
                        "Slow network found.Try again later", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("email", email);
                localHashMap.put("user", sharedpreferences.getString(AppConfig.user_id, ""));
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    private float getGrandTotal() {
        float grandTotal = 0f;
        for (int i = 0; i < productList.size(); i++) {
            String qty = productList.get(i).qty;
            try {
                if (qty == null || !qty.matches("-?\\d+(\\.\\d+)?")) {
                    qty = "1";
                }
            } catch (Exception e) {

            }
            float maintotal = Float.parseFloat(productList.get(i).price) * Integer.parseInt(qty);
            grandTotal = grandTotal + maintotal;
        }
        return grandTotal;
    }

    private void createAccessToken() {
        Instamojo.setBaseUrl("https://api.instamojo.com/");
        String tag_string_req = "req_register";
        pDialog.setMessage("Creating...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                "https://api.instamojo.com/oauth2/token/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                String errorMessage = null;
                String transactionID = null;
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.has("error")) {
                        errorMessage = responseObject.getString("error");
                    } else {
                        String accessToken = responseObject.getString("access_token");
//                        transactionID = responseObject.getString("transaction_id");
                        createPreOrder(accessToken, UniqueID.getDeviceId(PaymentActivity.this));
                    }
                } catch (Exception e) {
                    errorMessage = "Failed to fetch Order tokens";
                }
                showToast(errorMessage);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "Some Network Error.", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("client_id", "Bqmz8iFZETReiHtLeeZSWEjgDtBAkhXXNzpxhQbp");
                localHashMap.put("client_secret", "i5VHmF5ccc8gFKxZVyW4D7tZEayDZgXR1xCG6Ww3sctrUzICOoNwKKNL74FsX9c5Qk3kd1pHknpuic9VAPvB5YnhyZddWhmUQe26i0D4D766SUK1K3HU6zeZiAWUOlfJ");
                localHashMap.put("grant_type", "client_credentials");
                localHashMap.put("env", "Production");
                return localHashMap;
            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> localHashMap = super.getHeaders();
//                localHashMap.put("Content-Type", "application/x-www-form-urlencoded");
//                return localHashMap;
//            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void createPreOrder(String accessToken, String transactionID) {
        String name = sharedpreferences.getString(usernameKey, "");
        final String email = sharedpreferences.getString(emailKey, "");
        String phone = sharedpreferences.getString(AppConfig.phone, "");
        String amount = Float.parseFloat(subtotal.getText().toString().replace("???", "")) + "";
      //  String amount = "10.0";
        String description = comments.getText().toString().length() > 0 ? comments.getText().toString() : "comments";

        //Create the Order
        Order order = new Order(accessToken, sharedpreferences.getString(AppConfig.user_id, "") + "_" + System.currentTimeMillis(), name, email, phone, amount, description);

        //set webhook
        order.setWebhook("https://unniss.in/");

        //Validate the Order
        if (!order.isValid()) {
            //oops order validation failed. Pinpoint the issue(s).

            if (!order.isValidName()) {
                showToast("Buyer name is invalid");
            }

            if (!order.isValidEmail()) {
                showToast("Buyer email is invalid");
            }

            if (!order.isValidPhone()) {
                showToast("Buyer phone is invalid");
            }

            if (!order.isValidAmount()) {
                showToast("Amount is invalid or has more than two decimal places");
            }

            if (!order.isValidDescription()) {
                showToast("Description is invalid");
            }

            if (!order.isValidTransactionID()) {
                showToast("Transaction is Invalid");
            }

            if (!order.isValidRedirectURL()) {
                showToast("Redirection URL is invalid");
            }

            if (!order.isValidWebhook()) {
                showToast("Webhook URL is invalid");
            }

            return;
        }

        showDialog();
        com.instamojo.android.network.Request request = new com.instamojo.android.network.Request(order,
                new OrderRequestCallback() {
                    @Override
                    public void onFinish(final Order order, final Exception error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideDialog();
                                if (error != null) {
                                    if (error instanceof Errors.ConnectionError) {
                                        showToast("No internet connection");
                                    } else if (error instanceof Errors.ServerError) {
                                        showToast("Server Error. Try again");
                                    } else if (error instanceof Errors.AuthenticationError) {
                                        showToast("Access token is invalid or expired. Please Update the token!!");
                                    } else if (error instanceof Errors.ValidationError) {
                                        // Cast object to validation to pinpoint the issue
                                        Errors.ValidationError validationError = (Errors.ValidationError) error;

                                        if (!validationError.isValidTransactionID()) {
                                            showToast("Transaction ID is not Unique");
                                            return;
                                        }

                                        if (!validationError.isValidRedirectURL()) {
                                            showToast("Redirect url is invalid");
                                            return;
                                        }

                                        if (!validationError.isValidWebhook()) {
                                            showToast("Webhook url is invalid");
                                            return;
                                        }

                                        if (!validationError.isValidPhone()) {
                                            showToast("Buyer's Phone Number is invalid/empty");
                                            return;
                                        }

                                        if (!validationError.isValidEmail()) {
                                            showToast("Buyer's Email is invalid/empty");
                                            return;
                                        }

                                        if (!validationError.isValidAmount()) {
                                            showToast("Amount is either less than Rs.9 or has more than two decimal places");
                                            return;
                                        }

                                        if (!validationError.isValidName()) {
                                            showToast("Buyer's Name is required");
                                            return;
                                        }
                                    } else {
                                        showToast(error.getMessage());
                                    }
                                    return;
                                }

                                startPreCreatedUI(order);
                            }
                        });
                    }
                });

        request.execute();
    }


    private void startPreCreatedUI(Order order) {
        //Using Pre created UI
        Intent intent = new Intent(getBaseContext(), PaymentDetailsActivity.class);
        intent.putExtra(Constants.ORDER, order);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    private void createOrder(final String orderId) {
        String tag_string_req = "req_register";
        pDialog.setMessage("Creating...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.ORDER_CREATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d("Register Response: ", response);
                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    String msg = jObj.getString("message");
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    if (success) {

                        MyorderBean order = new MyorderBean();
                        db.deleteAllInCart("guest");
                        db.deleteAllInCart(sharedpreferences.getString(AppConfig.user_id, ""));

                        JSONObject jsonObject = jObj.getJSONObject("data");
                        order.setId(jsonObject.getString("id"));
                        order.setAmount(jsonObject.getString("price"));
                        order.setQuantity(jsonObject.getString("quantity"));
                        order.setStatus(jsonObject.getString("status"));
                        order.setitems(jsonObject.getString("items"));
                        order.setRqty_type(jsonObject.has("rqtyType") ?
                                jsonObject.getString("rqtyType") : "NA");
                        order.setProduct_price(jsonObject.has("product_price") ?
                                jsonObject.getString("product_price") : "NA");
                        order.setToPincode(jsonObject.has("toPincode") ?
                                jsonObject.getString("toPincode") : "NA");
                        order.setDelivery(jsonObject.has("delivery") ?
                                jsonObject.getString("delivery") : "NA");
                        order.setPayment(jsonObject.has("payment") ?
                                jsonObject.getString("payment") : "NA");
                        order.setGrandCost(jsonObject.has("grandCost") ?
                                jsonObject.getString("grandCost") : "NA");
                        order.setShipCost(jsonObject.has("shipCost") ?
                                jsonObject.getString("shipCost") : "NA");
                        order.setPayment_method_title(jsonObject.getString("payment_method_title"));
                        order.setAddress(jsonObject.getString("address"));
                        order.setPaymentId(jsonObject.has("paymentId") ?
                                jsonObject.getString("paymentId") : "NA");
                        order.setComments(jsonObject.has("comments") ?
                                jsonObject.getString("comments") : "NA");
                        order.setDeliveryTime(jsonObject.has("deliveryTime") ?
                                jsonObject.getString("deliveryTime") : "NA");
                        order.setName(jsonObject.has("name") ?
                                jsonObject.getString("name") : "NA");
                        order.setPhone(jsonObject.has("phone") ?
                                jsonObject.getString("phone") : "NA");
                        order.setSize(jsonObject.has("size") ?
                                jsonObject.getString("size") : "NA");
                        order.setAddressOrg(jsonObject.has("addressOrg") ?
                                jsonObject.getString("addressOrg") : "NA");
                        order.setCoupon(jsonObject.has("coupon") ?
                                jsonObject.getString("coupon") : "NA");
                        order.setCouponCost(jsonObject.has("couponCost") ?
                                jsonObject.getString("couponCost") : "NA");
                        order.setTrackId(jsonObject.has("trackId") ?
                                jsonObject.getString("trackId") : "NA");
                        order.setCreatedon(jsonObject.getString("createdon"));

                        //  order.setWalletAmount("- ???" + jsonObject.getString("wallet"));

                        ObjectMapper mapper = new ObjectMapper();
                        Object listBeans = new Gson().fromJson(jsonObject.getString("items"),
                                Object.class);
                        ArrayList<ProductListBean> accountList = mapper.convertValue(
                                listBeans,
                                new TypeReference<ArrayList<ProductListBean>>() {
                                }
                        );
                        order.setProductBeans(accountList);

                        getToOrderPage(order);
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Some Network Exception", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Some Network Exception", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "Some Network Error.", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("toPincode", "NA");
                localHashMap.put("delivery", "NA");

                localHashMap.put("payment", getPaymentMode());
                localHashMap.put("address", addressArrayList.get(selectedAddressItem).getId());
                // localHashMap.put("payment", getPaymentMode());
                localHashMap.put("coupon", couponsId.getText().toString());
             //   localHashMap.put("couponCost", couponTotal.getText().toString());
                if (orderId != null) {
                    localHashMap.put("paymentId", orderId);
                } else {
                    localHashMap.put("paymentId", "cod");
                }
                localHashMap.put("comments", comments.getText().toString());
                localHashMap.put("grandCost", subtotal.getText().toString());
                localHashMap.put("shipCost", shippingTotal.getText().toString());
             //   localHashMap.put("price", subtotal.getText().toString());
               /* String wallet = walletTotal.getText().toString().replace("- ???", "")
                        .split("\\.")[0];
                localHashMap.put("wallet", wallet);*/
                ArrayList<ProductListBean> productList = new ArrayList<>();
                productList = db.getAllProductsInCart(sharedpreferences.getString(AppConfig.user_id, ""));
                localHashMap.put("quantity", String.valueOf(productList.size()));
                //   localHashMap.put("status", "ordered");
                localHashMap.put("reason", "Ordered by " + sharedpreferences.getString(AppConfig.usernameKey, ""));
                localHashMap.put("user", sharedpreferences.getString(AppConfig.user_id, ""));
                for (int i = 0; i < productList.size(); i++) {
                    ProductListBean productListBean = new ProductListBean();
                    productListBean.setName(productList.get(i).getModel());
                    productListBean.setQty(productList.get(i).getQty());
                    productListBean.setRqty(productList.get(i).getRqty());
                    productListBean.setRqtyType(productList.get(i).getRqtyType());
                    productListBean.setId(productList.get(i).getId());
                    productListBean.setPrice(productList.get(i).price);
                    productListBean.setImage(productList.get(i).image);
                    productListBean.setSize(productList.get(i).size);
                    productListBean.setVariationId(productList.get(i).getVariationId());
                    localHashMap.put("items[]", new Gson().toJson(productListBean));
                }
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private String getPaymentMode() {
        if (online.isChecked()) {
            return "online";
        } else if (gpay.isChecked()) {
            return "gpay";
        }
        return "cod";
    }

    private void hideDialog() {

        if (this.pDialog.isShowing()) this.pDialog.dismiss();
    }

    private void showDialog() {

        if (!this.pDialog.isShowing()) this.pDialog.show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 21) {
            try {
                Coupon coupon = (Coupon) data.getSerializableExtra("data");
                couponsId.setText(coupon.getCoupon());
                getCouponVal();
            } catch (Exception e) {
                Log.e("xxxxxxxxxx", e.toString());
            }

        }
        if (requestCode == Constants.REQUEST_CODE && data != null) {
            String orderID = data.getStringExtra(Constants.ORDER_ID);
            String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
            String paymentID = data.getStringExtra(Constants.PAYMENT_ID);
            // Check transactionID, orderID, and orderID for null before using them to check the Payment status.

            if (transactionID != null || paymentID != null) {
                createOrder(paymentID);
            } else {
                showToast("Oops!! Payment was cancelled");
            }
        }
    }


    private void showBottomDialog(final boolean isUpdate, final Address addressOld) {
        final RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(PaymentActivity.this);
        LayoutInflater inflater = PaymentActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.bottom_address_layout, null);

        final TextInputEditText name = dialogView.findViewById(R.id.name);
        final TextInputEditText address = dialogView.findViewById(R.id.address);
        final TextInputEditText mobile = dialogView.findViewById(R.id.mobile);
        final TextInputEditText alternateMobile = dialogView.findViewById(R.id.alternateMobile);
        final TextInputEditText landmark = dialogView.findViewById(R.id.landmark);
        final TextInputEditText pincode = dialogView.findViewById(R.id.pincode);
        final TextInputLayout pincodeTxt = dialogView.findViewById(R.id.pincodeTxt);
        final TextInputEditText comments = dialogView.findViewById(R.id.comments);
        final ProgressBar pincodeProgress = dialogView.findViewById(R.id.pincodeProgress);

        final Button addAddress = dialogView.findViewById(R.id.addAddress);

        if (isUpdate) {
            name.setText(addressOld.name);
            address.setText(addressOld.address);
            mobile.setText(addressOld.mobile);
            alternateMobile.setText(addressOld.alternateMobile);
            landmark.setText(addressOld.landmark);
            pincode.setText(addressOld.pincode);
            comments.setText(addressOld.comments);
            addAddress.setText("Update");
        }
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name. getText().toString().length() > 0 &&
                        address.getText().toString().length() > 0 &&
                        mobile.getText().toString().length() > 0 &&
                        alternateMobile.getText().toString().length() > 0 &&
                        landmark.getText().toString().length() > 0 &&
                        pincode.getText().toString().length() > 0) {

                    Address address1 = new Address(
                            name.getText().toString(),
                            address.getText().toString(),
                            mobile.getText().toString(),
                            alternateMobile.getText().toString(),
                            landmark.getText().toString(),
                            pincode.getText().toString(),
                            comments.getText().toString()
                    );
                    if (isUpdate) {
                        address1.setId(addressOld.id);
                    }
                    validatePincode(pincodeProgress, pincode, pincodeTxt, address1, isUpdate, mBottomSheetDialog);
                }
            }
        });
        pincode.requestFocus();

        mBottomSheetDialog.setContentView(dialogView);
        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RoundedBottomSheetDialog d = (RoundedBottomSheetDialog) dialog;
                        FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }, 0);
            }
        });
        mBottomSheetDialog.show();
    }

    private void validatePincode(final ProgressBar progressBar,
                                 final TextInputEditText addressTxt,
                                 final TextInputLayout addressText,

                                 final Address address1, final boolean isUpdate, final RoundedBottomSheetDialog mBottomSheetDialog) {

        String tag_string_req = "req_register";
        progressBar.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_PINCODE + "?pincode=" + addressTxt.getText().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        String pincode = jObj.getString("pincode");
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        boolean isValidPincode = false;
                        if (pincode.length() > 0) {
                            editor.putString("pincode", pincode);
                            isValidPincode = true;
                        }
                        if (isValidPincode) {
                            editor.putString("pincode", addressTxt.getText().toString());
                            addressText.setError(null);
                            editor.commit();
                            addOrUpdateAddress(address1, isUpdate, mBottomSheetDialog);
                        } else {
                            addressText.setError(getString(R.string.pincodeError));
                        }
                        editor.commit();
                    } else {
                        addressText.setError(getString(R.string.pincodeError));
                    }
                } catch (Exception e) {
                    Log.e("xxxxxxxxxx", e.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("pincode", addressTxt.getText().toString());
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void addOrUpdateAddress(final Address address1, final boolean isUpdate,
                                    final RoundedBottomSheetDialog mBottomSheetDialog) {
        String tag_string_req = "req_register";
        pDialog.setMessage("Updating Address ...");
        showDialog();
        String addAddress = AppConfig.ADD_ADDRESS;
        if (isUpdate) {
            addAddress = AppConfig.UPDATE_ADDRESS;
        }
        StringRequest strReq = new StringRequest(isUpdate ? Request.Method.PUT :
                Request.Method.POST, addAddress, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    String msg = jObj.getString("message");
                    if (success) {
                        mBottomSheetDialog.hide();
                        fetchAddressList();
                    }
                } catch (Exception e) {
                    Log.e("xxxxxxxxxx", e.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),
                        "Slow network found.Try again later", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                if (isUpdate) {
                    localHashMap.put("id", address1.id);
                }
                localHashMap.put("userId", sharedpreferences.getString(AppConfig.user_id, ""));
                localHashMap.put("name", address1.name);
                localHashMap.put("address", address1.address);
                localHashMap.put("pincode", address1.pincode);
                localHashMap.put("mobile", address1.mobile);
                localHashMap.put("alternativeMobile", address1.alternateMobile);
                localHashMap.put("landmark", address1.landmark);
                localHashMap.put("pincode", address1.pincode);
                localHashMap.put("comments", address1.comments);

                return localHashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onSelectItem(int position) {
        selectedAddressItem = position;
        addressListAdapter.notifyData(position);
    }

    @Override
    public void ondeleteClick(int position) {
        deleteAddressList(position);
    }

    @Override
    public void onEditClick(int position) {
        showBottomDialog(true, addressArrayList.get(position));
    }

    private void deleteAddressList(final int position) {
        String tag_string_req = "req_register";
        addressProgress.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.DELETE,
                AppConfig.DELETE_ADDRESS + "?userId=" + sharedpreferences.getString(AppConfig.user_id, "") + "&id=" + addressArrayList.get(position).getId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                addressProgress.setVisibility(View.GONE);
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        fetchAddressList();
                    }
                    Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                addressProgress.setVisibility(View.GONE);
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("userId", sharedpreferences.getString(AppConfig.user_id, ""));
                localHashMap.put("id", addressArrayList.get(position).getId());
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void fetchAddressList() {
        String tag_string_req = "req_register";
        addressProgress.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.GET_ALL_ADDRESS + "?userId=" + sharedpreferences.getString(AppConfig.user_id, ""), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                addressProgress.setVisibility(View.GONE);
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    addressArrayList = new ArrayList<>();
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Address address = new Address();
                            address.setId(jsonObject.getString("id"));
                            address.setName(jsonObject.getString("name"));
                            address.setAddress(jsonObject.getString("address"));
                            address.setMobile(jsonObject.getString("mobile"));
                            address.setAlternateMobile(jsonObject.getString("alternativeMobile"));
                            address.setLandmark(jsonObject.getString("landmark"));
                            address.setPincode(jsonObject.getString("pincode"));
                            address.setComments(jsonObject.getString("comments"));
                            addressArrayList.add(address);
                        }
                    }
                    addressListAdapter.notifyData(addressArrayList);
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                addressProgress.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("userId", sharedpreferences.getString(AppConfig.user_id, ""));
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void getToOrderPage(MyorderBean orderId) {
        Intent intent = new Intent(PaymentActivity.this, SingleOrderPage.class);
        intent.putExtra("data", orderId);
        intent.putExtra("from", "payment");
        startActivity(intent);
    }

    private void getCouponVal() {
        String tag_string_req = "req_register";
        pDialog.setMessage("Updating coupon ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_COUPON+ "?code="+couponsId.getText().toString().toUpperCase(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                  //  boolean success = jObj.getBoolean("success");
                    int success = jObj.getInt("success");
                    String msg = jObj.getString("message");
                    int minimumOrder = 100;
                    try {
                        minimumOrder = Integer.parseInt(jObj.getString("minimum_amount"));
                    } catch (Exception e) {

                    }

                    if (getGrandTotal() >= minimumOrder) {
                        String isPercent = jObj.isNull("isPercent") ?
                                "0" : jObj.getString("isPercent");

                        int percentage = 0;

                        try {
                            percentage = Integer.parseInt(jObj.getString("percentage"));
                        } catch (Exception e) {

                        }
                        if (success == 1) {
                            getAllCart((int)Float.parseFloat(jObj.getString("amount")),
                                    percentage, isPercent);
                            AppConfig.hideSoftKeyboard(couponsId, PaymentActivity.this);
                        }

                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Order value should above ???" + minimumOrder + " to apply this coupon.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("xxxxxxxxxx", e.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),
                        "Slow network found.Try again later", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("coupon", couponsId.getText().toString().toUpperCase());
                localHashMap.put("userId", sharedpreferences.getString(AppConfig.user_id, ""));
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(AppConfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getWalletAmount() {
        String tag_string_req = "req_register";
        walletProgress.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.GET_USER_WALLET + "?userId="
                + sharedpreferences.getString(AppConfig.user_id, ""), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                walletProgress.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1) {
                        walletAmtUser = Integer.parseInt(jObj.getString("totalAmt"));
                        float subtotalTemp = Float.parseFloat(subtotal.getText().toString().replace("???", ""));
                        float grandtotalTemp = Float.parseFloat(grandtotal.getText().toString().replace("???", ""));
                        int walletDiscountAmt = (int) ((grandtotalTemp / 100) *
                                PreferenceBean.getInstance().getInvoicePercent());
                        if (walletAmtUser > 0) {
                            if (walletAmtUser >= walletDiscountAmt) {
                                walletAmtUser = walletDiscountAmt;
                            }
                            subtotalTemp = subtotalTemp - walletAmtUser;
                            walletLinear.setVisibility(View.VISIBLE);
                            walletTotal.setText("- ???" + decimalFormat.format(walletAmtUser) + ".00");
                        } else {
                            walletLinear.setVisibility(View.GONE);
                        }
                        subtotal.setText("???" + decimalFormat.format(subtotalTemp) + ".00");
                    }
                } catch (Exception e) {
                }
                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                walletProgress.setVisibility(View.GONE);
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


    private void getAllCart(int amount, int percentage, String isPercent) {
        productList.clear();
        productList = db.getAllProductsInCart(sharedpreferences.getString(AppConfig.user_id, ""));
        float grandTotal = getGrandTotal();
        grandtotal.setText("???" + decimalFormat.format(grandTotal) + ".00");

        if (couponsId.getText().toString().length() > 0) {
            if (isPercent.equalsIgnoreCase("1")) {
                int discountPercentage = (int) ((grandTotal / 100) * percentage);
                if (discountPercentage >= amount) {
                    couponTotal.setText("???" + decimalFormat.format(amount) + ".00");
                    grandTotal = grandTotal - amount;
                } else {
                    couponTotal.setText("???" + decimalFormat.format(discountPercentage) + ".00");
                    grandTotal = grandTotal - discountPercentage;
                }
            } else {
                couponTotal.setText("???" + decimalFormat.format(amount) + ".00");
                grandTotal = grandTotal - amount;
            }
        } else {
            couponTotal.setText("???" + decimalFormat.format(0) + ".00");
        }

        shippingTotal.setText("???" + decimalFormat.format(PreferenceBean.getInstance().getShippingCost()) + ".00");
        grandTotal = grandTotal + PreferenceBean.getInstance().getShippingCost();
        if (walletAmtUser > 0) {
            grandTotal = grandTotal - walletAmtUser;
            walletLinear.setVisibility(View.VISIBLE);
            walletTotal.setText("- ???" + decimalFormat.format(walletAmtUser) + ".00");
        } else {
            walletLinear.setVisibility(View.GONE);
        }

        subtotal.setText("???" + decimalFormat.format(grandTotal) + ".00");

        /*if (grandTotal > 1500) {
            cod.setVisibility(View.GONE);
        } else {
            cod.setVisibility(View.VISIBLE);
        }
*/
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void showToast(final String message) {
        Log.e("Payment Eror" ,message.toString());
        try {
            Toast.makeText(PaymentActivity.this, message, Toast.LENGTH_LONG).show();

        }catch (Exception e){
            Log.e("Payment Eror" ,e.toString());
        }

    }

}
