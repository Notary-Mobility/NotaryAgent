package main.com.notaryagent.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import main.com.notaryagent.R;
import main.com.notaryagent.constant.BaseUrl;


public class PrivacyPolicyAct extends AppCompatActivity {

    private RelativeLayout exit_app_but;
    private WebView privacypolicy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        idinti();
        clickevent();
    }

    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        privacypolicy.getSettings().setJavaScriptEnabled(true);
        privacypolicy.getSettings().setPluginState(WebSettings.PluginState.ON);
        privacypolicy.setWebViewClient(new Callback());
        //  String pdfURL = "http://mobileappdevelop.co/NAXCAN/about-us.html";
        String pdfURL = BaseUrl.privacy;
        privacypolicy.loadUrl(pdfURL);

    }
    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return(false);
        }
    }

    private void idinti() {
        exit_app_but = findViewById(R.id.exit_app_but);
        privacypolicy = findViewById(R.id.privacypolicy);
    }
}
