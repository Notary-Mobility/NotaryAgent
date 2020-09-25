package main.com.notaryagent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import main.com.notaryagent.R;


public class SettingActivity extends AppCompatActivity {

    private RelativeLayout exit_app_but,termscondlay,helplay,changepass,contactuslay,pricylay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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
        termscondlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingActivity.this,TermsConditionsAct.class);
                startActivity(i);
            }
        });
/*
        helplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingActivity.this,HelpActivity.class);
                startActivity(i);
            }
        });
*/
        pricylay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingActivity.this,PrivacyPolicyAct.class);
                startActivity(i);
            }
        });
/*
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingActivity.this,ChangePasswordAct.class);
                startActivity(i);
            }
        });
*/
        contactuslay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingActivity.this,ContactUsAct.class);
                startActivity(i);
            }
        });
    }

    private void idinti() {
        pricylay = findViewById(R.id.pricylay);
        changepass = findViewById(R.id.changepass);
        helplay = findViewById(R.id.helplay);
        exit_app_but = findViewById(R.id.exit_app_but);
        termscondlay = findViewById(R.id.termscondlay);
        contactuslay = findViewById(R.id.contactuslay);
    }
}
