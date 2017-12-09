package com.exoncloud.mobi.distributeapp.model;

/**
 * Created by Sameera on 11/5/2017.
 */

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.exoncloud.mobi.distributeapp.LoginActivity;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_DN = "dn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // ls  (make variable public to access from outside)
    public static final String KEY_LS = "ls";

    // ul  (make variable public to access from outside)
    public static final String KEY_UL = "ul";

    // org  (make variable public to access from outside)
    public static final String KEY_ORG = "org";

    // gup  (make variable public to access from outside)
    public static final String KEY_GUP = "gup";


    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String name, int id, int ls, int ul, int gup, int org){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing KEY_LS in pref
        editor.putString(KEY_LS, ls+"");

        // Storing KEY_UL in pref
        editor.putString(KEY_UL, ul+"");

        // Storing KEY_ORG in pref
        editor.putString(KEY_ORG, org +"");

        // Storing KEY_GUP in pref
        editor.putString(KEY_GUP, gup+"");

        // Storing KEY_DN in pref
        editor.putString(KEY_DN, id+"");

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_LS, pref.getString(KEY_LS, null));

        // user email id
        user.put(KEY_UL, pref.getString(KEY_UL, null));

        // user email id
        user.put(KEY_ORG, pref.getString(KEY_ORG, null));

        // user email id
        user.put(KEY_GUP, pref.getString(KEY_GUP, null));

        // user dn id
        user.put(KEY_DN, pref.getString(KEY_DN, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
