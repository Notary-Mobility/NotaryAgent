package main.com.notaryagent.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.notaryagent.R;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.MySession;

public class WithdrawReqAct extends AppCompatActivity {

    private TextView amount_tv, submitbut, accountholdername, accountnumber_tv, bankname_tv;
    private String amount = "0", type = "", bank_account_id = "", user_log_data = "", user_id = "";
    private MySession mySession;
    private ACProgressCustom ac_dialog;
    private RelativeLayout addcardlay, backlay;
    private LinearLayout bankdetaillay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_req);

        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();


        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                    user_id = jsonObject1.getString("id");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && !bundle.isEmpty()) {
            amount = bundle.getString("amount");
            type = bundle.getString("type");
        }
        idinit();
        clickevent();
    }

    private void clickevent() {
        submitbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bank_account_id == null || bank_account_id.equalsIgnoreCase("")) {
                    Toast.makeText(WithdrawReqAct.this, getResources().getString(R.string.selectbankaccount), Toast.LENGTH_LONG).show();

                } else {
                    new SendWithdrawReq().execute();
                }
            }
        });

        addcardlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WithdrawReqAct.this, AddBankAccountAct.class);
                startActivity(i);
            }
        });
        backlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void idinit() {
        backlay = findViewById(R.id.backlay);
        bankdetaillay = findViewById(R.id.bankdetaillay);
        bankname_tv = findViewById(R.id.bankname_tv);
        amount_tv = findViewById(R.id.amount_tv);
        accountnumber_tv = findViewById(R.id.accountnumber_tv);
        accountholdername = findViewById(R.id.accountholdername);
        amount_tv.setText("$" + amount);

        submitbut = findViewById(R.id.submitbut);
        addcardlay = findViewById(R.id.addcardlay);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetMyBankAccounts().execute();
    }

    private class GetMyBankAccounts extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ac_dialog != null) {
                ac_dialog.show();
            }
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_bank_account?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                StringBuilder postData = new StringBuilder();
                params.put("user_id", user_id);

                //account_holder_name , account_holder_address , account_number , bank_name , branch_name , branch_address , ifsc_code ,  routing_number

                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(urlParameters);
                writer.flush();
                String response = "";
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
                writer.close();
                reader.close();
                return response;
            } catch (UnsupportedEncodingException e1) {

                e1.printStackTrace();
            } catch (IOException e1) {

                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // prgressbar.setVisibility(View.GONE);
            Log.e("Get Account", " >> " + result);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("status");
                    if (message.equalsIgnoreCase("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (i == 0) {
                                addcardlay.setVisibility(View.GONE);
                                bankdetaillay.setVisibility(View.VISIBLE);
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                bank_account_id = jsonObject1.getString("id");
                                accountholdername.setText("" + jsonObject1.getString("account_holder_name"));
                                // holderaddres.setText("" + jsonObject1.getString("account_holder_address"));
                                accountnumber_tv.setText("" + jsonObject1.getString("account_number"));
                                bankname_tv.setText("" + jsonObject1.getString("bank_name"));
                                //branchname.setText("" + jsonObject1.getString("branch_name"));
                                //  branchaddress.setText("" + jsonObject1.getString("branch_address"));
                                // ifsccode.setText("" + jsonObject1.getString("ifsc_code"));
                                //  routingnumber.setText("" + jsonObject1.getString("routing_number"));
                                // submitbut.setText("" + getResources().getString(R.string.update));
                            }
                        }
                    } else {
                        addcardlay.setVisibility(View.VISIBLE);
                        bankdetaillay.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }


    private class SendWithdrawReq extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ac_dialog != null) {
                ac_dialog.show();
            }
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            //
            http:
//http://technorizen.com/notary/webservice/withdraw_request?user_id=1&amount=2.5&bank_account_id=2
            try {
                String postReceiverUrl = BaseUrl.baseurl + "withdraw_request?";
                Log.e("Base Url=", ">>" + postReceiverUrl + "user_id=" + user_id);
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("user_id", user_id);
                params.put("amount", amount);
                params.put("type", type);
                params.put("bank_account_id", bank_account_id);
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(urlParameters);
                writer.flush();
                String response = "";
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
                writer.close();
                reader.close();
                Log.e("Send With Req Res", ">>>>>>>>>>>>" + response);
                return response;
            } catch (UnsupportedEncodingException e1) {

                e1.printStackTrace();
            } catch (IOException e1) {

                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
                Toast.makeText(WithdrawReqAct.this, getResources().getString(R.string.somthingwrong), Toast.LENGTH_LONG).show();

            } else if (result.isEmpty()) {
                Toast.makeText(WithdrawReqAct.this, getResources().getString(R.string.somthingwrong), Toast.LENGTH_LONG).show();

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        Toast.makeText(WithdrawReqAct.this, getResources().getString(R.string.requestsendsucc), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(WithdrawReqAct.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }


}
