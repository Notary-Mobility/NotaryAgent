package main.com.notaryagent.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.notaryagent.R;
import main.com.notaryagent.app.Config;
import main.com.notaryagent.constant.ACProgressCustom;
import main.com.notaryagent.constant.BaseUrl;
import main.com.notaryagent.constant.BasicCustomAdp;
import main.com.notaryagent.constant.ExpandableHeightListView;
import main.com.notaryagent.constant.MultipartUtility;
import main.com.notaryagent.constant.MyCarBean;
import main.com.notaryagent.constant.MyReceiver;
import main.com.notaryagent.constant.MySession;
import main.com.notaryagent.service.GPSTracker;
import main.com.notaryagent.tabactivity.HomeActivity;
import main.com.notaryagent.tabactivity.MainTabActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private Button register;
    private TextView sellang;
    private RelativeLayout backbut;
    BasicCustomAdp basicCustomAdp;
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 0; // in Milliseconds
    LocationManager locationManager;
    Location location;
    private double latitude = 0, longitude = 0;
    GPSTracker gpsTracker;
    MySession mySession;
    ACProgressCustom ac_dialog;
    CustomSpinnerAdapter customSpinnerAdapter;
    private ImageView lic_img, insu_img, car_insp_img, car_img;
    private EditText email_id, password_et, username, car_model_et, car_registrationnum;
    private String email_str = "", password_str = "", mobile_str = "", username_str = "", car_model_str = "", car_registrationnum_str = "";
    private EditText mobile_et, last_name;
    private RelativeLayout image_lay, driver_licens_lay, insurance_copy, driver_car_ispe_lay, driver_car_lay;
    private String INS_PATH = "", DRIVER_IMG = "", DRIVER_LIC_PATH = "", CAR_IMG_PATH = "", CAR_INS_PATH = "", firebase_regid = "";
    private String click_on = "", last_name_str = "", car_col_str = "", car_manuyear = "", cartype_id_str = "";
    private ArrayList<String> modellist, carcolorlist;
    private Spinner carmanuyear, carcolspn, servicetype;
    ArrayList<MyCarBean> myCarBeanArrayList;
    CircleImageView user_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(SignupActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignupActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        mySession = new MySession(this);
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();
        checkGps();

        modellist = new ArrayList<>();
        carcolorlist = new ArrayList<>();

        modellist.add(getResources().getString(R.string.selmanyyear));
        carcolorlist.add(getResources().getString(R.string.selectcarcol));

        carcolorlist.add("White");
        carcolorlist.add("Black");
        carcolorlist.add("Blue");
        carcolorlist.add("Red");
        carcolorlist.add("Other");


        int year = getLastModelYear();
        int thisyear = getThisYear();
        for (int i = year; i <= thisyear; i++) {
            modellist.add("" + i);
        }
        modellist.add("" + thisyear);

        idint();
        clickevent();
        new GetCarLists().execute();

    }

    private static int getLastModelYear() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -10);
        return prevYear.get(Calendar.YEAR);
    }

    private static int getThisYear() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, 0);
        return prevYear.get(Calendar.YEAR);
    }

    private void clickevent() {
        backbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                firebase_regid = pref.getString("regId", null);
                email_str = email_id.getText().toString();
                password_str = password_et.getText().toString();
                mobile_str = mobile_et.getText().toString();
                username_str = username.getText().toString();
                last_name_str = last_name.getText().toString();
                car_model_str = car_model_et.getText().toString();
                car_registrationnum_str = car_registrationnum.getText().toString();


                if (email_str == null || email_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.pleaseenteremailid), Toast.LENGTH_LONG).show();
                } else if (password_str == null || password_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.plsentpass), Toast.LENGTH_LONG).show();
                } else if (mobile_str == null || mobile_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.plsmobilestr), Toast.LENGTH_LONG).show();
                } else if (username_str == null || username_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.plsenfrst), Toast.LENGTH_LONG).show();

                } else if (last_name_str == null || last_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.plsenlst), Toast.LENGTH_LONG).show();

                } else if (cartype_id_str == null || cartype_id_str.equalsIgnoreCase("") || cartype_id_str.equalsIgnoreCase("0")) {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.selservicetype), Toast.LENGTH_LONG).show();

                } else if (car_manuyear == null || car_manuyear.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.plsselyear), Toast.LENGTH_LONG).show();

                } else if (car_model_str == null || car_model_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.entercarmodel), Toast.LENGTH_LONG).show();

                } /*else if (car_col_str == null || car_col_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.selcarcol), Toast.LENGTH_LONG).show();

                }*/ else if (car_registrationnum_str == null || car_registrationnum_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.plsenterregi), Toast.LENGTH_LONG).show();
                } /*else if (INS_PATH == null || INS_PATH.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.pleasuplodains), Toast.LENGTH_LONG).show();
                }*/ else if (DRIVER_LIC_PATH == null || DRIVER_LIC_PATH.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.plsupldri), Toast.LENGTH_LONG).show();

                } else if (DRIVER_IMG == null || DRIVER_IMG.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.plsupyourprofile), Toast.LENGTH_LONG).show();

                } else {
                    new JsonSignupAsc().execute();
                }


            }
        });
        insurance_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_on = "insurance";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);

            }
        });
        driver_licens_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_on = "driverlicens";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);

            }
        });
        driver_car_ispe_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_on = "carinsp";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);

            }
        });
        driver_car_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_on = "carimg";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);

            }
        });
        image_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_on = "profimg";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);

            }
        });
    }

    private void idint() {
        last_name = findViewById(R.id.last_name);
        user_img = findViewById(R.id.user_img);
        image_lay = findViewById(R.id.image_lay);
        driver_car_lay = findViewById(R.id.driver_car_lay);
        driver_car_ispe_lay = findViewById(R.id.driver_car_ispe_lay);
        car_insp_img = findViewById(R.id.car_insp_img);
        car_img = findViewById(R.id.car_img);
        servicetype = findViewById(R.id.servicetype);
        carmanuyear = findViewById(R.id.carmanuyear);
        carcolspn = findViewById(R.id.carcolspn);
        insu_img = findViewById(R.id.insu_img);
        insurance_copy = findViewById(R.id.insurance_copy);
        car_registrationnum = findViewById(R.id.car_registrationnum);
        car_model_et = findViewById(R.id.car_model_et);
        lic_img = findViewById(R.id.lic_img);
        username = findViewById(R.id.username);
        driver_licens_lay = findViewById(R.id.driver_licens_lay);
        mobile_et = findViewById(R.id.mobile_et);
        password_et = findViewById(R.id.password_et);
        email_id = findViewById(R.id.email_id);
        backbut = findViewById(R.id.backbut);
        sellang = findViewById(R.id.sellang);
        register = findViewById(R.id.register);

        basicCustomAdp = new BasicCustomAdp(SignupActivity.this, android.R.layout.simple_spinner_item, modellist);
        carmanuyear.setAdapter(basicCustomAdp);
        basicCustomAdp = new BasicCustomAdp(SignupActivity.this, android.R.layout.simple_spinner_item, carcolorlist);
        carcolspn.setAdapter(basicCustomAdp);
        carcolspn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                car_col_str = carcolorlist.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        carmanuyear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                car_manuyear = modellist.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        servicetype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (myCarBeanArrayList == null) {

                } else {
                    if (myCarBeanArrayList.size() > 0) {
                        if (myCarBeanArrayList.get(position).getId().equalsIgnoreCase("0")) {
                            cartype_id_str = "";
                        } else {
                            cartype_id_str = myCarBeanArrayList.get(position).getId();

                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
/*
        mobile_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mobVeriPopup();
            }
        });
*/
    }


    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    private void checkGps() {
        gpsTracker = new GPSTracker(SignupActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            if (latitude == 0.0) {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;

            }
        } else {

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            } else {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;
                Log.e("LAT", "" + latitude);
                Log.e("LON", "" + longitude);

            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {


            switch (requestCode) {
                case 1:
                    Uri selectedImage = data.getData();
                    //  getPath(selectedImage);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String FinalPath = cursor.getString(columnIndex);
                    cursor.close();
                    String ImagePath = getPath(selectedImage);
                    Log.e("PATH From Gallery", "" + FinalPath);
                    Log.e("PATH Get Gallery", "" + ImagePath);
                    decodeFile(ImagePath);
                    break;
                case 2:

                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    // File file = new File(photo);
                    //  save(file.getAbsolutePath());
                    ImagePath = saveToInternalStorage(photo);
                    Log.e("PATH Camera", "" + ImagePath);

                    //  avt_imag.setImageBitmap(photo);
                    break;


            }
        }
    }

    public String getPath(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //  Log.e("image_path.===..", "" + path);
        }
        cursor.close();
        return path;
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        String dateToStr = format.format(today);
        ContextWrapper cw = new ContextWrapper(SignupActivity.this);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, "profile_" + dateToStr + ".JPEG");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }


    public void decodeFile(String filePath) {
        Log.e("ImagePath in decode", " >>" + filePath);
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);
        if (click_on.equalsIgnoreCase("driverlicens")) {
            DRIVER_LIC_PATH = saveToInternalStorage(bitmap);
            lic_img.setImageResource(R.drawable.checkimg);
        } else if (click_on.equalsIgnoreCase("carimg")) {
            CAR_IMG_PATH = saveToInternalStorage(bitmap);
            car_img.setImageResource(R.drawable.checkimg);
        } else if (click_on.equalsIgnoreCase("carinsp")) {
            CAR_INS_PATH = saveToInternalStorage(bitmap);
            car_insp_img.setImageResource(R.drawable.checkimg);
        } else if (click_on.equalsIgnoreCase("profimg")) {
            DRIVER_IMG = saveToInternalStorage(bitmap);
            user_img.setImageBitmap(bitmap);
        } else {
            INS_PATH = saveToInternalStorage(bitmap);
            insu_img.setImageResource(R.drawable.checkimg);
        }


    }


    private class GetCarLists extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressbar.setVisibility(View.VISIBLE);
            if (ac_dialog != null) {
                ac_dialog.show();
            }

            MyCarBean myCarBean = new MyCarBean();
            myCarBean.setId("0");
            myCarBean.setCarname("Service Type");
            myCarBeanArrayList = new ArrayList<>();
            myCarBeanArrayList.add(myCarBean);
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/car_list
            try {
                String postReceiverUrl = BaseUrl.baseurl + "car_list?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();


                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(urlParameters);
                writer.flush();
                String response = "";
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
                writer.close();
                reader.close();
                Log.e("Json Login Response", ">>>>>>>>>>>>" + response);
                return response;
            } catch (UnsupportedEncodingException e1) {

                e1.printStackTrace();
            } catch (IOException e1) {

                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // progressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            MyCarBean myCarBean = new MyCarBean();
                            myCarBean.setId(jsonObject1.getString("id"));
                            myCarBean.setCarname(jsonObject1.getString("car_name"));
                            //myCarBean.setCarname(jsonObject1.getString("car_name"));
                            myCarBeanArrayList.add(myCarBean);

                        }

                        customSpinnerAdapter = new CustomSpinnerAdapter(SignupActivity.this, android.R.layout.simple_spinner_item, myCarBeanArrayList);
                        servicetype.setAdapter(customSpinnerAdapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    public class CustomSpinnerAdapter extends ArrayAdapter<MyCarBean> {
        Context context;
        Activity activity;
        private ArrayList<MyCarBean> items;

        public CustomSpinnerAdapter(Context context, int resourceId, ArrayList<MyCarBean> aritems) {
            super(context, resourceId, aritems);
            this.context = context;

            this.items = aritems;
        }

        private class ViewHolder {
            TextView headername;
            TextView cartype;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public MyCarBean getItem(int position) {
//		Log.v("", "items.get("+position+")= "+items.get(position));
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
//		final BloodGroupPojo mytempojo = getItem(position);
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.spn_head_lay, null);
                holder = new ViewHolder();
                holder.headername = (TextView) convertView.findViewById(R.id.heading);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.headername.setText(items.get(position).getCarname());
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
//			final BloodGroupPojo mytempojo = getItem(position);
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.loc_spn_lay, null);
                holder = new ViewHolder();
                holder.cartype = (TextView) convertView.findViewById(R.id.cartype);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.cartype.setText(items.get(position).getCarname());


            return convertView;
        }
    }


    public class JsonSignupAsc extends AsyncTask<String, String, String> {

        String Jsondata;
        private boolean checkdata = false;

        protected void onPreExecute() {
            try {
                super.onPreExecute();
                //  prgressbar.setVisibility(View.VISIBLE);
                if (ac_dialog != null) {
                    ac_dialog.show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                checkdata = true;

            }

        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/user_update?user_id=21&first_name=er&last_name=vijay&mobile=8889994272&email=v@gmail.com&lang=en&currency=inr&place=indore&country=india&register_id=123&ios_register_id=321&lat=22.123456&lon=75.123456


            String charset = "UTF-8";
            String requestURL = BaseUrl.baseurl + "signup?";
            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addFormField("first_name", username_str);
                multipart.addFormField("last_name", last_name_str);
                multipart.addFormField("email", email_str);
                multipart.addFormField("mobile", mobile_str);
                multipart.addFormField("password", password_str);
                multipart.addFormField("lang_id", "");
                multipart.addFormField("lat", "" + latitude);
                multipart.addFormField("lon", "" + longitude);
                multipart.addFormField("register_id", firebase_regid);
                multipart.addFormField("type", "AGENT");
                multipart.addFormField("car_model", car_model_str);
                multipart.addFormField("year_of_manufacture", car_manuyear);
                multipart.addFormField("car_color", "Yellow");
                //  multipart.addFormField("car_color", car_col_str);
                multipart.addFormField("car_number", car_registrationnum_str);
                multipart.addFormField("car_type_id", "10");
                //  multipart.addFormField("car_type_id", cartype_id_str);
                if (INS_PATH.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(INS_PATH);
                    multipart.addFilePart("insurance", ImageFile);
                }
                if (DRIVER_IMG.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(DRIVER_IMG);
                    multipart.addFilePart("image", ImageFile);
                }
                if (CAR_IMG_PATH.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(CAR_IMG_PATH);
                    multipart.addFilePart("car_image", ImageFile);
                }
                if (CAR_INS_PATH.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(CAR_INS_PATH);
                    multipart.addFilePart("car_document", ImageFile);
                }
                if (DRIVER_LIC_PATH.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(DRIVER_LIC_PATH);
                    multipart.addFilePart("license", ImageFile);
                }
                List<String> response = multipart.finish();
                for (String line : response) {
                    Jsondata = line;
                    Log.e("Update Response ====", Jsondata);

                }
                JSONObject object = new JSONObject(Jsondata);
                return Jsondata;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //   prgressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                mySession.setlogindata(result);
                mySession.signinusers(true);
                mySession.onlineuser(false);
                setStartTime();

                Intent i = new Intent(SignupActivity.this, MainTabActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);

            }

        }


    }

    private void setStartTime() {

        AlarmManager alarmMgr = (AlarmManager) (SignupActivity.this).getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MyReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        System.out.println("cache_data alarm set n time zone dinesh: " + calendar.getTimeZone().getDisplayName());
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 2, alarmIntent);
    }

}
