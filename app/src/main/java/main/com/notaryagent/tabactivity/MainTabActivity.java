package main.com.notaryagent.tabactivity;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.CalendarContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabWidget;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.notaryagent.R;
import main.com.notaryagent.activity.LoginAct;
import main.com.notaryagent.app.Config;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.CalendarHelper;
import main.com.notaryagent.constant.CancelReasBean;
import main.com.notaryagent.constant.MyReceiver;
import main.com.notaryagent.constant.MySession;
import main.com.notaryagent.draweractivity.BaseActivity;
import main.com.notaryagent.service.TrackingService;
import main.com.notaryagent.utils.NotificationUtils;


public class MainTabActivity extends BaseActivity {


    int WhichIndex = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String WhichLanguage = "";
    private Boolean exit ;
    String scrsts="";

    TextView counter_wallet,counter_shedule,counter_order,counter_message;
    int counter_message_int=0,counter_shedule_int=0,counter_order_int=0;
    ScheduledExecutorService scheduleTaskExecutor;
    private String user_log_data="",user_id="";
    private MySession mySession;
    private TabHost tabhost;
    private TabWidget tabwidget;
    private FrameLayout contentFrameLayout;
    LocalActivityManager mlam;
    //request popup code
    private long timeCountInMilliSeconds;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    Dialog booking_request_dialog;
    private String request_id="",signing_loc_str="",signing_date_time_str="",diff_second="",request_id_main="",signing_charge="",status_job="",time_zone="";
    private boolean isVisible=false;
    private CountDownTimer countDownTimer;
    boolean dialogsts_show = false;
    private TextView textViewTime;
    ACProgressCustom ac_dialog;


    private ListView reasonlist;
    private CancelReasonAdp cancelReasonAdp;
    private ArrayList<CancelReasBean> cancellist;
    private ArrayList<String> list;
    private Hashtable<String, String> calendarIdTable;

    private String cancel_reaison="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                contentFrameLayout = (FrameLayout) findViewById(R.id.contentFrame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_main_tab, contentFrameLayout);
        list = new ArrayList<String>();
        calendarIdTable = CalendarHelper.listCalendarId(this);
        Enumeration enumeration = calendarIdTable.keys();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            list.add(key);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setStartTime();
        //setContentView(R.layout.activity_main_tab);
        tabhost = (TabHost)findViewById(R.id.tabhost);
        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        Log.e("TIME ZONE >>", tz.getDisplayName());
        Log.e("TIME ZONE ID>>", tz.getID());
        time_zone = tz.getID();
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();
       // tabhost.setup();
        mlam = new LocalActivityManager(this, false);
        mlam.dispatchCreate(savedInstanceState);
        tabhost.setup(mlam);

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

        adddataincancellist();
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
                        Log.e("KEY_HOME =", "" + keyMessage);
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
                        else if (keyMessage.equalsIgnoreCase("user pay the signing amount")) {
                            String picuplocation = data.getString("picuplocation");
                            String req_datetime = data.getString("req_datetime");
                            String picklaterdate = data.getString("picklaterdate");
                            String picklatertime = data.getString("picklatertime");

                            paymentDoneByUser(picuplocation,req_datetime,picklaterdate,picklatertime,"paymentdone");

                        }else if (keyMessage.equalsIgnoreCase("user confirm payment amount")) {
                            String picuplocation = data.getString("picuplocation");
                            String req_datetime = data.getString("req_datetime");
                            String picklaterdate = data.getString("picklaterdate");
                            String picklatertime = data.getString("picklatertime");

                            paymentDoneByUser(picuplocation,req_datetime,picklaterdate,picklatertime,"paymentconfirm");

                        }

                        else if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {
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

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Log.e("Get Notification >>", "NULL");
        } else {
            String message = bundle.getString("message");
            Log.e("Get Notification >>", "" + message);
            if (message == null || message.equalsIgnoreCase("") || message.equalsIgnoreCase("null")) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                   String keys = jsonObject.getString("key").trim();



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }


        Bundle extra = getIntent().getExtras();
        exit = false;
        if (extra == null) {


        } else {
            scrsts = extra.getString("scrsts");
            if (scrsts==null||scrsts.equalsIgnoreCase("")){
                if (extra.containsKey("WhichIndex")) {

                    WhichIndex = extra.getInt("WhichIndex", 0);
                }


            }
            else {
                if (scrsts.equalsIgnoreCase("3")){
                    WhichIndex = extra.getInt("WhichIndex", 3);
                }
                else if (scrsts.equalsIgnoreCase("wallet")){
                    WhichIndex = extra.getInt("WhichIndex", 2);
                }
                if (extra.containsKey("WhichIndex")) {

                    WhichIndex = extra.getInt("WhichIndex", 0);
                }

            }

        }

      //  TabHost tabHost = getTabHost();
        TabHost.TabSpec homespec = tabhost.newTabSpec("Home");
        View tabIndicator1 = LayoutInflater.from(this).inflate(R.layout.tab_indicator, tabhost.getTabWidget(), false);
        ((ImageView) tabIndicator1.findViewById(R.id.icon)).setImageResource(R.drawable.homedrawable);
        ((TextView) tabIndicator1.findViewById(R.id.tabtext)).setText(getResources().getString(R.string.home));
        TextView counter = ((TextView) tabIndicator1.findViewById(R.id.reqcount));
        homespec.setIndicator(tabIndicator1);
        Intent Intent1 = new Intent(this, HomeActivity.class);
        Intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homespec.setContent(Intent1);



        // Tab for OnSale
        TabHost.TabSpec onsalespec = tabhost.newTabSpec("Search");

        View tabIndicator2 = LayoutInflater.from(this).inflate(R.layout.tab_indicator, tabhost.getTabWidget(), false);
        ((ImageView) tabIndicator2.findViewById(R.id.icon)).setImageResource(R.drawable.earningdrawable);
        ((TextView) tabIndicator2.findViewById(R.id.tabtext)).setText(getResources().getString(R.string.earning));
        counter_wallet = ((TextView) tabIndicator2.findViewById(R.id.reqcount));
        onsalespec.setIndicator(tabIndicator2);

        Intent intent2 = new Intent(this, EarningActivity.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        onsalespec.setContent(intent2);


        // Tab for Deals
        TabHost.TabSpec dealsspec = tabhost.newTabSpec("Chat");

        View tabIndicator3 = LayoutInflater.from(this).inflate(R.layout.tab_indicator, tabhost.getTabWidget(), false);
        ((ImageView) tabIndicator3.findViewById(R.id.icon)).setImageResource(R.drawable.profiledrawable);
        ((TextView) tabIndicator3.findViewById(R.id.tabtext)).setText(getResources().getString(R.string.profile));
        counter_shedule = ((TextView) tabIndicator3.findViewById(R.id.reqcount));
        dealsspec.setIndicator(tabIndicator3);

        Intent intent3 = new Intent(this, ProfileActivity.class);
        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        dealsspec.setContent(intent3);


        TabHost.TabSpec message = tabhost.newTabSpec("Chat");

        View tabIndicator6 = LayoutInflater.from(this).inflate(R.layout.tab_indicator, tabhost.getTabWidget(), false);
        ((ImageView) tabIndicator6.findViewById(R.id.icon)).setImageResource(R.drawable.streetdrawable);
        ((TextView) tabIndicator6.findViewById(R.id.tabtext)).setText(getResources().getString(R.string.signing));
        counter_message = ((TextView) tabIndicator6.findViewById(R.id.reqcount));
        message.setIndicator(tabIndicator6);

      //  Intent intent6 = new Intent(this, StreatPickupActivity.class);
        Intent intent6 = new Intent(this, MyActiveSignings.class);
        intent6.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        message.setContent(intent6);
        // Tab for Profile




        // Adding all TabSpec to TabHost
        tabhost.addTab(homespec);
        tabhost.addTab(onsalespec);
        tabhost.addTab(dealsspec);
        tabhost.addTab(message);


        tabhost.setCurrentTab(WhichIndex);


       // new AddEventsData().execute();

    }
    private void adddataincancellist() {
        cancellist = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CancelReasBean cancelReasBean = new CancelReasBean();
            cancelReasBean.setId("");

            cancelReasBean.setChecked(false);
            if (i == 0) {
                cancelReasBean.setReason("Wrong address shown");
            } else if (i == 1) {
                cancelReasBean.setReason("Client requested cancel");
            } else if (i == 2) {
                cancelReasBean.setReason("Client no-show");
            }

            cancellist.add(cancelReasBean);
        }


    }

    private void paymentDoneByUser(String picuplocation, String req_datetime, String picklaterdate, String picklatertime, String paysts) {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainTabActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = canceldialog.findViewById(R.id.message_tv);
        final TextView other_msg_tv = canceldialog.findViewById(R.id.other_msg_tv);
        other_msg_tv.setVisibility(View.VISIBLE);
        if (picklaterdate!=null&&!picklaterdate.equalsIgnoreCase("")){
            other_msg_tv.setText(""+picuplocation+"\n"+"Date "+picklaterdate+" "+picklatertime);
        }
        else {
            other_msg_tv.setText(""+picuplocation);
        }
        if (paysts.equalsIgnoreCase("paymentconfirm")){
            message_tv.setText(getResources().getString(R.string.clientconfirmpay));
        }
        else {
            message_tv.setText(getResources().getString(R.string.paymentisdone));
        }

        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(MainTabActivity.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        unregisterReceiver(broadcastReceiver);
        mlam.dispatchPause(isFinishing());
        isVisible=false;
        if (scheduleTaskExecutor==null){

        }
        else {
            scheduleTaskExecutor.shutdown();
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (scheduleTaskExecutor==null){

        }
        else {
            scheduleTaskExecutor.shutdown();
        }
    }
    private class MyCounterVal extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(String... strings) {
//https://myngrewards.com/demo/wp-content/plugins/webservice/msg_unseen_count.php?user_id=181
            try {
                String postReceiverUrl = BaseUrl.baseurl + "msg_unseen_count.php?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                Log.e("Member Tab user_id >. "," >>"+user_id);
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
                Log.e("MainTabCounter Hire", ">>>>>>>>>>>>" + response);
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
            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String result = intent.getExtras().getString("result");
                    Log.e("resultbroadcast >> "," .. "+result);
                    if (result.equalsIgnoreCase("notmach")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainTabActivity.this);

                        builder.setMessage("Your login Session is expire,Please login again")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                               /* Intent serviceIntent = new Intent(BaseActivity.this, TrackingService.class);
                                stopService(serviceIntent);*/
                                        mySession.logoutUser();
                                        Intent ie = new Intent(MainTabActivity.this, TrackingService.class);
                                        stopService(ie);
                                        Intent i = new Intent(MainTabActivity.this, LoginAct.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(i);

                                    }
                                });

                        AlertDialog alert = builder.create();
                        if (alert != null && alert.isShowing()) {

                        } else {
                            alert.show();
                        }

                        bundle=null;
                    }
                }

            } catch (Exception e) {

            }


        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        mlam.dispatchResume();

        registerReceiver(broadcastReceiver, new IntentFilter("CHECKONLINESTS"));


        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }
        LocalBroadcastManager.getInstance(MainTabActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(MainTabActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(MainTabActivity.this.getApplicationContext());
        new GetCurrentBooking().execute();

        isVisible=true;
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
              //  new MyCounterVal().execute();
            }
        }, 0, 8, TimeUnit.SECONDS);

    }
// request popup
   private void showNewRequest(String firstname, String lastname, String picuplocation, String dropofflocation, final String request_id, String picklaterdate, String picklatertime, String booktype, String rating, String favorite_ride, String location_type_str, String realstate_signing_str, String number_of_witness_str, String name_str, String mobile_str, String email_str, String number_of_signing_str, String type_of_signing_name_str, String car_type_image, String car_type_name, String car_min_charge,String emailtoprit,String scanandemail,String prepaid,String overnight,String night_charge,String today_charge) {
    dialogsts_show = true;
    request_id_main = request_id;
    booking_request_dialog = new Dialog(MainTabActivity.this);
    booking_request_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    booking_request_dialog.setCancelable(false);
    booking_request_dialog.setContentView(R.layout.custom_new_job_lay);
    booking_request_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    TextView decline =  booking_request_dialog.findViewById(R.id.decline);
    TextView datetimetv =  booking_request_dialog.findViewById(R.id.datetimetv);
    TextView rating_tv =  booking_request_dialog.findViewById(R.id.rating_tv);
    TextView total_amt =  booking_request_dialog.findViewById(R.id.total_amt);
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
    TextView extranightcharge = booking_request_dialog.findViewById(R.id.extranightcharge);
    carname.setText(""+car_type_name);
    price.setText("$ "+car_min_charge);
    total_amt.setText("Total  $"+car_min_charge);
    if (car_type_image==null||car_type_image.equalsIgnoreCase("")||car_type_image.equalsIgnoreCase(BaseUrl.image_baseurl)){

    }
    else {
        Picasso.with(MainTabActivity.this).load(car_type_image).placeholder(R.drawable.mini).into(carimage);

    }
    if (picklaterdate==null||picklaterdate.equalsIgnoreCase("")){
        date_lay.setVisibility(View.GONE);
    }

    username.setText("" + firstname + " " + lastname);
    pick_location.setText("" + picuplocation);
    drop_location.setText("" + dropofflocation);

    signinglocation.setText("" + picuplocation);
    locationtype.setText("" + location_type_str);
    realstatesigning.setText("" + realstate_signing_str);
    numberofwitness.setText("" + number_of_witness_str);
    name_et.setText("" + name_str);
    phone_et.setText("" + mobile_str);
    email_et.setText("" + email_str);
    typeofsigning.setText("" + type_of_signing_name_str);
    numberofsigning.setText("" + number_of_signing_str);
    date_tv.setText("" + picklaterdate.trim() + " " + picklatertime.trim());
       signing_loc_str = picuplocation;
    signing_date_time_str = "" + picklaterdate.trim() + " " + picklatertime.trim();
    extranightcharge.setText( " $" + night_charge);
    extracharge.setText(" $"+today_charge);



    if (realstate_signing_str.equalsIgnoreCase("No")) {
        selectrealstatelay.setVisibility(View.GONE);
    } else {
        selectrealstatelay.setVisibility(View.VISIBLE);
        emailtoprit_tv.setText(" $"+emailtoprit);
        scanandemail_tv.setText(" $"+scanandemail);
        prepaid_tv.setText(" $"+prepaid);
        overnight_tv.setText(" $"+overnight);


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
             if (booking_request_dialog!=null){
                 booking_request_dialog.dismiss();
             }
             new DeclineRequest().execute(request_id);

        }
    });

    accept.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            addReminder();


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

    private void addReminder() {

        try {
            Date event = new SimpleDateFormat("yyyy-MM-dd hh:mm a").parse(signing_date_time_str.trim());

            if (CalendarHelper.haveCalendarReadWritePermissions(MainTabActivity.this)) {
                if (isEventInCalendar(MainTabActivity.this,getResources().getString(R.string.app_name),event.getTime(),event.getTime())){
                    Log.e("ADDED",">");
                }
                else {
                    Log.e("NOT ADDED",">");
                    addNewEvent(event,signing_loc_str);

                }

            } else {
                CalendarHelper.requestCalendarReadWritePermission(MainTabActivity.this);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


    private void addNewEvent(Date event, String picuplocation) {

        int minutes = 30;
        long currentDateTime = event.getTime();
       /* long currentDateTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(event);
*/
        Date currentDate = new Date(currentDateTime - minutes*60*1000);
        System.out.println(currentDate);

        if (calendarIdTable == null) {
            Toast.makeText(this, (String) "No calendars found. Please ensure at least one google account has been added.",
                    Toast.LENGTH_LONG).show();
            //Load calendars
            calendarIdTable = CalendarHelper.listCalendarId(this);
            if (list != null) {

            }
            //  String calendarString = calendarIdSpinner.getSelectedItem().toString();
            // updateCalendarIdSpinner();

            return;
        }


        final long oneHour = 1000 * 60 * 60;
        final long tenMinutes = 1000 * 60 * 10;

        long ridetime = (currentDate).getTime();
        long oneHourFromNow = (currentDate).getTime() - oneHour;
        long tenMinutesFromNow = (new Date()).getTime() + tenMinutes;
        String calendarString = "";
        if (list != null) {
            calendarString = list.get(0).toString();
        }


        int calendar_id = Integer.parseInt(calendarIdTable.get(calendarString));

        CalendarHelper.MakeNewCalendarEntry(this, getResources().getString(R.string.app_name), getResources().getString(R.string.schedulesigning), picuplocation, ridetime, ridetime, false, true, calendar_id, 3);

    }

    private static boolean isEventInCalendar(Context context, String titleText, long dtStart, long dtEnd) {

        final String[] projection = new String[]{CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.TITLE};
        Cursor cursor = CalendarContract.Instances.query(context.getContentResolver(), projection, dtStart, dtEnd);
        return cursor != null && cursor.moveToFirst() && cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.TITLE)).equalsIgnoreCase(titleText);
    }

    private void selectCancelReasionPop() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainTabActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.cancel_reasion_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        reasonlist = canceldialog.findViewById(R.id.reasonlist);
        cancelReasonAdp = new CancelReasonAdp(MainTabActivity.this, cancellist);
        reasonlist.setAdapter(cancelReasonAdp);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancel_reaison==null||cancel_reaison.equalsIgnoreCase("")){
                    Toast.makeText(MainTabActivity.this,getResources().getString(R.string.selectcancelres),Toast.LENGTH_LONG).show();
                }
                else {
                    if (booking_request_dialog != null || booking_request_dialog.isShowing()) {
                        booking_request_dialog.cancel();
                        booking_request_dialog.dismiss();
                        diff_second = "";
                    }


                    status_job = "Cancel";
                    new ResponseToRequest().execute(request_id, status_job);
                    stopCountDownTimer();
                }


            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
            }
        });
        canceldialog.show();


    }


    private void usercancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainTabActivity.this);
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
                params.put("cancel_reaison", cancel_reaison);


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

                           // addreminderGoogleCal();

                            /*Intent i = new Intent(MainTabActivity.this, TrackRideAct.class);
                            i.putExtra("request_id",request_id);
                            startActivity(i);*/

                            tripScheduled();
                            //finish();
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }
    private class DeclineRequest extends AsyncTask<String, String, String> {
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
//http://technorizen.com/notary/webservice/declinebyDriver?request_id=656&driver_id=94
            try {
                String postReceiverUrl = BaseUrl.baseurl + "declinebyDriver?";

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();


                params.put("request_id", strings[0]);
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
            Log.e("DECLINE_RESULT", "....... " + result);

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

                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }


    private void reideAllreadyAccepted() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainTabActivity.this);
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
    private void tripScheduled() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainTabActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.success_accept_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv =  canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv =  canceldialog.findViewById(R.id.message_tv);
        final TextView bodytv =  canceldialog.findViewById(R.id.bodytv);
        final TextView datetv =  canceldialog.findViewById(R.id.datetv);
        bodytv.setText(""+signing_loc_str);
        datetv.setText(""+signing_date_time_str);

        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
                tabhost.setCurrentTab(3);

                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }

    private void reideAllreadyCanceled() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainTabActivity.this);
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
    private class GetCurrentBooking extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressbar.setVisibility(View.VISIBLE);
            if (ac_dialog != null) {
                if (isVisible){
                    ac_dialog.show();
                }

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
//http://mobileappdevelop.co/NAXCAN/webservice/get_current_booking?user_id=1

                String postReceiverUrl = BaseUrl.baseurl + "get_current_booking?";
                Log.e("POST URL >>"," >> "+postReceiverUrl+"user_id="+user_id+"&type=AGENT&timezone="+time_zone);
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("type", "AGENT");

                params.put("timezone", time_zone);

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
            //progressbar.setVisibility(View.GONE);
            if (ac_dialog != null)
            {
                if (isVisible){
                    ac_dialog.dismiss();
                }
            }

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    Log.e("CURRENT BOOKING >>>", "" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successfull")) {
                        diff_second = jsonObject.getString("diff_second");
                        String  car_min_charge = jsonObject.getString("paid_amount");
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            signing_charge = jsonObject1.getString("signing_charge");
                            request_id = String.valueOf(jsonObject1.getString("id"));

                            String status = jsonObject1.getString("status");
                            if (status.equalsIgnoreCase("Pending")) {
                                String firstname = "";
                                String lastname = "";
                                String rating = "";
                                String picuplocation = jsonObject1.getString("picuplocation");
                                String dropofflocation = jsonObject1.getString("dropofflocation");
                                String picklaterdate = jsonObject1.getString("picklaterdate");
                                String picklatertime = jsonObject1.getString("picklatertime");
                                String booktype = jsonObject1.getString("booktype");
                                String favorite_ride = "";

                                String location_type_str = jsonObject1.getString("location_type");
                                String realstate_signing_str = jsonObject1.getString("realstate_signing");
                                String number_of_witness_str = jsonObject1.getString("number_of_witness");
                                String name_str = jsonObject1.getString("name");
                                String mobile_str = jsonObject1.getString("mobile");
                                String email_str = jsonObject1.getString("email");
                                String number_of_signing_str = jsonObject1.getString("number_of_signing");
                                String type_of_signing_name_str = jsonObject1.getString("type_of_signing_name");
                                String car_type_image = jsonObject1.getString("car_type_image");
                                //  String car_min_charge = jsonObject1.getString("car_min_charge");
                                String car_type_name = jsonObject1.getString("car_type_name");
                                String emailtoprit = jsonObject1.getString("emailtoprit");
                                String scanandemail = jsonObject1.getString("scanandemail");
                                String prepaid = jsonObject1.getString("prepaid");
                                String overnight = jsonObject1.getString("overnight");
                                String night_charge = jsonObject1.getString("night_charge");
                                String today_charge = jsonObject1.getString("today_charge");


                                JSONArray jsonArray1 = jsonObject1.getJSONArray("user_details");
                                for (int k = 0; k < jsonArray1.length(); k++) {
                                    JSONObject jsonObject2 = jsonArray1.getJSONObject(k);
                                    firstname = jsonObject2.getString("first_name");
                                    lastname = jsonObject2.getString("last_name");
                                    rating = jsonObject2.getString("rating");
                                }

                                if (booking_request_dialog == null) {

                                    if (isVisible){
                                        showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride,location_type_str,realstate_signing_str,number_of_witness_str,name_str,mobile_str,email_str,number_of_signing_str,type_of_signing_name_str,car_type_image,car_type_name,car_min_charge,emailtoprit,scanandemail,prepaid,overnight,night_charge,today_charge);

                                    }

                                } else if (booking_request_dialog.isShowing()) {

                                } else {

                                    if (isVisible){
                                        showNewRequest(firstname, lastname, picuplocation, dropofflocation, request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride,location_type_str,realstate_signing_str,number_of_witness_str,name_str,mobile_str,email_str,number_of_signing_str,type_of_signing_name_str,car_type_image,car_type_name,car_min_charge,emailtoprit,scanandemail,prepaid,overnight,night_charge,today_charge);

                                    }


                                }


                            }
                        }
                    }
                    else {
                        stopCountDownTimer();
                        if (booking_request_dialog == null) {

                        } else {
                            if (booking_request_dialog != null || booking_request_dialog.isShowing()) {
                                booking_request_dialog.cancel();
                                booking_request_dialog.dismiss();
                                diff_second = "";
                            }

                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class CancelReasonAdp extends BaseAdapter {
        String[] result;
        Context context;
        ArrayList<CancelReasBean> cancellist;
        private LayoutInflater inflater = null;


        public CancelReasonAdp(Activity activity, ArrayList<CancelReasBean> cancellist) {
            this.context = activity;
            this.cancellist = cancellist;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            //  return 4;        }
            return cancellist == null ? 0 : cancellist.size();
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

            rowView = inflater.inflate(R.layout.custom_reson_lay, null);
            TextView cancel_reasion_tv = rowView.findViewById(R.id.cancel_reasion_tv);
            ImageView chkimg = rowView.findViewById(R.id.chkimg);
            cancel_reasion_tv.setText("" + cancellist.get(position).getReason());
            if (cancellist.get(position).isChecked()) {
                chkimg.setImageResource(R.drawable.ic_checked_circle);

            } else {
                chkimg.setImageResource(R.drawable.ic_circle_border);
            }
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = position;
                    if (cancellist != null && !cancellist.isEmpty()) {
                        for (int k = 0; k < cancellist.size(); k++) {
                            if (pos == k) {
                                Log.e("CLICK TRUE", "SIZE");
                                if (cancellist.get(k).isChecked()) {
                                    cancellist.get(k).setChecked(false);
                                    cancel_reaison="";

                                } else {
                                    cancel_reaison =cancellist.get(k).getReason();
                                    cancellist.get(k).setChecked(true);
                                }
                            } else {
                                cancellist.get(k).setChecked(false);
                            }
                        }


                        cancelReasonAdp = new CancelReasonAdp(MainTabActivity.this, cancellist);
                        reasonlist.setAdapter(cancelReasonAdp);
                        cancelReasonAdp.notifyDataSetChanged();
                    }


                }
            });


            return rowView;
        }

    }
    private void setStartTime() {

        AlarmManager alarmMgr = (AlarmManager) (MainTabActivity.this).getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MyReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        System.out.println("cache_data alarm set n time zone dinesh: " + calendar.getTimeZone().getDisplayName());
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 2, alarmIntent);
    }

    private class AddEventsData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressbar.setVisibility(View.VISIBLE);

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                //addreminderGoogleCal();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //progressbar.setVisibility(View.GONE);


        }
    }


/*
    private void addreminderGoogleCal() {



        Event event = new Event()
                .setSummary("Google I/O 2015")
                .setLocation("800 Howard St., San Francisco, CA 94103")
                .setDescription("A chance to hear more about Google's developer products.");

        DateTime startDateTime = new DateTime("2015-05-28T09:00:00-07:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);

        DateTime endDateTime = new DateTime("2015-05-28T17:00:00-07:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setEnd(end);

        String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
        event.setRecurrence(Arrays.asList(recurrence));

        EventAttendee[] attendees = new EventAttendee[] {
                new EventAttendee().setEmail("lpage@example.com"),
                new EventAttendee().setEmail("sbrin@example.com"),
        };
        event.setAttendees(Arrays.asList(attendees));

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        try {
            //GoogleCredential credential = new GoogleCredential().setAccessToken("");
            GoogleCredential credential = new GoogleCredential();

            //final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(new NetHttpTransport(), JSON_FACTORY, credential)
            //com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(new NetHttpTransport(), JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            event = service.events().insert(calendarId, event).execute();
            Log.e("Event_created: %s\n"," "+ event.getHtmlLink());
        }catch (Exception e){
            e.printStackTrace();
        }



    }
*/

/*
Notary App ==

Solve the rush amount issue (Please see the attached video in which you see the when i choose the time before 3 hours from current time and select the regular then we add the rush hour charge and in second attempt we already select the rush service then we not show the rush charge)
In your screen shot you also select the rush service thats why the we not added rush charge
Now when user back from check out screen return to booking form
Now show 5 star rating by default

In admin dashboard ===
now the request is sending to multiple agent from admin request
Estimate amount is in working.....



TaxiMania ==
Integrate firebase notification
Complete booking module with billing (Please provide your ride amount calculation formula so we apply it)


*/

}

