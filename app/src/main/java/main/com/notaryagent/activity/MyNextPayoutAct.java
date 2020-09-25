package main.com.notaryagent.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import main.com.notaryagent.R;
import main.com.notaryagent.draweractivity.BaseActivity;

public class MyNextPayoutAct extends AppCompatActivity {

    private RelativeLayout exit_app_but;
    private TextView totalamount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_next_payout);
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
        totalamount = findViewById(R.id.totalamount);
        exit_app_but = findViewById(R.id.exit_app_but);
        totalamount.setText("$ "+ BaseActivity.amount);
    }
}
