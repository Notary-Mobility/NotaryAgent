package main.com.notaryagent.paymentclasses;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import main.com.notaryagent.R;
import main.com.notaryagent.activity.MainActivity;
import main.com.notaryagent.activity.MyNextPayoutAct;
import main.com.notaryagent.activity.MySignings;
import main.com.notaryagent.activity.StripeActivity;
import main.com.notaryagent.activity.TrackRideAct;
import main.com.notaryagent.activity.WithdrawActivity;
import main.com.notaryagent.app.Config;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.ExpandableHeightListView;
import main.com.notaryagent.constant.MySession;
import main.com.notaryagent.constant.RideBean;
import main.com.notaryagent.constant.RideHistoryBean;
import main.com.notaryagent.draweractivity.BaseActivity;
import main.com.notaryagent.tabactivity.MainTabActivity;
import main.com.notaryagent.utils.NotificationUtils;


public class WalletActivity extends AppCompatActivity {
    private TextView nextpaycheck,mysigning,transection_tv,withdraw_tv,walletamt,totalamount, onefifty_but, hundred_but, fifty_but, addmoney,addcards;
    private EditText amount_et;
    private RelativeLayout exit_app_but;
    private String amount_str="";
    String user_log_data="",user_id="",car_charge_str="",rating="",tips_amount_str="",comment_str="";
    MySession mySession;
    public static String cust_id="";
    private ProgressBar prgressbar;
    private ExpandableHeightListView mycompletesign;
    private ArrayList<RideHistoryBean> ridehislistCompletedlist;
    private RideHisAdp  ridehisadp;
    private TextView ride_sts_tv;
    //request popup code
    private long timeCountInMilliSeconds;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    Dialog booking_request_dialog;
    private String request_id="",diff_second="",request_id_main="",signing_charge="",status_job="",time_zone="";
    private boolean isVisible=false;
    private CountDownTimer countDownTimer;
    boolean dialogsts_show = false;
    private TextView textViewTime;
    ACProgressCustom ac_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wallet);
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

        idnit();
        clickevent();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");

                    JSONObject data = null;
                    try {
                        data = new JSONObject(message);
                        String keyMessage = data.getString("key").trim();
                        Log.e("KEY_WALLET =", "" + keyMessage);
                        if (keyMessage.equalsIgnoreCase("your booking request is Now")) {

                            if (booking_request_dialog == null) {
                                Log.e("COME ", "null");
                                String firstname = data.getString("first_name");
                                String lastname = data.getString("last_name");
                                String picuplocation = data.getString("picuplocation");
                                String dropofflocation = data.getString("dropofflocation");
                                request_id = String.valueOf(data.getInt("request_id"));
                                String picklaterdate = data.getString("picklaterdate");
                                String picklatertime = data.getString("picklatertime");
                                String booktype = data.getString("booktype");
                                String rating = "";
                                String favorite_ride = "";

                                diff_second = data.getString("diff_second");

                                String location_type_str = data.getString("location_type");
                                String realstate_signing_str = data.getString("realstate_signing");
                                String number_of_witness_str = data.getString("number_of_witness");
                                String name_str = data.getString("name");
                                String mobile_str = data.getString("mobile");
                                String email_str = data.getString("email");
                                String number_of_signing_str = data.getString("number_of_signing");
                                String type_of_signing_name_str = data.getString("type_of_signing_name");
                                String picklaterdate_str = data.getString("picklaterdate");
                                String picklatertime_str = data.getString("picklatertime");

                                String car_type_image = data.getString("car_type_image");
                                String paid_amount = data.getString("paid_amount");
                                String car_type_name = data.getString("car_type_name");
                                String emailtoprit = data.getString("emailtoprit");
                                String scanandemail = data.getString("scanandemail");
                                String prepaid = data.getString("prepaid");
                                String overnight = data.getString("overnight");
                                String night_charge = data.getString("night_charge");
                                String today_charge = data.getString("today_charge");
                                if (isVisible){
                                    Log.e("COME IN","TRUE");
                                    showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride,location_type_str,realstate_signing_str,number_of_witness_str,name_str,mobile_str,email_str,number_of_signing_str,type_of_signing_name_str,car_type_image,car_type_name,paid_amount,emailtoprit,scanandemail,prepaid,overnight,night_charge,today_charge);

                                }


                            } else if (booking_request_dialog.isShowing()) {
                                Log.e("COME ", "show");
                            } else {
                                Log.e("COME ", "else");
                                String firstname = data.getString("first_name");
                                String lastname = data.getString("last_name");
                                String picuplocation = data.getString("picuplocation");
                                String dropofflocation = data.getString("dropofflocation");
                                request_id = String.valueOf(data.getInt("request_id"));
                                String picklaterdate = data.getString("picklaterdate");
                                String picklatertime = data.getString("picklatertime");
                                String booktype = data.getString("booktype");
                                String rating = "";
                                diff_second = data.getString("diff_second");
                                String favorite_ride = "";
                                String location_type_str = data.getString("location_type");
                                String realstate_signing_str = data.getString("realstate_signing");
                                String number_of_witness_str = data.getString("number_of_witness");
                                String name_str = data.getString("name");
                                String mobile_str = data.getString("mobile");
                                String email_str = data.getString("email");
                                String number_of_signing_str = data.getString("number_of_signing");
                                String type_of_signing_name_str = data.getString("type_of_signing_name");
                                String picklaterdate_str = data.getString("picklaterdate");
                                String picklatertime_str = data.getString("picklatertime");
                                String car_type_image = data.getString("car_type_image");
                                String paid_amount = data.getString("paid_amount");
                                String car_type_name = data.getString("car_type_name");
                                String emailtoprit = data.getString("emailtoprit");
                                String scanandemail = data.getString("scanandemail");
                                String prepaid = data.getString("prepaid");
                                String overnight = data.getString("overnight");
                                String night_charge = data.getString("night_charge");
                                String today_charge = data.getString("today_charge");


                                if (isVisible){
                                    showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride,location_type_str,realstate_signing_str,number_of_witness_str,name_str,mobile_str,email_str,number_of_signing_str,type_of_signing_name_str,car_type_image,car_type_name,paid_amount,emailtoprit,scanandemail,prepaid,overnight,night_charge,today_charge);

                                }
                            }
                        }
                        if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {
                            request_id = data.getString("request_id");

                            usercancelRide();
                        }
                        else if (keyMessage.equalsIgnoreCase("Sorry this signing request is accepted by other agent")) {
                            stopCountDownTimer();
                            if (booking_request_dialog == null) {

                            } else {
                                if (booking_request_dialog != null || booking_request_dialog.isShowing()) {
                                    booking_request_dialog.cancel();
                                    booking_request_dialog.dismiss();
                                    diff_second = "";
                                }

                            }
                            // reideAllreadyCanceled();

                            reideAllreadyAccepted();
                        }
                        else if (keyMessage.equalsIgnoreCase("your booking request is Letter")) {
                            if (booking_request_dialog == null) {
                                String firstname = data.getString("first_name");
                                String lastname = data.getString("last_name");
                                String picuplocation = data.getString("picuplocation");
                                String dropofflocation = data.getString("dropofflocation");
                                String picklaterdate = data.getString("picklaterdate");
                                String picklatertime = data.getString("picklatertime");
                                String booktype = data.getString("booktype");
                                String rating = "";
                                String favorite_ride = "";
                                String location_type_str = data.getString("location_type");
                                String realstate_signing_str = data.getString("realstate_signing");
                                String number_of_witness_str = data.getString("number_of_witness");
                                String name_str = data.getString("name");
                                String mobile_str = data.getString("mobile");
                                String email_str = data.getString("email");
                                String number_of_signing_str = data.getString("number_of_signing");
                                String type_of_signing_name_str = data.getString("type_of_signing_name");
                                String picklaterdate_str = data.getString("picklaterdate");
                                String picklatertime_str = data.getString("picklatertime");
                                String car_type_image = data.getString("car_type_image");
                                String paid_amount = data.getString("paid_amount");
                                String car_type_name = data.getString("car_type_name");
                                String emailtoprit = data.getString("emailtoprit");
                                String scanandemail = data.getString("scanandemail");
                                String prepaid = data.getString("prepaid");
                                String overnight = data.getString("overnight");
                                String night_charge = data.getString("night_charge");
                                String today_charge = data.getString("today_charge");


                                request_id = String.valueOf(data.getInt("request_id"));
                                diff_second = data.getString("diff_second");
                                if (isVisible){
                                    showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride,location_type_str,realstate_signing_str,number_of_witness_str,name_str,mobile_str,email_str,number_of_signing_str,type_of_signing_name_str,car_type_image,car_type_name,paid_amount,emailtoprit,scanandemail,prepaid,overnight,night_charge,today_charge);

                                }
                            } else if (booking_request_dialog.isShowing()) {

                            } else {
                                String firstname = data.getString("first_name");
                                String lastname = data.getString("last_name");
                                String picuplocation = data.getString("picuplocation");
                                String dropofflocation = data.getString("dropofflocation");
                                request_id = String.valueOf(data.getInt("request_id"));
                                diff_second = data.getString("diff_second");
                                String picklaterdate = data.getString("picklaterdate");
                                String picklatertime = data.getString("picklatertime");
                                String booktype = data.getString("booktype");
                                String rating = "";
                                String favorite_ride = "";

                                String location_type_str = data.getString("location_type");
                                String realstate_signing_str = data.getString("realstate_signing");
                                String number_of_witness_str = data.getString("number_of_witness");
                                String name_str = data.getString("name");
                                String mobile_str = data.getString("mobile");
                                String email_str = data.getString("email");
                                String number_of_signing_str = data.getString("number_of_signing");
                                String type_of_signing_name_str = data.getString("type_of_signing_name");
                                String picklaterdate_str = data.getString("picklaterdate");
                                String picklatertime_str = data.getString("picklatertime");

                                String car_type_image = data.getString("car_type_image");
                                String paid_amount = data.getString("paid_amount");
                                String car_type_name = data.getString("car_type_name");
                                String emailtoprit = data.getString("emailtoprit");
                                String scanandemail = data.getString("scanandemail");
                                String prepaid = data.getString("prepaid");
                                String overnight = data.getString("overnight");
                                String night_charge = data.getString("night_charge");
                                String today_charge = data.getString("today_charge");


                                if (isVisible){
                                    showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride,location_type_str,realstate_signing_str,number_of_witness_str,name_str,mobile_str,email_str,number_of_signing_str,type_of_signing_name_str,car_type_image,car_type_name,paid_amount,emailtoprit,scanandemail,prepaid,overnight,night_charge,today_charge);

                                }
                            }


                        } else if (keyMessage.equalsIgnoreCase("your booking request is cancel by user")) {
                            stopCountDownTimer();
                            if (booking_request_dialog == null) {

                            } else {
                                if (booking_request_dialog != null || booking_request_dialog.isShowing()) {
                                    booking_request_dialog.cancel();
                                    booking_request_dialog.dismiss();
                                    diff_second = "";
                                }

                            }
                            // reideAllreadyCanceled();

                            usercancelRide();
                        } else if (keyMessage.equalsIgnoreCase("arriving latter booking request")) {
                            String picklaterdate = data.getString("picklaterdate");
                            String picklatertime = data.getString("picklatertime");

                            //  bookedRequestAlert(picklaterdate,picklatertime);


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


    }


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(WalletActivity.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isVisible=false;


    }


    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fifty_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_et.setText("50");
                fifty_but.setBackgroundResource(R.drawable.border_bluerounddrab);
                hundred_but.setBackgroundResource(R.drawable.border_grey_rec);
                onefifty_but.setBackgroundResource(R.drawable.border_grey_rec);

            }
        });
        hundred_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_et.setText("100");
                hundred_but.setBackgroundResource(R.drawable.border_bluerounddrab);
                onefifty_but.setBackgroundResource(R.drawable.border_grey_rec);
                fifty_but.setBackgroundResource(R.drawable.border_grey_rec);
            }
        });

        onefifty_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_et.setText("150");
                onefifty_but.setBackgroundResource(R.drawable.border_bluerounddrab);
                hundred_but.setBackgroundResource(R.drawable.border_grey_rec);
                fifty_but.setBackgroundResource(R.drawable.border_grey_rec);
            }
        });
        withdraw_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(WalletActivity.this, WithdrawActivity.class);
                startActivity(i);
            }
        });
        transection_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(WalletActivity.this, TransectionHistory.class);
                startActivity(i);
            }
        });
        mysigning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent i  = new Intent(WalletActivity.this, MySignings.class);
                startActivity(i);*/
                Intent i  = new Intent(WalletActivity.this, MySignings.class);
                startActivity(i);

            }
        });
        nextpaycheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent i  = new Intent(WalletActivity.this, MyNextPayoutAct.class);
                startActivity(i);*/
                Intent i  = new Intent(WalletActivity.this, StripeActivity.class);
                startActivity(i);
            }
        });
        addmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_str = amount_et.getText().toString();
                if (amount_str==null||amount_str.equalsIgnoreCase("")){
                    Toast.makeText(WalletActivity.this,getResources().getString(R.string.enteramount),Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i = new Intent(WalletActivity.this,MyCardsPayment.class);
                    i.putExtra("amount",amount_str);
                    startActivity(i);
                    /*if (cust_id==null||cust_id.equalsIgnoreCase("")||cust_id.equalsIgnoreCase("0")||cust_id.equalsIgnoreCase("null")){
                        Intent i = new Intent(WalletActivity.this,AddCreditCardAct.class);
                        i.putExtra("type","add_to_wallet");
                        startActivity(i);
                    }
                    else {
                        Intent i = new Intent(WalletActivity.this,MyCardsPayment.class);
                        i.putExtra("amount",amount_str);
                        startActivity(i);
                    }*/


                    /*Intent i = new Intent(WalletActivity.this,ConfirmPayment.class);
                    i.putExtra("amount_str",amount_str);
                    i.putExtra("request_id",request_id);
                    i.putExtra("car_charge_str",car_charge_str);
                    i.putExtra("rating",rating);
                    i.putExtra("tips_amount_str",tips_amount_str);
                    i.putExtra("comment_str",comment_str);
                    i.putExtra("time_zone",time_zone);
                    i.putExtra("transaction_type","add_to_wallet");
                    startActivity(i);
*/
                }

            }
        });

        addcards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cust_id==null||cust_id.equalsIgnoreCase("")||cust_id.equalsIgnoreCase("0")||cust_id.equalsIgnoreCase("null")){
                    Intent i = new Intent(WalletActivity.this,AddCreditCardAct.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(WalletActivity.this,MyaddedCards.class);
                    startActivity(i);
                }

            }
        });
    }

    private void idnit() {
        ride_sts_tv = findViewById(R.id.ride_sts_tv);
        mycompletesign = findViewById(R.id.mycompletesign);
        mycompletesign.setExpanded(true);
        nextpaycheck = findViewById(R.id.nextpaycheck);
        mysigning = findViewById(R.id.mysigning);
        transection_tv = findViewById(R.id.transection_tv);
        withdraw_tv = findViewById(R.id.withdraw_tv);
        prgressbar = findViewById(R.id.prgressbar);
        addcards = findViewById(R.id.addcards);
        addmoney = findViewById(R.id.addmoney);
        exit_app_but = findViewById(R.id.exit_app_but);
        totalamount = findViewById(R.id.totalamount);

        fifty_but = (TextView) findViewById(R.id.fifty_but);
        hundred_but = (TextView) findViewById(R.id.hundred_but);
        onefifty_but = (TextView) findViewById(R.id.onefifty_but);
        amount_et = (EditText) findViewById(R.id.amount_et);
        fifty_but.setText("$"+"50");
        hundred_but.setText("$"+"100");
        onefifty_but.setText("$"+"150");
        amount_et.setHint(""+getResources().getString(R.string.entermoney)+" $"+"50 -"+"$150");

    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }
        LocalBroadcastManager.getInstance(WalletActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(WalletActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(WalletActivity.this.getApplicationContext());

        new GetUserProfile().execute();

    }

    private class GetUserProfile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgressbar.setVisibility(View.VISIBLE);
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(String... strings) {
//http://technorizen.com/WORKSPACE1/shipper/webservice/get_driver?driver_id=36
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_profile?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("type", "USER");


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
                Log.e("Json Driver Profile", ">>>>>>>>>>>>" + response);
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
            prgressbar.setVisibility(View.GONE);
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        String online_status = jsonObject1.getString("online_status");
                        cust_id = jsonObject1.getString("cust_id");
                        BaseActivity.amount = jsonObject1.getString("amount");
                        totalamount.setText("$"+ BaseActivity.amount);

                    }
                    new GetMyCompleteEarning().execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }
    private class GetMyCompleteEarning extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgressbar.setVisibility(View.VISIBLE);

            ridehislistCompletedlist = new ArrayList<>();

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/get_user_history?user_id=22&type=DRIVER
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_user_history?";
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
            prgressbar.setVisibility(View.GONE);
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    Log.e("Resposne in my Booking", "" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successfull")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            RideHistoryBean ridebean = new RideHistoryBean();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            ridebean.setId(jsonObject1.getString("id"));
                            ridebean.setPicuplocation(jsonObject1.getString("picuplocation"));
                            ridebean.setDropofflocation(jsonObject1.getString("dropofflocation"));
                            ridebean.setStatus(jsonObject1.getString("status"));
                            // ridebean.setDistance(jsonObject1.getString("distance"));
                            ridebean.setFare_amount(jsonObject1.getString("ride_fare"));
                            // ridebean.setBooktype(jsonObject1.getString("booktype"));
                            ridebean.setPicklaterdate(jsonObject1.getString("picklaterdate"));
                            ridebean.setPicklatertime(jsonObject1.getString("picklatertime"));

                            try {
                                Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject1.getString("req_datetime"));
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy hh:mm a");
                                String strDate = formatter.format(date1);
                                ridebean.setReq_datetime(strDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            JSONArray jsonArray1 = jsonObject1.getJSONArray("user_details");
                            for (int j=0;j<jsonArray1.length();j++)
                            {
                                JSONObject jsonObject3 =  jsonArray1.getJSONObject(j);
                                ridebean.setUsername(jsonObject3.getString("first_name")+" "+jsonObject3.getString("last_name"));
                                ridebean.setUsernumber(jsonObject3.getString("mobile"));
                                ridebean.setUserimg(jsonObject3.getString("image"));
                            }


                            String sts = jsonObject1.getString("status");
                            if (sts.equalsIgnoreCase("Finish")||sts.equalsIgnoreCase("End")) {
                                ridehislistCompletedlist.add(ridebean);
                            }
                        }
                        //                        else if (sts.equalsIgnoreCase("Accept")||sts.equalsIgnoreCase("Arrived")||sts.equalsIgnoreCase("Start")){

                        ridehisadp = new RideHisAdp(WalletActivity.this, ridehislistCompletedlist);
                        mycompletesign.setAdapter(ridehisadp);
                        ridehisadp.notifyDataSetChanged();

                    }
                    if (ridehislistCompletedlist == null || ridehislistCompletedlist.isEmpty()) {
                        ride_sts_tv.setVisibility(View.VISIBLE);
                        ride_sts_tv.setText("" + getResources().getString(R.string.norides));
                    } else {
                        ride_sts_tv.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }
    public class RideHisAdp extends BaseAdapter {

        String[] result;
        Context context;
        ArrayList<RideHistoryBean> ridehislist;
        private LayoutInflater inflater = null;


        public RideHisAdp(Activity activity, ArrayList<RideHistoryBean> ridehislist) {
            this.ridehislist = ridehislist;
            this.context = activity;

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            //return 4;
            return ridehislist == null ? 0 : ridehislist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            View rowView;

            rowView = inflater.inflate(R.layout.my_earning_lay, null);
            TextView signinglocation = (TextView) rowView.findViewById(R.id.signinglocation);
            TextView date_tv = (TextView) rowView.findViewById(R.id.date_tv);
            TextView todayearning = (TextView) rowView.findViewById(R.id.todayearning);
            ImageView car_img = (ImageView) rowView.findViewById(R.id.car_img);
            signinglocation.setText("" + ridehislist.get(position).getPicuplocation());
            String req_datetime = ridehislist.get(position).getReq_datetime();
            String sts = ridehislist.get(position).getStatus();

            todayearning.setText("$ "+ridehislist.get(position).getFare_amount());
            if (ridehislist.get(position).getPicklaterdate()==null||ridehislist.get(position).getPicklaterdate().equalsIgnoreCase("")){
                date_tv.setText("" + ridehislist.get(position).getReq_datetime());
            }
            else {
                date_tv.setText("" + ridehislist.get(position).getPicklaterdate()+" "+ridehislist.get(position).getPicklatertime());

            }
            //date_tv.setText("" + req_datetime);

            return rowView;
        }

    }

    // code to show request popup

    private void showNewRequest(String firstname, String lastname, String picuplocation, String dropofflocation, final String request_id, String picklaterdate, String picklatertime, String booktype, String rating, String favorite_ride, String location_type_str, String realstate_signing_str, String number_of_witness_str, String name_str, String mobile_str, String email_str, String number_of_signing_str, String type_of_signing_name_str, String car_type_image, String car_type_name, String car_min_charge,String emailtoprit,String scanandemail,String prepaid,String overnight,String night_charge,String today_charge) {
        dialogsts_show = true;
        request_id_main = request_id;
        booking_request_dialog = new Dialog(WalletActivity.this);
        booking_request_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        booking_request_dialog.setCancelable(false);
        booking_request_dialog.setContentView(R.layout.custom_new_job_lay);
        booking_request_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView decline =  booking_request_dialog.findViewById(R.id.decline);
        TextView datetimetv =  booking_request_dialog.findViewById(R.id.datetimetv);
        TextView rating_tv =  booking_request_dialog.findViewById(R.id.rating_tv);
        rating_tv.setText("" + rating);
        if (booktype == null || booktype.equalsIgnoreCase("")) {
            datetimetv.setVisibility(View.GONE);
        } else {
            datetimetv.setVisibility(View.VISIBLE);
            // datetimetv.setText(""+getResources().getString(R.string.booktime)+" "+picklaterdate.trim()+" "+picklatertime);
        }

        TextView accept =  booking_request_dialog.findViewById(R.id.accept);
        TextView pick_location =  booking_request_dialog.findViewById(R.id.pick_location);
        TextView drop_location =  booking_request_dialog.findViewById(R.id.drop_location);
        textViewTime =  booking_request_dialog.findViewById(R.id.textViewTime);
        TextView username =  booking_request_dialog.findViewById(R.id.username);
        final ProgressBar progressBarCircle = (ProgressBar) booking_request_dialog.findViewById(R.id.progressBarCircle);

        TextView signinglocation =  booking_request_dialog.findViewById(R.id.signinglocation);
        TextView locationtype =  booking_request_dialog.findViewById(R.id.locationtype);
        TextView realstatesigning =  booking_request_dialog.findViewById(R.id.realstatesigning);
        TextView numberofwitness =  booking_request_dialog.findViewById(R.id.numberofwitness);
        TextView name_et =  booking_request_dialog.findViewById(R.id.name_et);
        TextView phone_et =  booking_request_dialog.findViewById(R.id.phone_et);
        TextView email_et =  booking_request_dialog.findViewById(R.id.email_et);
        TextView typeofsigning =  booking_request_dialog.findViewById(R.id.typeofsigning);
        TextView numberofsigning =  booking_request_dialog.findViewById(R.id.numberofsigning);
        LinearLayout selectrealstatelay =  booking_request_dialog.findViewById(R.id.selectrealstatelay);
        LinearLayout date_lay =  booking_request_dialog.findViewById(R.id.date_lay);
        ImageView carimage =  booking_request_dialog.findViewById(R.id.carimage);
        TextView carname =  booking_request_dialog.findViewById(R.id.carname);
        TextView price =  booking_request_dialog.findViewById(R.id.price);
        TextView date_tv =  booking_request_dialog.findViewById(R.id.date_tv);
        TextView emailtoprit_tv = booking_request_dialog.findViewById(R.id.emailtoprit);
        TextView scanandemail_tv = booking_request_dialog.findViewById(R.id.scanandemail);
        TextView prepaid_tv = booking_request_dialog.findViewById(R.id.prepaid);
        TextView overnight_tv = booking_request_dialog.findViewById(R.id.overnight);
        TextView extracharge = booking_request_dialog.findViewById(R.id.extracharge);
        carname.setText(""+car_type_name);
        price.setText("$ "+car_min_charge);
        if (car_type_image==null||car_type_image.equalsIgnoreCase("")||car_type_image.equalsIgnoreCase(BaseUrl.image_baseurl)){

        }
        else {
            Picasso.with(WalletActivity.this).load(car_type_image).placeholder(R.drawable.mini).into(carimage);

        }
        if (picklaterdate==null||picklaterdate.equalsIgnoreCase("")){
            date_lay.setVisibility(View.GONE);
        }

        username.setText("" + firstname + " " + lastname);
        pick_location.setText("" + picuplocation);
        drop_location.setText("" + dropofflocation);

        signinglocation.setText("" + picuplocation);
        locationtype.setText("Location Type : " + location_type_str);
        realstatesigning.setText("Real Estate Signing : " + realstate_signing_str);
        numberofwitness.setText("Number of witness : " + number_of_witness_str);
        name_et.setText("" + name_str);
        phone_et.setText("" + mobile_str);
        email_et.setText("" + email_str);
        typeofsigning.setText("Type of Signing : " + type_of_signing_name_str);
        numberofsigning.setText("Number of signing : " + number_of_signing_str);
        date_tv.setText("" + picklaterdate.trim() + " " + picklatertime.trim());
        extracharge.setText(getResources().getString(R.string.nightcharge) + " $" + night_charge+" , "+getResources().getString(R.string.extracharge)+" $"+today_charge);



        if (realstate_signing_str.equalsIgnoreCase("No")) {
            selectrealstatelay.setVisibility(View.GONE);
        } else {
            selectrealstatelay.setVisibility(View.VISIBLE);
            emailtoprit_tv.setText(getResources().getString(R.string.emailtoprint)+" $"+emailtoprit);
            scanandemail_tv.setText(getResources().getString(R.string.scanandemail)+" $"+scanandemail);
            prepaid_tv.setText(getResources().getString(R.string.prepaid)+" $"+prepaid);
            overnight_tv.setText(getResources().getString(R.string.overnight)+" $"+overnight);


        }




        int sec = 300;
        //  int sec = 17;
        int mili = 1000;
        int newsec = 1;
        Log.e("diff_second ?", "POPUP" + diff_second);
        if (diff_second == null || diff_second.equalsIgnoreCase("")) {
        } else {
            int difernce = Integer.parseInt(diff_second);
            newsec = sec - difernce;
        }
        Log.e("newsec >>", "dd " + newsec);
        timeCountInMilliSeconds = 1 * newsec * mili;
        Log.e("Count Timer", "gg " + timeCountInMilliSeconds);
        progressBarCircle.setMax((int) 300);
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                if (booking_request_dialog != null || booking_request_dialog.isShowing()) {
                    booking_request_dialog.cancel();
                    booking_request_dialog.dismiss();
                    diff_second = "";
                }

                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                stopCountDownTimer();
            }
        }.start();
        countDownTimer.start();


        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                if (booking_request_dialog != null || booking_request_dialog.isShowing()) {
                    booking_request_dialog.cancel();
                    booking_request_dialog.dismiss();
                    diff_second = "";
                }


                status_job = "Cancel";
                new ResponseToRequest().execute(request_id, status_job);
                stopCountDownTimer();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                if (booking_request_dialog != null || booking_request_dialog.isShowing()) {
                    booking_request_dialog.cancel();
                    booking_request_dialog.dismiss();
                    diff_second = "";
                }



                status_job = "Accept";
                new ResponseToRequest().execute(request_id, status_job);
                stopCountDownTimer();
            }
        });
        if (booking_request_dialog.isShowing()) {

        } else {
            booking_request_dialog.show();
        }


    }
    private void usercancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(WalletActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv =  canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv =  canceldialog.findViewById(R.id.message_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                canceldialog.dismiss();
                // new GetCurrentBooking().execute();

            }
        });
        canceldialog.show();


    }
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d",
               /* TimeUnit.MILLISECONDS.toHours(milliSeconds),*/
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }
    private class ResponseToRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            if (ac_dialog != null) {
                if (isVisible){
                    ac_dialog.show();
                }

            }

            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/driver_accept_and_Cancel_request?request_id=1&status=Accept
            //http://mobileappdevelop.co/NAXCAN/webservice/
            try {
                String postReceiverUrl = BaseUrl.baseurl + "driver_accept_and_Cancel_request?";

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();


                params.put("request_id", strings[0]);
                params.put("status", strings[1]);
                params.put("timezone", time_zone);
                params.put("driver_id", user_id);


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
            Log.e("ACCEPT RESULT", "....... " + result);

            if (result == null) {
                if (ac_dialog != null) {
                    if (isVisible){
                        ac_dialog.dismiss();
                    }

                }

            } else if (result.isEmpty()) {
                if (ac_dialog != null) {
                    if (isVisible){
                        ac_dialog.dismiss();
                    }

                }

            } else {

                try {
                    if (ac_dialog != null) {
                        if (isVisible){
                            ac_dialog.dismiss();
                        }

                    }

                    JSONObject jsonObject = new JSONObject(result);
                    Log.e("SIGNACTION "," >>> "+result);

                    if (jsonObject.getString("status").equalsIgnoreCase("0")) {
                        if (jsonObject.getString("result").equalsIgnoreCase("already accepted")) {
                            reideAllreadyAccepted();
                        } else {
                            reideAllreadyCanceled();
                        }

                    } else {
                        if (status_job.equalsIgnoreCase("Cancel")) {

                        } else {
                            Intent i = new Intent(WalletActivity.this, TrackRideAct.class);
                            i.putExtra("request_id",request_id);
                            startActivity(i);
                            //finish();
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }
    private void reideAllreadyAccepted() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(WalletActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv =  canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv =  canceldialog.findViewById(R.id.message_tv);
        message_tv.setText(""+getResources().getString(R.string.alreadyaccept));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }


                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }

    private void reideAllreadyCanceled() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(WalletActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv =  canceldialog.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }

                canceldialog.dismiss();

            }
        });
        canceldialog.show();
    }
    private void stopCountDownTimer() {
        if (countDownTimer == null) {

        } else {
            countDownTimer.cancel();
        }

    }


}
