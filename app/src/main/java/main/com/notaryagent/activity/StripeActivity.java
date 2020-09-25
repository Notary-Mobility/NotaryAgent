package main.com.notaryagent.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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

import main.com.notaryagent.R;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.MySession;
import main.com.notaryagent.paymentclasses.StripeExpressAcountAct;

public class StripeActivity extends AppCompatActivity {
    private RelativeLayout seestripedashboard,genrateloginlinklay,addstripeact,backlay;

    private MySession mySession;
    private ProgressBar progresbar;
    private String user_id="",stripe_account_id="",stripe_account_login_link="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe);

        mySession  = new MySession(this);

        idint();

        String user_log_data = mySession.getKeyAlldata();
        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");
                    stripe_account_id = jsonObject1.getString("stripe_account_id");
                    stripe_account_login_link = jsonObject1.getString("stripe_account_login_link");
                    if (stripe_account_id!=null&&!stripe_account_id.equalsIgnoreCase("")){
                        addstripeact.setVisibility(View.GONE);
                        if (stripe_account_login_link!=null&&!stripe_account_login_link.equalsIgnoreCase("")){
                            genrateloginlinklay.setVisibility(View.GONE);
                            seestripedashboard.setVisibility(View.VISIBLE);
                        }
                        else {
                            genrateloginlinklay.setVisibility(View.VISIBLE);
                        }

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        clickevetn();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (stripe_account_id==null||stripe_account_id.equalsIgnoreCase("")||stripe_account_login_link==null||stripe_account_login_link.equalsIgnoreCase(""))
        {
            new GetProfile().execute();
        }
    }


    private void clickevetn() {
        addstripeact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progresbar.getVisibility()!=View.VISIBLE){
                    Intent i = new Intent(StripeActivity.this, StripeExpressAcountAct.class);
                    startActivity(i);
                }
                // Intent i = new Intent(MerSettingActivity.this, AddPaypalEmail.class);

            }
        });

        genrateloginlinklay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stripe_account_id!=null&&!stripe_account_id.equalsIgnoreCase("")){
                    if (progresbar.getVisibility()!=View.VISIBLE) {

                        new GenrateLoginLink().execute();
                    }
                }

            }
        });
        seestripedashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progresbar.getVisibility()!=View.VISIBLE){
                    Intent i = new Intent(StripeActivity.this, SeeMyStripeDashBoardAct.class);
                    i.putExtra("stripe_login_url",stripe_account_login_link);
                    startActivity(i);
                }

            }
        });
        backlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
finish();
            }
        });
    }

    private void idint() {
        backlay = findViewById(R.id.backlay);
        progresbar = findViewById(R.id.progresbar);
        seestripedashboard = findViewById(R.id.seestripedashboard);
        genrateloginlinklay = findViewById(R.id.genrateloginlinklay);
        addstripeact = findViewById(R.id.addstripeact);
    }

    private class GetProfile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progresbar.setVisibility(View.VISIBLE);

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//https://myngrewards.com/wp-content/plugins/webservice/merchant_profile.php?merchant_id=332
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_profile?";
                Log.e("PROFILEURL =", ">>" + postReceiverUrl + "user_id=" + user_id+"&type=AGENT");

                URL url = new URL(postReceiverUrl);

                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("type", "AGENT");
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
                Log.e("GetProfile Response", ">>>>>>>>>>>>" + response);
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
            progresbar.setVisibility(View.GONE);
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("status");
                    if (message.equalsIgnoreCase("1")) {
                        mySession.setlogindata(result);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        stripe_account_id = jsonObject1.getString("stripe_account_id");
                        stripe_account_login_link = jsonObject1.getString("stripe_account_login_link");
                        if (stripe_account_id!=null&&!stripe_account_id.equalsIgnoreCase("")){
                            addstripeact.setVisibility(View.GONE);
                            if (stripe_account_login_link!=null&&!stripe_account_login_link.equalsIgnoreCase("")){
                                genrateloginlinklay.setVisibility(View.GONE);
                                seestripedashboard.setVisibility(View.VISIBLE);
                            }
                            else {
                                genrateloginlinklay.setVisibility(View.VISIBLE);
                            }

                        }



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }
    private class GenrateLoginLink extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progresbar.setVisibility(View.VISIBLE);

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//https://myngrewards.com/demo/wp-content/plugins/webservice/create_stripe_login_link.php?account_id=acct_1EAb6pHKeMff62ao&merchant_id=476
            try {
                String postReceiverUrl = BaseUrl.baseurl + "generate_login_link?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("merchant_id", user_id);
                params.put("account_id", stripe_account_id);
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
                Log.e("StripeLogin RES", ">>>>>>>>>>>>" + response);
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
            progresbar.setVisibility(View.GONE);
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("status");
                    if (message.equalsIgnoreCase("1")) {
                        new GetProfile().execute();




                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

}
