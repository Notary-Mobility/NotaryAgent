package main.com.notaryagent.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import main.com.notaryagent.R;
import main.com.notaryagent.constant.BaseUrl;


public class AboutUsAct extends AppCompatActivity {
    private RelativeLayout exit_app_but;
    private LinearLayout websitlink,email_lay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        idinit();
        clickevent();
    }

    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        websitlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = BaseUrl.websiturl;

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        email_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","admin@notarymobility.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Notary");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

               /* Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, "notarymobility@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Notary");
                intent.putExtra(Intent.EXTRA_TEXT, "");

                startActivity(Intent.createChooser(intent, "Send Email"));*/
            }
        });
    }

    private void idinit() {
        exit_app_but = findViewById(R.id.exit_app_but);
        email_lay = findViewById(R.id.email_lay);
        websitlink = findViewById(R.id.websitlink);
    }
}
