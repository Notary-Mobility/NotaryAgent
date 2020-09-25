package main.com.notaryagent.paymentclasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

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
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.notaryagent.R;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.MySession;


public class AddCreditCardAct extends AppCompatActivity {

    private EditText cardname, cardnumber, security_code;
    private String cardname_str = "", cardnumber_str = "", security_code_str = "";
    private TextView addcardbut;
    private RelativeLayout backlay;
    ArrayList<String> listOfPattern;
    ArrayList<String> modellist;
    ArrayList<String> datelist;
    private Spinner yearspinner, datespinner;
    private String expiryyear_str = "", date_str = "", user_id = "";
    private BasicCustomAdp basicCustomAdp;
    private ProgressBar progressBar;
    private MySession mySession;
    private boolean status_match = false;
    int month, year_int;
    private String token_id = "", cust_id = "", email_str = "", accountid = "";

    CreditCardFormatTextWatcher tv;
    MySavedCardInfo mySavedCardInfo;
    private ACProgressCustom ac_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_credit_card);
        mySession = new MySession(this);
        mySavedCardInfo = new MySavedCardInfo(this);
        String user_log_data = mySession.getKeyAlldata();
        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");
                    email_str = jsonObject1.getString("email");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();


        datelist = new ArrayList<>();
        modellist = new ArrayList<>();
        int year = getLastModelYear();
        int thisyear = getThisYear();
        for (int i = 0; i <= 10; i++) {
            int nextyear = thisyear + i;
            modellist.add("" + nextyear);
        }
        // modellist.add("" + thisyear);
        for (int i = 1; i < 13; i++) {
            String dates = String.valueOf(i);
            if (i < 10) {
                dates = "0" + dates;
            }
            datelist.add("" + dates);
        }
        listOfPattern = new ArrayList<String>();

        String ptVisa = "^4[0-9]{6,}$";
        listOfPattern.add(ptVisa);
        String ptMasterCard = "^5[1-5][0-9]{5,}$";
        listOfPattern.add(ptMasterCard);
        String ptAmeExp = "^3[47][0-9]{5,}$";
        listOfPattern.add(ptAmeExp);
        String ptDinClb = "^3(?:0[0-5]|[68][0-9])[0-9]{4,}$";
        listOfPattern.add(ptDinClb);
        String ptDiscover = "^6(?:011|5[0-9]{2})[0-9]{3,}$";
        listOfPattern.add(ptDiscover);
        String ptJcb = "^(?:2131|1800|35[0-9]{3})[0-9]{3,}$";
        listOfPattern.add(ptJcb);
        idinti();
        clickevent();

        //  new CreateStripeAccount().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetUserProfile().execute();
    }

    private void clickevent() {
        backlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addcardbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardname_str = cardname.getText().toString();
                cardnumber_str = cardnumber.getText().toString();
                security_code_str = security_code.getText().toString();
                if (cardname_str == null || cardname_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddCreditCardAct.this, getResources().getString(R.string.entercardname), Toast.LENGTH_LONG).show();
                } else if (cardnumber_str == null || cardnumber_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddCreditCardAct.this, getResources().getString(R.string.entercardnumber), Toast.LENGTH_LONG).show();

                } else if (date_str == null || date_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddCreditCardAct.this, getResources().getString(R.string.selectexpdate), Toast.LENGTH_LONG).show();

                } else if (expiryyear_str == null || expiryyear_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddCreditCardAct.this, getResources().getString(R.string.selexpyear), Toast.LENGTH_LONG).show();

                } else if (expiryyear_str == null || expiryyear_str.equalsIgnoreCase("")) {
                    Toast.makeText(AddCreditCardAct.this, getResources().getString(R.string.selexpyear), Toast.LENGTH_LONG).show();

                } else {


                    month = Integer.parseInt(date_str);
                    year_int = Integer.parseInt(expiryyear_str);

                    onClickSomething(cardnumber_str, month, year_int, security_code_str);
                    Card card = new Card(cardnumber_str, month, year_int, security_code_str);  // pk_test_2khGozRubEhBZxFXj3TnxrkO
                    card.setCurrency("usd");
                    card.setName(cardname_str);
                    Stripe stripe = new Stripe(AddCreditCardAct.this, BaseUrl.stripe_publish);  //pk_test_DpAdEXE4slfMy2FR7vsSj0ya
                    //Stripe stripe = new Stripe(CreateCustomerFromCard.this, "pk_test_tuLF7lx5bPZpsfwM4OzqE0HJ");  //pk_test_DpAdEXE4slfMy2FR7vsSj0ya
                    //progressBar.setVisibility(View.VISIBLE);
                    if (ac_dialog != null) {
                        ac_dialog.show();
                    }
                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server
                                    if (ac_dialog != null) {
                                        ac_dialog.dismiss();
                                    }

                                    //  progressBar.setVisibility(View.GONE);
                                    Log.e("Token", ">>" + token.getId());

                                    token_id = token.getId();
                                    paymentwithcard();

                                }

                                public void onError(Exception error) {
                                    if (ac_dialog != null) {
                                        ac_dialog.dismiss();
                                    }

                                    // progressBar.setVisibility(View.GONE);
                                    Log.e("Eeeeeeeeeeeeeeerrrrr", ">>" + error.toString());
                                    // Show localized error message
                                    Toast.makeText(AddCreditCardAct.this, "\n" + error.toString(),
                                            Toast.LENGTH_LONG
                                    ).show();


                                }
                            });







                  /*  if (status_match){
                        new AddCardAsc().execute();
                    }
                    else {
                        Toast.makeText(AddCreditCardAct.this,getResources().getString(R.string.cardnumberisnotvalid),Toast.LENGTH_LONG).show();

                    }*/

                }
            }
        });
    }

    public void onClickSomething(String cardNumber, int cardExpMonth, int cardExpYear, String cardCVC) {
        Card card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVC);
        card.validateNumber();
        card.validateCVC();
    }

    private void idinti() {
        addcardbut = findViewById(R.id.addcardbut);
        progressBar = findViewById(R.id.progressBar);
        security_code = findViewById(R.id.security_code);

        datespinner = findViewById(R.id.datespinner);
        yearspinner = findViewById(R.id.yearspinner);
        yearspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                expiryyear_str = modellist.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        datespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date_str = datelist.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        backlay = findViewById(R.id.backlay);
        cardname = findViewById(R.id.cardname);
        cardnumber = findViewById(R.id.cardnumber);
        tv = new CreditCardFormatTextWatcher(cardnumber);
        cardnumber.addTextChangedListener(tv);


        basicCustomAdp = new BasicCustomAdp(AddCreditCardAct.this, android.R.layout.simple_spinner_item, modellist);
        yearspinner.setAdapter(basicCustomAdp);
        basicCustomAdp = new BasicCustomAdp(AddCreditCardAct.this, android.R.layout.simple_spinner_item, datelist);
        datespinner.setAdapter(basicCustomAdp);
    }

    private static int getLastModelYear() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -10);
        return prevYear.get(Calendar.YEAR);
    }

    private static int getThisYear() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, 0);
        return prevYear.get(Calendar.YEAR);
    }


    public class BasicCustomAdp extends ArrayAdapter<String> {
        Context context;
        Activity activity;
        private ArrayList<String> carmodel;

        public BasicCustomAdp(Context context, int resourceId, ArrayList<String> carmodel) {
            super(context, resourceId);
            this.context = context;
            this.carmodel = carmodel;
        }

        private class ViewHolder {
            TextView headername;
            TextView cartype;
        }

        @Override
        public int getCount() {
            return carmodel.size();
        }

        @Override
        public String getItem(int position) {
            return carmodel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.spn_head_lay, null);
                holder = new ViewHolder();
                holder.headername = (TextView) convertView.findViewById(R.id.heading);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.headername.setText("" + carmodel.get(position));

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.spn_head_lay, null);
                holder = new ViewHolder();
                holder.cartype = (TextView) convertView.findViewById(R.id.heading);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.cartype.setText("" + carmodel.get(position));
            return convertView;
        }
    }


    private void paymentwithcard() {
        // Tag used to cancel the request
        if (Utils.isConnected(getApplicationContext())) {
            //new CreateCardCustomer().execute();
            if (cust_id == null || cust_id.equalsIgnoreCase("") || cust_id.equalsIgnoreCase("null")) {
                new AddCardAsc().execute();
            } else {
                new SavedCardAsc().execute();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Please Cheeck network conection..", Toast.LENGTH_SHORT).show();
        }
    }

    private class AddCardAsc extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressBar.setVisibility(View.VISIBLE);
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
            try {
                //http://technorizen.com/transport/webservice/save_card?user_id=&source=&description=card%20detail%20of%20vijay%20gurjar&email=vijay.technorizen@gmail.com
                String postReceiverUrl = BaseUrl.baseurl + "save_card?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("source", token_id);
                params.put("description", "");
                params.put("email", "" + email_str);

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
            Log.e("Card Add Res", ">>>>>>>>>>>>" + result);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        cust_id = jsonObject1.getString("id");
                        Log.e("customer_id >> ", " >> " + cust_id);

                        mySavedCardInfo.clearCardData();
                        Toast.makeText(AddCreditCardAct.this, getResources().getString(R.string.cardaddedsucc), Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }
    }

    private class SavedCardAsc extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressBar.setVisibility(View.VISIBLE);
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
            try {
//http://technorizen.com/transport/webservice/add_card?cus_id=&source=
                String postReceiverUrl = BaseUrl.baseurl + "add_card?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("cus_id", WalletActivity.cust_id);
                params.put("source", token_id);

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
            Log.e("Saved Card Res", ">>>>>>>>>>>>" + result);
            // progressBar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        mySavedCardInfo.clearCardData();
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        //  WalletActivity.cust_id =  jsonObject1.getString("id");
                        // Log.e("customer_id >> ", " >> "+WalletActivity.cust_id);
                        Toast.makeText(AddCreditCardAct.this, getResources().getString(R.string.cardaddedsucc), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(AddCreditCardAct.this, getResources().getString(R.string.somethingwrong), Toast.LENGTH_LONG).show();

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
                Log.e("Json Login Response", ">>>>>>>>>>>>" + response);
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
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                        cust_id = jsonObject1.getString("cust_id");


                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

}
//https://github.com/braintree/android-card-form