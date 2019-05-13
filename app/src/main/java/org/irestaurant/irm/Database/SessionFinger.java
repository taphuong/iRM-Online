package org.irestaurant.irm.Database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionFinger {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;
    private static final String FINGER = "FINGER";
    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";

    public SessionFinger(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(FINGER, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void creatFinger(String email, String password){
        editor.putBoolean(FINGER, true);
        editor.putString(EMAIL, email);
        editor.putString(PASSWORD, password);
        editor.apply();
    }
    public HashMap<String, String> getFinger (){
        HashMap<String, String> finger = new HashMap<>();
        finger.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        finger.put(PASSWORD, sharedPreferences.getString(PASSWORD, null));
        return finger;
    }
    public boolean isFinger(){return sharedPreferences.getBoolean(FINGER, false);    }

    public void clearFinger(){
        editor.clear();
        editor.commit();
    }

    public void checkFinger(){

        if (!this.isFinger()){
            clearFinger();
        }
    }

}
