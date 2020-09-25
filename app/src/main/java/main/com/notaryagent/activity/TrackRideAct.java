package main.com.notaryagent.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.notaryagent.R;
import main.com.notaryagent.app.Config;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.CancelReasBean;
import main.com.notaryagent.constant.MultipartUtility;
import main.com.notaryagent.constant.MySession;
import main.com.notaryagent.draglocation.DataParser;
import main.com.notaryagent.draweractivity.BaseActivity;
import main.com.notaryagent.service.BackButtonService;
import main.com.notaryagent.service.GPSTracker;
import main.com.notaryagent.tabactivity.HomeActivity;
import main.com.notaryagent.tabactivity.MainTabActivity;
import main.com.notaryagent.tabactivity.SigningActivity;
import main.com.notaryagent.utils.NotificationUtils;

public class TrackRideAct extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult> {
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ACProgressCustom ac_dialog;
    private String user_log_data = "", user_id = "", driver_id = "", Status_Chk = "", status = "";
    private MySession mySession;
    private CircleImageView driverimg;
    private TextView agentname, cardetail, cancel_tv, driverstatus, signing_sts_but;
    private RatingBar ratingbar;
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    public double longitude = 0.0, latitude = 0.0, signign_lat_str = 0, signin_lon_str = 0, drop_lat_str = 0, drop_lon_str = 0;
    public static String time_zone = "";
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 0; // in Milliseconds
    Location location;
    Location location_ar;
    LocationManager locationManager;
    private GoogleApiClient googleApiClient;
    Marker driver_marker;
    private String request_id = "",new_request_id, mobile = "",sch_status="",payment_status_str="";
    public static double pic_lat = 0.0, pick_lon = 0.0, drop_lat = 0.0, drop_lon = 0.0, driver_lat = 0.0, driver_lon = 0.0;
    LatLng driverlatlng, old_driver_lat;
    private TextView signinglocation, seetrip;
    private RelativeLayout exit_app_but, cancel_lay;
    LatLng startlatlong;
    Marker drivermarker;
    private ImageView chatlay, calllay,navigate;
    private boolean isVisible = true,isHere=true;
    private String status_job="",diff_second="",usermobile_str = "",signing_loc_str="",signing_date_time_str="",signinglocation_str="", userimage_str = "", booking_user_id = "", username_str = "";
    Dialog booking_request_dialog;
    private TextView textViewTime;
    private CountDownTimer countDownTimer;
    boolean dialogsts_show = false;
    private long timeCountInMilliSeconds;
    private ListView reasonlist;
    private CancelReasonAdp cancelReasonAdp;
    private ArrayList<CancelReasBean> cancellist;
    private String cancel_reaison="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_ride);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        isHere=true;
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();
        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        checklocation();
        getCurrentLocation();
        try {
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        adddataincancellist();
        idinit();
        clickevent();
        time_zone = tz.getID();
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
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Log.e("Push notification: ", "" + message);
                    JSONObject data = null;
                    try {
                        data = new JSONObject(message);
                        String keyMessage = data.getString("key").trim();
                        Log.e("KEY MSG =", "" + keyMessage);
                        if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {
                            String request_id_other = data.getString("request_id");
                            if (request_id_other.equalsIgnoreCase(request_id)) {
                                usercancelRide();
                            }

                        }
                        else if (keyMessage.equalsIgnoreCase("your booking request is Now")) {

                            if (booking_request_dialog == null) {
                                Log.e("COME ", "null");
                                String firstname = data.getString("first_name");
                                String lastname = data.getString("last_name");
                                String picuplocation = data.getString("picuplocation");
                                String dropofflocation = data.getString("dropofflocation");
                                new_request_id = data.getString("request_id");
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


                                showNewRequest(firstname, lastname, picuplocation, dropofflocation, new_request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride,location_type_str,realstate_signing_str,number_of_witness_str,name_str,mobile_str,email_str,number_of_signing_str,type_of_signing_name_str,car_type_image,car_type_name,paid_amount,emailtoprit,scanandemail,prepaid,overnight,night_charge,today_charge);

                            } else if (booking_request_dialog.isShowing()) {
                                Log.e("COME ", "show");
                            } else {
                                Log.e("COME ", "else");
                                String firstname = data.getString("first_name");
                                String lastname = data.getString("last_name");
                                String picuplocation = data.getString("picuplocation");
                                String dropofflocation = data.getString("dropofflocation");
                                new_request_id = data.getString("request_id");
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


                                showNewRequest(firstname, lastname, picuplocation, dropofflocation, new_request_id, picklaterdate, picklatertime, booktype, rating, favorite_ride,location_type_str,realstate_signing_str,number_of_witness_str,name_str,mobile_str,email_str,number_of_signing_str,type_of_signing_name_str,car_type_image,car_type_name,paid_amount,emailtoprit,scanandemail,prepaid,overnight,night_charge,today_charge);

                            }
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
                        else if (keyMessage.equalsIgnoreCase("user pay the signing amount")) {
                            if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                                NotificationUtils.r.stop();
                            }
                            String request_id_other = data.getString("request_id");
                            String picuplocation = data.getString("picuplocation");
                            String req_datetime = data.getString("req_datetime");
                            String picklaterdate = data.getString("picklaterdate");
                            String picklatertime = data.getString("picklatertime");

                            paymentDoneByUser(picuplocation,req_datetime,picklaterdate,picklatertime,"paymentdone");

                        }else if (keyMessage.equalsIgnoreCase("user confirm payment amount")) {
                            if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                                NotificationUtils.r.stop();
                            }
                            String request_id_other = data.getString("request_id");
                            String picuplocation = data.getString("picuplocation");
                            String req_datetime = data.getString("req_datetime");
                            String picklaterdate = data.getString("picklaterdate");
                            String picklatertime = data.getString("picklatertime");

                            paymentDoneByUser(picuplocation,req_datetime,picklaterdate,picklatertime,"paymentconfirm");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && !bundle.isEmpty()) {
            request_id = bundle.getString("request_id");

            new RideDetailAsc().execute();
        }

        // tripEnd();
    }

    private void usercancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TrackRideAct.this);
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
                finish();

            }
        });
        canceldialog.show();


    }

    private void clickevent() {
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  toCancelRide();
                selectCancelReasion();
              /*  Status_Chk = "Cancel";
                new ResponseToRequest().execute(Status_Chk,request_id);*/
            }
        });
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + signinglocation_str));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(TrackRideAct.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 2);

                    } else {
                        try {
                            startService(new Intent(getApplicationContext(), BackButtonService.class));
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + signinglocation_str));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        catch (Exception e){

                        }


                    }
                } else {
                    try {
                        startService(new Intent(getApplicationContext(), BackButtonService.class));
                        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + signinglocation_str));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }catch (Exception e){

                    }


                }


              //  naviGationWith();
            }
        });
        cancel_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // toCancelRide();
                selectCancelReasion();


            }
        });
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        chatlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(TrackRideAct.this, ChatingAct.class);
                i.putExtra("receiver_id", booking_user_id);
                i.putExtra("request_id", request_id);
                i.putExtra("receiver_img", userimage_str);
                i.putExtra("receiver_name", username_str);
                startActivity(i);
            }
        });
        seetrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(TrackRideAct.this, SigningActivity.class);
                i.putExtra("request_id", request_id);
                startActivity(i);
            }
        });
        calllay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("usermobile_str >>", "> " + usermobile_str);
                confirmDialNumber();

            }
        });
        signing_sts_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equalsIgnoreCase("Accept")) {
                    // startTrip();
                    //areUsureEnd("arrived");
                    areUsureEnd("start");
                } else if (status.equalsIgnoreCase("Arrived")) {
                    // startTrip();
                   // areUsureEnd("start");
                    areUsureEnd("end");
                } else if (status.equalsIgnoreCase("Start")) {

                    areUsureEnd("arrived");

                    /*double distance_difference = distFrom(latitude, longitude, drop_lat, drop_lon);
                    if (distance_difference > 1) {
                        //distanceWarning();
                        distanceWarningPop();
                    } else {
                        //endTrip();
                        areUsureEnd("end");
                    }
*/
                    //    endTrip();
                }
              /*  if (sch_status.equalsIgnoreCase("Now")){
                    if (status.equalsIgnoreCase("Accept")) {
                        // startTrip();
                        areUsureEnd("arrived");
                    } else if (status.equalsIgnoreCase("Arrived")) {
                        // startTrip();
                        areUsureEnd("start");
                    } else if (status.equalsIgnoreCase("Start")) {
                        areUsureEnd("end");
                    *//*double distance_difference = distFrom(latitude, longitude, drop_lat, drop_lon);
                    if (distance_difference > 1) {
                        //distanceWarning();
                        distanceWarningPop();
                    } else {
                        //endTrip();
                        areUsureEnd("end");
                    }
*//*
                        //    endTrip();
                    }
                }
                else {
                    Toast.makeText(TrackRideAct.this,getResources().getString(R.string.pleasewaitforsignigntime),Toast.LENGTH_LONG).show();
                }*/



            }
        });


    }


    private void confirmDialNumber() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TrackRideAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.sure_to_cancle);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView heading_tv = (TextView) canceldialog.findViewById(R.id.heading_tv);
        final TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        heading_tv.setText(""+getResources().getString(R.string.dialnumber));
        if (usermobile_str==null||usermobile_str.equalsIgnoreCase("")){
            message_tv.setText(""+getResources().getString(R.string.numberisnotavb));
        }
        else {
            message_tv.setText(""+usermobile_str);
        }

        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                canceldialog.dismiss();
                if (usermobile_str == null || usermobile_str.equalsIgnoreCase("")) {
                    Toast.makeText(TrackRideAct.this, getResources().getString(R.string.mobilenumbernotavb), Toast.LENGTH_LONG).show();
                } else {
                    if (ActivityCompat.checkSelfPermission(TrackRideAct.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Log.e("COME ", "dd" + usermobile_str);
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + usermobile_str));
                    startActivity(callIntent);

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


    private void distanceWarningPop() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TrackRideAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_heading_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = canceldialog.findViewById(R.id.message_tv);
        final TextView heading_tv = canceldialog.findViewById(R.id.heading_tv);
        final TextView no_tv = canceldialog.findViewById(R.id.no_tv);

        heading_tv.setText("" + getResources().getString(R.string.warning));
        message_tv.setText("" + getResources().getString(R.string.notonpoint));
        yes_tv.setText("" + getResources().getString(R.string.ok));
        no_tv.setText("" + getResources().getString(R.string.arrived));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();

            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                Status_Chk = "End";
                new ResponseToRequest().execute(Status_Chk,request_id);


            }
        });
        canceldialog.show();


    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        return dist * 1.60934;
    }


    private void areUsureEnd(final String status) {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TrackRideAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.bookig_cancel_me_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView yes_tv = canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = canceldialog.findViewById(R.id.message_tv);
        final TextView no_tv = canceldialog.findViewById(R.id.no_tv);
        if (status.equalsIgnoreCase("end")) {
            message_tv.setText("" + getResources().getString(R.string.toendtrip));
        } else if (status.equalsIgnoreCase("arrived")) {
            message_tv.setText("" + getResources().getString(R.string.toarrived));
        } else {
            message_tv.setText("" + getResources().getString(R.string.tostarttrip));
        }
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equalsIgnoreCase("end")) {

                    Status_Chk = "End";
                    new ResponseToRequest().execute(Status_Chk,request_id);
                } else if (status.equalsIgnoreCase("arrived")) {
                    Status_Chk = "Arrived";
                    new ResponseToRequest().execute(Status_Chk,request_id);
                } else {
                    Status_Chk = "Start";
                    new ResponseToRequest().execute(Status_Chk,request_id);
                }
                canceldialog.dismiss();
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

    private void initilizeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void toCancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TrackRideAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.sure_to_cancle);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Status_Chk = "Cancel";
                new ResponseToRequest().execute(Status_Chk,request_id);
                canceldialog.dismiss();

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


    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(TrackRideAct.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackRideAct.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMyLocationEnabled(true);
        gMap.setBuildingsEnabled(false);
        // gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        gMap.getUiSettings().setMapToolbarEnabled(false);
        gMap.getUiSettings().setZoomControlsEnabled(false);

        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        gMap.animateCamera(cameraUpdate);

        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (location != null) {
                    LatLng l = new LatLng(location.getLatitude(), location.getLongitude());
                    if (driver_marker != null) {
                        driver_marker.setPosition(l);
                    }

                }
            }
        });

    }

    private void idinit() {
        navigate = findViewById(R.id.navigate);
        seetrip = findViewById(R.id.seetrip);
        calllay = findViewById(R.id.calllay);
        chatlay = findViewById(R.id.chatlay);
        cancel_lay = findViewById(R.id.cancel_lay);
        signing_sts_but = findViewById(R.id.signing_sts_but);
        exit_app_but = findViewById(R.id.exit_app_but);
        signinglocation = findViewById(R.id.signinglocation);
        ratingbar = findViewById(R.id.ratingbar);
        driverimg = findViewById(R.id.driverimg);
        agentname = findViewById(R.id.agentname);
        cardetail = findViewById(R.id.cardetail);
        cancel_tv = findViewById(R.id.cancel_tv);
        driverstatus = findViewById(R.id.driverstatus);
    }

    private void checklocation() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location_ar = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (location_ar != null) {
            //Getting longitude and latitude
            longitude = location_ar.getLongitude();
            latitude = location_ar.getLatitude();
            driver_lat = latitude;
            driver_lon = longitude;

            if (latitude == 0.0) {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;
                driver_lat = latitude;
                driver_lon = longitude;

            }


        } else {
            System.out.println("----------------geting Location from GPS----------------");
            GPSTracker tracker = new GPSTracker(this);
            location_ar = tracker.getLocation();
            if (location_ar == null) {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;
                driver_lat = latitude;
                driver_lon = longitude;

            } else {
                longitude = location_ar.getLongitude();
                latitude = location_ar.getLatitude();
                driver_lat = latitude;
                driver_lon = longitude;

                if (latitude == 0.0) {
                    latitude = SplashActivity.latitude;
                    longitude = SplashActivity.longitude;
                    driver_lat = latitude;
                    driver_lon = longitude;

                }


            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        LocalBroadcastManager.getInstance(TrackRideAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(TrackRideAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(TrackRideAct.this.getApplicationContext());
        //new GetCurrentBooking().execute();

    }

    @Override
    public void onPause() {
        super.onPause();
        isVisible = false;
        LocalBroadcastManager.getInstance(TrackRideAct.this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {

            longitude = location.getLongitude();
            latitude = location.getLatitude();


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }


    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + getResources().getString(R.string.googlekey_other);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {

        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);

            } catch (Exception e) {

            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            ArrayList<LatLng> animation_list = null;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                animation_list = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                    animation_list.add(position);

                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

            }
            if (lineOptions != null) {
                gMap.addPolyline(lineOptions);
             /*   MarkerOptions pick = new MarkerOptions().position(picklatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pick_marker)).flat(true).anchor(0.5f, 0.5f);
                MarkerOptions drop = new MarkerOptions().position(droplatlong).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)).flat(true).anchor(0.5f, 0.5f);
                gMap.addMarker(pick);
                gMap.addMarker(drop);*/

            } else {

            }
        }
    }

    public class ResponseToRequest extends AsyncTask<String, String, String> {
        String Jsondata;

        protected void onPreExecute() {
            try {
                super.onPreExecute();
                if (ac_dialog != null) {
                    if (isVisible) {
                        ac_dialog.show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            //http://technorizen.com/transport/webservice/signup?timezone=&first_name=&last_name=&mobile=&email=&country=&state=&city=&password=&type=&lat=&lon=&register_id=&ios_register_id=&vehicle_type_id=&license_number=&licence_expiry_date=&vehicle_number=&referrer_code=&
            String charset = "UTF-8";
            String requestURL = BaseUrl.baseurl + "driver_accept_and_Cancel_request?";
            Log.e("requestURL >>", requestURL+"request_id="+strings[1]+"&status="+strings[0]+"&timezone="+time_zone+"&driver_id="+user_id.trim());
            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addFormField("request_id", strings[1]);
                multipart.addFormField("status", strings[0]);
                multipart.addFormField("timezone", time_zone);
                multipart.addFormField("driver_id", user_id.trim());
                multipart.addFormField("cancel_reaison", cancel_reaison);

                List<String> response = multipart.finish();
                for (String line : response) {
                    Jsondata = line;


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
            Log.e("Submit DD", " fff " + result);
            super.onPostExecute(result);
            if (result == null) {
                if (ac_dialog != null) {
                    if (isVisible) {
                        ac_dialog.dismiss();
                    }
                }

                Toast.makeText(TrackRideAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                if (ac_dialog != null) {
                    if (isVisible) {
                        ac_dialog.dismiss();
                    }
                }

                Toast.makeText(TrackRideAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else {
                try {
                    if (ac_dialog != null) {
                        if (isVisible) {
                            ac_dialog.dismiss();
                        }
                    }

                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("0")) {
                        if (jsonObject.getString("result").equalsIgnoreCase("already accepted")) {
                            reideAllreadyAccepted();
                        } else {
                            reideAllreadyCanceled();
                        }
                    } else {
                        if (jsonObject.getString("status").equalsIgnoreCase("2")){
                            paymentPending();
                        }
                        else {
                             if (new_request_id==null||new_request_id.equalsIgnoreCase("")){
                                 if (Status_Chk.equalsIgnoreCase("Arrived")) {
                                     status = "Arrived";
                                     //signing_sts_but.setText(getResources().getString(R.string.starttrip));
                                     signing_sts_but.setText(getResources().getString(R.string.endtrip));
                                 } else if (Status_Chk.equalsIgnoreCase("Start")) {
                                     status = "Start";
                                   //  signing_sts_but.setText(getResources().getString(R.string.endtrip));
                                     signing_sts_but.setText(getResources().getString(R.string.arrived));
                                 } else if (Status_Chk.equalsIgnoreCase("End")) {
                                     status = "End";
                                     Intent i = new Intent(TrackRideAct.this, SigningActivity.class);
                                     i.putExtra("request_id", request_id);
                                     startActivity(i);
                                     finish();
                           /* Intent i = new Intent(TrackRideAct.this, MainTabActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);*/
                                 } else if (Status_Chk.equalsIgnoreCase("Cancel")) {
                                     //finish();
                                     Intent i = new Intent(TrackRideAct.this, MainTabActivity.class);
                                     i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                     i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                     i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                     startActivity(i);
                                 }
                             }
                             else {
                                 tripScheduled();
                             }


                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }
    private void tripScheduled() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TrackRideAct.this);
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
                signing_date_time_str="";
                signing_loc_str="";
                new_request_id="";

                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }

    public class ResponseToComplete extends AsyncTask<String, String, String> {
        String Jsondata;

        protected void onPreExecute() {
            try {
                super.onPreExecute();
                if (ac_dialog != null) {
                    if (isVisible) {
                        ac_dialog.show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            //http://technorizen.com/transport/webservice/signup?timezone=&first_name=&last_name=&mobile=&email=&country=&state=&city=&password=&type=&lat=&lon=&register_id=&ios_register_id=&vehicle_type_id=&license_number=&licence_expiry_date=&vehicle_number=&referrer_code=&
            String charset = "UTF-8";
            String requestURL = BaseUrl.baseurl + "driver_accept_and_Cancel_request?";
            Log.e("requestURL >>", requestURL);
            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addFormField("request_id", request_id);
                multipart.addFormField("status", "Finish");
                multipart.addFormField("timezone", time_zone);
                multipart.addFormField("driver_id", user_id.trim());


                List<String> response = multipart.finish();
                for (String line : response) {
                    Jsondata = line;


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
            Log.e("Submit DD", " fff " + result);
            super.onPostExecute(result);

            if (result == null) {
                if (ac_dialog != null) {
                    if (isVisible) {
                        ac_dialog.dismiss();
                    }
                }
                Toast.makeText(TrackRideAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                if (ac_dialog != null) {
                    if (isVisible) {
                        ac_dialog.dismiss();
                    }
                }
                Toast.makeText(TrackRideAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else {
                try {
                    if (ac_dialog != null) {
                        if (isVisible) {
                            ac_dialog.dismiss();
                        }
                    }
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("message").equalsIgnoreCase("unsuccessfull")) {
                        reideAllreadyCanceled();
                    } else {
                        Intent i = new Intent(TrackRideAct.this, MainTabActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void reideAllreadyCanceled() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TrackRideAct.this);
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
                finish();

                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }
    private void paymentPending() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TrackRideAct.this);
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
    private void paymentDoneByUser(String picuplocation, String req_datetime, String picklaterdate, String picklatertime, String paysts) {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TrackRideAct.this);
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

    private class RideDetailAsc extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ac_dialog != null) {
                if (isVisible) {
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
//http://halatx.halasmart.com/hala/webservice/get_ride_detail?user_id=76&request_id=379
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_ride_detail?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("request_id", request_id);


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
            // progressbar.setVisibility(View.GONE);

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

                    Log.e("CURRENT BOOK TRACK>>>", "" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successfull")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            request_id = jsonObject1.getString("id");
                            booking_user_id = jsonObject1.getString("user_id");
                            payment_status_str = jsonObject1.getString("payment_status");
                            JSONArray jsonArray3 = jsonObject1.getJSONArray("user_details");
                            for (int user = 0; user < jsonArray3.length(); user++) {
                                JSONObject jsonObject2 = jsonArray3.getJSONObject(user);
                                username_str = jsonObject2.getString("first_name") + " " + jsonObject2.getString("last_name");
                                // ridecrname_str = jsonObject2.getString("first_name");
                                usermobile_str = jsonObject2.getString("mobile");
                                userimage_str = jsonObject2.getString("profile_image");

                            }
                            agentname.setText("" + username_str);
                            if (userimage_str == null || userimage_str.equalsIgnoreCase(BaseUrl.image_baseurl) || userimage_str.equalsIgnoreCase("")) {
                            } else {
                                Picasso.with(TrackRideAct.this).load(userimage_str).placeholder(R.drawable.ic_user_prof).into(driverimg);
                            }

                            signinglocation_str = jsonObject1.getString("picuplocation");
                            signinglocation.setText("" + jsonObject1.getString("picuplocation"));
                            // dropofflocation.setText("" + dropofflocation_str);
                            status = jsonObject1.getString("status");
                            sch_status = jsonObject1.getString("sch_status");
                            Log.e("Track Status >> ", " >> " + status);
                            if (status == null || status.equalsIgnoreCase("")) {
                            } else {
                            }
                            if (status.equalsIgnoreCase("Accept")) {

                                //signing_sts_but.setText(getResources().getString(R.string.arrived));
                                signing_sts_but.setText(getResources().getString(R.string.starttrip));
                                //navigate.setVisibility(View.GONE);
/*
                                if (isHere){
                                    //naviGationWith();

                                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + signinglocation_str));
                                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                    isHere=false;
                                }
*/

                            } else if (status.equalsIgnoreCase("Arrived")) {
                                signing_sts_but.setText(getResources().getString(R.string.endtrip));
                                //signing_sts_but.setText(getResources().getString(R.string.starttrip));
                                // navigate.setVisibility(View.GONE);

                            } else if (status.equalsIgnoreCase("Start")) {
                                signing_sts_but.setText(getResources().getString(R.string.arrived));
                                //signing_sts_but.setText(getResources().getString(R.string.endtrip));

                            } else if (status.equalsIgnoreCase("End")) {
                                /*Intent j = new Intent(TripStatusAct.this, PaymentAct.class);
                                startActivity(j);
                                finish();*/

                            }

                            if (jsonObject1.getString("picuplat") == null || jsonObject1.getString("picuplat").equalsIgnoreCase("")) {
                            } else {

                                pic_lat = Double.parseDouble(jsonObject1.getString("picuplat"));
                                pick_lon = Double.parseDouble(jsonObject1.getString("pickuplon"));
                                drop_lat = Double.parseDouble(jsonObject1.getString("droplat"));
                                drop_lon = Double.parseDouble(jsonObject1.getString("droplon"));
                                if (gMap == null) {
                                    Log.e("Come Map Null", "");
                                } else {


                                    MarkerOptions markers1 = new MarkerOptions().position(new LatLng(driver_lat, driver_lon)).flat(true).anchor(0.5f, 0.5f).title("Your Agent");
                                   /* MarkerOptions markers = new MarkerOptions().position(new LatLng(pic_lat, pick_lon)).icon(BitmapDescriptorFactory.fromBitmap(cretaorUser(ridecrname_str, "Pickup"))).flat(true).anchor(0.5f, 0.5f).title("Pickup: "+jsonObject1.getString("picuplocation"));
                                    MarkerOptions marker2 = new MarkerOptions().position(new LatLng(drop_lat, drop_lon)).icon(BitmapDescriptorFactory.fromBitmap(cretaorUser(ridecrname_str, "Destination"))).flat(true).anchor(0.5f, 0.5f).title("Dropoff: "+jsonObject1.getString("dropofflocation"));
*/
                                    MarkerOptions markers = new MarkerOptions().position(new LatLng(pic_lat, pick_lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)).flat(true).anchor(0.5f, 0.5f).title("Signing Location");
                                    // MarkerOptions marker2 = new MarkerOptions().position(new LatLng(drop_lat, drop_lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)).flat(true).anchor(0.5f, 0.5f);

                                    driver_marker = gMap.addMarker(markers1);
                                    driver_marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));


                                    gMap.addMarker(markers);
                                    // gMap.addMarker(marker2);
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(pic_lat, pick_lon), 16);
                                    gMap.animateCamera(cameraUpdate);
/*
                                    timer.schedule(new TimerTask() {
                                        public void run() {
                                            System.out.println("-------------runing-------------");
                                            UpdateDriverLoction task = new UpdateDriverLoction();
                                            task.execute();
                                        }
                                    }, 0, 10000);
*/

                                    // gMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                                    Log.e("Come Map True", "" + pic_lat);


                                    // String url = getUrl(new LatLng(pic_lat, pick_lon), new LatLng(drop_lat, drop_lon));
                                    String url = getUrl(new LatLng(driver_lat, driver_lon), new LatLng(pic_lat, pick_lon));
                                    FetchUrl FetchUrl = new FetchUrl();
                                    FetchUrl.execute(url, "first");
                                    LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                            .include(new LatLng(driver_lat, driver_lon))
                                            .include(new LatLng(pic_lat, pick_lon))
                                            .build();
                                    int width = getResources().getDisplayMetrics().widthPixels;
                                    int padding = (int) (width * 0); // offset from edges of the map 12% of screen
                                    gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));
                                    // }
                                }
                            }


                        }
/*
                        timer.schedule(new TimerTask() {
                            public void run() {
                                System.out.println("-------------runing-------------");

                                if (driver_lat != 0) {
                                    String driver_lat_sr = String.valueOf(driver_lat);
                                    String driver_lon_sr = String.valueOf(driver_lon);
                                    new GetDriverLat().execute(driver_lat_sr, driver_lon_sr);
                                }
                            }
                        }, 1000, 10000);
*/


                    } else {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void naviGationWith() {


        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(TrackRideAct.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.custom_navigate_option);
        //  dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.back_pop_col)));
        TextView waze = (TextView) dialogSts.findViewById(R.id.waze);
        TextView googlemap = (TextView) dialogSts.findViewById(R.id.googlemap);
        TextView cancel = (TextView) dialogSts.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();

                //employerAccept();

            }
        });
        googlemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();

                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + signinglocation_str));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });
        waze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();


                String uri = "https://waze.com/ul?ll=" + pic_lat + "," + pick_lon + "&navigate=yes";
                startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uri)));


                //String uri = "geo: latitude,longtitude";

            }
        });

        dialogSts.show();


    }

    private void showNewRequest(String firstname, String lastname, String picuplocation, String dropofflocation, final String request_id, String picklaterdate, String picklatertime, String booktype, String rating, String favorite_ride, String location_type_str, String realstate_signing_str, String number_of_witness_str, String name_str, String mobile_str, String email_str, String number_of_signing_str, String type_of_signing_name_str, String car_type_image, String car_type_name, String car_min_charge,String emailtoprit,String scanandemail,String prepaid,String overnight,String night_charge,String today_charge) {
        Log.e("REQUESTID "," >> "+request_id);
        Log.e("NEWREQUESTID "," >> "+new_request_id);

        dialogsts_show = true;

        booking_request_dialog = new Dialog(TrackRideAct.this);
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
        TextView extranightcharge = booking_request_dialog.findViewById(R.id.extranightcharge);
        carname.setText(""+car_type_name);
        price.setText("$ "+car_min_charge);
        if (car_type_image==null||car_type_image.equalsIgnoreCase("")||car_type_image.equalsIgnoreCase(BaseUrl.image_baseurl)){

        }
        else {
            Picasso.with(TrackRideAct.this).load(car_type_image).placeholder(R.drawable.mini).into(carimage);

        }
        if (picklaterdate==null||picklaterdate.equalsIgnoreCase("")){
            date_lay.setVisibility(View.GONE);
        }

        username.setText("" + firstname + " " + lastname);
        pick_location.setText("" + picuplocation);
        drop_location.setText("" + dropofflocation);
        signing_loc_str = picuplocation;
        signing_date_time_str = "" + picklaterdate.trim() + " " + picklatertime.trim();
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
        extranightcharge.setText( " $" + night_charge);
        extracharge.setText(" $"+today_charge);


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


                selectCancelReasionPop();

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
                new ResponseToRequest().execute(status_job,new_request_id);
                stopCountDownTimer();
            }
        });
        if (booking_request_dialog.isShowing()) {

        } else {
            booking_request_dialog.show();
        }


    }
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d",
               /* TimeUnit.MILLISECONDS.toHours(milliSeconds),*/
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }
    private void stopCountDownTimer() {
        if (countDownTimer == null) {

        } else {
            countDownTimer.cancel();
        }

    }
    private void selectCancelReasion() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TrackRideAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.cancel_reasion_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        reasonlist = canceldialog.findViewById(R.id.reasonlist);
        cancelReasonAdp = new CancelReasonAdp(TrackRideAct.this, cancellist);
        reasonlist.setAdapter(cancelReasonAdp);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancel_reaison==null||cancel_reaison.equalsIgnoreCase("")){
                    Toast.makeText(TrackRideAct.this,getResources().getString(R.string.selectcancelres),Toast.LENGTH_LONG).show();
                }
                else {
                    canceldialog.dismiss();
                    Status_Chk = "Cancel";
                    new ResponseToRequest().execute(Status_Chk,request_id);
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
    private void selectCancelReasionPop() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TrackRideAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.cancel_reasion_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        reasonlist = canceldialog.findViewById(R.id.reasonlist);
        cancelReasonAdp = new CancelReasonAdp(TrackRideAct.this, cancellist);
        reasonlist.setAdapter(cancelReasonAdp);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancel_reaison==null||cancel_reaison.equalsIgnoreCase("")){
                    Toast.makeText(TrackRideAct.this,getResources().getString(R.string.selectcancelres),Toast.LENGTH_LONG).show();
                }
                else {
                    if (booking_request_dialog != null || booking_request_dialog.isShowing()) {
                        booking_request_dialog.cancel();
                        booking_request_dialog.dismiss();
                        diff_second = "";
                    }

                    status_job = "Cancel";
                    new ResponseToRequest().execute(status_job,new_request_id);
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


                        cancelReasonAdp = new CancelReasonAdp(TrackRideAct.this, cancellist);
                        reasonlist.setAdapter(cancelReasonAdp);
                        cancelReasonAdp.notifyDataSetChanged();
                    }


                }
            });


            return rowView;
        }

    }
    private void reideAllreadyAccepted() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TrackRideAct.this);
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

}
