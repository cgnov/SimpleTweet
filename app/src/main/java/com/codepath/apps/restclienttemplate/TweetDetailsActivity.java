package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TweetDetailsActivity extends AppCompatActivity {
    final int MEDIA_RADIUS = 100;

    ImageView ivProfileImage;
    TextView tvBody;
    TextView tvDisplayName;
    TextView tvScreenName;
    TextView tvAbsoluteTime;
    ImageView ivFirstPhoto;

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

        Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        tvBody.setText(tweet.body);
        tvScreenName.setText(tweet.user.screenName);
        tvDisplayName.setText(tweet.user.name);
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
    }
}