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
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.StatementEvent;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.notaryagent.R;
import main.com.notaryagent.app.Config;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.CountryBean;
import main.com.notaryagent.constant.MultipartUtility;
import main.com.notaryagent.constant.MyReceiver;
import main.com.notaryagent.constant.MySession;
import main.com.notaryagent.service.GPSTracker;
import main.com.notaryagent.tabactivity.MainTabActivity;

public class AgentSignup extends AppCompatActivity {
    private CountryListAdapter countryListAdapter;
    private ACProgressCustom ac_dialog;
    private MySession mySession;
    ArrayList<CountryBean> countryBeanArrayList, statelistbean, citylistbean;
    private Spinner city_spn, country_spn, state_spn;
    private ProgressBar progresbar;
    private RadioGroup prodliclay,workwithnotaryrdgrp, currentlynotary, startimyesno,nnacertified,eoinsurance;
    private RadioButton firsttimeconnect, iamdirectpartenr, conthraggretr;
    private RadioButton yesnotary, nonotary;
    private RadioButton startimeyes, startimeno,prodlicyes,prodlicno;
    private EditText numberofyear;
    private CheckBox termscondition;
    private EditText state_licenced, email_id, password_et, first_name, mobile_et, last_name, company_name;
    private String numberofyear_str = "",nnacertified_str="",eoinsurance_str="", state_licenced_str = "", email_str = "", password_str = "", first_name_str = "", mobile_str = "", last_name_str = "", company_name_str = "";
    private String insproducerlic_str="No",work_with_notary_status = "", curentnotary_str = "", startstatus = "", firebase_regid = "", country_str = "", state_str = "", city_str = "";

    private Button register;
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 0; // in Milliseconds
    LocationManager locationManager;
    Location location;
    private double latitude = 0, longitude = 0;
    GPSTracker gpsTracker;
    private TextView termstext;
    private RelativeLayout backbut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_signup);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(AgentSignup.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AgentSignup.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        mySession = new MySession(this);
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();
        checkGps();
        idinti();
        new GetCountryList().execute();
        clickevent();
    }

    private void checkGps() {
        gpsTracker = new GPSTracker(AgentSignup.this);
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
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                firebase_regid = pref.getString("regId", null);
                email_str = email_id.getText().toString();
                password_str = password_et.getText().toString();
                numberofyear_str = numberofyear.getText().toString();
                mobile_str = mobile_et.getText().toString();
                first_name_str = first_name.getText().toString();
                last_name_str = last_name.getText().toString();
                company_name_str = company_name.getText().toString();
                state_licenced_str = state_licenced.getText().toString();

               /* if (company_name_str == null || company_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentSignup.this, getResources().getString(R.string.plsentercomname), Toast.LENGTH_LONG).show();
                } else*/
                if (first_name_str == null || first_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentSignup.this, getResources().getString(R.string.plsenfrst), Toast.LENGTH_LONG).show();

                } else if (last_name_str == null || last_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentSignup.this, getResources().getString(R.string.plsenlst), Toast.LENGTH_LONG).show();

                } else if (mobile_str == null || mobile_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentSignup.this, getResources().getString(R.string.plsmobilestr), Toast.LENGTH_LONG).show();
                }
                else if (mobile_str.length()<10) {
                    Toast.makeText(AgentSignup.this,getResources().getString(R.string.plsentervalidnumber),Toast.LENGTH_LONG).show();
                }

                else if (email_str == null || email_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentSignup.this, getResources().getString(R.string.pleaseenteremailid), Toast.LENGTH_LONG).show();
                } else if (password_str == null || password_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentSignup.this, getResources().getString(R.string.plsentpass), Toast.LENGTH_LONG).show();
                }
                /*else if (state_licenced_str == null || state_licenced_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentSignup.this, getResources().getString(R.string.statelicenced), Toast.LENGTH_LONG).show();
                }*/ else if (country_str == null || country_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentSignup.this, getResources().getString(R.string.select_country), Toast.LENGTH_LONG).show();
                } else if (state_str == null || state_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentSignup.this, getResources().getString(R.string.selectstate), Toast.LENGTH_LONG).show();
                } /*else if (city_str == null || city_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentSignup.this, getResources().getString(R.string.selectcity), Toast.LENGTH_LONG).show();
                }*/ /*else if (work_with_notary_status == null || work_with_notary_status.equalsIgnoreCase("")) {

                }*/  /*else if (startstatus == null || startstatus.equalsIgnoreCase("")) {

                } else if (curentnotary_str == null || curentnotary_str.equalsIgnoreCase("")) {

                }*/  else {
                    if (termscondition.isChecked()) {
                        int workwithrdsts = workwithnotaryrdgrp.getCheckedRadioButtonId();
                        if (workwithrdsts == R.id.firsttimeconnect) {
                            work_with_notary_status = firsttimeconnect.getText().toString();
                        } else if (workwithrdsts == R.id.iamdirectpartenr) {
                            work_with_notary_status = iamdirectpartenr.getText().toString();

                        } else if (workwithrdsts == R.id.conthraggretr) {
                            work_with_notary_status = conthraggretr.getText().toString();

                        }
                        int curnotary = currentlynotary.getCheckedRadioButtonId();
                        if (curnotary == R.id.yesnotary) {
                            curentnotary_str = yesnotary.getText().toString();

                        } else if (curnotary == R.id.nonotary) {
                            curentnotary_str = nonotary.getText().toString();
                        }
                        int startimmediet = startimyesno.getCheckedRadioButtonId();
                        if (startimmediet == R.id.startimeyes) {
                            startstatus = startimeyes.getText().toString();
                        } else if (startimmediet == R.id.startimeno) {
                            startstatus = startimeno.getText().toString();
                        }

                        int nnacertified_i = nnacertified.getCheckedRadioButtonId();
                        if (nnacertified_i == R.id.nnacertifiedyes) {
                            nnacertified_str = "Yes";

                        } else if (nnacertified_i == R.id.nnacertifiedno) {
                            nnacertified_str = "No";
                        }

                        int eoins = eoinsurance.getCheckedRadioButtonId();
                        if (eoins == R.id.eoinsuranceyes) {
                            eoinsurance_str = "Yes";

                        } else if (eoins == R.id.eoinsuranceno) {
                            eoinsurance_str = "No";
                        }

                        int insproducerlic = prodliclay.getCheckedRadioButtonId();
                        if (insproducerlic == R.id.prodlicyes) {
                            insproducerlic_str = "Yes";

                        } else if (insproducerlic == R.id.prodlicno) {
                            insproducerlic_str = "No";
                        }
/*
                        else if (numberofyear_str == null || numberofyear_str.equalsIgnoreCase("")) {
                            Toast.makeText(AgentSignup.this, getResources().getString(R.string.enternumyears), Toast.LENGTH_LONG).show();

                        }
*/
                        Log.e("startstatus ..", " >>" + startstatus + " , " + curentnotary_str + " , " + work_with_notary_status);
                        new JsonSignupAsc().execute();
                    } else {
                        Toast.makeText(AgentSignup.this, getResources().getString(R.string.plsaccepterms), Toast.LENGTH_LONG).show();

                    }

                }
            }
        });


        termstext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AgentSignup.this, TermsConditionsAct.class);
                startActivity(i);
            }
        });
        backbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

    }


    public class JsonSignupAsc extends AsyncTask<String, String, String> {
        String Jsondata;
        private boolean checkdata = false;
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                //  prgressbar.setVisibility(View.VISIBLE);
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
            String requestURL = BaseUrl.baseurl + "signup?";
            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addFormField("first_name", first_name_str);
                multipart.addFormField("last_name", last_name_str);
                multipart.addFormField("email", email_str);
                multipart.addFormField("mobile", mobile_str);
                multipart.addFormField("password", password_str);
                multipart.addFormField("lang_id", "");
                multipart.addFormField("lat", "" + latitude);
                multipart.addFormField("lon", "" + longitude);
                multipart.addFormField("register_id", firebase_regid);
                multipart.addFormField("type", "AGENT");
                multipart.addFormField("car_type_id", "10");

                multipart.addFormField("state_licenced", state_licenced_str);
                multipart.addFormField("signup_mobility_status", work_with_notary_status);
                multipart.addFormField("curently_mobile_notary", curentnotary_str);
                multipart.addFormField("number_of_years", numberofyear_str);
                multipart.addFormField("approved_start_immediately", startstatus);
                multipart.addFormField("company_name", company_name_str);
                multipart.addFormField("country", country_str);
                multipart.addFormField("state", state_str);
                multipart.addFormField("city", city_str);
                multipart.addFormField("insurance_producer_licenses", insproducerlic_str);

                multipart.addFormField("nna_certified_with_back_check", nnacertified_str);
                multipart.addFormField("e_and_o_ensurance", eoinsurance_str);


                //  multipart.addFormField("car_color", car_col_str);


                //  multipart.addFormField("car_type_id", cartype_id_str);

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
            //   prgressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject  = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")){
                        mySession.setlogindata(result);
                        mySession.signinusers(true);
                        mySession.onlineuser(false);
                        accountCreated();


                    }
                    else {
                        Toast.makeText(AgentSignup.this,""+jsonObject.getString("result"),Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }


    }
    private void accountCreated() {
        final Dialog dialogSts = new Dialog(AgentSignup.this, R.style.DialogSlideAnim);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.signupsuccess);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView yes_tv = dialogSts.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();

                //  setStartTime();

                Intent i = new Intent(AgentSignup.this, LoginAct.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });


        dialogSts.show();
    }

    private void setStartTime() {

        AlarmManager alarmMgr = (AlarmManager) (AgentSignup.this).getSystemService(Context.ALARM_SERVICE);
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


    private void idinti() {

        prodliclay = findViewById(R.id.prodliclay);
        prodlicno = findViewById(R.id.prodlicno);
        prodlicyes = findViewById(R.id.prodlicyes);
        eoinsurance = findViewById(R.id.eoinsurance);
        nnacertified = findViewById(R.id.nnacertified);
        backbut = findViewById(R.id.backbut);
        termstext = findViewById(R.id.termstext);
        register = findViewById(R.id.register);

        termscondition = findViewById(R.id.termscondition);

        String styledText = getResources().getString(R.string.termsfirst) + "<font color='blue'> Terms of agreement </font>" + getResources().getString(R.string.termsthird);
        termstext.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);


        numberofyear = findViewById(R.id.numberofyear);
        company_name = findViewById(R.id.company_name);
        last_name = findViewById(R.id.last_name);
        mobile_et = findViewById(R.id.mobile_et);
        first_name = findViewById(R.id.first_name);
        email_id = findViewById(R.id.email_id);
        password_et = findViewById(R.id.password_et);
        state_licenced = findViewById(R.id.state_licenced);

        startimeno = findViewById(R.id.startimeno);
        startimeyes = findViewById(R.id.startimeyes);
        startimyesno = findViewById(R.id.startimyesno);
        yesnotary = findViewById(R.id.yesnotary);
        nonotary = findViewById(R.id.nonotary);
        currentlynotary = findViewById(R.id.currentlynotary);
        iamdirectpartenr = findViewById(R.id.iamdirectpartenr);
        conthraggretr = findViewById(R.id.conthraggretr);
        firsttimeconnect = findViewById(R.id.firsttimeconnect);
        workwithnotaryrdgrp = findViewById(R.id.workwithnotaryrdgrp);
        progresbar = findViewById(R.id.progresbar);
        city_spn = findViewById(R.id.city_spn);
        state_spn = findViewById(R.id.state_spn);
        country_spn = findViewById(R.id.country_spn);
        citylistbean = new ArrayList<>();
        CountryBean countryListBean = new CountryBean();
        countryListBean.setId("0");
        countryListBean.setCountry("City");
        citylistbean.add(countryListBean);
        countryListAdapter = new CountryListAdapter(AgentSignup.this, citylistbean);
        city_spn.setAdapter(countryListAdapter);

        yesnotary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    numberofyear.setVisibility(View.VISIBLE);
                }

            }
        });
        nonotary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    numberofyear.setVisibility(View.GONE);
                }

            }
        });
        country_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (countryBeanArrayList != null && !countryBeanArrayList.isEmpty()) {
                }
                if (countryBeanArrayList.get(position).getId() == null || countryBeanArrayList.get(position).getId().equalsIgnoreCase("0")) {
                    country_str = "";
                } else {
                    country_str = countryBeanArrayList.get(position).getId();
                    new GetStateList().execute(countryBeanArrayList.get(position).getId());


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        state_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (statelistbean != null && !statelistbean.isEmpty()) {
                    if (statelistbean.get(position).getId() == null || statelistbean.get(position).getId().equalsIgnoreCase("0")) {

                    } else {
                        state_str = statelistbean.get(position).getId();
                        new GetCityList().execute(statelistbean.get(position).getId());
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        city_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (citylistbean != null && !citylistbean.isEmpty()) {
                   if (citylistbean.size()<position){

                   }
                   else {
                       if (citylistbean.get(position).getId() == null || citylistbean.get(position).getId().equalsIgnoreCase("0")) {

                       } else {
                           city_str = citylistbean.get(position).getId();
                       }
                   }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public class CountryListAdapter extends BaseAdapter {
        Context context;

        LayoutInflater inflter;
        private ArrayList<CountryBean> values;

        public CountryListAdapter(Context applicationContext, ArrayList<CountryBean> values) {
            this.context = applicationContext;
            this.values = values;

            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {

            return values == null ? 0 : values.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.spn_head_lay, null);

            TextView names = (TextView) view.findViewById(R.id.heading);
            //  TextView countryname = (TextView) view.findViewById(R.id.countryname);


            names.setText(values.get(i).getCountry());


            return view;
        }
    }

    private class GetCountryList extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // prgressbar.setVisibility(View.VISIBLE);
            if (ac_dialog != null) {
                ac_dialog.show();
            }

            countryBeanArrayList = new ArrayList<>();


           /* CountryBean countryListBean = new CountryBean();
            countryListBean.setId("0");
            countryListBean.setCountry("Country");
            countryListBean.setCurrency("");
            countryBeanArrayList.add(countryListBean);
*/
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/country_list
            try {
                String postReceiverUrl = BaseUrl.baseurl + "country_list";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();

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
                Log.e("Json Country Response", ">>>>>>>>>>>>" + response);
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
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            CountryBean countryBean = new CountryBean();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            countryBean.setId(jsonObject1.getString("id"));
                            countryBean.setCountry(jsonObject1.getString("name"));
                            countryBean.setCurrency("");
                            countryBeanArrayList.add(countryBean);
                        }

                        countryListAdapter = new CountryListAdapter(AgentSignup.this, countryBeanArrayList);
                        country_spn.setAdapter(countryListAdapter);

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private class GetStateList extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progresbar.setVisibility(View.VISIBLE);
            statelistbean = new ArrayList<>();
            CountryBean countryListBean = new CountryBean();
            countryListBean.setId("0");
            countryListBean.setCountry("State");

            statelistbean.add(countryListBean);
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//https://myngrewards.com/wp-content/plugins/webservice/state_lists.php?country_id=101
            try {
                String postReceiverUrl = BaseUrl.baseurl + "state_list?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                Log.e("strings[0] >> ", " >> " + strings[0]);
                params.put("country_id", strings[0]);
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
                Log.e("Json Country Response", ">>>>>>>>>>>>" + response);
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
                    String message = jsonObject.getString("message");
                    if (message.equalsIgnoreCase("successful")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            CountryBean countryBean = new CountryBean();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            countryBean.setId(jsonObject1.getString("id"));
                            countryBean.setCountry(jsonObject1.getString("name"));
                            statelistbean.add(countryBean);
                        }

                        countryListAdapter = new CountryListAdapter(AgentSignup.this, statelistbean);
                        state_spn.setAdapter(countryListAdapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private class GetCityList extends AsyncTask<String, String, String> {
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
//https://myngrewards.com/wp-content/plugins/webservice/city_list.php?state_id=21
            try {
                String postReceiverUrl = BaseUrl.baseurl + "city_list?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("state_id", strings[0]);
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
                Log.e("Json CIty Response", ">>>>>>>>>>>>" + response);
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
                citylistbean = new ArrayList<>();
                CountryBean countryListBean = new CountryBean();
                countryListBean.setId("0");
                countryListBean.setCountry("City");
                citylistbean.add(countryListBean);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("message");
                    if (message.equalsIgnoreCase("successful")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            CountryBean countryBean = new CountryBean();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            countryBean.setId(jsonObject1.getString("id"));
                            countryBean.setCountry(jsonObject1.getString("name"));
                            citylistbean.add(countryBean);
                        }

                        countryListAdapter = new CountryListAdapter(AgentSignup.this, citylistbean);
                        city_spn.setAdapter(countryListAdapter);
                        countryListAdapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

}
