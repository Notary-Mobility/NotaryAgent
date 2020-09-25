package main.com.notaryagent.draweractivity;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;
import com.stripe.android.model.BankAccount;

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
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.notaryagent.R;
import main.com.notaryagent.activity.AboutUsAct;
import main.com.notaryagent.activity.AddBankAccountAct;
import main.com.notaryagent.activity.EmergencyActivity;
import main.com.notaryagent.activity.InviteEarnAct;
import main.com.notaryagent.activity.LoginAct;
import main.com.notaryagent.activity.MySignings;
import main.com.notaryagent.activity.SettingActivity;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.MySession;
import main.com.notaryagent.paymentclasses.WalletActivity;
import main.com.notaryagent.tabactivity.HomeActivity;
import main.com.notaryagent.tabactivity.ProfileActivity;


public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private DrawerLayout drawer_layout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationview;
    boolean exit = false;
    MySession mySession;
    private LinearLayout paymentmethod,emergencylay,invitelay, bankaccount, settinglay, aboutus, logout, homelay, myprofile, schedulesigning, ridehistory;
    private String user_log_data = "", user_id = "";
    private TextView user_name, user_mobile;
    private CircleImageView user_imguser_img;
    //private ToggleButton toggle_driver_sts;
    ACProgressCustom ac_dialog;
    private String my_online_sts = "";
    public static String trip_count = "",amount="0.00", promo_code = "", today_ear_amt = "", week_ear_amt = "", week_trip_count = "";
    private Switch switch_driver_sts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);
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
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        idinit();
        adddrawer();
        clickevents();

    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetUserProfile().execute();


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
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        String image_url = jsonObject1.getString("image");
                        user_id = jsonObject1.getString("id");
                        Log.e("COME TRUE ", " >." + jsonObject1.getString("first_name"));
                        user_name.setText("" + jsonObject1.getString("first_name"));
                        user_mobile.setText("" + jsonObject1.getString("mobile"));
                        my_online_sts = jsonObject1.getString("online_status");
                        promo_code = jsonObject1.getString("promo_code");
                        amount = jsonObject1.getString("amount");
                        String online_status = jsonObject1.getString("online_status");
                       /* if (online_status.equalsIgnoreCase("ONLINE")) {
                            toggle_driver_sts.setChecked(true);
                        } else {
                            toggle_driver_sts.setChecked(false);

                        }*/
                        if (online_status.equalsIgnoreCase("ONLINE")) {
                            switch_driver_sts.setChecked(true);
                        } else {
                            switch_driver_sts.setChecked(false);

                        }
                        if (image_url == null || image_url.equalsIgnoreCase("") || image_url.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                        } else {
                            Picasso.with(BaseActivity.this).load(image_url).placeholder(R.drawable.ic_user_prof).into(user_imguser_img);

                        }
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("today_earn");
                        if (jsonObject2.getString("return").equalsIgnoreCase("success")) {
                            today_ear_amt = jsonObject2.getString("amount");
                            trip_count = jsonObject2.getString("trip");
                        }

                        JSONObject jsonweek_earn = jsonObject1.getJSONObject("week_earn");
                        if (jsonObject2.getString("return").equalsIgnoreCase("success")) {
                            week_ear_amt = jsonObject2.getString("amount");
                            week_trip_count = jsonObject2.getString("trip");
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private void clickevents() {
        homelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer_layout != null) {
                    drawer_layout.closeDrawers();
                }

            }
        });
        invitelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent i = new Intent(BaseActivity.this, InviteEarnAct.class);
              startActivity(i);
            }
        });
        schedulesigning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ridehistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, MySignings.class);
                startActivity(i);
            }
        });
        paymentmethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, WalletActivity.class);
                startActivity(i);
            }
        });
        emergencylay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, EmergencyActivity.class);
                startActivity(i);
            }
        });
        settinglay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, SettingActivity.class);
                startActivity(i);
            }
        });
        myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, AboutUsAct.class);
                startActivity(i);
            }
        });
        bankaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, AddBankAccountAct.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySession.signinusers(false);
                Intent i = new Intent(BaseActivity.this, LoginAct.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });

    }

    private void idinit() {
        paymentmethod = findViewById(R.id.paymentmethod);
        emergencylay = findViewById(R.id.emergencylay);
        switch_driver_sts = findViewById(R.id.switch_driver_sts);
        switch_driver_sts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Log.e("COME ","CHEKCKED");
                    my_online_sts = "ONLINE";
                    new ChgStatus().execute();
                }
                else
                {
                    Log.e("COME ","ELSE");
                    my_online_sts = "OFFLINE";
                    new ChgStatus().execute();
                }

            }
        });


        invitelay = findViewById(R.id.invitelay);
        bankaccount = findViewById(R.id.bankaccount);
        settinglay = findViewById(R.id.settinglay);
        //toggle_driver_sts = findViewById(R.id.toggle_driver_sts);
        aboutus = findViewById(R.id.aboutus);
        user_name = findViewById(R.id.user_name);
        user_imguser_img = findViewById(R.id.user_imguser_img);
        user_mobile = findViewById(R.id.user_mobile);


        ridehistory = findViewById(R.id.ridehistory);
        schedulesigning = findViewById(R.id.schedulesigning);

        logout = findViewById(R.id.logout);
        homelay = findViewById(R.id.homelay);
        myprofile = findViewById(R.id.myprofile);
/*
        toggle_driver_sts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    my_online_sts = "ONLINE";
                    new ChgStatus().execute();
                } else {
                    my_online_sts = "OFFLINE";
                    new ChgStatus().execute();

                }

            }
        });
*/



    }


    private void adddrawer() {

        setSupportActionBar(toolbar);
        navigationview = (NavigationView) findViewById(R.id.navigationview);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        drawer_layout.setDrawerListener(actionBarDrawerToggle);
// toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);

     /*   View header = View.inflate(context, R.layout.headerlayout, null);
        navigationview.addHeaderView(header);*/
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, getResources().getString(R.string.pressagain),
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private class ChgStatus extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ac_dialog!=null){
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
//http://mobileappdevelop.co/NAXCAN/webservice/update_online_status?status=OFFLINE&user_id=1&type=DRIVER
            try {
                String postReceiverUrl = BaseUrl.baseurl + "update_online_status?";
                Log.e("ONLINE STS"," .. "+postReceiverUrl+"user_id="+user_id+"&status="+my_online_sts+"&type=AGENT");
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
                ac_dialog.dismiss();
            }
            Log.e("ONLINE STATUS >>"," >> "+result);
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


}



/*
Complete the feedback of given

Change the status of screen a
*/