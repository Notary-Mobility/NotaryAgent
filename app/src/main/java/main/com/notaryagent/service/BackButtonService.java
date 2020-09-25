package main.com.notaryagent.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.List;

import main.com.notaryagent.R;
import main.com.notaryagent.activity.TrackRideAct;


/**
 * Created by technorizen on 27/1/18.
 */

public class BackButtonService extends Service {


    private WindowManager mWindowManager;
    private View mOverlayView;
    ImageView back_but;
String TAG="BACKBUTTONSERVICE";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        setTheme(R.style.AppTheme);

        mOverlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);


        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 10;


        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mOverlayView, params);


        back_but = (ImageView) mOverlayView.findViewById(R.id.back_but);
        back_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(getApplicationContext(), TrackRideAct.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(i);


                   /* Intent intent = new Intent();
                    intent.setComponent(new ComponentName("technorizen.com.naxcandriver.TripStatusAct",TripStatusAct.class.getName()));
                    startActivity(intent);
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_MAIN);
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
                    i.setComponent(new ComponentName(getApplicationContext().getPackageName(), TripStatusAct.class.getName()));
                    getApplicationContext().startActivity(i);
*/
                }
                catch (Throwable t) {
                    Log.i(TAG, "Throwable caught: "
                            + t.getMessage(), t);
                }

                stopService(new Intent(getApplicationContext(),BackButtonService.class));

            }
        });

    }
    private void killAppBypackage(String packageTokill){

        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(0);


        ActivityManager mActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String myPackage = getApplicationContext().getPackageName();

        for (ApplicationInfo packageInfo : packages) {
            Log.e("packageName >","kill"+packageInfo.packageName);
            Log.e("packageTokill >","TO kill"+packageTokill);
/*
            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1) {
                continue;

            }
            if(packageInfo.packageName.equals(myPackage)) {
                continue;
            }
*/
            if(packageInfo.packageName.equals(packageTokill)) {
                Log.e("COME TO KILL","TRUE");
                try {

                   

                    mActivityManager.killBackgroundProcesses(packageInfo.packageName);

                    break;
                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.e("EXCEPTION",""+e.getMessage());
                }

            }

        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOverlayView != null)
            mWindowManager.removeView(mOverlayView);
    }


}
