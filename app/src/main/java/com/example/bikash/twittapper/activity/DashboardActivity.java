package com.example.bikash.twittapper.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bikash.twittapper.R;
import com.example.bikash.twittapper.apiclient.MyTwitterApiClient;
import com.example.bikash.twittapper.utils.TwitterHelper;
import com.example.bikash.twittapper.utils.Util;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class DashboardActivity extends AppCompatActivity {

    private ListView listView;
    TextView followerNumber,followingNumber,screenName,name, locationValue, tweetText,rewardPointView;
    private String userName,userScreenName, location, imageUri;
    private int followingNo, followerNo,rewardPoint;
    private long userId;
    private Context context;
    private SharedPreferences preferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        listView = findViewById(R.id.listView);
        context = this;
        preferences = getApplicationContext().getSharedPreferences("RewardPoint", 0); // 0 - for private mode
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            userId = bundle.getLong("userId");
            userName = bundle.getString("userName");
            userScreenName = bundle.getString("userScreenName");
            location = bundle.getString("location");
            followerNo = bundle.getInt("followersCount");
            followingNo = bundle.getInt("friendsCount");
            imageUri = bundle.getString("imageUri");
        }
        name = findViewById(R.id.userName);
        locationValue = findViewById(R.id.location);
        screenName = findViewById(R.id.screenName);
        followerNumber = findViewById(R.id.numberOfFollower);
        followingNumber = findViewById(R.id.numberOfFollowing);
        tweetText = findViewById(R.id.tweetText);
        rewardPointView = findViewById(R.id.rewardPoint);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRewardPoint(false);
        setValues();
        Util.loadImage(this, imageUri, (ImageView) findViewById(R.id.profilePic));
        loadTimeLine();
    }

    public void setValues() {
        name.setText(userName);
        screenName.setText("@"+userScreenName);
        followingNumber.setText(followingNo+"");
        followerNumber.setText(followerNo+"");
        locationValue.setText(location);

    }
    public void updateStatus(View view) {
        Util.hideKeyboard((DashboardActivity)context);
        final String tText = tweetText.getText().toString().trim();
        if (!(tText.length() > 0)) {
            Toast.makeText(context,"please write something to teweet",Toast.LENGTH_SHORT).show();
            return;
        }
        tweetText.setText("");
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        Call<User> userCall = twitterApiClient.getAccountService().verifyCredentials(true, false, true);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(TwitterHelper.getTwitterSession());
                myTwitterApiClient.getCustomService().update(tText).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void success(Result<ResponseBody> response) {
                        Log.e("list",response.data.toString());
                        updateRewardPoint(true);
                        Toast.makeText(context,"Twitted Successfully",Toast.LENGTH_SHORT).show();
                        loadTimeLine();

                    }
                    @Override
                    public void failure(TwitterException exception) {
                        Toast.makeText(context,"Tweet UnSuccessfull",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(context,"please try again",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateRewardPoint(boolean isAdd) {
        SharedPreferences.Editor editor = preferences.edit();
        rewardPoint = preferences.getInt(userId + "", 0);
        if (isAdd) {
            rewardPoint = rewardPoint + 5;
            editor.putInt(userId + "", rewardPoint);
        }
        editor.apply();
        rewardPointView.setText(rewardPoint + "");
    }

    public void loadTimeLine() {
        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(userScreenName)
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .build();
        listView.setAdapter(adapter);
    }

}
