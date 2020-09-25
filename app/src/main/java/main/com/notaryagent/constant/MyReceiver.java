package main.com.notaryagent.constant;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import main.com.notaryagent.service.TrackingService;


public class MyReceiver extends BroadcastReceiver {
    MySession mySession;
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mySession = new MySession(context);
        //Toast.makeText(context, "test time run", Toast.LENGTH_SHORT).show();
        Log.e("alarm_run_offline :", "cache_data current time sec:###" + System.currentTimeMillis() / 1000);

        if (isMyServiceRunning(TrackingService.class,context)){
            Log.e("Service ... ","running");
        }
        else {
            Log.e("Service ... NOT","running> "+mySession.IsLoggedIn());
            try {
                if (mySession!=null){
                    if (mySession.IsLoggedIn()){
                        Intent intent1 = new Intent(context, TrackingService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent1);
                        } else {
                            context.startService(intent1);
                        }
                    }

                }


            } catch (Exception e) {
                Log.e("EXC BACK >>"," >"+e.getMessage());

            }
        }


    }
    private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service already","running");
                return true;
            }
        }
        Log.i("Service not","running");
        return false;
    }
/*    private void startAlarmSetup() {

        //Alarm manager  from where start the alarm
        // Date date = (Date) formatter.parse(formattedDate + " " + allGroupDTO.getNottime());
        java.util.Date date = Calendar.getInstance().getTime();
        Intent notifyIntent = new Intent(*//*App.getInstance()*//*MyReceiver.this, MyReceiver.class);
        // notifyIntent.putExtra("GroupName",allGroupDTO.getGname());
        notifyIntent.putExtra("title","Dinesh");
        // notifyIntent.putExtra("timeStamp",datefor+" "+allGroupDTO.getNottime());
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (*//*App.getInstance()*//*SplashActivity.this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)*//*App.getInstance()*//*getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, date.getTime(),
                1000 * 60 * 3*//* * 60 * 24*//*, null);
    }*/

}


/*
//Alarm manager  from where start the alarm

 try {

            Date date = (Date) formatter.parse(formattedDate + " " + allGroupDTO.getNottime());
            Intent notifyIntent = new Intent(App.getInstance(), MyReceiver.class);
            notifyIntent.putExtra("GroupName",allGroupDTO.getGname());
            notifyIntent.putExtra("title","Read AlQuran");
            notifyIntent.putExtra("timeStamp",datefor+" "+allGroupDTO.getNottime());
            PendingIntent pendingIntent = PendingIntent.getBroadcast
                    (App.getInstance(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) App.getInstance().getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, date.getTime(),
                    1000  60  60 * 24, pendingIntent);

        } catch (ParseException e) {
            e.printStackTrace();
        }

      */


