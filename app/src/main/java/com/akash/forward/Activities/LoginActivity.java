package com.akash.forward.Activities;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

import static com.akash.forward.Constants.ForwardConstant.FIRST_NAME;
import static com.akash.forward.Constants.ForwardConstant.ID;
import static com.akash.forward.Constants.ForwardConstant.LOGIN_FAILED;
import static com.akash.forward.Constants.ForwardConstant.TWITTER_KEY;
import static com.akash.forward.Constants.ForwardConstant.TWITTER_SECRET;


public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    private String TAG = LoginActivity.class.getSimpleName();
    private LoginButton loginButton;
    FirebaseAuth firebaseAuth;
    private TwitterLoginButton twitterLoginButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                TWITTER_KEY,
                TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...");

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        setFacebookLogin();
        setTwitterLogin();
        Log.d(TAG, "onCreate: current userr : " + currentUser);
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

    private void setTwitterLogin() {
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "success: " + result);
                TwitterSession session = result.data;
                // requestTwitterEmail(session);
                progressDialog.show();
                if (session != null) {
                    try {
                        Log.d(TAG, "success: session user name  : " + session.getUserName());
                        Log.d(TAG, "success: session user id : " + session.getUserId());
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(FIRST_NAME, session.getUserName());
                        jsonObject.put(ID, session.getUserId());
                        SPManager.saveUserInfo(LoginActivity.this, jsonObject.toString());
                        Log.d(TAG, "success: " + SPManager.getUserInfo(LoginActivity.this).toString());
                        handleTwitterSession(result.data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void failure(TwitterException exception) {
                Log.d(TAG, "failure: " + exception);
                Utils.showMessage(LoginActivity.this, LOGIN_FAILED);
            }

        });
    }

    public void requestTwitterEmail(TwitterSession session) {
        TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                Log.d(TAG, "success: " + result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.d(TAG, "fail  twitter email : " + exception);
            }
        });
    }

    private void handleTwitterSession(TwitterSession session) {
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);
        Log.d(TAG, "handleTwitterSession: token : " + session.getAuthToken().token + " secret " + session.getAuthToken().secret);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: success twitter firebase");
                            SPManager.setTwitterLoggedIn(LoginActivity.this);
                            launchFeedActivity();
                        } else {
                            Log.d(TAG, "onComplete: fail twitter" + task.getException());
                            Utils.showMessage(LoginActivity.this, LOGIN_FAILED);
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void setFacebookLogin() {
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressDialog.show();
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
                Utils.showMessage(LoginActivity.this, LOGIN_FAILED);
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
                            SPManager.setFacebookLoggedIn(LoginActivity.this);
                            launchFeedActivity();
                            Log.d(TAG, "onComplete: " + " user display name " + user.getDisplayName() + " email  " + user.getEmail());
                        } else {
                            Utils.showMessage(LoginActivity.this, LOGIN_FAILED);
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: twitter code " + TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE);
        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: twitter");
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
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
