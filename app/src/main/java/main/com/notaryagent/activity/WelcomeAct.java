package main.com.notaryagent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import main.com.notaryagent.R;


public class WelcomeAct extends AppCompatActivity {

    private Button loginbut,signupbut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_welcome);

        idinit();
        clickevent();
    }

    private void clickevent() {
        loginbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeAct.this,LoginAct.class);
                startActivity(i);
            }
        });
        signupbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(WelcomeAct.this,SignupActivity.class);
                Intent i = new Intent(WelcomeAct.this,AgentSignup.class);
                startActivity(i);
            }
        });
    }

    private void idinit() {
        loginbut = findViewById(R.id.loginbut);
        signupbut = findViewById(R.id.signupbut);
    }

}
