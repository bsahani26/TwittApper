package com.example.bikash.twittapper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bikash.twittapper.R;
import com.example.bikash.twittapper.apiclient.MyTwitterApiClient;
import com.example.bikash.twittapper.utils.TwitterHelper;
import com.example.bikash.twittapper.utils.Util;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private TwitterLoginButton twitterLoginButton;
    private TwitterAuthClient client;
    private Context context;
    private ProgressBar progressBar;
    private TextView waitLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new TwitterAuthClient();
        context = this;
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressLayout);
        waitLayout = findViewById(R.id.waitLayout);
        if (Util.isNetworkAvailable(context)) {
            logintToTwitter();
        } else {
            Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void logintToTwitter() {
        if (TwitterHelper.getTwitterSession() == null) {
            if (progressBar != null && waitLayout != null) {
                waitLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
            twitterLoginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    startSession(result.data);
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(MainActivity.this, "Failed to authenticate. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User already authenticated", Toast.LENGTH_SHORT).show();
            startSession(TwitterHelper.getTwitterSession());
        }
    }

    public void startSession(final TwitterSession twitterSession) {
        client.requestEmail(twitterSession, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                getData(null);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(MainActivity.this, "Failed to authenticate. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getData(View view) {
        if (TwitterHelper.getTwitterSession() != null) {
            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
            Call<User> call = twitterApiClient.getAccountService().verifyCredentials(true, false, true);
            call.enqueue(new Callback<User>() {
                @Override
                public void success(Result<User> result) {
                    if (progressBar != null && waitLayout != null) {
                        waitLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                    User user = result.data;

                    String imageProfileUrl = user.profileImageUrlHttps;
                    imageProfileUrl = imageProfileUrl.replace("_normal", "");

                    Bundle bundle = new Bundle();
                    bundle.putLong("userId", user.id);
                    bundle.putString("userName", user.name);
                    bundle.putString("userScreenName", user.screenName);
                    bundle.putInt("followersCount", user.followersCount);
                    bundle.putInt("friendsCount", user.friendsCount);
                    bundle.putString("location", user.location);
                    bundle.putString("imageUri", imageProfileUrl);
                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(MainActivity.this, "Failed to authenticate. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "First loging to Twitter auth to Verify Credentials.", Toast.LENGTH_SHORT).show();
        }
//        getFollowerList();
    }
/*

this method can be use to get the list of followers
 */

    private void getFollowerList() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();

        Call<User> userCall = twitterApiClient.getAccountService().verifyCredentials(true, false, true);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> call) {
                MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(TwitterHelper.getTwitterSession());
                myTwitterApiClient.getCustomService().list(call.data.id).enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void success(Result<ResponseBody> response) {
                        Log.e("list", response.data.toString());
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();

                        reader = new BufferedReader(new InputStreamReader(response.data.byteStream()));

                        String line;

                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        String result = sb.toString();
                        Log.i("Response :", result);
                        JsonObject obj = new Gson().fromJson(result, JsonObject.class);
                        JsonArray usersArray = (JsonArray) obj.get("users");
                        Log.i("Total count is :", usersArray.size() + "");

                        JsonArray userIds = new JsonArray();
                        JsonArray userNames = new JsonArray();
                        for (int i = 0; i < usersArray.size(); i++) {
                            JsonObject userObject = (JsonObject) usersArray.get(i);
                            userIds.add(userObject.get("id_str"));
                            userNames.add(userObject.get("name"));
                        }
                        Log.i("Response :", userNames.toString());

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.add("twitter_ids", userIds);

                    }

                    @Override
                    public void failure(TwitterException exception) {

                    }

                });
            }

            @Override
            public void failure(TwitterException exception) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (client != null)
            client.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }
}
