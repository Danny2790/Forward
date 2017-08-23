package com.akash.forward.Utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.akash.forward.Models.UserInfo;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;
import static com.akash.forward.Constants.ForwardConstant.FACEBOOK_LOGIN;
import static com.akash.forward.Constants.ForwardConstant.FORWARD_PREF;
import static com.akash.forward.Constants.ForwardConstant.TWITTER_LOGIN;
import static com.akash.forward.Constants.ForwardConstant.USER_EMAIl;
import static com.akash.forward.Constants.ForwardConstant.USER_ID;
import static com.akash.forward.Constants.ForwardConstant.USER_INFO;
import static com.akash.forward.Constants.ForwardConstant.USER_NAME;

/**
 * Created by akash on 8/19/2017.
 */

public class SPManager {

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(FORWARD_PREF, MODE_PRIVATE);
    }

    public static void saveUserInfo(Context context, String userid, String name, String email) {
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_ID, userid);
        editor.putString(USER_NAME, name);
        editor.putString(USER_EMAIl, email);
        editor.apply();
    }

    public static void saveUserInfo(Context context, String response) {
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_INFO, response);
        editor.apply();
    }

    public static UserInfo getUserInfo(Context context) {
        Gson gson = new Gson();
        String response = getSharedPreferences(context).getString(USER_INFO, null);
        return gson.fromJson(response, UserInfo.class);
    }

    public static void setFacebookLoggedIn(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FACEBOOK_LOGIN, true);
        editor.apply();
    }

    public static void setTwitterLoggedIn(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(TWITTER_LOGIN, true);
        editor.apply();
    }

    public static boolean isLoggedInWithFacebook(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(FACEBOOK_LOGIN, false);
    }

    public static boolean isLoggedInWithTwitter(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(TWITTER_LOGIN, false);
    }

    public static void clearSession(Context context) {
        getSharedPreferences(context).edit().clear().apply();
    }
}
