package main.com.notaryagent.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import main.com.notaryagent.R;
import main.com.notaryagent.constant.MySession;


public class SeeMyStripeDashBoardAct extends AppCompatActivity {

    private RelativeLayout backlay;
    private WebView stripewebview;
    private ProgressBar progressabar;
    private String user_id="",stripe_login_url="";
    private MySession mySession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_my_stripe_dash_board);
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
        Bundle bundle = getIntent().getExtras();
        if (bundle==null||bundle.isEmpty())
        {

        }
        else {
            stripe_login_url = bundle.getString("stripe_login_url");
        }idinitn();
        clickevent();
    }

    private void clickevent() {
        backlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void idinitn() {
        progressabar = findViewById(R.id.progressabar);
        backlay = findViewById(R.id.backlay);
        stripewebview = findViewById(R.id.stripewebview);
        stripewebview.clearCache(true);

        String stripeurl = stripe_login_url;
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
                progressabar.setVisibility(View.VISIBLE);

                view.loadUrl(urlNewString);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                progressabar.setVisibility(View.VISIBLE);
//SHOW LOADING IF IT ISNT ALREADY VISIBLE
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressabar.setVisibility(View.GONE);



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

    }


    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return (false);
        }
    }


}