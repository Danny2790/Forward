package com.akash.forward;

import android.app.Application;
import android.util.Log;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import static com.akash.forward.Constants.ForwardConstant.TWITTER_KEY;
import static com.akash.forward.Constants.ForwardConstant.TWITTER_SECRET;

/**
 * Created by akash on 8/21/2017.
 */

public class ForwardApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }
}
