package main.com.notaryagent.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.notaryagent.R;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.CountryBean;
import main.com.notaryagent.constant.MySession;


public class AddBankAccountAct extends AppCompatActivity {
    private TextView submitbut;
    private RelativeLayout exit_app_but;
    private EditText holdername_et, holderaddres, accountnumber, bankname, branchname, branchaddress, ifsccode, routingnumber;
    private String holdername_str = "", holderaddres_str = "", accountnumber_str = "", bankname_str = "", branchname_str = "", branchaddress_str = "", ifsccode_str = "", routingnumber_str = "";
    private ACProgressCustom ac_dialog;
    private MySession mySession;
    private String user_log_data = "", user_id = "", bank_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbankaccount);
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
        idinti();
        clickevetn();
    }

    private void clickevetn() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submitbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holdername_str = holdername_et.getText().toString();
                holderaddres_str = holderaddres.getText().toString();
                accountnumber_str = accountnumber.getText().toString();
                bankname_str = bankname.getText().toString();
                branchname_str = branchname.getText().toString();
                branchaddress_str = branchaddress.getText().toString();
                ifsccode_str = ifsccode.getText().toString();
                routingnumber_str = routingnumber.getText().toString();

                if (holdername_str == null || holdername_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddBankAccountAct.this, getResources().getString(R.string.enterholdername), Toast.LENGTH_LONG).show();
                } else if (holderaddres_str == null || holderaddres_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddBankAccountAct.this, getResources().getString(R.string.holderaddress), Toast.LENGTH_LONG).show();

                } else if (accountnumber_str == null || accountnumber_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddBankAccountAct.this, getResources().getString(R.string.enteraccountnumber), Toast.LENGTH_LONG).show();

                } else if (bankname_str == null || bankname_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddBankAccountAct.this, getResources().getString(R.string.enterbankname), Toast.LENGTH_LONG).show();

                } else if (branchname_str == null || branchname_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddBankAccountAct.this, getResources().getString(R.string.enterbranchname), Toast.LENGTH_LONG).show();

                } else if (branchaddress_str == null || branchaddress_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddBankAccountAct.this, getResources().getString(R.string.enterbranchaddress), Toast.LENGTH_LONG).show();

                } /*else if (ifsccode_str == null || ifsccode_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddBankAccountAct.this, getResources().getString(R.string.enterifccode), Toast.LENGTH_LONG).show();

                }*/ else if (routingnumber_str == null || routingnumber_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddBankAccountAct.this, getResources().getString(R.string.enterroutingnum), Toast.LENGTH_LONG).show();
                } else {
                    if (bank_id!=null&&!bank_id.equalsIgnoreCase("")) {
                        new UpdateBankAccount().execute();
                    } else {
                        new AddBankAsc().execute();
                    }

                }

            }
        });
    }

    private void idinti() {
        exit_app_but = findViewById(R.id.exit_app_but);

        holdername_et = findViewById(R.id.holdername_et);
        holderaddres = findViewById(R.id.holderaddres);
        accountnumber = findViewById(R.id.accountnumber);
        bankname = findViewById(R.id.bankname);
        branchname = findViewById(R.id.branchname);
        branchaddress = findViewById(R.id.branchaddress);
        ifsccode = findViewById(R.id.ifsccode);
        routingnumber = findViewById(R.id.routingnumber);
        submitbut = findViewById(R.id.submitbut);
    }


    private class AddBankAsc extends AsyncTask<String, String, String> {
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
                String postReceiverUrl = BaseUrl.baseurl + "add_bank_account";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                StringBuilder postData = new StringBuilder();
                params.put("user_id", user_id);
                params.put("account_holder_name", holdername_str);
                params.put("account_holder_address", holderaddres_str);
                params.put("account_number", accountnumber_str);
                params.put("bank_name", bankname_str);
                params.put("branch_name", branchname_str);
                params.put("branch_address", branchaddress_str);
                params.put("ifsc_code", ifsccode_str);
                params.put("routing_number", routingnumber_str);
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
            Log.e("Add Account", " >> " + result);
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
                        Toast.makeText(AddBankAccountAct.this, getResources().getString(R.string.accountadedsucc), Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private class UpdateBankAccount extends AsyncTask<String, String, String> {
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
                String postReceiverUrl = BaseUrl.baseurl + "update_bank_account";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                StringBuilder postData = new StringBuilder();
                params.put("user_id", user_id);
                params.put("account_id", bank_id);
                params.put("account_holder_name", holdername_str);
                params.put("account_holder_address", holderaddres_str);
                params.put("account_number", accountnumber_str);
                params.put("bank_name", bankname_str);
                params.put("branch_name", branchname_str);
                params.put("branch_address", branchaddress_str);
                params.put("ifsc_code", ifsccode_str);
                params.put("routing_number", routingnumber_str);
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
            Log.e("Update Account", " >> " + result);
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
                        Toast.makeText(AddBankAccountAct.this, getResources().getString(R.string.accountupdated), Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
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
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                bank_id = jsonObject1.getString("id");
                                holdername_et.setText("" + jsonObject1.getString("account_holder_name"));
                                holderaddres.setText("" + jsonObject1.getString("account_holder_address"));
                                accountnumber.setText("" + jsonObject1.getString("account_number"));
                                bankname.setText("" + jsonObject1.getString("bank_name"));
                                branchname.setText("" + jsonObject1.getString("branch_name"));
                                branchaddress.setText("" + jsonObject1.getString("branch_address"));
                                ifsccode.setText("" + jsonObject1.getString("ifsc_code"));
                                routingnumber.setText("" + jsonObject1.getString("routing_number"));
                                submitbut.setText("" + getResources().getString(R.string.update));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

}
