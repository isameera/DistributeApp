package com.exoncloud.mobi.distributeapp;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.exoncloud.mobi.distributeapp.model.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Receipt extends AppCompatActivity {

    EditText date;
    DatePickerDialog datePickerDialog;
    String bankId = "";
    public static int invoice_id_static = 0;
    public static int chequeController = 0;
    public static int COC = 0;

    // Session Manager Class
    SessionManager session;

    // name
    String name; String id; String ls; String ul; String gup; String org;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        name = user.get(SessionManager.KEY_NAME);
        id = user.get(SessionManager.KEY_DN);
        ls = user.get(SessionManager.KEY_LS);
        ul = user.get(SessionManager.KEY_UL);
        gup = user.get(SessionManager.KEY_GUP);
        org = user.get(SessionManager.KEY_ORG);


        TextView invoiceId = (TextView) findViewById(R.id.invoice_id);
        TextView invoiceDate = (TextView) findViewById(R.id.invoice_date);
        TextView invoiceAge = (TextView) findViewById(R.id.invoice_age);
        TextView invoiceTotal = (TextView) findViewById(R.id.invoice_total);
        TextView invoicePaid = (TextView) findViewById(R.id.invoice_paid);
        TextView invoiceDue = (TextView) findViewById(R.id.invoice_due);
        TextView invoiceCustomer = (TextView) findViewById(R.id.receipt_customer);

        invoiceId.setText(getIntent().getStringExtra("invoice_id"));
        invoiceDate.setText(getIntent().getStringExtra("invoice_date"));
        invoiceAge.setText(getIntent().getStringExtra("invoice_age"));
        invoiceTotal.setText(getIntent().getStringExtra("invoice_total"));
        invoicePaid.setText(getIntent().getStringExtra("invoice_paid"));
        invoiceDue.setText(getIntent().getStringExtra("invoice_due"));
        invoiceCustomer.setText(getIntent().getStringExtra("invoice_customer"));


        // initiate the date picker and a button
        date = (EditText) findViewById(R.id.receipt_cheque_date);
        // perform click event on edit text
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(Receipt.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date.setText(year + "-"+ (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });





        RadioGroup rg = (RadioGroup) findViewById(R.id.receipt_rg_pm);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            View current_payment = findViewById(R.id.receipt_current_payment);
            View btn = findViewById(R.id.receipt_btn);
            View rg_ct = findViewById(R.id.receipt_rg_ct);
            View cheque_number = findViewById(R.id.receipt_cheque_number);
            View cheque_date = findViewById(R.id.receipt_cheque_date);
            View cheque_bank = findViewById(R.id.receipt_cheque_bank);
            View cheque_bank_branch = findViewById(R.id.receipt_cheque_bank_branch);

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.receipt_pm_cash:

                        COC = 1;
                        current_payment.setVisibility(View.VISIBLE);
                        btn.setVisibility(View.VISIBLE);

                        rg_ct.setVisibility(View.GONE);
                        cheque_number.setVisibility(View.GONE);
                        cheque_date.setVisibility(View.GONE);
                        cheque_bank.setVisibility(View.GONE);
                        cheque_bank_branch.setVisibility(View.GONE);
                        break;

                    case R.id.receipt_pm_cheque:

                        COC = 2;
                        current_payment.setVisibility(View.VISIBLE);
                        btn.setVisibility(View.VISIBLE);
                        rg_ct.setVisibility(View.VISIBLE);
                        cheque_number.setVisibility(View.VISIBLE);
                        cheque_date.setVisibility(View.VISIBLE);
                        cheque_bank.setVisibility(View.VISIBLE);
                        cheque_bank_branch.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });

        RadioGroup rgCT = (RadioGroup) findViewById(R.id.receipt_rg_ct);
        rgCT.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.receipt_ct_cash:
                        chequeController = 1;
                        break;

                    case R.id.receipt_ct_acpay:
                        chequeController = 2;
                        break;

                    case R.id.receipt_ct_cross:
                        chequeController = 3;
                        break;
                }
            }
        });

        new FetchDataTaskSearchBank().execute();

        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.receipt_cheque_bank);

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                // here is your selected item
//                Toast.makeText(Receipt.this, "selectedItem "+selectedItem,  Toast.LENGTH_LONG).show();

                String result = selectedItem.split("%")[1];
                bankId = result;

           //     Toast.makeText(Receipt.this, "http://173.212.247.25/distribution_restful_api_test/resources/bank-branches-list/"+bankId+"=0", Toast.LENGTH_SHORT).show();

                new FetchDataTaskSearchBankBranch().execute();

            }
        });


        Button button = (Button) findViewById(R.id.receipt_btn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                EditText current_paymentET = (EditText) findViewById(R.id.receipt_current_payment);
                EditText cheque_numberET = (EditText) findViewById(R.id.receipt_cheque_number);
                EditText cheque_dateET = (EditText) findViewById(R.id.receipt_cheque_date);
                AutoCompleteTextView bankACT = (AutoCompleteTextView) findViewById(R.id.receipt_cheque_bank);
                AutoCompleteTextView bank_branchACT = (AutoCompleteTextView) findViewById(R.id.receipt_cheque_bank_branch);


                String current_payment_text = current_paymentET.getText().toString();
                String cheque_number = cheque_numberET.getText().toString();
                String cheque_date = cheque_dateET.getText().toString();
                String bank_text = bankACT.getText().toString();
                String bank_branch = bank_branchACT.getText().toString();

                double current_payment = Double.valueOf(current_payment_text);

                if(COC == 1){


                    if(current_payment_text.isEmpty()){

                        Toast.makeText(Receipt.this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();

                    }else{

                        saveReceipt(invoice_id_static, current_payment,"-","-",0,"-",chequeController,1);
                        Toast.makeText(Receipt.this, "Successfully Saved!", Toast.LENGTH_SHORT).show();
                          Receipt.this.finish();
                    }

                }else if(COC == 2){

                    if(current_payment_text.isEmpty() || cheque_number.isEmpty() || cheque_date.isEmpty() || bank_text.isEmpty() || bank_branch.isEmpty() || COC == 0 || chequeController==0){

                        Toast.makeText(Receipt.this, "Fields cannot be empty or not selected!", Toast.LENGTH_SHORT).show();

                    }else{

                        int bank = Integer.parseInt(bank_text.split("%")[1]);

//                    Toast.makeText(Receipt.this, "invoice_id_static: "+invoice_id_static +" current_payment: "+ current_payment+" cheque_number: "+cheque_number+" cheque_date: "+cheque_date+" bank: "+bank+" bank_branch: "+bank_branch+" chequeController: "+chequeController+" COC: "+COC, Toast.LENGTH_SHORT).show();

                        saveReceipt(invoice_id_static, current_payment,cheque_number,cheque_date,bank,bank_branch,chequeController,COC);
                        Toast.makeText(Receipt.this, "Successfully Saved!", Toast.LENGTH_SHORT).show();
                          Receipt.this.finish();
                    }

                }





            }
        });

    }


    List<String> ListBank = new ArrayList<String>();

    public class FetchDataTaskSearchBank extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... param) {

            SyncHttpClient client = new SyncHttpClient();

            client.get("http://173.212.247.25/distribution_restful_api_test/resources/banks-list/0", null,  new JsonHttpResponseHandler() {
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

                            ListBank.add(nameWithId);

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


                    Toast.makeText(Receipt.this, "Request Failed...", Toast.LENGTH_SHORT).show();

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Receipt.this,
                    android.R.layout.simple_dropdown_item_1line, ListBank);
            AutoCompleteTextView textView = (AutoCompleteTextView)
                    findViewById(R.id.receipt_cheque_bank);
            textView.setAdapter(adapter);

        }
    }



    List<String> ListBankBranch = new ArrayList<String>();

    public class FetchDataTaskSearchBankBranch extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... param) {

            SyncHttpClient client = new SyncHttpClient();

            client.get("http://173.212.247.25/distribution_restful_api_test/resources/bank-branches-list/"+bankId+"=0", null,  new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    Log.d("Clima", "Success Json! "+response.toString());

                    try{

                        Log.v("length", response.length() + "");
                        for (int i = 0; i < response.length(); i++) {

                            Log.v("BRAD_", i + "");

                            String nameWithId;

                            JSONObject jsonObject = (JSONObject) response.getJSONObject(i);

                            nameWithId = jsonObject.getString("name");

                            Log.v("nameWithId", nameWithId + "");

                            ListBankBranch.add(nameWithId);

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


                    Toast.makeText(Receipt.this, "Request Failed...", Toast.LENGTH_SHORT).show();

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Receipt.this,
                    android.R.layout.simple_dropdown_item_1line, ListBankBranch);
            AutoCompleteTextView textView = (AutoCompleteTextView)
                    findViewById(R.id.receipt_cheque_bank_branch);
            textView.setAdapter(adapter);

        }
    }


    private void saveReceipt(int voucherId, double paymentD, String chequeNo, String chequeDate, int bank, String bank_branch, int chequeController, int coc) {

//        final String URL = "http://173.212.247.25/distribution_restful_api_test/resources/receipt-save/";
        final String URL = "http://173.212.247.25/distribution_restful_api_test/resources/receipt-save/?voucherId="+voucherId+"&paymentD="+paymentD+"&chequeNo="+chequeNo+"&chequeDate="+chequeDate+"&bank="+bank+"&bank_branch="+bank_branch+"&chequeController="+chequeController+"&coc="+coc+"&description="+id+"&gupId="+gup+"&orgId="+org+"&LS="+ls+"&UL="+ul;

        Log.d("JAXRS", "URL " + URL);

//        RequestParams params = new RequestParams();
//        params.put("voucherId", voucherId);
//        params.put("paymentD", paymentD);
//        params.put("chequeNo", chequeNo);
//        params.put("chequeDate", chequeDate);
//        params.put("bank", bank);
//        params.put("bank_branch", bank_branch);
//        params.put("chequeController", chequeController);
//        params.put("coc", coc);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("JAXRS", "Success Json! " + responseBody.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                Log.d("JAXRS", "error Json! " + error);
                Toast.makeText(Receipt.this, "error:"+error, Toast.LENGTH_SHORT).show();
                error.printStackTrace(System.out);
            }
        });
    }


    public static class Bank {

        public Bank(int id, String name) {
            this.id = id;
            this.name = name;
        }

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}