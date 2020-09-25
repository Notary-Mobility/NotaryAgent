package main.com.notaryagent.paymentclasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import main.com.notaryagent.R;
import main.com.notaryagent.app.Config;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.MySession;
import main.com.notaryagent.utils.NotificationUtils;


public class StripeExpressAcountAct extends AppCompatActivity {
    ScheduledExecutorService scheduleTaskExecutor;
    private RelativeLayout backlay;
    private WebView stripewebview;
    private MySession mySession;
    private String user_id = "";
    boolean loadingFinished = true;
    boolean redirect = false;
    private ProgressBar progressabar;
    BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_express_acount);

        mySession = new MySession(this);
        String user_log_data = mySession.getKeyAlldata();
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
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Log.e("Push_STRIPE: ", "" + message);
                    JSONObject data = null;
                    try {
                        data = new JSONObject(message);
                        String keyMessage = data.getString("key").trim();
                        if (keyMessage.equalsIgnoreCase("Your Stripe Connect Account Created Successfully")) {
                            Toast.makeText(StripeExpressAcountAct.this, getResources().getString(R.string.youraccountcreatedsucc), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        idinit();
        clickevent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        scheduleTaskExecutor.shutdown();
    }

    @Override
    public void onResume() {


        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());

        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {

            }
        }, 10, 20, TimeUnit.SECONDS);
    }

    private void clickevent() {
        backlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void idinit() {
        progressabar = findViewById(R.id.progressabar);
        backlay = findViewById(R.id.backlay);
        stripewebview = findViewById(R.id.stripewebview);
        stripewebview.clearCache(true);

        String stripeurl = BaseUrl.STRIPE_OAUTH_URL + user_id;
       /* stripewebview.getSettings().setJavaScriptEnabled(true);
        stripewebview.getSettings().setPluginState(WebSettings.PluginState.ON);
        stripewebview.setWebViewClient(new Callback());
        //  String pdfURL = "http://mobileappdevelop.co/NAXCAN/about-us.html";
        String stripeurl = BaseUrl.STRIPE_OAUTH_URL+user_id;
        Log.e("stripeurl >>"," >> "+stripeurl);

        stripewebview.loadUrl(stripeurl);*/



        stripewebview.getSettings().setLoadsImagesAutomatically(true);
        stripewebview.getSettings().setJavaScriptEnabled(true);
        stripewebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        stripewebview.setWebViewClient(new HelloWebViewClient());
        stripewebview.getSettings().setDomStorageEnabled(true);
        stripewebview.getSettings().setAppCacheEnabled(false);
        stripewebview.getSettings().setLoadsImagesAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            stripewebview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        stripewebview.loadUrl(stripeurl);

//stripewebview.loadUrl("http://www.tutorialspoint.com");


        stripewebview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                if (!loadingFinished) {
                    redirect = true;
                }
                progressabar.setVisibility(View.VISIBLE);
                loadingFinished = false;
                view.loadUrl(urlNewString);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                loadingFinished = false;
                progressabar.setVisibility(View.VISIBLE);
//SHOW LOADING IF IT ISNT ALREADY VISIBLE
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressabar.setVisibility(View.GONE);
                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
//HIDE LOADING IT HAS FINISHED

                } else {
                    redirect = false;
                }

            }
        });


    }


    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        scheduleTaskExecutor.shutdown();
    }


    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return (false);
        }
    }


}
