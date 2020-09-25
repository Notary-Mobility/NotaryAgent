package main.com.notaryagent.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import main.com.notaryagent.R;
import main.com.notaryagent.constant.CountryBean;
import main.com.notaryagent.constant.RideBean;

public class DemoAct extends AppCompatActivity {
    SpecilityAdapter specilityAdapter;
    private Spinner specialspn;
    private ListView docotrslist;
    private DoctorsAdp doctorsAdp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        idinit();
    }

    private void idinit() {
        specialspn = findViewById(R.id.specialspn);
        docotrslist = findViewById(R.id.docotrslist);
        doctorsAdp = new DoctorsAdp(DemoAct.this);
        docotrslist.setAdapter(doctorsAdp);
    }

    public class SpecilityAdapter extends BaseAdapter {
        Context context;

        LayoutInflater inflter;
        private ArrayList<CountryBean> values;

        public SpecilityAdapter(Context applicationContext, ArrayList<CountryBean> values) {
            this.context = applicationContext;
            this.values = values;

            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {

            return values == null ? 0 : values.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.spn_head_lay, null);

            TextView names = (TextView) view.findViewById(R.id.heading);
            //  TextView countryname = (TextView) view.findViewById(R.id.countryname);


            names.setText(values.get(i).getCountry());


            return view;
        }
    }
    public class DoctorsAdp extends BaseAdapter {

        String[] result;
        Context context;
        ArrayList<RideBean> ridehislist;
        private LayoutInflater inflater = null;


        public DoctorsAdp(Activity activity) {
            this.ridehislist = ridehislist;
            this.context = activity;

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 4;
            // return ridehislist == null ? 0 : ridehislist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            View rowView;

            rowView = inflater.inflate(R.layout.ride_history_lay, null);
            TextView pickuplocation = (TextView) rowView.findViewById(R.id.pickuplocation);
            TextView date_tv = (TextView) rowView.findViewById(R.id.date_tv);
            TextView statustv = (TextView) rowView.findViewById(R.id.statustv);
            ImageView car_img = (ImageView) rowView.findViewById(R.id.car_img);


            return rowView;
        }

    }


}
/*
https://api.moh.gov.kw/api/DoctorVist/GetDoctorBySpeciality/5


https://api.moh.gov.kw/api/DoctorVist/GetSpeciality
https://api.moh.gov.kw/api/DoctorVist/GetDoctorBySpeciality/5
*/