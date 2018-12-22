package com.example.bikash.twittapper.application;

import android.app.Application;
import android.util.Log;

import com.example.bikash.twittapper.R;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

public class TwittApper extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.key), getResources().getString(R.string.secret_key)))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }
}
