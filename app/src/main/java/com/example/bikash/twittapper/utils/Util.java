package com.example.bikash.twittapper.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.example.bikash.twittapper.R;
import com.squareup.picasso.Picasso;

public class Util {
    public static void hideKeyboard(Activity activity) {
        if (activity == null) {
            return;
        }
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    public static void loadImage(Context context, String imageProfileUrl, ImageView profilPic) {
        Picasso.with(context)
                .load(imageProfileUrl)
                .placeholder(context.getResources().getDrawable(R.drawable.user_icon))
                .into(profilPic);
    }
}
