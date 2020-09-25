package main.com.notaryagent.paymentclasses;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shyam on 11/04/2016.
 */
public class MySavedCardInfo {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;


    private static final String PREF_NAME = "MyCard";
    private static final String KEY_CARDDATA = "carddata";


    public MySavedCardInfo(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void clearCardData() {
        editor.clear();
        editor.commit();
    }
    public void setKeyCarddata(String carddata) {
        editor.putString(KEY_CARDDATA, carddata);
        //editor.putString(KEY_TYPE, type);
        editor.commit();

    }
    public String getKeyCarddata() {
        return pref.getString(KEY_CARDDATA, null);
    }


}