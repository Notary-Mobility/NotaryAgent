package main.com.notaryagent.constant;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import main.com.notaryagent.R;


/**
 * Created by technorizen on 20/2/18.
 */

public class BasicCustomAdp extends ArrayAdapter<String> {
    Context context;
    Activity activity;
    private ArrayList<String> carmodel;

    public BasicCustomAdp(Context context, int resourceId, ArrayList<String> carmodel) {
        super(context, resourceId);
        this.context = context;
        this.carmodel = carmodel;
    }

    private class ViewHolder {
        TextView headername;
        TextView cartype;
    }

    @Override
    public int getCount() {
        return carmodel.size();
    }

    @Override
    public String getItem(int position) {
        return carmodel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
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
        holder.headername.setText("" + carmodel.get(position));

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
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

        holder.cartype.setText("" + carmodel.get(position));
        return convertView;
    }
}




