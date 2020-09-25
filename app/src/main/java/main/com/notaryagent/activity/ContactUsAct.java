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


public class ContactUsAct extends AppCompatActivity {
private ACProgressCustom ac_dialog;
    private RelativeLayout exit_app_but;
    private MySession mySession;
    private String user_log_data="",user_id="";
    private TextView submitbut;
    private EditText name_et,email_et,address_et,city_et,state_et,zipcode_et,phonenumber_et,message_et;
    private String name_str="",email_str="",address_str="",city_str="",state_str="",zipcode_str="",phonenumber_str="",message_str="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

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
               name_str = name_et.getText().toString();
               email_str = email_et.getText().toString();
               address_str = address_et.getText().toString();
               city_str = city_et.getText().toString();
               state_str = state_et.getText().toString();
               zipcode_str = zipcode_et.getText().toString();
               phonenumber_str = phonenumber_et.getText().toString();
               message_str = message_et.getText().toString();

                if (name_str==null||name_str.equalsIgnoreCase("")){
                    Toast.makeText(ContactUsAct.this,getResources().getString(R.string.entername),Toast.LENGTH_LONG).show();
                }
                else if (email_str==null||email_str.equalsIgnoreCase("")){
                    Toast.makeText(ContactUsAct.this,getResources().getString(R.string.enteremail),Toast.LENGTH_LONG).show();
                }
                else if (address_str==null||address_str.equalsIgnoreCase("")){
                    Toast.makeText(ContactUsAct.this,getResources().getString(R.string.enteraddres),Toast.LENGTH_LONG).show();
                }
                else if (city_str==null||city_str.equalsIgnoreCase("")){
                    Toast.makeText(ContactUsAct.this,getResources().getString(R.string.entercity),Toast.LENGTH_LONG).show();
                }
                else if (state_str==null||state_str.equalsIgnoreCase("")){
                    Toast.makeText(ContactUsAct.this,getResources().getString(R.string.enterstate),Toast.LENGTH_LONG).show();
                }
                else if (zipcode_str==null||zipcode_str.equalsIgnoreCase("")){
                    Toast.makeText(ContactUsAct.this,getResources().getString(R.string.enterzipcode),Toast.LENGTH_LONG).show();
                }
                else if (phonenumber_str==null||phonenumber_str.equalsIgnoreCase("")){
                    Toast.makeText(ContactUsAct.this,getResources().getString(R.string.plsenterphone),Toast.LENGTH_LONG).show();
                }
                else if (phonenumber_str.length()<10){
                    Toast.makeText(ContactUsAct.this,getResources().getString(R.string.plsentervalidnumber),Toast.LENGTH_LONG).show();
                }
                else if (message_str==null||message_str.equalsIgnoreCase("")){
                    Toast.makeText(ContactUsAct.this,getResources().getString(R.string.entermessage),Toast.LENGTH_LONG).show();
                }

               else {
                   new AddContactUs().execute();
               }



            }
        });
    }

    private void idinti() {
        message_et = findViewById(R.id.message_et);
        phonenumber_et = findViewById(R.id.phonenumber_et);
        zipcode_et = findViewById(R.id.zipcode_et);
        state_et = findViewById(R.id.state_et);
        city_et = findViewById(R.id.city_et);
        address_et = findViewById(R.id.address_et);
        email_et = findViewById(R.id.email_et);
        name_et = findViewById(R.id.name_et);
        exit_app_but = findViewById(R.id.exit_app_but);
        submitbut = findViewById(R.id.submitbut);
    }


    private class AddContactUs extends AsyncTask<String, String, String> {
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
                String postReceiverUrl = BaseUrl.baseurl + "contact_us";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                StringBuilder postData = new StringBuilder();
                params.put("user_id", user_id);
                params.put("name", name_str);
                params.put("email", email_str);
                params.put("address", address_str);
                params.put("city", city_str);
                params.put("state", state_str);
                params.put("zipcode", zipcode_str);
                params.put("phonenumber", phonenumber_str);
                params.put("message", message_str);



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
                        Toast.makeText(ContactUsAct.this, getResources().getString(R.string.yourrequestsendsuccess), Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

}
