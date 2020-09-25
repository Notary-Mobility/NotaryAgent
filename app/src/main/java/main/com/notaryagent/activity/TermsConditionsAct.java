package main.com.notaryagent.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import main.com.notaryagent.R;
import main.com.notaryagent.constant.BaseUrl;

public class TermsConditionsAct extends AppCompatActivity {

    WebView termscondition;
    private RelativeLayout exit_app_but;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
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
    }

    private void idinti() {
        exit_app_but = findViewById(R.id.exit_app_but);
        termscondition = findViewById(R.id.termscondition);
        termscondition.getSettings().setJavaScriptEnabled(true);
        termscondition.getSettings().setPluginState(WebSettings.PluginState.ON);
        termscondition.setWebViewClient(new Callback());
        //  String pdfURL = "http://mobileappdevelop.co/NAXCAN/about-us.html";
        String pdfURL = BaseUrl.terms;
        termscondition.loadUrl(pdfURL);

    }
    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return(false);
        }
    }
}
