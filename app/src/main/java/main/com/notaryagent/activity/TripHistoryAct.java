package main.com.notaryagent.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import main.com.notaryagent.R;
import main.com.notaryagent.app.Config;
import main.com.notaryagent.constant.RideBean;
import main.com.notaryagent.utils.NotificationUtils;

public class TripHistoryAct extends AppCompatActivity {

    ProgressBar prgressbar;
    private RelativeLayout exit_app_but;
    private ListView ridehistory_list;
    RideHisAdp ridehisadp;
    TextView completedtv, upcomingtv, allrides, ride_sts_tv;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    ArrayList<RideBean> rideBeanArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triphistory);

        idinit();
        clicjkevt();


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
                        Log.e("KEY ACCEPT REJ", "" + keyMessage);
                        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                            NotificationUtils.r.stop();
                        }

                        //(keyMessage.equalsIgnoreCase("new shared user"))
                        if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {

                        } else if (keyMessage.equalsIgnoreCase("your booking request is Arrived")) {

                        } else if (keyMessage.equalsIgnoreCase("your booking request is Start")) {

                        } else if (keyMessage.equalsIgnoreCase("your booking request is End")) {

                        } else if (keyMessage.equalsIgnoreCase("your ride is Finish")) {

                        } else if (keyMessage.equalsIgnoreCase("please complete ride payment")) {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

    }


    private void clicjkevt() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        allrides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allrides.setTextColor(getResources().getColor(R.color.white));
                upcomingtv.setTextColor(getResources().getColor(R.color.toobarcolor));
                completedtv.setTextColor(getResources().getColor(R.color.toobarcolor));
                allrides.setBackgroundResource(R.drawable.border_filled_red);
                upcomingtv.setBackgroundResource(R.drawable.border_rec_toolcolor);
                completedtv.setBackgroundResource(R.drawable.border_rec_toolcolor);
                ridehisadp = new RideHisAdp(TripHistoryAct.this, rideBeanArrayList);
                ridehistory_list.setAdapter(ridehisadp);
                ridehisadp.notifyDataSetChanged();
               /* if (rideBeanArrayList == null || rideBeanArrayList.isEmpty()) {
                    ride_sts_tv.setVisibility(View.VISIBLE);
                    ride_sts_tv.setText("" + getResources().getString(R.string.norides));
                } else {
                    ride_sts_tv.setVisibility(View.GONE);
                }*/

            }
        });
        upcomingtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allrides.setTextColor(getResources().getColor(R.color.toobarcolor));
                upcomingtv.setTextColor(getResources().getColor(R.color.white));
                completedtv.setTextColor(getResources().getColor(R.color.toobarcolor));
                upcomingtv.setBackgroundResource(R.drawable.border_filled_red);
                allrides.setBackgroundResource(R.drawable.border_rec_toolcolor);
                completedtv.setBackgroundResource(R.drawable.border_rec_toolcolor);
                ridehisadp = new RideHisAdp(TripHistoryAct.this, rideBeanArrayList);
                ridehistory_list.setAdapter(ridehisadp);
                ridehisadp.notifyDataSetChanged();
               /* if (rideBeanArrayList == null || rideBeanArrayList.isEmpty()) {
                    ride_sts_tv.setVisibility(View.VISIBLE);
                    ride_sts_tv.setText("" + getResources().getString(R.string.nobookedride));
                } else {
                    ride_sts_tv.setVisibility(View.GONE);
                }*/

            }
        });
        completedtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allrides.setTextColor(getResources().getColor(R.color.toobarcolor));
                upcomingtv.setTextColor(getResources().getColor(R.color.toobarcolor));
                completedtv.setTextColor(getResources().getColor(R.color.white));
                upcomingtv.setBackgroundResource(R.drawable.border_rec_toolcolor);
                allrides.setBackgroundResource(R.drawable.border_rec_toolcolor);
                completedtv.setBackgroundResource(R.drawable.border_filled_red);
                ridehisadp = new RideHisAdp(TripHistoryAct.this, rideBeanArrayList);
                ridehistory_list.setAdapter(ridehisadp);
                ridehisadp.notifyDataSetChanged();
              /*  if (rideBeanArrayList == null || rideBeanArrayList.isEmpty()) {
                    ride_sts_tv.setVisibility(View.VISIBLE);
                    ride_sts_tv.setText("" + getResources().getString(R.string.noridecomp));
                } else {
                    ride_sts_tv.setVisibility(View.GONE);
                }*/

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(TripHistoryAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(TripHistoryAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(TripHistoryAct.this.getApplicationContext());


    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(TripHistoryAct.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();

    }

    private void idinit() {
        ride_sts_tv =  findViewById(R.id.ride_sts_tv);
        allrides =  findViewById(R.id.allrides);
        completedtv = findViewById(R.id.completedtv);
        upcomingtv =  findViewById(R.id.upcomingtv);
        prgressbar =  findViewById(R.id.prgressbar);
        ridehistory_list = findViewById(R.id.ridehistory_list);
        exit_app_but = findViewById(R.id.exit_app_but);

        ridehisadp = new RideHisAdp(TripHistoryAct.this, rideBeanArrayList);
        ridehistory_list.setAdapter(ridehisadp);
        ridehisadp.notifyDataSetChanged();

    }


    public class RideHisAdp extends BaseAdapter {

        String[] result;
        Context context;
        ArrayList<RideBean> ridehislist;
        private LayoutInflater inflater = null;


        public RideHisAdp(Activity activity, ArrayList<RideBean> ridehislist) {
            this.ridehislist = ridehislist;
            this.context = activity;

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 4;
           // return ridehislist == null ? 0 : ridehislist.size();
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

            rowView = inflater.inflate(R.layout.ride_history_lay, null);
            TextView pickuplocation = (TextView) rowView.findViewById(R.id.pickuplocation);
            TextView date_tv = (TextView) rowView.findViewById(R.id.date_tv);
            TextView statustv = (TextView) rowView.findViewById(R.id.statustv);
            ImageView car_img = (ImageView) rowView.findViewById(R.id.car_img);
         //   pickuplocation.setText(""+);

            return rowView;
        }

    }

}
