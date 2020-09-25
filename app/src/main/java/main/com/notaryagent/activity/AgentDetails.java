package main.com.notaryagent.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.notaryagent.R;
import main.com.notaryagent.app.Config;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.CountryBean;
import main.com.notaryagent.constant.MultipartUtility;
import main.com.notaryagent.constant.MySession;

public class AgentDetails extends AppCompatActivity {
    private String user_log_data = "", user_id = "";
    private MySession mySession;
    private RelativeLayout exit_app_but;
    private EditText numberofyear,first_name, last_name, mobile_et, email_id, state_licenced;
    private RadioButton firsttimeconnect, iamdirectpartenr, conthraggretr, yesnotary, nonotary, nnacertifiedyes, nnacertifiedno, eoinsuranceyes, eoinsuranceno, prodlicyes, prodlicno, startimeyes, startimeno;
    private Spinner country_spn, state_spn, city_spn;
    private String state_id = "", country_id = "", city_id = "";
    ArrayList<CountryBean> countryBeanArrayList, statelistbean, citylistbean;
    private ACProgressCustom ac_dialog;
    private CountryListAdapter countryListAdapter;
    private ProgressBar progresbar;
    private RadioGroup workwithnotaryrdgrp,prodliclay,currentlynotary,startimyesno,nnacertified,eoinsurance;
    private String insurance_producer_licenses = "",startstatus="", e_and_o_ensurance = "", signup_mobility_status = "", curently_mobile_notary = "", nna_certified_with_back_check = "",approved_start_immediately="";
private Button update_but;

    String company_name_str="",insproducerlic_str="",eoinsurance_str="",nnacertified_str="",curentnotary_str="",work_with_notary_status="",email_str="",numberofyear_str="",mobile_str="",first_name_str="",last_name_str="",state_licenced_str="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_details);
        mySession = new MySession(this);
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();

        user_log_data = mySession.getKeyAlldata();
        idinti();
        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");
                    country_id = jsonObject1.getString("country");
                    state_id = jsonObject1.getString("state");
                    city_id = jsonObject1.getString("city");
                    numberofyear_str = jsonObject1.getString("number_of_years");
                    numberofyear.setText(""+numberofyear_str);
                    email_id.setText("" + jsonObject1.getString("email"));
                    first_name.setText("" + jsonObject1.getString("first_name"));
                    last_name.setText("" + jsonObject1.getString("last_name"));
                    mobile_et.setText("" + jsonObject1.getString("mobile"));
                    state_licenced.setText("" + jsonObject1.getString("state_licenced"));
                    //    nu.setText(""+jsonObject1.getString("number_of_years"));
                    signup_mobility_status = jsonObject1.getString("signup_mobility_status");
                    curently_mobile_notary = jsonObject1.getString("curently_mobile_notary");
                    nna_certified_with_back_check = jsonObject1.getString("nna_certified_with_back_check");
                    e_and_o_ensurance = jsonObject1.getString("e_and_o_ensurance");
                    insurance_producer_licenses = jsonObject1.getString("insurance_producer_licenses");
                    approved_start_immediately = jsonObject1.getString("approved_start_immediately");
                    company_name_str = jsonObject1.getString("company_name");

                    if (approved_start_immediately != null && approved_start_immediately.equalsIgnoreCase("Yes")) {
                        startimeyes.setChecked(true);
                    } else if (approved_start_immediately != null && approved_start_immediately.equalsIgnoreCase("No")) {
                        startimeno.setChecked(true);
                    }

                    if (insurance_producer_licenses != null && insurance_producer_licenses.equalsIgnoreCase("Yes")) {
                        prodlicyes.setChecked(true);
                    } else if (insurance_producer_licenses != null && insurance_producer_licenses.equalsIgnoreCase("No")) {
                        prodlicno.setChecked(true);
                    }

                    if (e_and_o_ensurance != null && e_and_o_ensurance.trim().equalsIgnoreCase("Yes")) {
                        eoinsuranceyes.setChecked(true);
                    } else if (e_and_o_ensurance != null && e_and_o_ensurance.trim().equalsIgnoreCase("No")) {
                        eoinsuranceno.setChecked(true);
                    }

                    if (nna_certified_with_back_check != null && nna_certified_with_back_check.equalsIgnoreCase("Yes")) {
                        nnacertifiedyes.setChecked(true);
                    } else if (nna_certified_with_back_check != null && nna_certified_with_back_check.equalsIgnoreCase("No")) {
                        nnacertifiedno.setChecked(true);
                    }

                    if (curently_mobile_notary != null && curently_mobile_notary.equalsIgnoreCase("Yes")) {
                        yesnotary.setChecked(true);
                        numberofyear.setVisibility(View.VISIBLE);
                    } else if (curently_mobile_notary != null && curently_mobile_notary.equalsIgnoreCase("No")) {
                        nonotary.setChecked(true);
                    }

                    if (curently_mobile_notary != null && curently_mobile_notary.equalsIgnoreCase("Yes")) {
                        yesnotary.setChecked(true);
                    } else if (curently_mobile_notary != null && curently_mobile_notary.equalsIgnoreCase("No")) {
                        nonotary.setChecked(true);
                    }

                    if (signup_mobility_status != null && signup_mobility_status.equalsIgnoreCase(getResources().getString(R.string.noiamfirst))) {
                        firsttimeconnect.setChecked(true);
                    } else if (signup_mobility_status != null && signup_mobility_status.equalsIgnoreCase(getResources().getString(R.string.yesiampartner))) {
                        iamdirectpartenr.setChecked(true);
                    } else if (signup_mobility_status != null && signup_mobility_status.equalsIgnoreCase(getResources().getString(R.string.yesconagg))) {
                        conthraggretr.setChecked(true);
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        new GetCountryList().execute();
        if (country_id != null && !country_id.equalsIgnoreCase("")) {
            new GetStateList().execute(country_id);
        }


        clickevent();
    }

    private void clickevent() {
        update_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_str = email_id.getText().toString();

                numberofyear_str = numberofyear.getText().toString();
                mobile_str = mobile_et.getText().toString();
                first_name_str = first_name.getText().toString();
                last_name_str = last_name.getText().toString();

                state_licenced_str = state_licenced.getText().toString();

               /* if (company_name_str == null || company_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentSignup.this, getResources().getString(R.string.plsentercomname), Toast.LENGTH_LONG).show();
                } else*/
                if (first_name_str == null || first_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentDetails.this, getResources().getString(R.string.plsenfrst), Toast.LENGTH_LONG).show();

                } else if (last_name_str == null || last_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentDetails.this, getResources().getString(R.string.plsenlst), Toast.LENGTH_LONG).show();

                } else if (mobile_str == null || mobile_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentDetails.this, getResources().getString(R.string.plsmobilestr), Toast.LENGTH_LONG).show();
                }
                else if (mobile_str.length()<10) {
                    Toast.makeText(AgentDetails.this,getResources().getString(R.string.plsentervalidnumber),Toast.LENGTH_LONG).show();
                }

                else if (email_str == null || email_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentDetails.this, getResources().getString(R.string.pleaseenteremailid), Toast.LENGTH_LONG).show();
                }
                /*else if (state_licenced_str == null || state_licenced_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentSignup.this, getResources().getString(R.string.statelicenced), Toast.LENGTH_LONG).show();
                }*/ else if (country_id == null || country_id.equalsIgnoreCase("")) {
                    Toast.makeText(AgentDetails.this, getResources().getString(R.string.select_country), Toast.LENGTH_LONG).show();
                } else if (state_id == null || state_id.equalsIgnoreCase("")) {
                    Toast.makeText(AgentDetails.this, getResources().getString(R.string.selectstate), Toast.LENGTH_LONG).show();
                } /*else if (city_str == null || city_str.equalsIgnoreCase("")) {
                    Toast.makeText(AgentSignup.this, getResources().getString(R.string.selectcity), Toast.LENGTH_LONG).show();
                }*/ /*else if (work_with_notary_status == null || work_with_notary_status.equalsIgnoreCase("")) {

                }*/  /*else if (startstatus == null || startstatus.equalsIgnoreCase("")) {

                } else if (curentnotary_str == null || curentnotary_str.equalsIgnoreCase("")) {

                }*/  else {
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
                        new JsonUpdateAsc().execute();


                }
            }
        });
    }

    private void idinti() {
        prodliclay = findViewById(R.id.prodliclay);
        eoinsurance = findViewById(R.id.eoinsurance);
        nnacertified = findViewById(R.id.nnacertified);
        startimyesno = findViewById(R.id.startimyesno);
        currentlynotary = findViewById(R.id.currentlynotary);
        workwithnotaryrdgrp = findViewById(R.id.workwithnotaryrdgrp);
        numberofyear = findViewById(R.id.numberofyear);
        update_but = findViewById(R.id.update_but);
        progresbar = findViewById(R.id.progresbar);
        exit_app_but = findViewById(R.id.exit_app_but);
        email_id = findViewById(R.id.email_id);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        mobile_et = findViewById(R.id.mobile_et);
        state_licenced = findViewById(R.id.state_licenced);
        iamdirectpartenr = findViewById(R.id.iamdirectpartenr);
        firsttimeconnect = findViewById(R.id.firsttimeconnect);
        conthraggretr = findViewById(R.id.conthraggretr);
        country_spn = findViewById(R.id.country_spn);
        state_spn = findViewById(R.id.state_spn);
        city_spn = findViewById(R.id.city_spn);
        yesnotary = findViewById(R.id.yesnotary);
        nonotary = findViewById(R.id.nonotary);
        nnacertifiedyes = findViewById(R.id.nnacertifiedyes);
        nnacertifiedno = findViewById(R.id.nnacertifiedno);
        eoinsuranceyes = findViewById(R.id.eoinsuranceyes);
        eoinsuranceno = findViewById(R.id.eoinsuranceno);
        prodlicyes = findViewById(R.id.prodlicyes);
        prodlicno = findViewById(R.id.prodlicno);
        startimeyes = findViewById(R.id.startimeyes);
        startimeno = findViewById(R.id.startimeno);

        country_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (countryBeanArrayList != null && !countryBeanArrayList.isEmpty()) {
                }
                if (countryBeanArrayList.get(position).getId() == null || countryBeanArrayList.get(position).getId().equalsIgnoreCase("0")) {
                    // country_id = "";
                } else {
                    country_id = countryBeanArrayList.get(position).getId();
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
                        state_id = statelistbean.get(position).getId();
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
                    if (citylistbean.get(position).getId() == null || citylistbean.get(position).getId().equalsIgnoreCase("0")) {

                    } else {
                        city_id = citylistbean.get(position).getId();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

                        countryListAdapter = new CountryListAdapter(AgentDetails.this, countryBeanArrayList);
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
                    int state_pos = 0;
                    if (message.equalsIgnoreCase("successful")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            CountryBean countryBean = new CountryBean();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            countryBean.setId(jsonObject1.getString("id"));
                            countryBean.setCountry(jsonObject1.getString("name"));
                            statelistbean.add(countryBean);
                            if (state_id != null && state_id.equalsIgnoreCase(jsonObject1.getString("id"))) {
                                state_pos = i;
                                Log.e("state_pos >>"," .. "+state_pos);
                            }
                        }

                        countryListAdapter = new CountryListAdapter(AgentDetails.this, statelistbean);
                        state_spn.setAdapter(countryListAdapter);
                        state_spn.setSelection(state_pos);
                        if (state_id != null && !state_id.equalsIgnoreCase("")) {
                            new GetCityList().execute(state_id);
                        }

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
            citylistbean = new ArrayList<>();
            CountryBean countryListBean = new CountryBean();
            countryListBean.setId("0");
            countryListBean.setCountry("City");
            citylistbean.add(countryListBean);
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
                Log.e("strings state d", " > " + strings[0]);
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
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("message");
                    if (message.equalsIgnoreCase("successful")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        int city_pos = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            CountryBean countryBean = new CountryBean();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            countryBean.setId(jsonObject1.getString("id"));
                            countryBean.setCountry(jsonObject1.getString("name"));
                            citylistbean.add(countryBean);
                            if (city_id != null && city_id.equalsIgnoreCase(jsonObject1.getString("id"))) {
                                city_pos = i;
                                Log.e("city_pos >>"," .. "+city_pos);
                            }
                        }

                        countryListAdapter = new CountryListAdapter(AgentDetails.this, citylistbean);
                        city_spn.setAdapter(countryListAdapter);
                        countryListAdapter.notifyDataSetChanged();
                        city_spn.setSelection(city_pos);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }


    public class JsonUpdateAsc extends AsyncTask<String, String, String> {
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
            String requestURL = BaseUrl.baseurl + "update_profile?";
            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addFormField("user_id", user_id);
                multipart.addFormField("first_name", first_name_str);
                multipart.addFormField("last_name", last_name_str);
                multipart.addFormField("email", email_str);
                multipart.addFormField("mobile", mobile_str);

                multipart.addFormField("lang_id", "");
               // multipart.addFormField("lat", "" + latitude);
              //  multipart.addFormField("lon", "" + longitude);
               // multipart.addFormField("register_id", firebase_regid);
                multipart.addFormField("type", "AGENT");
                multipart.addFormField("car_type_id", "10");
Log.e("insproducerlic_str >> "," .. "+insproducerlic_str);
                multipart.addFormField("state_licenced", state_licenced_str);
                multipart.addFormField("signup_mobility_status", work_with_notary_status);
                multipart.addFormField("curently_mobile_notary", curentnotary_str);
                multipart.addFormField("number_of_years", numberofyear_str);
                multipart.addFormField("approved_start_immediately", startstatus);
                multipart.addFormField("company_name", company_name_str);
                multipart.addFormField("country", country_id);
                multipart.addFormField("state", state_id);
                multipart.addFormField("city", city_id);
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
                        Toast.makeText(AgentDetails.this,""+getResources().getString(R.string.profileupdatesucces),Toast.LENGTH_LONG).show();



                    }
                    else {
                        Toast.makeText(AgentDetails.this,""+jsonObject.getString("result"),Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }


    }


}
