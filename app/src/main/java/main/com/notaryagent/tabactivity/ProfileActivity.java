package main.com.notaryagent.tabactivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.notaryagent.R;
import main.com.notaryagent.activity.AgentDetails;
import main.com.notaryagent.activity.InviteEarnAct;
import main.com.notaryagent.activity.TrackRideAct;
import main.com.notaryagent.activity.WithdrawActivity;
import main.com.notaryagent.app.Config;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.MultipartUtility;
import main.com.notaryagent.constant.MySession;
import main.com.notaryagent.draweractivity.BaseActivity;
import main.com.notaryagent.paymentclasses.WalletActivity;
import main.com.notaryagent.utils.NotificationUtils;

public class ProfileActivity extends AppCompatActivity {
    private String ImagePath="",click_on="",firebase_regid="",image_url="",email_str = "",last_name_str="",car_manuyear="", password_str = "", mobile_str = "", username_str = "", car_model_str = "", car_registrationnum_str = "";
private ACProgressCustom ac_dialog;
    private TextView withdraw,invitefriend,agentdetail;
    private EditText first_name,last_name,email_et,phone_et,password_et;
    private CircleImageView user_img;
    private RelativeLayout image_lay;
    private Button update;
    private MySession mySession;
    private RelativeLayout exit_app_but;
    private TextView total_amount;


    private long timeCountInMilliSeconds;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    Dialog booking_request_dialog;
    private String request_id="",request_id_main="",status_job="",signing_charge="";
    private String diff_second = "",time_zone="",user_log_data="",user_id="";
    private CountDownTimer countDownTimer;
    boolean dialogsts_show = false;
    private TextView textViewTime;
    private boolean isVisible=true;
    public static String promo_code="";
    private RatingBar average_rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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

        idinit();
        clickevent();
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

                    Log.e("user_id >>>>", "" + user_id);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
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
                        if (keyMessage.equalsIgnoreCase("your booking request is Now")) {


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

    }
    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;

        LocalBroadcastManager.getInstance(ProfileActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(ProfileActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(ProfileActivity.this.getApplicationContext());
      //new GetCurrentBooking().execute();
         new GetUserProfile().execute();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(ProfileActivity.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        isVisible = false;
/*
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
*/
        // unregisterReceiver(broadcastReceiver);
    }

    private void showNewRequest(String firstname, String lastname, String picuplocation, String dropofflocation, final String request_id, String picklaterdate, String picklatertime, String booktype, String rating, String favorite_ride, String location_type_str, String realstate_signing_str, String number_of_witness_str, String name_str, String mobile_str, String email_str, String number_of_signing_str, String type_of_signing_name_str, String car_type_image, String car_type_name, String car_min_charge,String emailtoprit,String scanandemail,String prepaid,String overnight,String night_charge,String today_charge) {
        dialogsts_show = true;
        request_id_main = request_id;
        booking_request_dialog = new Dialog(ProfileActivity.this);
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
        total_amt.setText("$ "+car_min_charge);
        if (car_type_image==null||car_type_image.equalsIgnoreCase("")||car_type_image.equalsIgnoreCase(BaseUrl.image_baseurl)){

        }
        else {
            Picasso.with(ProfileActivity.this).load(car_type_image).placeholder(R.drawable.mini).into(carimage);

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
        extranightcharge.setText(getResources().getString(R.string.nightcharge) + " $" + night_charge);
        extracharge.setText(getResources().getString(R.string.extracharge)+" $"+today_charge);



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


                    if (jsonObject.getString("message").equalsIgnoreCase("unsuccessfull")) {
                        if (jsonObject.getString("result").equalsIgnoreCase("already accepted")) {
                            reideAllreadyAccepted();
                        } else {
                            reideAllreadyCanceled();
                        }

                    } else {
                        if (status_job.equalsIgnoreCase("Cancel")) {

                        } else {
                            Intent i = new Intent(ProfileActivity.this, TrackRideAct.class);
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


    private void stopCountDownTimer() {
        if (countDownTimer == null) {

        } else {
            countDownTimer.cancel();
        }

    }

    private void usercancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(ProfileActivity.this);
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
    private void reideAllreadyCanceled() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(ProfileActivity.this);
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


    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d",
               /* TimeUnit.MILLISECONDS.toHours(milliSeconds),*/
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }
    private void reideAllreadyAccepted() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(ProfileActivity.this);
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
    private void clickevent() {
        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent i = new Intent(ProfileActivity.this, WithdrawActivity.class);
                Intent i = new Intent(ProfileActivity.this, WalletActivity.class);
                startActivity(i);
            }
        });
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        agentdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, AgentDetails.class);
                startActivity(i);
            }
        });
        invitefriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent(ProfileActivity.this, InviteEarnAct.class);
               startActivity(i);

            }
        });
        image_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_on = "proimg";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);


            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                firebase_regid = pref.getString("regId", null);
                email_str = email_et.getText().toString();
                password_str = password_et.getText().toString();
                mobile_str = phone_et.getText().toString();
                username_str = first_name.getText().toString();
                last_name_str = last_name.getText().toString();




                if (email_str == null || email_str.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.pleaseenteremailid), Toast.LENGTH_LONG).show();
                } /*else if (password_str == null || password_str.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileAct.this, getResources().getString(R.string.plsentpass), Toast.LENGTH_LONG).show();
                }*/ else if (mobile_str == null || mobile_str.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.plsmobilestr), Toast.LENGTH_LONG).show();
                } else if (username_str == null || username_str.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.plsenfrst), Toast.LENGTH_LONG).show();

                }else if (last_name_str == null || last_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.plsenlst), Toast.LENGTH_LONG).show();

                }else if (last_name_str == null || last_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.plsenfrst), Toast.LENGTH_LONG).show();

                }   else {
                    new JsonUpdateProfile().execute();
                }
            }
        });



    }

    private void idinit() {
        total_amount = findViewById(R.id.total_amount);
        average_rating = findViewById(R.id.average_rating);
        exit_app_but = findViewById(R.id.exit_app_but);
        agentdetail = findViewById(R.id.agentdetail);
        update = findViewById(R.id.update);
        image_lay = findViewById(R.id.image_lay);
        user_img = findViewById(R.id.user_img);
        password_et = findViewById(R.id.password_et);
        phone_et = findViewById(R.id.phone_et);
        email_et = findViewById(R.id.email_et);
        last_name = findViewById(R.id.last_name);
        first_name = findViewById(R.id.first_name);
        withdraw = findViewById(R.id.withdraw);
        invitefriend = findViewById(R.id.invitefriend);
    }

    private class GetUserProfile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // prgressbar.setVisibility(View.VISIBLE);
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
//http://technorizen.com/WORKSPACE1/shipper/webservice/get_user?user_id=61
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_profile?";
                Log.e("Base Url=", ">>" + postReceiverUrl + "user_id=" + user_id+"&type=AGENT");

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
                Log.e("Profile Response", ">>>>>>>>>>>>" + response);
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
            //  prgressbar.setVisibility(View.GONE);

            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }


            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        first_name.setText("" + jsonObject1.getString("first_name"));
                        last_name.setText("" + jsonObject1.getString("last_name"));
                        email_et.setText("" + jsonObject1.getString("email"));
                       String rating_str= jsonObject1.getString("rating");
                       if (rating_str==null||rating_str.equalsIgnoreCase("")){

                       }else {
                           average_rating.setRating(Float.parseFloat(rating_str));
                       }
                        if (jsonObject1.getString("email") == null || jsonObject1.getString("email").equalsIgnoreCase("") || jsonObject1.getString("email").equalsIgnoreCase("0")) {
                            email_et.setEnabled(true);
                        }
                        BaseActivity.promo_code = jsonObject1.getString("promo_code");
                        email_str = jsonObject1.getString("email");
                        password_str = jsonObject1.getString("password");
                        car_manuyear = jsonObject1.getString("year_of_manufacture");
                      //  car_col_str = jsonObject1.getString("car_color");
                        car_registrationnum_str = jsonObject1.getString("car_number");
                        car_model_str = jsonObject1.getString("car_model");
                        // sellang.setText("" + jsonObject1.getString("language"));
                        phone_et.setText("" + jsonObject1.getString("mobile"));
                        password_et.setText("" + jsonObject1.getString("password"));
                        total_amount.setText("$" + jsonObject1.getString("amount"));
                        image_url = jsonObject1.getString("image");

                        if (image_url == null) {

                        } else if (image_url.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                        } else if (image_url.equalsIgnoreCase("")) {

                        } else {
                            //  Picasso.with(ProfileAct.this).load(image_url).into(user_img);
                            Glide.with(ProfileActivity.this)
                                    .load(image_url)
                                    .thumbnail(0.5f)
                                    .override(200, 200)
                                    .centerCrop()
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                                            return false;
                                        }
                                    })
                                    .into(user_img);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    public class JsonUpdateProfile extends AsyncTask<String, String, String> {

        String Jsondata;
        private boolean checkdata = false;

        protected void onPreExecute() {
            try {
                super.onPreExecute();
                // prgressbar.setVisibility(View.VISIBLE);
                if (ac_dialog != null) {
                    ac_dialog.show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                checkdata = true;

            }

        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/user_update?user_id=21&first_name=er&last_name=vijay&mobile=8889994272&email=v@gmail.com&lang=en&currency=inr&place=indore&country=india&register_id=123&ios_register_id=321&lat=22.123456&lon=75.123456
            String charset = "UTF-8";
            String requestURL = BaseUrl.baseurl + "update_profile?";
            Log.e("requestURL >>", requestURL);

            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addFormField("user_id", user_id);
                multipart.addFormField("first_name", username_str);
                multipart.addFormField("last_name", last_name_str);
                multipart.addFormField("email", email_str);
                multipart.addFormField("password", password_str);
                multipart.addFormField("mobile", mobile_str);
                multipart.addFormField("lang", "");
                multipart.addFormField("register_id", firebase_regid);
                multipart.addFormField("type", "AGENT");
                multipart.addFormField("car_model", car_model_str);
                multipart.addFormField("year_of_manufacture", car_manuyear);
                //  multipart.addFormField("car_color", car_col_str);
                multipart.addFormField("car_color", "Yellow");
                multipart.addFormField("car_number", car_registrationnum_str);
                if (ImagePath.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(ImagePath);
                    multipart.addFilePart("image", ImageFile);
                }
                List<String> response = multipart.finish();
                for (String line : response) {
                    Jsondata = line;
                    Log.e("Update Response ====", Jsondata);

                }
                JSONObject object = new JSONObject(Jsondata);
                return Jsondata;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // prgressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                mySession.setlogindata(result);
                mySession.signinusers(true);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")){
                        Toast.makeText(ProfileActivity.this,getResources().getString(R.string.profileupdsucc),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {


            switch (requestCode) {
                case 1:
                    Uri selectedImage = data.getData();
                    String ImagePath = getPath(selectedImage);
                    decodeFile(ImagePath);
                    break;
                case 2:

                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    // File file = new File(photo);
                    //  save(file.getAbsolutePath());
                    ImagePath = saveToInternalStorage(photo);
                    Log.e("PATH Camera", "" + ImagePath);

                    //  avt_imag.setImageBitmap(photo);
                    break;


            }
        }
    }

    public String getPath(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //  Log.e("image_path.===..", "" + path);
        }
        cursor.close();
        return path;
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, "profile.JPEG");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }


    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);
        if (click_on.equalsIgnoreCase("proimg")) {
            Log.e("COME", "Yes2");
            ImagePath = saveToInternalStorage(bitmap);
            user_img.setImageBitmap(bitmap);
        }

    }


}
