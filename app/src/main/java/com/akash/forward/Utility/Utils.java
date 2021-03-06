package com.akash.forward.Utility;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.twitter.sdk.android.Twitter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by akash on 8/19/2017.
 */

public class Utils {
    private static FirebaseDatabase mDatabase;

    public static void getHashKey(Context context) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo("com.akash.forward", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.d("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    public static boolean isUserLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Log.d("LoginActivity ", "current token  : " + accessToken);
        return accessToken != null;
    }

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void logOutUserFromFacebook(Context context) {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        SPManager.clearSession(context);
    }

    public static void logOutUserFromTwitter(Context context) {
        FirebaseAuth.getInstance().signOut();
        Twitter.logOut();
        SPManager.clearSession(context);
    }

}
