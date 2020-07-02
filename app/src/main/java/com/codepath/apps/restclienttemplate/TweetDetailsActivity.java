package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {
    final int MEDIA_RADIUS = 100;
    public static final String TAG = "TweetDetailsActivity";

    ImageView ivProfileImage;
    TextView tvBody;
    TextView tvDisplayName;
    TextView tvScreenName;
    TextView tvAbsoluteTime;
    ImageView ivFirstPhoto;
    TextView tvLikeCount;
    TextView tvRetweetCount;
    ImageButton ibLike;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        ivProfileImage = findViewById(R.id.ivDetProfileImage);
        tvBody = findViewById(R.id.tvDetBody);
        tvDisplayName = findViewById(R.id.tvDetDisplayName);
        tvScreenName = findViewById(R.id.tvDetScreenName);
        tvAbsoluteTime = findViewById(R.id.tvDetAbsoluteTime);
        ivFirstPhoto = findViewById(R.id.ivDetFirstPhoto);
        tvLikeCount = findViewById(R.id.tvDetLikeCount);
        tvRetweetCount = findViewById(R.id.tvDetRetweetCount);
        ibLike = findViewById(R.id.ibDetLike);

        final Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        tvBody.setText(tweet.body);
        tvScreenName.setText(tweet.user.screenName);
        tvDisplayName.setText(tweet.user.name);
        tvLikeCount.setText(String.valueOf(tweet.numLikes));
        tvRetweetCount.setText(String.valueOf(tweet.numRetweets));
        Glide.with(this).load(tweet.user.profileImageUrl).transform(new CircleCrop()).into(ivProfileImage);
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat oldFormat = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        SimpleDateFormat newFormat = new SimpleDateFormat("h:mm aa '·' M/d/yy", Locale.ENGLISH);
        try {
            Date postDate = oldFormat.parse(tweet.createdAt);
            tvAbsoluteTime.setText(newFormat.format(postDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(tweet.mediaLink!=null) {
            ivFirstPhoto.setVisibility(View.VISIBLE);

            Glide.with(this).load(tweet.mediaLink).transform(new RoundedCorners(MEDIA_RADIUS)).into(ivFirstPhoto);
        } else {
            ivFirstPhoto.setVisibility(View.GONE);
        }
        client = TwitterApp.getRestClient(this);
        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tweet.liked){
                    tvLikeCount.setText(String.valueOf(Integer.parseInt((String) tvLikeCount.getText())-1));
                    tweet.liked = false;
                    client.unlikeTweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            // Successful, don't need to do anything
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure: " + response, throwable);
                            tvLikeCount.setText(String.valueOf(Integer.parseInt((String) tvLikeCount.getText())+1));
                            tweet.liked = true;
                        }
                    });
                } else {
                    tweet.liked = true;
                    tvLikeCount.setText(String.valueOf(Integer.parseInt((String) tvLikeCount.getText())+1));
                    client.likeTweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {

                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure: " + response, throwable);
                            tweet.liked = false;
                            tvLikeCount.setText(String.valueOf(Integer.parseInt((String) tvLikeCount.getText())-1));
                        }
                    });
                }
            }
        });
    }
}