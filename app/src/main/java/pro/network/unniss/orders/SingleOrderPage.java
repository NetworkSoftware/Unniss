package pro.network.unniss.orders;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import pro.network.unniss.HomeActivity;
import pro.network.unniss.R;
import pro.network.unniss.app.AppConfig;
import pro.network.unniss.app.AppController;
import pro.network.unniss.app.HeaderFooterPageEvent;
import pro.network.unniss.app.PdfConfig;
import pro.network.unniss.product.ProductListBean;

import static pro.network.unniss.app.AppConfig.FETCH_ADDRESS;
import static pro.network.unniss.app.AppConfig.decimalFormat;


public class SingleOrderPage extends AppCompatActivity implements ReturnOnClick {

    RecyclerView myorders_list;
    MyOrderListSubAdapter myOrderListAdapter;
    SharedPreferences sharedpreferences;
    TextView address;
    TextView delivery;
    TextView payment;
    TextView grandtotal;
    TextView shippingTotal;
    TextView orderId, name, phone, quantity, pincode, createdon;
    TextView subtotal, status, paymentId, deliveryTime, comments,trackId;
    TextView walletTotal;
    ProgressDialog pDialog;
    LinearLayout track_lay;
    LinkedHashMap<String, String> stringStringMap = new LinkedHashMap<>();
    private ArrayList<ProductListBean> myorderBeans = new ArrayList<>();

    private MaterialButton invoice;
    private MyorderBean myorderBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order);


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        sharedpreferences = getSharedPreferences(AppConfig.mypreference, Context.MODE_PRIVATE);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setTitle("My Orders");
        //   getSupportActionBar().setTitle("Order id:#" + (myorderBean.getId()));


        myorders_list = findViewById(R.id.myorders_list);
        myOrderListAdapter = new MyOrderListSubAdapter(getApplicationContext(), myorderBeans);
        final LinearLayoutManager addManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        myorders_list.setLayoutManager(addManager1);
        myorders_list.setAdapter(myOrderListAdapter);
        invoice = findViewById(R.id.invoice);

        address = findViewById(R.id.address);
        delivery = findViewById(R.id.delivery);
        payment = findViewById(R.id.payment);
        grandtotal = findViewById(R.id.grandtotal);
        shippingTotal = findViewById(R.id.shippingTotal);
        subtotal = findViewById(R.id.subtotal);
        status = findViewById(R.id.status);
        paymentId = findViewById(R.id.paymentId);
        comments = findViewById(R.id.comments);
        deliveryTime = findViewById(R.id.deliveryTime);
        trackId = findViewById(R.id.trackId);
        orderId = findViewById(R.id.id);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        pincode = findViewById(R.id.toPincode);
        quantity = findViewById(R.id.quantity);
        createdon = findViewById(R.id.createdon);
        track_lay = findViewById(R.id.track_lay);


        invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SingleOrderPage.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SingleOrderPage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                } else {
                    printFunction();
                }
            }
        });
        getValuesFromIntent();
    }

    private void getValuesFromIntent() {
        try {
            myorderBean = (MyorderBean) getIntent().getSerializableExtra("data");
            address.setText(myorderBean.address);
            stringStringMap.put("Order Id", "SCF" + myorderBean.getId());
            orderId.setText(myorderBean.id);
            stringStringMap.put("Address", myorderBean.address);
            status.setText(myorderBean.status);
            stringStringMap.put("Status", myorderBean.status);
            delivery.setText(myorderBean.delivery);

            stringStringMap.put("name", myorderBean.name);
            name.setText(myorderBean.name);
            stringStringMap.put("phone", myorderBean.phone);
            phone.setText(myorderBean.phone);
            stringStringMap.put("toPincode", myorderBean.toPincode);
            pincode.setText(myorderBean.toPincode);
            stringStringMap.put("quantity", myorderBean.quantity);
            quantity.setText(myorderBean.quantity);
            stringStringMap.put("createdon", myorderBean.createdon);
            createdon.setText(myorderBean.createdon);
            stringStringMap.put("Delivery", myorderBean.delivery);
            payment.setText(myorderBean.payment);
            stringStringMap.put("Payment mode", myorderBean.payment);
            paymentId.setText(myorderBean.paymentId);
            stringStringMap.put("Payment ID", myorderBean.paymentId);
            comments.setText(myorderBean.comments);
            stringStringMap.put("Comments", myorderBean.comments);
            trackId.setText(myorderBean.trackId);
            stringStringMap.put("trackId", myorderBean.trackId);
            track_lay.setVisibility(View.VISIBLE);
            if(myorderBean.trackId.equalsIgnoreCase("0")){
                track_lay.setVisibility(View.GONE);
            }
            comments.setVisibility(View.VISIBLE);
            if (myorderBean.comments.equalsIgnoreCase("NA")) {
                comments.setVisibility(View.GONE);
            }
            deliveryTime.setText(myorderBean.deliveryTime);
            stringStringMap.put("Delivery time", myorderBean.deliveryTime);
            if (myorderBean.getDeliveryTime().equalsIgnoreCase("NA")) {
                deliveryTime.setVisibility(View.GONE);
            } else {
                deliveryTime.setVisibility(View.VISIBLE);
            }
            grandtotal.setText(myorderBean.grandCost);
            stringStringMap.put("Date", myorderBean.createdOn);
            myorderBeans = myorderBean.productBeans;
            for (int i = 0; i < myorderBeans.size(); i++) {
                ProductListBean productListBean = myorderBeans.get(i);
                String qty = productListBean.getQty();
                try {
                    if (qty == null || !qty.matches("-?\\d+(\\.\\d+)?")) {
                        qty = "1";
                    }
                    float startValue = Float.parseFloat(productListBean.getPrice()) * Integer.parseInt(qty);
                    String s = productListBean.getQty() + "*" + productListBean.getPrice() + "/" +
                            productListBean.getQty() + " " + productListBean.getPrice() +
                            "=" + "₹" + decimalFormat.format(startValue) + ".00";
                    stringStringMap.put(productListBean.getCategory() + " " + productListBean.getBrand(),
                            s);
                } catch (Exception e) {

                }
            }
            stringStringMap.put("Grand total", myorderBean.grandCost);
            shippingTotal.setText("₹" +myorderBean.shipCost);
            stringStringMap.put("Shipping total", myorderBean.shipCost);

            subtotal.setText("₹" +myorderBean.grandCost);
            stringStringMap.put("Sub total",  myorderBean.grandCost);

            myOrderListAdapter.notifyData(myorderBeans);

            if (paymentId != null) {
                paymentId.setText(myorderBean.getPaymentId());
                if (myorderBean.getPayment().equalsIgnoreCase("NA")) {
                    paymentId.setVisibility(View.GONE);
                } else {
                    paymentId.setVisibility(View.VISIBLE);
                }
            }

            // getSupportActionBar().setTitle("Order id:#" + (myorderBean.getId()));

            fetchAddressById(myorderBean.getAddress());

        } catch (Exception e) {
            Log.e("xxxxxxxxx", e.toString());
        }

        //   getySupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

  /*  private void showShopMore() {
        if (getIntent().getStringExtra("from") != null &&
                getIntent().getStringExtra("from").equalsIgnoreCase("payment")) {
            shopMore.setVisibility(View.VISIBLE);
        } else {
            shopMore.setVisibility(View.GONE);
        }
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getValuesFromIntent();
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
    public void onBackPressed() {
        if (getIntent().getStringExtra("from") != null &&
                getIntent().getStringExtra("from").equalsIgnoreCase("payment")) {
            Intent intent = new Intent(SingleOrderPage.this, HomeActivity.class);
            startActivity(intent);
            finishAffinity();
        } else {
            finish();
        }
    }

    private void fetchAddressById(final String id) {
        String tag_string_req = "req_register_add";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                FETCH_ADDRESS + "?id=" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(jsonObject.getString("name")).append("\n");
                        StringBuilder stringBuilder1 = new StringBuilder();
                        stringBuilder1.append(jsonObject.getString("address")).append("\n");
                        stringBuilder1.append(jsonObject.getString("mobile")).append("\n");
                        stringBuilder1.append(jsonObject.getString("alternativeMobile")).append("\n");
                        stringBuilder1.append(jsonObject.getString("landmark")).append("\n");
                        stringBuilder1.append(jsonObject.getString("pincode"));
                        address.setText(stringBuilder.toString() + "\n" + stringBuilder1.toString());
                        myorderBean.setAddress(stringBuilder1.toString());
                        myorderBean.setName(jsonObject.getString("name"));
                        myorderBean.setPhone(jsonObject.getString("mobile"));

                        stringStringMap.put("Address", stringBuilder.toString());
                    }
                } catch (JSONException e) {
                    Log.e("Xxxxxxxxx", "Something went wrong");

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Xxxxxxxxx", "Something went wrong");
            }
        });
        strReq.setRetryPolicy(AppConfig.getTimeOut());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void showDialog() {
        if (!this.pDialog.isShowing()) this.pDialog.show();

    }

    private void hideDialog() {

        if (this.pDialog.isShowing()) this.pDialog.dismiss();
    }


    public void printFunction() {
        try {
            String path = getExternalCacheDir().getPath() + "/PDF";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();
            Log.d("PDFCreator", "PDF Path: " + path);
            File file = new File(dir, stringStringMap.get("Order Id") + "_" + System.currentTimeMillis() + ".pdf");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fOut = new FileOutputStream(file);


            Document document = new Document(PageSize.A4, 30, 28, 40, 119);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fOut);

            document.open();
            PdfConfig.addMetaData(document);
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            pdfWriter.setPageEvent(event);
            PdfConfig.addContent(document, myorderBean, SingleOrderPage.this);
            document.close();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                Intent intent = Intent.createChooser(target, "Open File");
                startActivity(intent);
            }

        } catch (Error | Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        hideDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // printFunction();
            } else {
                Toast.makeText(SingleOrderPage.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onReturnClick(String id) {

    }

    @Override
    public void onCartClick(MyorderBean position) {
        //
    }
}
