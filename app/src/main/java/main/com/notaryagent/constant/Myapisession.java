package main.com.notaryagent.constant;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by technorizen on 7/8/18.
 */

public class Myapisession {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;


    private static final String PREF_NAME = "MyApiPref";

    public static final String KEY_VEHICLETYPE = "vehicletype";
    public static final String KEY_SIZETYPE = "sizetype";
    public static final String KEY_PACKAGETYPE = "packagetype";



    public Myapisession(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setKeyVehicletype(String vehicletype) {
        editor.putString(KEY_VEHICLETYPE, vehicletype);
        //editor.putString(KEY_TYPE, type);
        editor.commit();

    }
    public void setKeyPackagetype(String packagetype) {
        editor.putString(KEY_PACKAGETYPE, packagetype);
        //editor.putString(KEY_TYPE, type);
        editor.commit();

    }
    public void setKeySizetype(String sizetype) {
        editor.putString(KEY_SIZETYPE, sizetype);
        //editor.putString(KEY_TYPE, type);
        editor.commit();

    }

    public String getKeyVehicletype() {
        return pref.getString(KEY_VEHICLETYPE, null);
    }
    public String getKeyPackagetype() {
        return pref.getString(KEY_PACKAGETYPE, null);
    }
    public String getKeySizetype() {
        return pref.getString(KEY_SIZETYPE, null);
    }






}
