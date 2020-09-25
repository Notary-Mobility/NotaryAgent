package main.com.notaryagent.tabactivity;

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
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.notaryagent.R;
import main.com.notaryagent.activity.MySignings;
import main.com.notaryagent.activity.TrackRideAct;
import main.com.notaryagent.app.Config;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.MySession;
import main.com.notaryagent.constant.RideBean;
import main.com.notaryagent.constant.RideHistoryBean;
import main.com.notaryagent.utils.NotificationUtils;

public class MyActiveSignings extends AppCompatActivity {
    ProgressBar prgressbar;
    private RelativeLayout exit_app_but;
    private ListView ridehistory_list;
    RideHisAdp ridehisadp;
    TextView completedtv, upcomingtv, allrides, ride_sts_tv, canceledtv;
    ArrayList<RideBean> rideBeanArrayList;
    ArrayList<RideHistoryBean> ridehislistschedulelist, ridehislistcanceledlist, ridehislistCompletedlist, rideUpcominglist;
    private MySession mySession;

    private long timeCountInMilliSeconds;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    Dialog booking_request_dialog;
    private String request_id = "", request_id_main = "", status_job = "", signing_charge = "";
    private String diff_second = "", time_zone = "", user_log_data = "", user_id = "";
    private CountDownTimer countDownTimer;
    boolean dialogsts_show = false;
    private TextView textViewTime;
    public static boolean isVisible = true;
    private String showtab = "Schedule";
    private ACProgressCustom ac_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myactive_signing);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        time_zone = tz.getID();
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
        idinit();
        clicjkevt();
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
                        Log.e("KEY_EARNING =", "" + keyMessage);
                        if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {
                            request_id = data.getString("request_id");

                            usercancelRide();
                        }
                        else if (keyMessage.equalsIgnoreCase("user pay the signing amount")) {

                            new RideHistoryJson().execute();
                          /*  String picuplocation = data.getString("picuplocation");
                            String req_datetime = data.getString("req_datetime");
                            String picklaterdate = data.getString("picklaterdate");
                            String picklatertime = data.getString("picklatertime");

                            paymentDoneByUser(picuplocation,req_datetime,picklaterdate,picklatertime,"paymentdone");
                     */   }else if (keyMessage.equalsIgnoreCase("user confirm payment amount")) {

                            new RideHistoryJson().execute();
                         /*   String picuplocation = data.getString("picuplocation");
                            String req_datetime = data.getString("req_datetime");
                            String picklaterdate = data.getString("picklaterdate");
                            String picklatertime = data.getString("picklatertime");

                            paymentDoneByUser(picuplocation,req_datetime,picklaterdate,picklatertime,"paymentconfirm");
                      */  }

                        else if (keyMessage.equalsIgnoreCase("your booking request is cancel by user")) {
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
                        }else if (keyMessage.equalsIgnoreCase("Sorry this signing request is accepted by other agent")) {
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
                        } else if (keyMessage.equalsIgnoreCase("arriving latter booking request")) {
                            String picklaterdate = data.getString("picklaterdate");
                            String picklatertime = data.getString("picklatertime");

                            //  bookedRequestAlert(picklaterdate,picklatertime);


                        } else if (keyMessage.equalsIgnoreCase("your payment is successfull")) {
                            new RideHistoryJson().execute();
                        } else if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {

                            new RideHistoryJson().execute();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        // new GetCurrentBooking().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;

        new RideHistoryJson().execute();

        LocalBroadcastManager.getInstance(MyActiveSignings.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(MyActiveSignings.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(MyActiveSignings.this.getApplicationContext());
    }

    private void clicjkevt() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        allrides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allrides.setTextColor(getResources().getColor(R.color.white));
                upcomingtv.setTextColor(getResources().getColor(R.color.toobarcolor));
                canceledtv.setTextColor(getResources().getColor(R.color.toobarcolor));
                completedtv.setTextColor(getResources().getColor(R.color.toobarcolor));
                allrides.setBackgroundResource(R.drawable.border_filled_red);
                upcomingtv.setBackgroundResource(R.drawable.border_rec_red);
                completedtv.setBackgroundResource(R.drawable.border_rec_red);
                canceledtv.setBackgroundResource(R.drawable.border_rec_red);
                ridehisadp = new RideHisAdp(MyActiveSignings.this, ridehislistschedulelist);
                ridehistory_list.setAdapter(ridehisadp);
                ridehisadp.notifyDataSetChanged();
                showtab = "Schedule";
                if (ridehislistschedulelist == null || ridehislistschedulelist.isEmpty()) {
                    ride_sts_tv.setVisibility(View.VISIBLE);
                    ride_sts_tv.setText("" + getResources().getString(R.string.norides));
                } else {
                    ride_sts_tv.setVisibility(View.GONE);
                }

            }
        });
        upcomingtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceledtv.setTextColor(getResources().getColor(R.color.toobarcolor));
                allrides.setTextColor(getResources().getColor(R.color.toobarcolor));
                upcomingtv.setTextColor(getResources().getColor(R.color.white));
                completedtv.setTextColor(getResources().getColor(R.color.toobarcolor));
                upcomingtv.setBackgroundResource(R.drawable.border_filled_red);
                allrides.setBackgroundResource(R.drawable.border_rec_red);
                canceledtv.setBackgroundResource(R.drawable.border_rec_red);
                completedtv.setBackgroundResource(R.drawable.border_rec_red);
                ridehisadp = new RideHisAdp(MyActiveSignings.this, rideUpcominglist);
                ridehistory_list.setAdapter(ridehisadp);
                ridehisadp.notifyDataSetChanged();
                showtab = "Onride";
                if (rideUpcominglist == null || rideUpcominglist.isEmpty()) {
                    ride_sts_tv.setVisibility(View.VISIBLE);
                    ride_sts_tv.setText("" + getResources().getString(R.string.noactiveride));
                } else {
                    ride_sts_tv.setVisibility(View.GONE);
                }

            }
        });
        completedtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allrides.setTextColor(getResources().getColor(R.color.toobarcolor));
                upcomingtv.setTextColor(getResources().getColor(R.color.toobarcolor));
                canceledtv.setTextColor(getResources().getColor(R.color.toobarcolor));
                completedtv.setTextColor(getResources().getColor(R.color.white));
                upcomingtv.setBackgroundResource(R.drawable.border_rec_red);
                allrides.setBackgroundResource(R.drawable.border_rec_red);
                canceledtv.setBackgroundResource(R.drawable.border_rec_red);
                completedtv.setBackgroundResource(R.drawable.border_filled_red);

                ridehisadp = new RideHisAdp(MyActiveSignings.this, ridehislistCompletedlist);
                ridehistory_list.setAdapter(ridehisadp);
                ridehisadp.notifyDataSetChanged();
                showtab = "Completed";
                if (ridehislistCompletedlist == null || ridehislistCompletedlist.isEmpty()) {
                    ride_sts_tv.setVisibility(View.VISIBLE);
                    ride_sts_tv.setText("" + getResources().getString(R.string.noridecomp));
                } else {
                    ride_sts_tv.setVisibility(View.GONE);
                }
            }
        });
        canceledtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allrides.setTextColor(getResources().getColor(R.color.toobarcolor));
                upcomingtv.setTextColor(getResources().getColor(R.color.toobarcolor));
                completedtv.setTextColor(getResources().getColor(R.color.toobarcolor));
                canceledtv.setTextColor(getResources().getColor(R.color.white));
                upcomingtv.setBackgroundResource(R.drawable.border_rec_red);
                allrides.setBackgroundResource(R.drawable.border_rec_red);
                completedtv.setBackgroundResource(R.drawable.border_rec_red);
                canceledtv.setBackgroundResource(R.drawable.border_filled_red);
                ridehisadp = new RideHisAdp(MyActiveSignings.this, ridehislistcanceledlist);
                ridehistory_list.setAdapter(ridehisadp);
                showtab = "Canceled";
                ridehisadp.notifyDataSetChanged();
                if (ridehislistcanceledlist == null || ridehislistcanceledlist.isEmpty()) {
                    ride_sts_tv.setVisibility(View.VISIBLE);
                    ride_sts_tv.setText("" + getResources().getString(R.string.nocanceledride));
                } else {
                    ride_sts_tv.setVisibility(View.GONE);
                }
            }
        });

    }


    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(MyActiveSignings.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        isVisible = false;
    }

    private void idinit() {
        canceledtv = findViewById(R.id.canceledtv);
        ride_sts_tv = findViewById(R.id.ride_sts_tv);
        allrides = findViewById(R.id.allrides);
        completedtv = findViewById(R.id.completedtv);
        upcomingtv = findViewById(R.id.upcomingtv);
        prgressbar = findViewById(R.id.prgressbar);
        ridehistory_list = findViewById(R.id.ridehistory_list);
        exit_app_but = findViewById(R.id.exit_app_but);


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
            final Holder holder;
            holder = new Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.ride_history_lay, null);
            TextView pickuplocation = (TextView) rowView.findViewById(R.id.pickuplocation);
            TextView date_tv = (TextView) rowView.findViewById(R.id.date_tv);
            TextView statustv = (TextView) rowView.findViewById(R.id.statustv);
            ImageView car_img = (ImageView) rowView.findViewById(R.id.car_img);
            pickuplocation.setText("" + ridehislist.get(position).getPicuplocation());
            String req_datetime = ridehislist.get(position).getReq_datetime();
            String sts = ridehislist.get(position).getStatus();
            String paymentsts = ridehislist.get(position).getPayment_status();
            String sch_status = ridehislist.get(position).getSch_status();

            if (sts.equalsIgnoreCase("Cancel") || sts.equalsIgnoreCase("Cancel_by_user") || sts.equalsIgnoreCase("Cancel_by_driver")) {
                statustv.setTextColor(getResources().getColor(R.color.red));
                statustv.setText("" + getResources().getString(R.string.canceled));
            } else if (sts.equalsIgnoreCase("Finish")||sts.equalsIgnoreCase("End")) {
                if (paymentsts.equalsIgnoreCase("Pending")){
                    statustv.setTextColor(getResources().getColor(R.color.yellow));
                    statustv.setText("" + getResources().getString(R.string.notpaid));
                }
                else {
                    statustv.setTextColor(getResources().getColor(R.color.yellow));
                    statustv.setText("" + getResources().getString(R.string.paid));
                }

            } else if (sts.equalsIgnoreCase("Accept") || sts.equalsIgnoreCase("Start") || sts.equalsIgnoreCase("Arrived")) {
                if (sch_status.equalsIgnoreCase("Now")) {
                    statustv.setTextColor(getResources().getColor(R.color.toobarcolor));
                    statustv.setText("" + sts);
                } else {
                    statustv.setTextColor(getResources().getColor(R.color.green));
                    statustv.setText("" + getResources().getString(R.string.scheduled));
                }


            } /*else if (sts.equalsIgnoreCase("End")) {
                statustv.setTextColor(getResources().getColor(R.color.yellow));
                statustv.setText("" + getResources().getString(R.string.notpaid));
            }*/
           // date_tv.setText("" + ridehislist.get(position).getReq_datetime());

            if (ridehislist.get(position).getPicklaterdate()==null||ridehislist.get(position).getPicklaterdate().equalsIgnoreCase("")){
                date_tv.setText("" + ridehislist.get(position).getReq_datetime());
            }
            else {
                date_tv.setText("" + ridehislist.get(position).getPicklaterdate()+" "+ridehislist.get(position).getPicklatertime());

            }
            /*try {
                Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(req_datetime);
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                String strDate = formatter.format(date1);
                date_tv.setText("" + strDate);

            } catch (ParseException e) {
                e.printStackTrace();
                date_tv.setText(""+ridehislist.get(position).getReq_datetime());

            }*/
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sts = ridehislist.get(position).getStatus();
                    String paysts = ridehislist.get(position).getCard_select_status();

                    if (sts.equalsIgnoreCase("Accept") && paysts.equalsIgnoreCase("No")){
                        paymentPending();
                    }

                    else if (sts.equalsIgnoreCase("Accept") || sts.equalsIgnoreCase("Start") || sts.equalsIgnoreCase("Arrived")) {
                        Intent i = new Intent(MyActiveSignings.this, TrackRideAct.class);
                        i.putExtra("request_id", ridehislist.get(position).getId());
                        startActivity(i);
                    } else if (sts.equalsIgnoreCase("End")||sts.equalsIgnoreCase("Finish")) {
                        Intent i = new Intent(MyActiveSignings.this, SigningActivity.class);
                        i.putExtra("request_id", ridehislist.get(position).getId());
                        startActivity(i);

                    }

                }
            });
            return rowView;
        }

    }

    private void paymentPending() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MyActiveSignings.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = canceldialog.findViewById(R.id.message_tv);
        message_tv.setText(getResources().getString(R.string.usernotdonepayment));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }

    private class RideHistoryJson extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgressbar.setVisibility(View.VISIBLE);
            ridehislistschedulelist = new ArrayList<>();
            ridehislistCompletedlist = new ArrayList<>();
            rideUpcominglist = new ArrayList<>();
            ridehislistcanceledlist = new ArrayList<>();
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
                            ridebean.setSch_status(jsonObject1.getString("sch_status"));
                            ridebean.setPicklaterdate(jsonObject1.getString("picklaterdate"));
                            ridebean.setPicklatertime(jsonObject1.getString("picklatertime"));
                            ridebean.setPayment_status(jsonObject1.getString("payment_status"));
                            ridebean.setCard_select_status(jsonObject1.getString("card_select_status"));
                            // ridebean.setDistance(jsonObject1.getString("distance"));
                            // ridebean.setFare_amount(jsonObject1.getString("ride_fare"));
                            // ridebean.setBooktype(jsonObject1.getString("booktype"));

                            try {
                                Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject1.getString("req_datetime"));
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy hh:mm a");
                                String strDate = formatter.format(date1);
                                ridebean.setReq_datetime(strDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            JSONArray jsonArray1 = jsonObject1.getJSONArray("user_details");
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                                ridebean.setUsername(jsonObject3.getString("first_name") + " " + jsonObject3.getString("last_name"));
                                ridebean.setUsernumber(jsonObject3.getString("mobile"));
                                ridebean.setUserimg(jsonObject3.getString("image"));
                            }


                            String sts = jsonObject1.getString("status");
                            String sch_status = jsonObject1.getString("sch_status");
                            if (sts.equalsIgnoreCase("Finish")||sts.equalsIgnoreCase("End")) {
                                ridehislistCompletedlist.add(ridebean);
                            } else if (sts.equalsIgnoreCase("Accept") ) {
                              /*  if (sch_status.equalsIgnoreCase("Now")) {
                                    rideUpcominglist.add(ridebean);
                                } else {
                                    ridehislistschedulelist.add(ridebean);
                                }*/
                                ridehislistschedulelist.add(ridebean);


                            }else if (sts.equalsIgnoreCase("Accept") || sts.equalsIgnoreCase("Arrived") || sts.equalsIgnoreCase("Start")) {
                               /* if (sch_status.equalsIgnoreCase("Now")) {
                                    rideUpcominglist.add(ridebean);
                                } else {
                                    ridehislistschedulelist.add(ridebean);
                                }*/
                                rideUpcominglist.add(ridebean);


                            } else if (sts.equalsIgnoreCase("Cancel") || sts.equalsIgnoreCase("Cancel_by_user") || sts.equalsIgnoreCase("Cancel_by_driver")) {
                                ridehislistcanceledlist.add(ridebean);


                            }

                        }
                        //                        else if (sts.equalsIgnoreCase("Accept")||sts.equalsIgnoreCase("Arrived")||sts.equalsIgnoreCase("Start")){




                    }

                    if (showtab.equalsIgnoreCase("Onride")) {
                        ridehisadp = new RideHisAdp(MyActiveSignings.this, rideUpcominglist);
                        ridehistory_list.setAdapter(ridehisadp);
                        ridehisadp.notifyDataSetChanged();
                    } else if (showtab.equalsIgnoreCase("Canceled")) {
                        ridehisadp = new RideHisAdp(MyActiveSignings.this, ridehislistcanceledlist);
                        ridehistory_list.setAdapter(ridehisadp);
                        ridehisadp.notifyDataSetChanged();
                    } else if (showtab.equalsIgnoreCase("Completed")) {
                        ridehisadp = new RideHisAdp(MyActiveSignings.this, ridehislistCompletedlist);
                        ridehistory_list.setAdapter(ridehisadp);
                        ridehisadp.notifyDataSetChanged();
                    } else {
                        ridehisadp = new RideHisAdp(MyActiveSignings.this, ridehislistschedulelist);
                        ridehistory_list.setAdapter(ridehisadp);
                        ridehisadp.notifyDataSetChanged();
                    }

                    if (showtab.equalsIgnoreCase("Onride")) {
                        if (rideUpcominglist == null || rideUpcominglist.isEmpty()) {
                            ride_sts_tv.setVisibility(View.VISIBLE);
                            ride_sts_tv.setText("" + getResources().getString(R.string.nobookedride));
                        } else {
                            ride_sts_tv.setVisibility(View.GONE);
                        }
                    } else if (showtab.equalsIgnoreCase("Canceled")) {
                        if (ridehislistcanceledlist == null || ridehislistcanceledlist.isEmpty()) {
                            ride_sts_tv.setVisibility(View.VISIBLE);
                            ride_sts_tv.setText("" + getResources().getString(R.string.nobookedride));
                        } else {
                            ride_sts_tv.setVisibility(View.GONE);
                        }
                    } else if (showtab.equalsIgnoreCase("Completed")) {
                        if (ridehislistCompletedlist == null || ridehislistCompletedlist.isEmpty()) {
                            ride_sts_tv.setVisibility(View.VISIBLE);
                            ride_sts_tv.setText("" + getResources().getString(R.string.nobookedride));
                        } else {
                            ride_sts_tv.setVisibility(View.GONE);
                        }
                    } else {
                        if (ridehislistschedulelist == null || ridehislistschedulelist.isEmpty()) {
                            ride_sts_tv.setVisibility(View.VISIBLE);
                            ride_sts_tv.setText("" + getResources().getString(R.string.nobookedride));
                        } else {
                            ride_sts_tv.setVisibility(View.GONE);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private void showNewRequest(String firstname, String lastname, String picuplocation, String dropofflocation, final String request_id, String picklaterdate, String picklatertime, String booktype, String rating, String favorite_ride, String location_type_str, String realstate_signing_str, String number_of_witness_str, String name_str, String mobile_str, String email_str, String number_of_signing_str, String type_of_signing_name_str, String car_type_image, String car_type_name, String car_min_charge, String emailtoprit, String scanandemail, String prepaid, String overnight, String night_charge, String today_charge) {
        dialogsts_show = true;
        request_id_main = request_id;
        booking_request_dialog = new Dialog(MyActiveSignings.this);
        booking_request_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        booking_request_dialog.setCancelable(false);
        booking_request_dialog.setContentView(R.layout.custom_new_job_lay);
        booking_request_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView decline = booking_request_dialog.findViewById(R.id.decline);
        TextView datetimetv = booking_request_dialog.findViewById(R.id.datetimetv);
        TextView rating_tv = booking_request_dialog.findViewById(R.id.rating_tv);
        rating_tv.setText("" + rating);
        if (booktype == null || booktype.equalsIgnoreCase("")) {
            datetimetv.setVisibility(View.GONE);
        } else {
            datetimetv.setVisibility(View.VISIBLE);
            // datetimetv.setText(""+getResources().getString(R.string.booktime)+" "+picklaterdate.trim()+" "+picklatertime);
        }

        TextView accept = booking_request_dialog.findViewById(R.id.accept);
        TextView pick_location = booking_request_dialog.findViewById(R.id.pick_location);
        TextView drop_location = booking_request_dialog.findViewById(R.id.drop_location);
        textViewTime = booking_request_dialog.findViewById(R.id.textViewTime);
        TextView username = booking_request_dialog.findViewById(R.id.username);
        final ProgressBar progressBarCircle = (ProgressBar) booking_request_dialog.findViewById(R.id.progressBarCircle);

        TextView signinglocation = booking_request_dialog.findViewById(R.id.signinglocation);
        TextView locationtype = booking_request_dialog.findViewById(R.id.locationtype);
        TextView realstatesigning = booking_request_dialog.findViewById(R.id.realstatesigning);
        TextView numberofwitness = booking_request_dialog.findViewById(R.id.numberofwitness);
        TextView name_et = booking_request_dialog.findViewById(R.id.name_et);
        TextView phone_et = booking_request_dialog.findViewById(R.id.phone_et);
        TextView email_et = booking_request_dialog.findViewById(R.id.email_et);
        TextView typeofsigning = booking_request_dialog.findViewById(R.id.typeofsigning);
        TextView numberofsigning = booking_request_dialog.findViewById(R.id.numberofsigning);
        LinearLayout selectrealstatelay = booking_request_dialog.findViewById(R.id.selectrealstatelay);
        LinearLayout date_lay = booking_request_dialog.findViewById(R.id.date_lay);
        ImageView carimage = booking_request_dialog.findViewById(R.id.carimage);
        TextView carname = booking_request_dialog.findViewById(R.id.carname);
        TextView price = booking_request_dialog.findViewById(R.id.price);
        TextView date_tv = booking_request_dialog.findViewById(R.id.date_tv);
        TextView emailtoprit_tv = booking_request_dialog.findViewById(R.id.emailtoprit);
        TextView scanandemail_tv = booking_request_dialog.findViewById(R.id.scanandemail);
        TextView prepaid_tv = booking_request_dialog.findViewById(R.id.prepaid);
        TextView overnight_tv = booking_request_dialog.findViewById(R.id.overnight);
        TextView extracharge = booking_request_dialog.findViewById(R.id.extracharge);
        carname.setText("" + car_type_name);
        price.setText("$ " + car_min_charge);
        if (car_type_image == null || car_type_image.equalsIgnoreCase("") || car_type_image.equalsIgnoreCase(BaseUrl.image_baseurl)) {

        } else {
            Picasso.with(MyActiveSignings.this).load(car_type_image).placeholder(R.drawable.mini).into(carimage);

        }
        if (picklaterdate == null || picklaterdate.equalsIgnoreCase("")) {
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
        extracharge.setText(getResources().getString(R.string.nightcharge) + " $" + night_charge + " , " + getResources().getString(R.string.extracharge) + " $" + today_charge);


        if (realstate_signing_str.equalsIgnoreCase("No")) {
            selectrealstatelay.setVisibility(View.GONE);
        } else {
            selectrealstatelay.setVisibility(View.VISIBLE);
            emailtoprit_tv.setText(getResources().getString(R.string.emailtoprint) + " $" + emailtoprit);
            scanandemail_tv.setText(getResources().getString(R.string.scanandemail) + " $" + scanandemail);
            prepaid_tv.setText(getResources().getString(R.string.prepaid) + " $" + prepaid);
            overnight_tv.setText(getResources().getString(R.string.overnight) + " $" + overnight);


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

    private class ResponseToRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            if (ac_dialog != null) {
                if (isVisible) {
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
                    if (isVisible) {
                        ac_dialog.dismiss();
                    }

                }

            } else if (result.isEmpty()) {
                if (ac_dialog != null) {
                    if (isVisible) {
                        ac_dialog.dismiss();
                    }

                }

            } else {

                try {
                    if (ac_dialog != null) {
                        if (isVisible) {
                            ac_dialog.dismiss();
                        }

                    }

                    JSONObject jsonObject = new JSONObject(result);


                    if (jsonObject.getString("message").equalsIgnoreCase("unsuccessfull")) {
                        if (jsonObject.getString("result").equalsIgnoreCase("already accepted")) {
                            reideAllreadyAccepted();
                        } else {
                            reideAllreadyCanceled();
                        }

                    } else {
                        if (status_job.equalsIgnoreCase("Cancel")) {

                        } else {
                            Intent i = new Intent(MyActiveSignings.this, TrackRideAct.class);
                            i.putExtra("request_id", request_id);
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


    private void stopCountDownTimer() {
        if (countDownTimer == null) {

        } else {
            countDownTimer.cancel();
        }

    }

    private void usercancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MyActiveSignings.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = canceldialog.findViewById(R.id.message_tv);
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

    private void reideAllreadyCanceled() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MyActiveSignings.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = canceldialog.findViewById(R.id.yes_tv);
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


    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d",
               /* TimeUnit.MILLISECONDS.toHours(milliSeconds),*/
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

    private void reideAllreadyAccepted() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MyActiveSignings.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = canceldialog.findViewById(R.id.message_tv);
        message_tv.setText("" + getResources().getString(R.string.alreadyaccept));
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
    private void paymentDoneByUser(String picuplocation, String req_datetime, String picklaterdate, String picklatertime, String paysts) {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MyActiveSignings.this);
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

}
