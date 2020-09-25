package main.com.notaryagent.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import main.com.notaryagent.R;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.MySession;

public class AgentDetailsOld extends AppCompatActivity {
    private ImageView insu_img_show, car_insp_show, car_img_show, lic_img_show;
    private ImageView lic_img, insu_img, car_insp_img, car_img;
    private String click_on = "", car_col_str = "", car_manuyear = "", cartype_id_str = "";
    private String user_log_data="",user_id="";
    private MySession mySession;
    private TextView manufac_year;
    private EditText car_model_et,car_registrationnum;
    private RelativeLayout exit_app_but;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_details);
        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        idinti();
        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");
                    car_manuyear = jsonObject1.getString("year_of_manufacture");
                    car_col_str = jsonObject1.getString("car_color");
                    manufac_year.setText("" + car_manuyear);
                    //car_color.setText("" + car_col_str);
                    //later added param
                    car_model_et.setText("" + jsonObject1.getString("car_model"));
                    car_registrationnum.setText("" + jsonObject1.getString("car_number"));
                    String license_str = jsonObject1.getString("license");
                    if (license_str == null || license_str.equalsIgnoreCase("") || license_str.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                    } else {
                        Picasso.with(AgentDetailsOld.this).load(license_str).into(lic_img_show);
                        lic_img.setImageResource(R.drawable.checkimg);
                    }
                    String driver_insstr = jsonObject1.getString("insurance");
                    Log.e("driver_insstr >>", " > " + driver_insstr);
                    if (driver_insstr == null || driver_insstr.equalsIgnoreCase("") || driver_insstr.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                    } else {
                        Picasso.with(AgentDetailsOld.this).load(driver_insstr).into(insu_img_show);

                        insu_img.setImageResource(R.drawable.checkimg);
                    }
                    String car_imgs = jsonObject1.getString("car_image");
                    if (car_imgs == null || car_imgs.equalsIgnoreCase("") || car_imgs.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                    } else {
                        Picasso.with(AgentDetailsOld.this).load(car_imgs).into(car_img_show);
                        car_img.setImageResource(R.drawable.checkimg);
                    }
                    String car_ins_img = jsonObject1.getString("car_document");
                    if (car_ins_img == null || car_ins_img.equalsIgnoreCase("") || car_ins_img.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                    } else {
                        Picasso.with(AgentDetailsOld.this).load(car_ins_img).into(car_insp_show);
                        car_insp_img.setImageResource(R.drawable.checkimg);
                    }


                    Log.e("user_id >>>>", "" + user_id);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void idinti() {
        exit_app_but = findViewById(R.id.exit_app_but);
        car_model_et = findViewById(R.id.car_model_et);
        car_registrationnum = findViewById(R.id.car_registrationnum);

        manufac_year = findViewById(R.id.manufac_year);
        insu_img_show = findViewById(R.id.insu_img_show);
        car_insp_show = findViewById(R.id.car_insp_show);
        car_img_show = findViewById(R.id.car_img_show);
        lic_img_show = findViewById(R.id.lic_img_show);

        lic_img = findViewById(R.id.lic_img);
        insu_img = findViewById(R.id.insu_img);
        car_insp_img = findViewById(R.id.car_insp_img);
        car_img = findViewById(R.id.car_img);
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
