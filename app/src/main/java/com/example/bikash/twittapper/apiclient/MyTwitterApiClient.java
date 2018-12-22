package com.example.bikash.twittapper.apiclient;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class MyTwitterApiClient extends TwitterApiClient {
    public MyTwitterApiClient(TwitterSession session) {
        super(session);
    }

    /**
     * Provide CustomService with defined endpoints
     */
    public Api getCustomService() {
        return getService(Api.class);
    }
    public interface Api {
        @GET("/1.1/followers/list.json")
        Call<ResponseBody> list(@Query("user_id") long id);
        @POST("/1.1/statuses/update.json")
        Call<ResponseBody> update(@Query("status") String status);
    }
}
