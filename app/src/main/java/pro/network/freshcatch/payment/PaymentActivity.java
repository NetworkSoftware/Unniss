package pro.network.freshcatch.payment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pro.network.freshcatch.R;
import pro.network.freshcatch.app.AppConfig;
import pro.network.freshcatch.app.AppController;
import pro.network.freshcatch.app.DbCart;
import pro.network.freshcatch.orders.MyOrderPage;
import pro.network.freshcatch.orders.MyorderBean;
import pro.network.freshcatch.product.ProductListBean;

import static pro.network.freshcatch.app.AppConfig.ORDER_CREATE;
import static pro.network.freshcatch.app.AppConfig.decimalFormat;
import static pro.network.freshcatch.app.AppConfig.mypreference;
import static pro.network.freshcatch.app.AppConfig.phone;

public class PaymentActivity extends FragmentActivity implements PaymentResultListener, AddressItemClick {
    final int UPI_PAYMENT = 90;
    TextView grandtotal, subtotal, shippingTotal;
    ProgressDialog pDialog;
    SharedPreferences sharedpreferences;
    Button paynow;
    TextView orderDiabled;
    private DbCart db;
    private List<ProductListBean> productList = new ArrayList<>();
    private RadioButton cod, online, gpay, express, schedule;
    private TextView viewAllCoupon, expressTxt;
    private ArrayList<Address> addressArrayList = new ArrayList<>();
    private AddressListAdapter addressListAdapter;
    private ProgressBar addressProgress;
    private int selectedAddressItem = 0;
    private Button addAddressBtn;
    private EditText comments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Checkout.preload(getApplicationContext());

        orderDiabled = findViewById(R.id.orderDiabled);
        viewAllCoupon = findViewById(R.id.viewAllCoupon);
        addressProgress = findViewById(R.id.addressProgress);

        SpannableString text = new SpannableString("View all Coupons");
        text.setSpan(new UnderlineSpan(), 0, text.toString().length() - 1, 0);
        viewAllCoupon.setText(text);


        comments = findViewById(R.id.comments);
        addAddressBtn = findViewById(R.id.Add);
        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog(false, null);
            }
        });



        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new DbCart(this);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);


        expressTxt = findViewById(R.id.expressTxt);
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
               if (online.isChecked()) {
                    callGpayOrOnline();
                } else if (gpay.isChecked()) {
                        callGpayOrOnline();

                } else if (cod.isChecked()) {
                    orderpage(null);
                }
            }
        });


//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        RecyclerView address_list = findViewById(R.id.address_list);
        addressListAdapter = new AddressListAdapter(this, addressArrayList, this);
        final LinearLayoutManager addManager10 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        address_list.setLayoutManager(addManager10);
        address_list.setAdapter(addressListAdapter);

        getAllCart(0, 0, "0");
        fetchAddressList();


    }

    private void callGpayOrOnline() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_gnSCNkGmN989fB");
        checkout.setImage(R.drawable.fresh_icon);
        JSONObject object = new JSONObject();
        try {

            object.put("name", "Chennai Fresh");
            object.put("description", "Reference No. #" +
                    sharedpreferences.getString(AppConfig.user_id, "") + "_" + System.currentTimeMillis());

            JSONObject theme = new JSONObject();
            object.put("color", "#3399cc");
            object.put("theme", theme);

            object.put("currency", "INR");
            object.put("send_sms_hash", false);
            object.put("amount", Float.parseFloat(subtotal.getText().toString().replace(
                    "₹", "").replace(".","")));//pass amount in currency subunits

            JSONObject prefill = new JSONObject();
            String string = sharedpreferences.getString(phone, "");
            prefill.put("contact", string);

            if (gpay.isChecked()) {
                prefill.put("method", "upi");
            }

            object.put("prefill", prefill);

            JSONObject readonly = new JSONObject();
            readonly.put("email", prefill);
            readonly.put("contact", prefill);
            object.put("readonly", readonly);

            checkout.open(PaymentActivity.this, object);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getAllCart(int amount, int percentage, String isPercent) {
        productList.clear();
        productList = db.getAllProductsInCart(sharedpreferences.getString(AppConfig.user_id, ""));
        float grandTotal = getGrandTotal();
        grandtotal.setText("₹" + decimalFormat.format(grandTotal) + ".00");



        shippingTotal.setText("₹" + decimalFormat.format(30) + ".00");
        grandTotal = grandTotal + 30;

        subtotal.setText("₹" + decimalFormat.format(grandTotal) + ".00");

        if (grandTotal > 1500) {
            cod.setVisibility(View.GONE);
        } else {
            cod.setVisibility(View.VISIBLE);
        }


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

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(getApplicationContext(), "Successfully Paid" + " - " + s, Toast.LENGTH_SHORT).show();
        orderpage(s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getApplicationContext(), "Payment Failed", Toast.LENGTH_SHORT).show();
    }

    private void orderpage(final String orderId) {
        String tag_string_req = "req_register";
        pDialog.setMessage("Processing ...");
        showDialog();
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                ORDER_CREATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response.split("0000")[1]);
                    boolean success = jsonObject.getBoolean("success");
                    String msg = jsonObject.getString("message");
                    if (success) {


                        MyorderBean order = new MyorderBean();

                        ArrayList<ProductListBean> productList = new ArrayList<>();
                        productList = db.getAllProductsInCart(sharedpreferences.getString(AppConfig.user_id, ""));
                        order.setQuantity(String.valueOf(productList.size()));
                        order.setStatus("ordered");
                        order.setReson("Ordered by " + sharedpreferences.getString(AppConfig.usernameKey, ""));
                        ArrayList<ProductListBean> productListBeans = new ArrayList<>();
                        for (int i = 0; i < productList.size(); i++) {
                            ProductListBean productListBean = productList.get(i);
                            ArrayList<String> urls = new Gson().fromJson(productListBean.image, (Type) List.class);
                            productListBean.setImage(urls.get(0));
                            productListBeans.add(productListBean);
                        }
                        order.setitems(new Gson().toJson(productListBeans));
                        order.setProductBeans(productListBeans);

                        db.deleteAllInCart("guest");
                        db.deleteAllInCart(sharedpreferences.getString(AppConfig.user_id, ""));

                        order.setId(jsonObject.getString("orderId"));
                        order.setAmount(subtotal.getText().toString());
                        order.setAddress(addressArrayList.get(selectedAddressItem).getId());
                        order.setToPincode("");
                        order.setDelivery(express.isChecked() ? "express" : "schedule");
                        order.setPayment(getPaymentMode());
                        order.setGrandCost(grandtotal.getText().toString());
                        order.setShipCost(shippingTotal.getText().toString());
                        order.setDeliveryTime(expressTxt.getText().toString());
                        order.setComments(comments.getText().toString());
                        order.setPaymentId(orderId);
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date = new Date();

                        order.setCreatedon(formatter.format(date));


                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
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
                localHashMap.put("address", addressArrayList.get(selectedAddressItem).getId());
                localHashMap.put("delivery", express.isChecked() ? "express" : "schedule");
                localHashMap.put("payment", getPaymentMode());
                if (orderId != null) {
                    localHashMap.put("paymentId", orderId);
                } else {
                    localHashMap.put("paymentId", "cod");
                }
                localHashMap.put("comments", comments.getText().toString());
                localHashMap.put("deliveryTime", expressTxt.getText().toString());
                localHashMap.put("grandCost", grandtotal.getText().toString());
                localHashMap.put("shipCost", shippingTotal.getText().toString());
                localHashMap.put("price", subtotal.getText().toString());

                ArrayList<ProductListBean> productList = new ArrayList<>();
                productList = db.getAllProductsInCart(sharedpreferences.getString(AppConfig.user_id, ""));
                localHashMap.put("quantity", String.valueOf(productList.size()));
                localHashMap.put("status", "ordered");
                localHashMap.put("reason", "Ordered by " + sharedpreferences.getString(AppConfig.usernameKey, ""));
                localHashMap.put("user", sharedpreferences.getString(AppConfig.user_id, ""));
                ArrayList<ProductListBean> productListBeans = new ArrayList<>();
                for (int i = 0; i < productList.size(); i++) {
                    ProductListBean productListBean = productList.get(i);
                    ArrayList<String> urls = new Gson().fromJson(productListBean.image, (Type) List.class);
                    productListBean.setImage(urls.get(0));
                    productListBeans.add(productListBean);
                }
                localHashMap.put("items", new Gson().toJson(productListBeans));

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

        }
    }


    private void showBottomDialog(boolean isUpdate, Address addressOld) {
        final RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(PaymentActivity.this);
        LayoutInflater inflater = PaymentActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.bottom_address_layout, null);

        final TextInputEditText name = dialogView.findViewById(R.id.name);
        final TextInputEditText address = dialogView.findViewById(R.id.address);
        final TextInputEditText mobile = dialogView.findViewById(R.id.mobile);
        final TextInputEditText alternateMobile = dialogView.findViewById(R.id.alternateMobile);
        final TextInputEditText landmark = dialogView.findViewById(R.id.landmark);
        final TextInputEditText pincode = dialogView.findViewById(R.id.pincode);
        final TextInputEditText comments = dialogView.findViewById(R.id.comments);

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
                if (name.getText().toString().length() > 0 &&
                        address.getText().toString().length() > 0 &&
                        mobile.getText().toString().length() > 0 &&
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

    private void addOrUpdateAddress(final Address address1, final boolean isUpdate, final RoundedBottomSheetDialog mBottomSheetDialog) {
        String tag_string_req = "req_register";
        pDialog.setMessage("Updating Address ...");
        showDialog();
        String addAddress = AppConfig.ADD_ADDRESS;
        if (isUpdate) {
            addAddress = AppConfig.UPDATE_ADDRESS;
        }
        StringRequest strReq = new StringRequest(Request.Method.POST, addAddress, new Response.Listener<String>() {
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
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.DELETE_ADDRESS, new Response.Listener<String>() {
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
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.GET_ALL_ADDRESS, new Response.Listener<String>() {
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








}