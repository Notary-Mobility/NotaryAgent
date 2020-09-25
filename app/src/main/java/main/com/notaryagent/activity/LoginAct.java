package main.com.notaryagent.activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.notaryagent.R;
import main.com.notaryagent.app.Config;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.MyReceiver;
import main.com.notaryagent.constant.MySession;
import main.com.notaryagent.draweractivity.BaseActivity;
import main.com.notaryagent.restapi.ApiClient;
import main.com.notaryagent.service.GPSTracker;
import main.com.notaryagent.tabactivity.HomeActivity;
import main.com.notaryagent.tabactivity.MainTabActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginAct extends AppCompatActivity {
    private Button loginbut;
    private RelativeLayout backbut;
    private TextView forgot_tv,donthaveaccount;
    private EditText phone_et, password_et;
    private String phone_str = "", password_str = "";
    ACProgressCustom ac_dialog;
    MySession mySession;

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 0; // in Milliseconds
    LocationManager locationManager;
    Location location;
    private double latitude = 0, longitude = 0;
    GPSTracker gpsTracker;



    String user_log_data="",firebase_regid="",login_sts="no",facebook_name="", facebook_email="", facebook_id="", facebook_image="", face_gender, face_locale,facebook_lastname="", face_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_login);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(LoginAct.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LoginAct.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();
        checkGps();
        idinti();
        clickevent();
    }

    private void checkGps() {
        gpsTracker = new GPSTracker(LoginAct.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            if (latitude == 0.0) {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;

            }
        } else {

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            } else {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;
                Log.e("LAT", "" + latitude);
                Log.e("LON", "" + longitude);

            }
        }


    }


    private void clickevent() {


        backbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loginbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                firebase_regid = pref.getString("regId", null);
                phone_str = phone_et.getText().toString();
                password_str = password_et.getText().toString();
                if (phone_str == null || phone_str.equalsIgnoreCase("")) {
                    Toast.makeText(LoginAct.this, getResources().getString(R.string.entermobile), Toast.LENGTH_LONG).show();
                } else if (password_str == null || password_str.equalsIgnoreCase("")) {
                    Toast.makeText(LoginAct.this, getResources().getString(R.string.enterpass), Toast.LENGTH_LONG).show();
                } else {

                    callapi(phone_str, password_str, "" + firebase_regid, "" + latitude, "" + longitude, "AGENT");
                }

            }
        });
        forgot_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginAct.this, ForgotPassword.class);
                startActivity(i);
            }
        });
        donthaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(LoginAct.this, SignupActivity.class);
                Intent i = new Intent(LoginAct.this, AgentSignup.class);
                startActivity(i);
            }
        });
    }

    private void idinti() {

        donthaveaccount = findViewById(R.id.donthaveaccount);
        phone_et = findViewById(R.id.phone_et);
        password_et = findViewById(R.id.password_et);
        forgot_tv = findViewById(R.id.forgot_tv);
        backbut = findViewById(R.id.backbut);
        loginbut = findViewById(R.id.loginbut);
        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    if (jsonObject1.getString("status").equalsIgnoreCase("Active")){
                        phone_et.setText(jsonObject1.getString("mobile"));
                        password_et.setText(jsonObject1.getString("password"));
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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



    private void alreadyLogin() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(LoginAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.confirmlogoutother_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);

        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_sts="yes";
                canceldialog.dismiss();
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                firebase_regid = pref.getString("regId", null);
                callapi(phone_str, password_str, "" + firebase_regid, "" + latitude, "" + longitude, "AGENT");

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


    private void accountNotVerified() {
        final Dialog dialogSts = new Dialog(LoginAct.this, R.style.DialogSlideAnim);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.signupsuccess);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView yes_tv = dialogSts.findViewById(R.id.yes_tv);
        TextView body_tv = dialogSts.findViewById(R.id.body_tv);
        body_tv.setText(""+getResources().getString(R.string.notapprove));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();

                //  setStartTime();


            }
        });


        dialogSts.show();
    }



    private void callapi(String input_mobile, String input_pass_str, String firebase_regid, String lat, String lon, String user) {
//http://testing.bigclicki.com/webservice/loginapp?email=0&password=0
        Log.e("loginCall >", " > FIRST");
        if (ac_dialog != null) {
            ac_dialog.show();
        }

        Call<ResponseBody> call = ApiClient.getApiInterface().loginCall(input_mobile, input_pass_str, firebase_regid, lat, lon, user,login_sts);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (ac_dialog != null) {
                    ac_dialog.dismiss();
                }

                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject object = new JSONObject(responseData);
                        Log.e("loginCall >", " >" + responseData);
                        if (object.getString("status").equals("1")) {

                            mySession.setlogindata(responseData);
                            mySession.signinusers(true);
                            setStartTime();
                          //  Intent i = new Intent(LoginAct.this, MainTabActivity.class);
                            Intent i = new Intent(LoginAct.this, MainTabActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);


                        } else {
                            if (object.getString("result").equalsIgnoreCase("user already logged in")){
                                alreadyLogin();
                            }
                            else if (object.getString("result").equalsIgnoreCase("Account is not approved by admin")){
                                accountNotVerified();
                            }
                            else {
                                login_sts="no";
                                Toast.makeText(LoginAct.this, getResources().getString(R.string.invalidcredential), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (IOException e) {
                        login_sts="no";
                        e.printStackTrace();
                    } catch (JSONException e) {
                        login_sts="no";
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                t.printStackTrace();
                if (ac_dialog != null) {
                    ac_dialog.dismiss();
                }
                login_sts="no";
                Log.e("TAG", t.toString());
            }
        });
    }
    private void setStartTime() {

        AlarmManager alarmMgr = (AlarmManager) (LoginAct.this).getSystemService(Context.ALARM_SERVICE);
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

}
