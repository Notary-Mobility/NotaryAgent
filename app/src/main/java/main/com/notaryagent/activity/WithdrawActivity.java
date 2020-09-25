package main.com.notaryagent.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.notaryagent.R;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.MySession;
import main.com.notaryagent.constant.RideHistoryBean;
import main.com.notaryagent.constant.WithdrarReqBean;
import main.com.notaryagent.constant.WithdrarReqBeanList;
import main.com.notaryagent.restapi.ApiClient;
import main.com.notaryagent.tabactivity.MainTabActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawActivity extends AppCompatActivity {

    private RelativeLayout exit_app_but;
    private TextView referal_amount_tv, tripamount_tv, referal_withdraw, trip_withdraw;
    private ListView withdrawrequest;
    private LinearLayout withdraw_lay;
    private MySession mySession;
    private String user_log_data = "", user_id = "";
    private ACProgressCustom ac_dialog;
    private ArrayList<WithdrarReqBeanList> withdrarReqBeanListArrayList;
    private WithdrawAdapter withdrawAdapter;
    private ImageView refresh;
    private String ride_earning_amount = "0", invite_earning_amount = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        mySession = new MySession(this);
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();

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

        idinti();
        clickevetn();
    }

    private void clickevetn() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        referal_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (invite_earning_amount == null || invite_earning_amount.equalsIgnoreCase("") || invite_earning_amount.equalsIgnoreCase("0")||invite_earning_amount.equalsIgnoreCase("0.00") || invite_earning_amount.equalsIgnoreCase("null")) {
                    Toast.makeText(WithdrawActivity.this, getResources().getString(R.string.yourbalance), Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(WithdrawActivity.this, WithdrawReqAct.class);
                    i.putExtra("amount",invite_earning_amount);
                    i.putExtra("type","Invite");
                    startActivity(i);
                }


            }
        });
        trip_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ride_earning_amount == null || ride_earning_amount.equalsIgnoreCase("") || ride_earning_amount.equalsIgnoreCase("0")||ride_earning_amount.equalsIgnoreCase("0.00")  || ride_earning_amount.equalsIgnoreCase("null")) {
                    Toast.makeText(WithdrawActivity.this, getResources().getString(R.string.yourbalance), Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(WithdrawActivity.this, WithdrawReqAct.class);
                    i.putExtra("amount",ride_earning_amount);
                    i.putExtra("type","Ride");
                    startActivity(i);
                }

               /* Intent i = new Intent(WithdrawActivity.this, WithdrawReqAct.class);
                startActivity(i);*/
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWithdrawDetail();
            }
        });
    }

    private void idinti() {
        refresh = findViewById(R.id.refresh);
        withdraw_lay = findViewById(R.id.withdraw_lay);
        withdrawrequest = findViewById(R.id.withdrawrequest);
        exit_app_but = findViewById(R.id.exit_app_but);
        referal_amount_tv = findViewById(R.id.referal_amount_tv);
        tripamount_tv = findViewById(R.id.tripamount_tv);
        referal_withdraw = findViewById(R.id.referal_withdraw);
        trip_withdraw = findViewById(R.id.trip_withdraw);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWithdrawDetail();
        //  new GetMyAmountWithdraReq().execute();
    }

    private void getWithdrawDetail() {
        if (ac_dialog != null) {
            ac_dialog.show();
        }
        withdrarReqBeanListArrayList = new ArrayList<>();
        Call<ResponseBody> call = ApiClient.getApiInterface().getWithdrawDetail(user_id);
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
                        Log.e("GetWithdraw >", " >" + responseData);
                        if (object.getString("status").equals("1")) {
                            withdraw_lay.setVisibility(View.VISIBLE);
                            WithdrarReqBean successData = new Gson().fromJson(responseData, WithdrarReqBean.class);
                            withdrarReqBeanListArrayList.addAll(successData.getResult());
                            referal_amount_tv.setText("$" + successData.getInvite_earning());
                            tripamount_tv.setText("$" + successData.getRide_earning());
                            ride_earning_amount = successData.getRide_earning();
                            invite_earning_amount = successData.getInvite_earning();

                        } else {
                            withdraw_lay.setVisibility(View.GONE);
                            referal_amount_tv.setText("$" + object.getString("invite_earning"));
                            tripamount_tv.setText("$" + object.getString("ride_earning"));
                            ride_earning_amount = object.getString("ride_earning");
                            invite_earning_amount = object.getString("invite_earning");
                        }

                        withdrawAdapter = new WithdrawAdapter(WithdrawActivity.this, withdrarReqBeanListArrayList);
                        withdrawrequest.setAdapter(withdrawAdapter);
                        withdrawAdapter.notifyDataSetChanged();
                    } catch (Exception e) {

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

                Log.e("TAG", t.toString());
            }
        });
    }

    public class WithdrawAdapter extends BaseAdapter {

        String[] result;
        Context context;
        ArrayList<WithdrarReqBeanList> withdrarReqBeanListArrayList;
        private LayoutInflater inflater = null;


        public WithdrawAdapter(Activity activity, ArrayList<WithdrarReqBeanList> withdrarReqBeanListArrayList) {
            this.withdrarReqBeanListArrayList = withdrarReqBeanListArrayList;
            this.context = activity;

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            //return 4;
            return withdrarReqBeanListArrayList == null ? 0 : withdrarReqBeanListArrayList.size();
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

            rowView = inflater.inflate(R.layout.payment_request_lay, null);
            TextView withdrawrequest_tv = (TextView) rowView.findViewById(R.id.withdrawrequest_tv);
            TextView date_tv = (TextView) rowView.findViewById(R.id.date_tv);
            TextView statustv = (TextView) rowView.findViewById(R.id.statustv);
            withdrawrequest_tv.setText("Amount : $" + withdrarReqBeanListArrayList.get(position).getAmount());
            String sts = withdrarReqBeanListArrayList.get(position).getStatus();

            if (sts.equalsIgnoreCase("Pending")) {
                statustv.setTextColor(getResources().getColor(R.color.yellow));
                statustv.setText("" + sts);
            } else if (sts.equalsIgnoreCase("Accept")) {
                statustv.setTextColor(getResources().getColor(R.color.green));
                statustv.setText("" + sts);
            } else if (sts.equalsIgnoreCase("Reject")) {
                statustv.setTextColor(getResources().getColor(R.color.red));
                statustv.setText("" + sts);
            }
            date_tv.setText("" + withdrarReqBeanListArrayList.get(position).getDateTime());
            try {
                Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(withdrarReqBeanListArrayList.get(position).getDateTime());
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                String strDate = formatter.format(date1);
                date_tv.setText("" + strDate);

            } catch (ParseException e) {
                e.printStackTrace();
                date_tv.setText("" + withdrarReqBeanListArrayList.get(position).getDateTime());

            }
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
            return rowView;
        }

    }

    private class GetMyAmountWithdraReq extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            //http://technorizen.com/notary/webservice/get_my_withdraw_request?user_id=1
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_my_withdraw_request?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                StringBuilder postData = new StringBuilder();
                params.put("user_id", user_id);

                //account_holder_name , account_holder_address , account_number , bank_name , branch_name , branch_address , ifsc_code ,  routing_number

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
            // prgressbar.setVisibility(View.GONE);
            Log.e("Get Request", " >> " + result);
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

                        }
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }


}
