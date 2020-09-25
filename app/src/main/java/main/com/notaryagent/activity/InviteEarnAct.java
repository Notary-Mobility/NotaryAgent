package main.com.notaryagent.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import main.com.notaryagent.R;
import main.com.notaryagent.draweractivity.BaseActivity;
import main.com.notaryagent.tabactivity.ProfileActivity;


public class InviteEarnAct extends AppCompatActivity {
    private TextView invite_tv,myreferal;
    private RelativeLayout exit_app_but;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_earn);
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
        invite_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts( "mailto", "", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                emailIntent.putExtra(Intent.EXTRA_TEXT, "I have an Notary coupon worth USD100 for you.Signup with my referral code "+ BaseActivity.promo_code);
                startActivity(Intent.createChooser(emailIntent, null));

               /* Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                email.putExtra(Intent.EXTRA_SUBJECT, "");
                email.putExtra(Intent.EXTRA_TEXT, "");
                email.setType("plain/text");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
*/
                //sendEmail();

               /* Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                //sendIntent.putExtra(Intent.EXTRA_TEXT,"Please use my code as referal code and earn money , my code is ="+ BaseActivity.promo_code);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "I have an Notary coupon worth USD100 for you.Signup with my referral code "+ ProfileActivity.promo_code);

               // sendIntent.putExtra(Intent.EXTRA_TEXT,"Install Notary Agent App from play store");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);*/
                /*Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Naxcan");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I have an Naxcan coupon worth USD100 for you.Signup with my referral code "+MainActivity.promo_code);
                shareIntent.setType("image/png");*/

            }
        });
    }

    private void idinit() {
        exit_app_but = findViewById(R.id.exit_app_but);
        invite_tv = findViewById(R.id.invite_tv);
        myreferal = findViewById(R.id.myreferal);
        myreferal.setText(""+ BaseActivity.promo_code);
    }
    protected void sendEmail() {

        String[] TO = {""};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        //emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        emailIntent.putExtra(Intent.EXTRA_TEXT, "I have an Notary coupon worth USD100 for you.Signup with my referral code ");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(InviteEarnAct.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
