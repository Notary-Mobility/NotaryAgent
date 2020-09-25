package main.com.notaryagent.tabactivity;

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
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.prefs.BackingStoreException;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.notaryagent.R;
import main.com.notaryagent.activity.TrackRideAct;
import main.com.notaryagent.activity.SplashActivity;
import main.com.notaryagent.app.Config;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.MySession;
import main.com.notaryagent.constant.RideHistoryBean;
import main.com.notaryagent.draglocation.LoadAdressSyncPlaceId;
import main.com.notaryagent.draglocation.MyTask;
import main.com.notaryagent.draglocation.WebOperations;
import main.com.notaryagent.draweractivity.BaseActivity;
import main.com.notaryagent.service.BackButtonHomeService;
import main.com.notaryagent.service.BackButtonService;
import main.com.notaryagent.service.GPSTracker;
import main.com.notaryagent.utils.NotificationUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult> {
    private FrameLayout contentFrameLayout;
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 0; // in Milliseconds
    Location location;
    Location location_ar;
    LocationManager locationManager;
    private GoogleApiClient googleApiClient;
    private TextView pickuplocation, triphistory;
    public static double longitude = 0.0, latitude = 0.0;
    private long timeCountInMilliSeconds;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    Dialog booking_request_dialog;
    private String request_id = "", request_id_main = "", status_job = "", signing_charge = "", last_signing_location = "";
    private String diff_second = "", time_zone = "", user_log_data = "", user_id = "";
    private CountDownTimer countDownTimer;
    boolean dialogsts_show = false;
    private TextView direction_tv, textViewTime, lasttrip, date_tv, amounttv, typetv, signingtype, tripamount;
    ACProgressCustom ac_dialog;
    MySession mySession;
    Marker agentmarker;


    private String my_online_sts = "";
    private boolean isVisible = true;
    private RecyclerView active_signing_list;
    private CustomActiveSigningAdp customActiveSigningAdp;
    private ArrayList<RideHistoryBean> rideHistoryBeanArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //  contentFrameLayout = (FrameLayout) findViewById(R.id.contentFrame); //Remember this is the FrameLayout area within your activity_main.xml
        // getLayoutInflater().inflate(R.layout.activity_home, contentFrameLayout);
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
        last_signing_location = "";
        checklocation();
        idinits();
        getCurrentLocation();

        // checkGps();

        clickevetn();
        try {
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
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
                        Log.e("KEY_HOME =", "" + keyMessage);
                        if (keyMessage.equalsIgnoreCase("your booking request is Now")) {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


    }

    private void usercancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(HomeActivity.this);
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

    private void clickevetn() {
        triphistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent i = new Intent(HomeActivity.this, TripHistoryAct.class);
                startActivity(i);*/
            }
        });
        direction_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (last_signing_location == null || last_signing_location.equalsIgnoreCase("")) {
                        Toast.makeText(HomeActivity.this, getResources().getString(R.string.noactivesigning), Toast.LENGTH_LONG).show();
                    } else {
                       /* final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+last_signing_location));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);*/


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (!Settings.canDrawOverlays(HomeActivity.this)) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                                startActivity(intent);

                            } else {

                                startService(new Intent(getApplicationContext(), BackButtonHomeService.class));
                                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + last_signing_location));
                                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }
                        } else {


                            startService(new Intent(getApplicationContext(), BackButtonHomeService.class));
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + last_signing_location));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }


                    }

                } catch (Exception e) {

                }

            }
        });
    }

    private void initilizeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void idinits() {

        active_signing_list = findViewById(R.id.active_signing_list);

        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        active_signing_list.setLayoutManager(horizontalLayoutManagaer);

        direction_tv = findViewById(R.id.direction_tv);
        signingtype = findViewById(R.id.signingtype);
        typetv = findViewById(R.id.typetv);
        amounttv = findViewById(R.id.amounttv);
        date_tv = findViewById(R.id.date_tv);
        lasttrip = findViewById(R.id.lasttrip);
        triphistory = findViewById(R.id.triphistory);
        pickuplocation = findViewById(R.id.pickuplocation);
        tripamount = findViewById(R.id.tripamount);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }
        LocalBroadcastManager.getInstance(HomeActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(HomeActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(HomeActivity.this.getApplicationContext());
        new GetCurrentBooking().execute();
        new GetUserProfile().execute();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(HomeActivity.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        isVisible = false;
/*
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
*/
        // unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        agentmarker = gMap.addMarker(marker);
        agentmarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        gMap.animateCamera(cameraUpdate);


        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                if (latLng1 != null) {
                    if (agentmarker != null) {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng1, 17);
                        gMap.animateCamera(cameraUpdate);
                        agentmarker.setPosition(latLng1);
                    }
                }

            }
        });


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
            if (latitude == 0.0) {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;

            }
            /*address_complete = loadAddress(latitude, longitude);
            pickuplocation.setText(address_complete);*/
            loadAddress1(latitude, longitude);


        } else {
            System.out.println("----------------geting Location from GPS----------------");
            GPSTracker tracker = new GPSTracker(this);
            location_ar = tracker.getLocation();
            if (location_ar == null) {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;
                /*address_complete = loadAddress(latitude, longitude);
                pickuplocation.setText(address_complete);*/
                loadAddress1(latitude, longitude);


            } else {
                longitude = location_ar.getLongitude();
                latitude = location_ar.getLatitude();

                if (latitude == 0.0) {
                    latitude = SplashActivity.latitude;
                    longitude = SplashActivity.longitude;

                }
                /*address_complete = loadAddress(latitude, longitude);
                pickuplocation.setText(address_complete);*/
                loadAddress1(latitude, longitude);


            }
        }
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
        LatLng latLng = null;
        if (gMap == null) {

        } else {
            if (location == null) {

            } else {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
            if (latLng == null) {

            } else {
                //  googlemarker_pos.setPosition(latLng);
            }


        }
        //    updateLocationUI();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

/*
        if (requestCode == 11) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    //Freely draw over other apps
                }
            }
        }
*/

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
          /*  case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");

                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        finish();
                        break;

                }
                break;*/
            case 0:
                // startService(new Intent(getApplicationContext(), BackButtonService.class));
                stopService(new Intent(HomeActivity.this, BackButtonHomeService.class));
                //  startActivityForResult(intent,22);
                break;

        }

    }

    private String loadAddress1(double latitude, double longitude) {

        String address = "", address_short = "";
        //  prgressbar.setVisibility(View.VISIBLE);

        WebOperations wo = new WebOperations(HomeActivity.this.getApplicationContext());
        //wo.setUrl("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key="+getResources().getString(R.string.google_search)+"&location_type=ROOFTOP&result_type=street_address");
        wo.setUrl("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + getResources().getString(R.string.googlekey_other));
        new MyTask(wo, 3) {
            @Override
            protected void onPostExecute(String s) {


                new LoadAdressSyncPlaceId() {
                    @Override
                    protected void onPostExecute(String s) {
                        if (s != null && !s.equalsIgnoreCase("null") && !s.equalsIgnoreCase("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                String place_id = jsonObject.getString("place_id");
                                Log.e("place_id >>", " >> " + place_id);
                                String address_complete = jsonObject.getString("address");
                                //  pickuplocation.setText("" + address_complete);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }.execute(s);


            }
        }.execute();

        return "";

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

    private void showNewRequest(String firstname, String lastname, String picuplocation, String dropofflocation, final String request_id, String picklaterdate, String picklatertime, String booktype, String rating, String favorite_ride, String location_type_str, String realstate_signing_str, String number_of_witness_str, String name_str, String mobile_str, String email_str, String number_of_signing_str, String type_of_signing_name_str, String car_type_image, String car_type_name, String car_min_charge, String emailtoprit, String scanandemail, String prepaid, String overnight, String night_charge, String today_charge) {
        dialogsts_show = true;
        request_id_main = request_id;
        booking_request_dialog = new Dialog(HomeActivity.this);
        booking_request_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        booking_request_dialog.setCancelable(false);
        booking_request_dialog.setContentView(R.layout.custom_new_job_lay);
        booking_request_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView decline = booking_request_dialog.findViewById(R.id.decline);
        TextView datetimetv = booking_request_dialog.findViewById(R.id.datetimetv);
        TextView rating_tv = booking_request_dialog.findViewById(R.id.rating_tv);
        TextView total_amt = booking_request_dialog.findViewById(R.id.total_amt);
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
        TextView extranightcharge = booking_request_dialog.findViewById(R.id.extranightcharge);
        carname.setText("" + car_type_name);
        price.setText("$ " + car_min_charge);
        total_amt.setText("$ " + car_min_charge);
        if (car_type_image == null || car_type_image.equalsIgnoreCase("") || car_type_image.equalsIgnoreCase(BaseUrl.image_baseurl)) {

        } else {
            Picasso.with(HomeActivity.this).load(car_type_image).placeholder(R.drawable.mini).into(carimage);

        }
        if (picklaterdate == null || picklaterdate.equalsIgnoreCase("")) {
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
        extracharge.setText(getResources().getString(R.string.extracharge) + " $" + today_charge);


        if (realstate_signing_str.equalsIgnoreCase("No")) {
            selectrealstatelay.setVisibility(View.GONE);
        } else {
            selectrealstatelay.setVisibility(View.VISIBLE);
            emailtoprit_tv.setText(" $" + emailtoprit);
            scanandemail_tv.setText(" $" + scanandemail);
            prepaid_tv.setText(" $" + prepaid);
            overnight_tv.setText(" $" + overnight);


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

    private void stopCountDownTimer() {
        if (countDownTimer == null) {

        } else {
            countDownTimer.cancel();
        }

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
                            Intent i = new Intent(HomeActivity.this, TrackRideAct.class);
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

    private void reideAllreadyAccepted() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(HomeActivity.this);
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

    private void reideAllreadyCanceled() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(HomeActivity.this);
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


    private class GetCurrentBooking extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressbar.setVisibility(View.VISIBLE);
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
            try {
//http://mobileappdevelop.co/NAXCAN/webservice/get_current_booking?user_id=1

                String postReceiverUrl = BaseUrl.baseurl + "get_current_booking?";
                Log.e("POST URL >>", " >> " + postReceiverUrl + "user_id=" + user_id + "&type=AGENT&timezone=" + time_zone);
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
            if (ac_dialog != null) {
                if (isVisible) {
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
                        String car_min_charge = jsonObject.getString("paid_amount");
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            pickuplocation.setText("" + jsonObject1.getString("picuplocation"));
                            signing_charge = jsonObject1.getString("signing_charge");
                            // signingtype.setText(""+jsonObject1.getString("car_type_name"));
                            //  tripamount.setText("$ "+jsonObject1.getString("signing_charge"));
                            request_id = String.valueOf(jsonObject1.getString("id"));


                        }
                    } else {
                        //  signingtype.setText("No Request");
                        // tripamount.setText("$ 0.00");

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private class GetUserProfile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // prgressbar.setVisibility(View.VISIBLE);
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
                Log.e("Base Url=", ">>" + postReceiverUrl + "user_id=" + user_id);

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


            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    rideHistoryBeanArrayList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        String image_url = jsonObject1.getString("image");
                        user_id = jsonObject1.getString("id");
                        Log.e("COME TRUE ", " >." + jsonObject1.getString("first_name"));
                        my_online_sts = jsonObject1.getString("online_status");
                        BaseActivity.promo_code = jsonObject1.getString("promo_code");


                        JSONObject jsonObject2 = jsonObject1.getJSONObject("today_earn");
                        if (jsonObject2.getString("return").equalsIgnoreCase("success")) {
                            BaseActivity.today_ear_amt = jsonObject2.getString("amount");
                            BaseActivity.trip_count = jsonObject2.getString("trip");
                            tripamount.setText("$ " + BaseActivity.today_ear_amt);
                        }
                        JSONObject jsonObject4 = jsonObject1.getJSONObject("active_signing");
                        if (jsonObject4.getString("return").equalsIgnoreCase("success")) {
                            last_signing_location = jsonObject4.getString("picuplocation");
                           // signingtype.setText("Active : " + jsonObject4.getString("result"));
                            JSONArray jsonArray = jsonObject4.getJSONArray("active_bookings");
                            for (int kk = 0; kk < jsonArray.length(); kk++) {
                                RideHistoryBean ridebean = new RideHistoryBean();
                                JSONObject rideobject = jsonArray.getJSONObject(kk);
                                ridebean.setId(rideobject.getString("id"));
                                ridebean.setPicuplocation(rideobject.getString("picuplocation"));
                                ridebean.setDropofflocation(rideobject.getString("dropofflocation"));
                                ridebean.setStatus(rideobject.getString("status"));
                                //ridebean.setSch_status(rideobject.getString("sch_status"));
                                ridebean.setPicklaterdate(rideobject.getString("picklaterdate"));
                                ridebean.setPicklatertime(rideobject.getString("picklatertime"));
                                ridebean.setPayment_status(rideobject.getString("payment_status"));
                                ridebean.setCard_select_status(rideobject.getString("card_select_status"));
                                     try {

                                    Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(rideobject.getString("picklaterdate"));
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
                                    String strDate = formatter.format(date1);
                                    ridebean.setPicklaterdate(strDate);

                                } catch (ParseException e) {
                                         ridebean.setPicklaterdate(rideobject.getString("picklaterdate"));
                                    e.printStackTrace();
                                }
                                rideHistoryBeanArrayList.add(ridebean);
                            }
                            customActiveSigningAdp = new CustomActiveSigningAdp(rideHistoryBeanArrayList);
                            active_signing_list.setAdapter(customActiveSigningAdp);
                            customActiveSigningAdp.notifyDataSetChanged();

                        } else {
                            last_signing_location = "";
                            signingtype.setText("No Activities");
                            customActiveSigningAdp = new CustomActiveSigningAdp(null);
                            active_signing_list.setAdapter(customActiveSigningAdp);
                            customActiveSigningAdp.notifyDataSetChanged();
                        }

                        JSONObject jsonweek_earn = jsonObject1.getJSONObject("week_earn");
                        if (jsonweek_earn.getString("return").equalsIgnoreCase("success")) {
                            BaseActivity.week_ear_amt = jsonweek_earn.getString("amount");
                            BaseActivity.week_trip_count = jsonweek_earn.getString("trip");
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private class ChgStatus extends AsyncTask<String, String, String> {
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
//http://mobileappdevelop.co/NAXCAN/webservice/update_online_status?status=OFFLINE&user_id=1&type=DRIVER
            try {
                String postReceiverUrl = BaseUrl.baseurl + "update_online_status?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("status", my_online_sts);
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
            if (ac_dialog != null && ac_dialog.isShowing()) {
                if (isVisible) {
                    ac_dialog.dismiss();
                }

            }
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                if (my_online_sts != null) {
                    if (my_online_sts.equalsIgnoreCase("ONLINE")) {
                        mySession.onlineuser(true);
                    } else {
                        mySession.onlineuser(false);
                    }
                }


            }


        }
    }
// horizontal list

    class CustomActiveSigningAdp extends RecyclerView.Adapter<CustomActiveSigningAdp.MyViewHolder> {
        ArrayList<RideHistoryBean> rideHistoryBeanArrayList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView active_ride_date;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.active_ride_date = (TextView) itemView.findViewById(R.id.active_ride_date);


            }
        }

        public CustomActiveSigningAdp(ArrayList<RideHistoryBean> rideHistoryBeanArrayList) {
            this.rideHistoryBeanArrayList = rideHistoryBeanArrayList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_active_signing, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {


            holder.active_ride_date.setText("" + rideHistoryBeanArrayList.get(listPosition).getPicklaterdate() + " " + rideHistoryBeanArrayList.get(listPosition).getPicklatertime());


        }

        @Override
        public int getItemCount() {
            // return 4;
            return rideHistoryBeanArrayList == null ? 0 : rideHistoryBeanArrayList.size();
        }
    }

}
