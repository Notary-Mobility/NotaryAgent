package main.com.notaryagent.draglocation;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by technorizen on 10/9/18.
 */

public class LoadAdressSyncPlaceId extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {

        String address ="",address_short="";
        //  prgressbar.setVisibility(View.VISIBLE);
        try {

            JSONObject jk = new JSONObject(strings[0]);
            JSONArray results = jk.getJSONArray("results");
            JSONObject jk1 = results.getJSONObject(0);
            String add1 = jk1.getString("formatted_address");
            String place_id = jk1.getString("place_id");
            Log.e("PLACE ID >> "," >> "+place_id);
            boolean isFound = add1.indexOf("Unnamed Road") !=-1? true: false;
            if (isFound){
                Log.e("FOUND UNNAMED >"," > ");
                JSONArray jsonArray = jk1.getJSONArray("address_components");
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (address.equalsIgnoreCase("")){
                        address = jsonObject.getString("long_name");

                    }
                    else {
                        address = address+","+jsonObject.getString("long_name");

                    }
                    address_short = address_short+","+jsonObject.getString("short_name");
                }
                add1 = address;
                Log.e("LONG ADD  0>"," > "+add1);
                add1= add1.replace("Unnamed Road,","");
                Log.e("LONG ADD  1>"," > "+add1);
                Log.e("LONG ADD >"," > "+address);
                Log.e("SHORT ADD >"," > "+address_short);
            }
            Log.e("SCROLL ADDRESS >"," > "+add1);
            Log.e("SCROLL ARRAY >"," > "+results);
            // System.out.println("SCROLL ARRAY 2===============================" + results);

            // prgressbar.setVisibility(View.GONE);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("place_id",""+place_id);
            jsonObject.put("address",""+add1);
            //return add1;
            return jsonObject.toString();

        } catch (Exception e) {
            // prgressbar.setVisibility(View.GONE);
            e.printStackTrace();
            return "";
        }
    }





}