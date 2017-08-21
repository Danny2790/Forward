package com.akash.forward.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.akash.forward.Models.UserInfo;
import com.akash.forward.R;
import com.akash.forward.Utility.SPManager;
import com.akash.forward.Utility.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;

import static com.akash.forward.Constants.ForwardConstant.LOGIN_FAILED;


public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    private String TAG = LoginActivity.class.getSimpleName();
    private LoginButton loginButton;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        setFacebookLogin();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "onStart: " + currentUser.toString());
            launchFeedActivity();
        }
    }

    private void launchFeedActivity() {
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void setFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFacebookProfileInfo(loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: facebook ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "onError: facebook " + error.toString());
            }
        });
    }

    public void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: " + "sign in success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            launchFeedActivity();
                            Log.d(TAG, "onComplete: " + " user display name " + user.getDisplayName() + " email  " + user.getEmail());
                        } else {
                            Utils.showMessage(LoginActivity.this, LOGIN_FAILED);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void getFacebookProfileInfo(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject,
                                            GraphResponse response) {
                        // Getting FB User Data
                        getFacebookData(jsonObject);
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void getFacebookData(JSONObject object) {
        try {
            if (object != null) {
                String id = object.getString("id");
                URL profile_pic;
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                Log.i("profile_pic", profile_pic.toString() + "");
                object.put("profile_pic", profile_pic);
                Log.d(TAG, "getFacebookData: " + object.toString());
                SPManager.saveUserInfo(this, object.toString());
                UserInfo userInfo = SPManager.getUserInfo(this);
                Log.d(TAG, "get user info : " + userInfo.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Bundle bundle = new Bundle();
//        try {
//            String id = object.getString("id");
//            bundle.putString("idFacebook", id);
//            if (object.has("first_name"))
//                bundle.putString("first_name", object.getString("first_name"));
//            if (object.has("email"))
//                bundle.putString("email", object.getString("email"));
//            SPManager.saveUserInfo(this, id, object.getString("first_name"), object.getString("email"));
//        } catch (Exception e) {
//            Log.d(TAG, "BUNDLE Exception : " + e.toString());
//        }
    }

    public void signOutFirebase() {
        FirebaseAuth.getInstance().signOut();
    }
}
