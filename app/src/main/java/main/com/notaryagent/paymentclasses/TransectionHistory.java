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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.notaryagent.R;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.MySession;
import main.com.notaryagent.constant.TransectionBean;

public class TransectionHistory extends AppCompatActivity {

    private TranjectionAdp tranjsadp;
    private ListView tranjectionhistory;
    ACProgressCustom ac_dialog;
    private RelativeLayout exit_app_but;
    private MySession mySession;
    private String user_log_data="",user_id="";
    private ArrayList<TransectionBean> transectionBeanArrayList;
    private TextView notrans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transection_history);
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
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();
        idinit();
        clickevt();
        new GetTransection().execute();
    }

    private void clickevt() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void idinit() {
        notrans = findViewById(R.id.notrans);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
        tranjectionhistory = (ListView) findViewById(R.id.tranjectionhistory);

    }
    public class TranjectionAdp extends BaseAdapter {

        String[] result;
        Context context;
        ArrayList<TransectionBean> transectionBeanArrayList;
        private LayoutInflater inflater = null;


        public TranjectionAdp(Activity activity, ArrayList<TransectionBean> transectionBeanArrayList) {
            this.context = activity;
            this.transectionBeanArrayList = transectionBeanArrayList;

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
           // return 9;
            return transectionBeanArrayList == null ? 0 : transectionBeanArrayList.size();
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

            rowView = inflater.inflate(R.layout.custom_tranjection_lay, null);
            TextView amount_tv = rowView.findViewById(R.id.amount_tv);
            TextView payment_desc = rowView.findViewById(R.id.payment_desc);
            TextView date_tv = rowView.findViewById(R.id.date_tv);
            ImageView arrow_img = rowView.findViewById(R.id.arrow_img);
            amount_tv.setText("$"+transectionBeanArrayList.get(position).getAmount());

            if (transectionBeanArrayList.get(position).getTransaction_type().equalsIgnoreCase("Credit")){
                arrow_img.setImageResource(R.drawable.ic_uparrow);
            }
            else {
                arrow_img.setImageResource(R.drawable.ic_down);

            }
            if (transectionBeanArrayList.get(position).getDescription().equalsIgnoreCase("Added to wallet")){
                //user and driver
                payment_desc.setText(getResources().getString(R.string.addedtoyourwallet));
            }
            else if (transectionBeanArrayList.get(position).getDescription().equalsIgnoreCase("Paid for ride"))
            {//user
                payment_desc.setText(getResources().getString(R.string.paidforride));

            }
            else if (transectionBeanArrayList.get(position).getDescription().equalsIgnoreCase("Receive from ride"))
            {
                payment_desc.setText(getResources().getString(R.string.addinwalletforcardride));

            }
            else if (transectionBeanArrayList.get(position).getDescription().equalsIgnoreCase("Receive from ride in wallet"))
            {
                payment_desc.setText(getResources().getString(R.string.addinwalletforwallettyperide));
            }
            else if (transectionBeanArrayList.get(position).getDescription().equalsIgnoreCase("Deduct for ride from wallet"))
            {//user
                payment_desc.setText(getResources().getString(R.string.amountdeductforwalletride));
            }
            else if (transectionBeanArrayList.get(position).getDescription().equalsIgnoreCase("Deduct from wallet for cash ride"))
            {
                payment_desc.setText(getResources().getString(R.string.deductfromwalletforcashride));
            }
            else {
                payment_desc.setText(transectionBeanArrayList.get(position).getDescription());

            }
            payment_desc.setText(""+transectionBeanArrayList.get(position).getDescription());
            date_tv.setText(""+transectionBeanArrayList.get(position).getDate_time());
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            return rowView;
        }

    }
    private class GetTransection extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //prgressbar.setVisibility(View.VISIBLE);
            if(ac_dialog!=null){
                ac_dialog.show();
            }
            transectionBeanArrayList = new ArrayList<>();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(String... strings) {
//http://hitchride.net/webservice/get_my_transaction?user_id=1
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_my_transaction?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);



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
                Log.e("Transection History", ">>>>>>>>>>>>" + response);
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
            if(ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
                notrans.setVisibility(View.VISIBLE);
            } else if (result.isEmpty()) {
                notrans.setVisibility(View.VISIBLE);
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1"))
                    {
                        notrans.setVisibility(View.GONE);

                       JSONArray jsonArray = jsonObject.getJSONArray("result");
                       for (int k=0;k<jsonArray.length();k++){
                           TransectionBean transectionBean = new TransectionBean();
                           JSONObject jsonObject1 = jsonArray.getJSONObject(k);
                           transectionBean.setId(jsonObject1.getString("id"));
                           transectionBean.setUser_id(jsonObject1.getString("user_id"));
                           transectionBean.setType(jsonObject1.getString("type"));
                           transectionBean.setType_id(jsonObject1.getString("type_id"));
                           transectionBean.setDescription(jsonObject1.getString("description"));
                           transectionBean.setTime_zone(jsonObject1.getString("time_zone"));
                           transectionBean.setDate_time(jsonObject1.getString("date_time"));
                           transectionBean.setStatus(jsonObject1.getString("status"));
                           transectionBean.setAmount(jsonObject1.getString("amount"));
                           transectionBean.setTransaction_type(jsonObject1.getString("transaction_type"));

transectionBeanArrayList.add(transectionBean);
                       }
                    }
                    else {
                        notrans.setVisibility(View.VISIBLE);
                    }
                    tranjsadp = new TranjectionAdp(TransectionHistory.this,transectionBeanArrayList);
                    tranjectionhistory.setAdapter(tranjsadp);
                }
                    catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

}
