package com.codepath.apps.restclienttemplate.models;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {
    public static final String TAG = "TweetJson";

    public String body;
    public String createdAt;
    public User user;
    public String mediaLink;
    public int numLikes;
    public boolean retweetedStatus;
    public User retweetUser;
    public int numRetweets;
    public long id;
    public boolean liked;

    // Empty constructor needed by Parceler library
    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.retweetedStatus = jsonObject.has("retweeted_status");
        if(tweet.retweetedStatus){
            tweet.retweetUser = User.fromJson(jsonObject.getJSONObject("user"));
            jsonObject = jsonObject.getJSONObject("retweeted_status");
        }
        tweet.id = jsonObject.getLong("id");
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.liked = jsonObject.getBoolean("favorited");
        if(jsonObject.getJSONObject("entities").has("media")) {
            JSONArray media = jsonObject.getJSONObject("entities").getJSONArray("media");
            JSONObject nextMedia;
            int nextMediaIndex = 0;
            while(tweet.mediaLink==null && nextMediaIndex<media.length()) {
                nextMedia = (JSONObject) media.get(nextMediaIndex);
                if (nextMedia.getString("type").equals("photo")) {
                    tweet.mediaLink = nextMedia.getString("media_url_https");
                }
                nextMediaIndex++;
            }
        }
        tweet.numLikes = jsonObject.getInt("favorite_count");
        tweet.numRetweets = jsonObject.getInt("retweet_count");
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
