package org.irestaurant.irm.Database;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.irestaurant.irm.LoginActivity;
import org.irestaurant.irm.MainActivity;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String PHONE = "PHONE";
    public static final String PASSWORD = "PASSWORD";
    public static final String RESPHONE = "RESPHONE";
    public static final String RESNAME = "RESNAME";
    public static final String RESADDRESS = "RESADDRESS";

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(int id, String name, String phone, String password, String resname,String resphone, String resaddress){
        editor.putBoolean(LOGIN, true);
        editor.putInt(ID, id);
        editor.putString(NAME, name);
        editor.putString(PHONE, phone);
        editor.putString(PASSWORD, password);
        editor.putString(RESNAME, resname);
        editor.putString(RESPHONE, resphone);
        editor.putString(RESADDRESS, resaddress);
        editor.apply();
    }

    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLoggin(){

        if (!this.isLoggin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((MainActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(PHONE, sharedPreferences.getString(PHONE, null));
        user.put(PASSWORD, sharedPreferences.getString(PASSWORD, null));
        user.put(RESNAME, sharedPreferences.getString(RESNAME, null));
        user.put(RESPHONE, sharedPreferences.getString(RESPHONE, null));
        user.put(RESADDRESS, sharedPreferences.getString(RESADDRESS, null));
        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((MainActivity) context).finish();
    }

}
