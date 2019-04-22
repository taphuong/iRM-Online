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
    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";
    public static final String RESPHONE = "RESPHONE";
    public static final String RESNAME = "RESNAME";
    public static final String RESADDRESS = "RESADDRESS";
    public static final String IMAGE = "IMAGE";

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String id, String name, String email, String password, String resname,String resphone, String resaddress, String image){
        editor.putBoolean(LOGIN, true);
        editor.putString(ID, id);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(PASSWORD, password);
        editor.putString(RESNAME, resname);
        editor.putString(RESPHONE, resphone);
        editor.putString(RESADDRESS, resaddress);
        editor.putString(IMAGE, image);
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
        user.put(ID, sharedPreferences.getString(ID, null));
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(PASSWORD, sharedPreferences.getString(PASSWORD, null));
        user.put(RESNAME, sharedPreferences.getString(RESNAME, null));
        user.put(RESPHONE, sharedPreferences.getString(RESPHONE, null));
        user.put(RESADDRESS, sharedPreferences.getString(RESADDRESS, null));
        user.put(IMAGE, sharedPreferences.getString(IMAGE, null));
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
