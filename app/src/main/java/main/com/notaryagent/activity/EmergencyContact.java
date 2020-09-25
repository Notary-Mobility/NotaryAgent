package main.com.notaryagent.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import main.com.notaryagent.R;


public class EmergencyContact extends AppCompatActivity {
    private RelativeLayout exit_app_but;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);
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
    }

    private void idinit() {
        exit_app_but = findViewById(R.id.exit_app_but);
    }


}
