package com.exoncloud.mobi.distributeapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.exoncloud.mobi.distributeapp.adapters.VoucherAdapter;
import com.exoncloud.mobi.distributeapp.adapters.VoucherAdapter08to60;
import com.exoncloud.mobi.distributeapp.adapters.VoucherAdapter61to75;
import com.exoncloud.mobi.distributeapp.adapters.VoucherAdapter76to90;
import com.exoncloud.mobi.distributeapp.adapters.VoucherAdapterOver90;
import com.exoncloud.mobi.distributeapp.model.SessionManager;
import com.exoncloud.mobi.distributeapp.model.Voucher;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView mRestaurantRecyclerView;
    private VoucherAdapter mAdapter;
    private ArrayList<Voucher> mRestaurantCollection;

    private RecyclerView m08to60RecyclerView;
    private VoucherAdapter08to60 mVoucherAdapter08to60;
    private ArrayList<Voucher> m08to60List;

    private RecyclerView m61to75RecyclerView;
    private VoucherAdapter61to75 mVoucherAdapter61to75;
    private ArrayList<Voucher> m61to75List;

    private RecyclerView m76to90RecyclerView;
    private VoucherAdapter76to90 mVoucherAdapter76to90;
    private ArrayList<Voucher> m76to90List;

    private RecyclerView mOver90RecyclerView;
    private VoucherAdapterOver90 mVoucherAdapterOver90;
    private ArrayList<Voucher> mOver90List;

    public static int customer_id= 0;
    public static String customer_name= "";

    private static double days0to08total= 0;
    private static double days08to61total= 0;
    private static double days61to75total= 0;
    private static double days76to90total= 0;
    private static double daysOver90total= 0;

    private static double days0to08paid= 0;
    private static double days08to61paid= 0;
    private static double days61to75paid= 0;
    private static double days76to90paid= 0;
    private static double daysOver90paid= 0;

    private static double days0to08due= 0;
    private static double days08to61due= 0;
    private static double days61to75due= 0;
    private static double days76to90due= 0;
    private static double daysOver90due= 0;

    public static String total_invoice_amount= "";
    public static String total_invoice_paid_amount= "";
    public static String total_invoice_due_amount= "";


    // Session Manager Class
    SessionManager session;

    // Button Logout
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Session class instance
        session = new SessionManager(getApplicationContext());


        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        // Button logout
        btnLogout = (Button) findViewById(R.id.log_out_button);

        /**
         * Logout button click event
         * */
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Clear the session data
                // This will clear all session data and
                // redirect user to LoginActivity
                session.logoutUser();
            }
        });

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        String name = user.get(SessionManager.KEY_NAME);

        // name
        String DN = user.get(SessionManager.KEY_DN);

        TextView lu_name = (TextView) findViewById(R.id.loged_user_name);
        lu_name.setText(name);

        TextView daily_number = (TextView) findViewById(R.id.daily_number);
        daily_number.setText(DN);


//        Button button = (Button) findViewById(R.id.search_button);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                AutoCompleteTextView source = (AutoCompleteTextView) findViewById(R.id.main_customer_id);
//                String Source = source.getText().toString();
//                String result = Source.split("%")[1];
//                customer_id =result;
//                source.setText("");
//
//                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//
//                init();
//                new FetchDataTask().execute();
//
//                init2();
//                new FetchDataTask31to60().execute();
//
//
//
//            }
//        });

        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.main_customer_id);

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);

                String result = selectedItem.split("%")[1];
                customer_id = Integer.valueOf(result);

                AutoCompleteTextView customerSearch = (AutoCompleteTextView) findViewById(R.id.main_customer_id);
                customerSearch.setText("");

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                init();
                new FetchDataTaskDays0to07().execute();

                init2();
                new FetchDataTaskDays08to60().execute();

                init3();
                new FetchDataTaskDays61to75().execute();

                init4();
                new FetchDataTaskDays76to90().execute();

                init5();
                new FetchDataTaskDaysOver90().execute();

                View auto_pay = findViewById(R.id.auto_pay);
                auto_pay.setVisibility(View.VISIBLE);


            }
        });


        new FetchDataTaskSearch().execute();

        Button button = (Button) findViewById(R.id.auto_pay);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                double total_amount = days0to08total+days08to61total+days61to75total+days76to90total+daysOver90total;
                double total_paid = days0to08paid+days08to61paid+days61to75paid+days76to90paid+daysOver90paid;
                double total_due = days0to08due+days08to61due+days61to75due+days76to90due+daysOver90due;

                total_invoice_amount = getDouble(total_amount);
                total_invoice_paid_amount = getDouble(total_paid);
                total_invoice_due_amount = getDouble(total_due);

                Intent intent = new Intent(MainActivity.this, ReceiptAuto.class);
                startActivity(intent);



            }
        });
    }




    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent myNewActivity = new Intent();
            startActivity(myNewActivity);
        }
    };

    IntentFilter mIntentFilter=new IntentFilter("OPEN_NEW_ACTIVITY");

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }



    @Override
    protected void onPause() {
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onPause();
    }

    private void init() {
        mRestaurantRecyclerView = (RecyclerView) findViewById(R.id.days0to07);
        mRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRestaurantRecyclerView.setHasFixedSize(true);
        mRestaurantCollection = new ArrayList<>();
        mAdapter = new VoucherAdapter(mRestaurantCollection, this, this);
        mRestaurantRecyclerView.setAdapter(mAdapter);
    }


    private void init2() {
        m08to60RecyclerView = (RecyclerView) findViewById(R.id.days08to60);
        m08to60RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        m08to60RecyclerView.setHasFixedSize(true);
        m08to60List= new ArrayList<>();
        mVoucherAdapter08to60 = new VoucherAdapter08to60(m08to60List,this, this);
        m08to60RecyclerView.setAdapter(mVoucherAdapter08to60);
    }

    private void init3() {
        m61to75RecyclerView = (RecyclerView) findViewById(R.id.days61to75);
        m61to75RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        m61to75RecyclerView.setHasFixedSize(true);
        m61to75List= new ArrayList<>();
        mVoucherAdapter61to75 = new VoucherAdapter61to75(m61to75List,this, this);
        m61to75RecyclerView.setAdapter(mVoucherAdapter61to75);
    }

    private void init4() {
        m76to90RecyclerView = (RecyclerView) findViewById(R.id.days76to90);
        m76to90RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        m76to90RecyclerView.setHasFixedSize(true);
        m76to90List= new ArrayList<>();
        mVoucherAdapter76to90 = new VoucherAdapter76to90(m76to90List,this, this);
        m76to90RecyclerView.setAdapter(mVoucherAdapter76to90);
    }

    private void init5() {
        mOver90RecyclerView = (RecyclerView) findViewById(R.id.daysOver90);
        mOver90RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOver90RecyclerView.setHasFixedSize(true);
        mOver90List= new ArrayList<>();
        mVoucherAdapterOver90 = new VoucherAdapterOver90(mOver90List,this, this);
        mOver90RecyclerView.setAdapter(mVoucherAdapterOver90);
    }


    public class FetchDataTaskDays0to07 extends AsyncTask<Void, Void, Void> {
        private String mZomatoString;

        @Override
        protected Void doInBackground(Void... param) {

            SyncHttpClient client = new SyncHttpClient();

            client.get("http://173.212.247.25/distribution_restful_api_test/resources/total-oustanding/"+customer_id+"=7", null,  new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    Log.d("Clima", "Success Json! "+response.toString());

                    try{

                        if(mRestaurantRecyclerView != null){
                            mRestaurantRecyclerView.getRecycledViewPool().clear();
                        }

                        if(mRestaurantCollection.size() > 0){
                            mRestaurantCollection.clear();
                        }

                        days0to08total = 0;
                        days0to08paid = 0;
                        days0to08due = 0;

                        Log.v("length", response.length() + "");
                        for (int i = 0; i < response.length(); i++) {

                            Log.v("BRAD_", i + "");

                             int invoiceIdInt;
                             String invoiceId;
                             String invoiceDate;
                             String incoiceTotal;
                             String invoicePaid;
                             String incoiceDue;
                             String incoiceAge;
                             String incoiceCustomer;

                            JSONObject jsonObject = (JSONObject) response.getJSONObject(i);
//                         jRestaurant = jRestaurant.getJSONObject("-");
//                         JSONObject jLocattion = jRestaurant.getJSONObject("location");

                            invoiceIdInt = jsonObject.getInt("voucherId");
                            invoiceId = jsonObject.getString("voucherNumber");
                            invoiceDate = jsonObject.getString("date");
                            incoiceTotal = jsonObject.getString("total");
                            invoicePaid = jsonObject.getString("paid");
                            incoiceDue = jsonObject.getString("due");
                            incoiceAge = jsonObject.getString("days");
                            incoiceCustomer = jsonObject.getString("cudstomer");

                            days0to08total += Double.valueOf(incoiceTotal.replaceAll(",",""));
                            days0to08paid += Double.valueOf(invoicePaid.replaceAll(",",""));
                            days0to08due += Double.valueOf(incoiceDue.replaceAll(",",""));

                            if(i==1) {
                                customer_name=jsonObject.getString("cudstomer");
                            }

                            Log.v("invoiceIdInt", invoiceIdInt + "");
                            Log.v("invoiceId", invoiceId + "");
                            Log.v("invoiceDate", invoiceDate + "");
                            Log.v("incoiceTotal", incoiceTotal + "");
                            Log.v("invoicePaid", invoicePaid + "");
                            Log.v("incoiceDue", incoiceDue + "");
                            Log.v("incoiceCustomer", incoiceCustomer + "");

                            Voucher voucher = new Voucher();
                            voucher.setInvoiceIdInt(invoiceIdInt);
                            voucher.setInvoiceId(invoiceId);
                            voucher.setInvoiceDate(invoiceDate);
                            voucher.setInvoiceTotal(incoiceTotal);
                            voucher.setInvoicePaid(invoicePaid);
                            voucher.setInvoiceDue(incoiceDue);
                            voucher.setInvoiceAge(incoiceAge);
                            voucher.setInvoiceCustomer(incoiceCustomer);

                            mRestaurantCollection.add(voucher);
                        }


                    }catch (JSONException e){
                        e.printStackTrace();
                    }


                }


                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);

                    Log.d("Clima", "Fail..! "+throwable.toString());
                    Log.d("Clima", "Status Code... "+statusCode);
                    Log.d("Clima", "errorResponse... "+errorResponse);


                    Toast.makeText(MainActivity.this, "Request Failed...", Toast.LENGTH_SHORT).show();

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mAdapter.notifyDataSetChanged();

            TextView c_name = (TextView) findViewById(R.id.customer_name);
            c_name.setText(customer_name);
//
//            TextView f_total = (TextView) findViewById(R.id.first_total);
//            f_total.setText(getDouble(days0to08total));
//
//            TextView f_paid = (TextView) findViewById(R.id.first_paid);
//            f_paid.setText(getDouble(days0to08paid));
//
//            TextView f_due = (TextView) findViewById(R.id.first_due);
//            f_due.setText(getDouble(days0to08due));
        }
    }


    public class FetchDataTaskDays08to60 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... param) {

            Log.d("Clima", "param "+param);

            SyncHttpClient client = new SyncHttpClient();

            client.get("http://173.212.247.25/distribution_restful_api_test/resources/total-oustanding/"+customer_id+"=60", null,  new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    Log.d("Clima", "Success Json! m31to60"+response.toString());

                    try{

                        if(m08to60RecyclerView != null){
                            m08to60RecyclerView.getRecycledViewPool().clear();
                        }

                        if(m08to60List.size() > 0){
                            m08to60List.clear();
                        }

                        days08to61total = 0;
                        days08to61paid = 0;
                        days08to61due = 0;

                        Log.v("length", response.length() + "");
                        for (int i = 0; i < response.length(); i++) {

                            Log.v("BRAD_", i + "");

                            int invoiceIdInt;
                            String invoiceId;
                            String invoiceDate;
                            String incoiceTotal;
                            String invoicePaid;
                            String incoiceDue;
                            String incoiceAge;
                            String incoiceCustomer;

                            JSONObject jsonObject = (JSONObject) response.getJSONObject(i);
//                         jRestaurant = jRestaurant.getJSONObject("-");
//                         JSONObject jLocattion = jRestaurant.getJSONObject("location");

                            invoiceIdInt = jsonObject.getInt("voucherId");
                            invoiceId = jsonObject.getString("voucherNumber");
                            invoiceDate = jsonObject.getString("date");
                            incoiceTotal = jsonObject.getString("total");
                            invoicePaid = jsonObject.getString("paid");
                            incoiceDue = jsonObject.getString("due");
                            incoiceAge = jsonObject.getString("days");
                            incoiceCustomer = jsonObject.getString("cudstomer");

                            if(i==1) {
                                customer_name=jsonObject.getString("cudstomer");
                            }

                            days08to61total += Double.valueOf(incoiceTotal.replaceAll(",",""));
                            days08to61paid += Double.valueOf(invoicePaid.replaceAll(",",""));
                            days08to61due += Double.valueOf(incoiceDue.replaceAll(",",""));

                            Log.v("invoiceId", invoiceId + "");
                            Log.v("invoiceDate", invoiceDate + "");
                            Log.v("incoiceTotal", incoiceTotal + "");
                            Log.v("invoicePaid", invoicePaid + "");
                            Log.v("incoiceDue", incoiceDue + "");

                            Voucher voucher = new Voucher();
                            voucher.setInvoiceIdInt(invoiceIdInt);
                            voucher.setInvoiceId(invoiceId);
                            voucher.setInvoiceDate(invoiceDate);
                            voucher.setInvoiceTotal(incoiceTotal);
                            voucher.setInvoicePaid(invoicePaid);
                            voucher.setInvoiceDue(incoiceDue);
                            voucher.setInvoiceAge(incoiceAge);
                            voucher.setInvoiceCustomer(incoiceCustomer);

                            m08to60List.add(voucher);
                        }


                    }catch (JSONException e){
                        e.printStackTrace();
                    }


                }


                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);

                    Log.d("Clima", "Fail..! "+throwable.toString());
                    Log.d("Clima", "Status Code... "+statusCode);
                    Log.d("Clima", "errorResponse... "+errorResponse);


                    Toast.makeText(MainActivity.this, "Request Failed...", Toast.LENGTH_SHORT).show();

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mVoucherAdapter08to60.notifyDataSetChanged();

            TextView c_name = (TextView) findViewById(R.id.customer_name);
            c_name.setText(customer_name);
//
//            TextView f_total = (TextView) findViewById(R.id.second_total);
//            f_total.setText(getDouble(days08to61total));
//
//            TextView f_paid = (TextView) findViewById(R.id.second_paid);
//            f_paid.setText(getDouble(days08to61paid));
//
//            TextView f_due = (TextView) findViewById(R.id.second_due);
//            f_due.setText(getDouble(days08to61due));
        }
    }


    public class FetchDataTaskDays61to75 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... param) {

            Log.d("Clima", "param "+param);

            SyncHttpClient client = new SyncHttpClient();

            client.get("http://173.212.247.25/distribution_restful_api_test/resources/total-oustanding/"+customer_id+"=75", null,  new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    Log.d("Clima", "Success Json! m61to75"+response.toString());

                    try{

                        if(m61to75RecyclerView != null){
                            m61to75RecyclerView.getRecycledViewPool().clear();
                        }

                        if(m61to75List.size() > 0){
                            m61to75List.clear();
                        }

                        days61to75total = 0;
                        days61to75paid = 0;
                        days61to75due = 0;

                        Log.v("length", response.length() + "");
                        for (int i = 0; i < response.length(); i++) {

                            Log.v("BRAD_", i + "");

                            int invoiceIdInt;
                            String invoiceId;
                            String invoiceDate;
                            String incoiceTotal;
                            String invoicePaid;
                            String incoiceDue;
                            String incoiceAge;
                            String incoiceCustomer;

                            JSONObject jsonObject = (JSONObject) response.getJSONObject(i);
//                         jRestaurant = jRestaurant.getJSONObject("-");
//                         JSONObject jLocattion = jRestaurant.getJSONObject("location");

                            invoiceIdInt = jsonObject.getInt("voucherId");
                            invoiceId = jsonObject.getString("voucherNumber");
                            invoiceDate = jsonObject.getString("date");
                            incoiceTotal = jsonObject.getString("total");
                            invoicePaid = jsonObject.getString("paid");
                            incoiceDue = jsonObject.getString("due");
                            incoiceAge = jsonObject.getString("days");
                            incoiceCustomer = jsonObject.getString("cudstomer");

                            if(i==1) {
                                customer_name=jsonObject.getString("cudstomer");
                            }

                            days61to75total += Double.valueOf(incoiceTotal.replaceAll(",",""));
                            days61to75paid += Double.valueOf(invoicePaid.replaceAll(",",""));
                            days61to75due += Double.valueOf(incoiceDue.replaceAll(",",""));

                            Log.v("invoiceId", invoiceId + "");
                            Log.v("invoiceDate", invoiceDate + "");
                            Log.v("incoiceTotal", incoiceTotal + "");
                            Log.v("invoicePaid", invoicePaid + "");
                            Log.v("incoiceDue", incoiceDue + "");

                            Voucher voucher = new Voucher();
                            voucher.setInvoiceIdInt(invoiceIdInt);
                            voucher.setInvoiceId(invoiceId);
                            voucher.setInvoiceDate(invoiceDate);
                            voucher.setInvoiceTotal(incoiceTotal);
                            voucher.setInvoicePaid(invoicePaid);
                            voucher.setInvoiceDue(incoiceDue);
                            voucher.setInvoiceAge(incoiceAge);
                            voucher.setInvoiceCustomer(incoiceCustomer);

                            m61to75List.add(voucher);
                        }


                    }catch (JSONException e){
                        e.printStackTrace();
                    }


                }


                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);

                    Log.d("Clima", "Fail..! "+throwable.toString());
                    Log.d("Clima", "Status Code... "+statusCode);
                    Log.d("Clima", "errorResponse... "+errorResponse);


                    Toast.makeText(MainActivity.this, "Request Failed...", Toast.LENGTH_SHORT).show();

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mVoucherAdapter61to75.notifyDataSetChanged();

            TextView c_name = (TextView) findViewById(R.id.customer_name);
            c_name.setText(customer_name);
//
//            TextView f_total = (TextView) findViewById(R.id.third_total);
//            f_total.setText(getDouble(days61to75total));
//
//            TextView f_paid = (TextView) findViewById(R.id.third_paid);
//            f_paid.setText(getDouble(days61to75paid));
//
//            TextView f_due = (TextView) findViewById(R.id.third_due);
//            f_due.setText(getDouble(days61to75due));
        }
    }

    public class FetchDataTaskDays76to90 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... param) {

            Log.d("Clima", "param "+param);

            SyncHttpClient client = new SyncHttpClient();

            client.get("http://173.212.247.25/distribution_restful_api_test/resources/total-oustanding/"+customer_id+"=90", null,  new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    Log.d("Clima", "Success Json! m61to75"+response.toString());

                    try{

                        if(m76to90RecyclerView != null){
                            m76to90RecyclerView.getRecycledViewPool().clear();
                        }

                        if(m76to90List.size() > 0){
                            m76to90List.clear();
                        }

                        days76to90total = 0;
                        days76to90paid = 0;
                        days76to90due = 0;

                        Log.v("length", response.length() + "");
                        for (int i = 0; i < response.length(); i++) {

                            Log.v("BRAD_", i + "");

                            int invoiceIdInt;
                            String invoiceId;
                            String invoiceDate;
                            String incoiceTotal;
                            String invoicePaid;
                            String incoiceDue;
                            String incoiceAge;
                            String incoiceCustomer;

                            JSONObject jsonObject = (JSONObject) response.getJSONObject(i);
//                         jRestaurant = jRestaurant.getJSONObject("-");
//                         JSONObject jLocattion = jRestaurant.getJSONObject("location");

                            invoiceIdInt = jsonObject.getInt("voucherId");
                            invoiceId = jsonObject.getString("voucherNumber");
                            invoiceDate = jsonObject.getString("date");
                            incoiceTotal = jsonObject.getString("total");
                            invoicePaid = jsonObject.getString("paid");
                            incoiceDue = jsonObject.getString("due");
                            incoiceAge = jsonObject.getString("days");
                            incoiceCustomer = jsonObject.getString("cudstomer");

                            if(i==1) {
                                customer_name=jsonObject.getString("cudstomer");
                            }

                            days76to90total += Double.valueOf(incoiceTotal.replaceAll(",",""));
                            days76to90paid += Double.valueOf(invoicePaid.replaceAll(",",""));
                            days76to90due += Double.valueOf(incoiceDue.replaceAll(",",""));

                            Log.v("invoiceId", invoiceId + "");
                            Log.v("invoiceDate", invoiceDate + "");
                            Log.v("incoiceTotal", incoiceTotal + "");
                            Log.v("invoicePaid", invoicePaid + "");
                            Log.v("incoiceDue", incoiceDue + "");

                            Voucher voucher = new Voucher();
                            voucher.setInvoiceIdInt(invoiceIdInt);
                            voucher.setInvoiceId(invoiceId);
                            voucher.setInvoiceDate(invoiceDate);
                            voucher.setInvoiceTotal(incoiceTotal);
                            voucher.setInvoicePaid(invoicePaid);
                            voucher.setInvoiceDue(incoiceDue);
                            voucher.setInvoiceAge(incoiceAge);
                            voucher.setInvoiceCustomer(incoiceCustomer);

                            m76to90List.add(voucher);
                        }


                    }catch (JSONException e){
                        e.printStackTrace();
                    }


                }


                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);

                    Log.d("Clima", "Fail..! "+throwable.toString());
                    Log.d("Clima", "Status Code... "+statusCode);
                    Log.d("Clima", "errorResponse... "+errorResponse);


                    Toast.makeText(MainActivity.this, "Request Failed...", Toast.LENGTH_SHORT).show();

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mVoucherAdapter76to90.notifyDataSetChanged();

            TextView c_name = (TextView) findViewById(R.id.customer_name);
            c_name.setText(customer_name);
//
//            TextView f_total = (TextView) findViewById(R.id.fourth_total);
//            f_total.setText(getDouble(days76to90total));
//
//            TextView f_paid = (TextView) findViewById(R.id.fourth_paid);
//            f_paid.setText(getDouble(days76to90paid));
//
//            TextView f_due = (TextView) findViewById(R.id.fourth_due);
//            f_due.setText(getDouble(days76to90due));
        }
    }


    public class FetchDataTaskDaysOver90 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... param) {

            Log.d("Clima", "param "+param);

            SyncHttpClient client = new SyncHttpClient();

            client.get("http://173.212.247.25/distribution_restful_api_test/resources/total-oustanding/"+customer_id+"=91", null,  new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    Log.d("Clima", "Success Json! m61to75"+response.toString());

                    try{

                        if(mOver90RecyclerView != null){
                            mOver90RecyclerView.getRecycledViewPool().clear();
                        }

                        if(mOver90List.size() > 0){
                            mOver90List.clear();
                        }

                        daysOver90total = 0;
                        daysOver90paid = 0;
                        daysOver90due = 0;

                        Log.v("length", response.length() + "");
                        for (int i = 0; i < response.length(); i++) {

                            Log.v("BRAD_", i + "");

                            int invoiceIdInt;
                            String invoiceId;
                            String invoiceDate;
                            String incoiceTotal;
                            String invoicePaid;
                            String incoiceDue;
                            String incoiceAge;
                            String incoiceCustomer;

                            JSONObject jsonObject = (JSONObject) response.getJSONObject(i);
//                         jRestaurant = jRestaurant.getJSONObject("-");
//                         JSONObject jLocattion = jRestaurant.getJSONObject("location");

                            invoiceIdInt = jsonObject.getInt("voucherId");
                            invoiceId = jsonObject.getString("voucherNumber");
                            invoiceDate = jsonObject.getString("date");
                            incoiceTotal = jsonObject.getString("total");
                            invoicePaid = jsonObject.getString("paid");
                            incoiceDue = jsonObject.getString("due");
                            incoiceAge = jsonObject.getString("days");
                            incoiceCustomer = jsonObject.getString("cudstomer");

                            if(i==1) {
                                customer_name=jsonObject.getString("cudstomer");
                            }

                            daysOver90total += Double.valueOf(incoiceTotal.replaceAll(",",""));
                            daysOver90paid += Double.valueOf(invoicePaid.replaceAll(",",""));
                            daysOver90due += Double.valueOf(incoiceDue.replaceAll(",",""));

                            Log.v("invoiceId", invoiceId + "");
                            Log.v("invoiceDate", invoiceDate + "");
                            Log.v("incoiceTotal", incoiceTotal + "");
                            Log.v("invoicePaid", invoicePaid + "");
                            Log.v("incoiceDue", incoiceDue + "");

                            Voucher voucher = new Voucher();
                            voucher.setInvoiceIdInt(invoiceIdInt);
                            voucher.setInvoiceId(invoiceId);
                            voucher.setInvoiceDate(invoiceDate);
                            voucher.setInvoiceTotal(incoiceTotal);
                            voucher.setInvoicePaid(invoicePaid);
                            voucher.setInvoiceDue(incoiceDue);
                            voucher.setInvoiceAge(incoiceAge);
                            voucher.setInvoiceCustomer(incoiceCustomer);

                            mOver90List.add(voucher);
                        }


                    }catch (JSONException e){
                        e.printStackTrace();
                    }


                }


                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);

                    Log.d("Clima", "Fail..! "+throwable.toString());
                    Log.d("Clima", "Status Code... "+statusCode);
                    Log.d("Clima", "errorResponse... "+errorResponse);


                    Toast.makeText(MainActivity.this, "Request Failed...", Toast.LENGTH_SHORT).show();

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mVoucherAdapterOver90.notifyDataSetChanged();

            TextView c_name = (TextView) findViewById(R.id.customer_name);
            c_name.setText(customer_name);
//
//            TextView f_total = (TextView) findViewById(R.id.fifth_total);
//            f_total.setText(getDouble(daysOver90total));
//
//            TextView f_paid = (TextView) findViewById(R.id.fifth_paid);
//            f_paid.setText(getDouble(daysOver90paid));
//
//            TextView f_due = (TextView) findViewById(R.id.fifth_due);
//            f_due.setText(getDouble(daysOver90due));
        }
    }

    List<String> ListData = new ArrayList<String>();

    public class FetchDataTaskSearch extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... param) {

            SyncHttpClient client = new SyncHttpClient();

            client.get("http://173.212.247.25/distribution_restful_api_test/resources/customers-list/0", null,  new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    Log.d("Clima", "Success Json! "+response.toString());

                    try{

                        Log.v("length", response.length() + "");
                        for (int i = 0; i < response.length(); i++) {

                            Log.v("BRAD_", i + "");

                            String nameWithId;

                            JSONObject jsonObject = (JSONObject) response.getJSONObject(i);

                            nameWithId = jsonObject.getString("name_with_id");

                            Log.v("nameWithId", nameWithId + "");

                            ListData.add(nameWithId);

                        }


                    }catch (JSONException e){
                        e.printStackTrace();
                    }


                }


                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);

                    Log.d("Clima", "Fail..! "+throwable.toString());
                    Log.d("Clima", "Status Code... "+statusCode);
                    Log.d("Clima", "errorResponse... "+errorResponse);


                    Toast.makeText(MainActivity.this, "Request Failed...", Toast.LENGTH_SHORT).show();

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_dropdown_item_1line, ListData);
            AutoCompleteTextView textView = (AutoCompleteTextView)
                    findViewById(R.id.main_customer_id);
            textView.setAdapter(adapter);

        }
    }

    public static String getDouble(double value) {

        DecimalFormat myFormatter = new DecimalFormat("###,###,##0.00");
        String output = myFormatter.format(value);
        return output;

    }
}
