package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    final int MEDIA_RADIUS = 100;
    Context context;
    List<Tweet> tweets;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);

        // Bind the tweet with the ViewHolder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clear out all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    // Define a ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRetweeted;
        TextView tvRetweetedStatus;
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvDisplayName;
        TextView tvScreenName;
        TextView tvRelativeTime;
        ImageView ivFirstPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRetweeted = itemView.findViewById(R.id.ivRetweeted);
            tvRetweetedStatus = itemView.findViewById(R.id.tvRetweetedStatus);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvDisplayName = itemView.findViewById(R.id.tvDisplayName);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            ivFirstPhoto = itemView.findViewById(R.id.ivFirstPhoto);
        }

        public void bind(final Tweet tweet) {
            if(tweet.retweetedStatus){
                ivRetweeted.setVisibility(View.VISIBLE);
                tvRetweetedStatus.setVisibility(View.VISIBLE);
                tvRetweetedStatus.setText(String.format("%s Retweeted", tweet.retweetUser.name));
            } else {
                ivRetweeted.setVisibility(View.GONE);
                tvRetweetedStatus.setVisibility(View.GONE);
            }
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvDisplayName.setText(tweet.user.name);
            Glide.with(context).load(tweet.user.profileImageUrl).transform(new CircleCrop()).into(ivProfileImage);
            tvRelativeTime.setText(getRelativeTimeAgo(tweet.createdAt));
            if(tweet.mediaLink!=null) {
                ivFirstPhoto.setVisibility(View.VISIBLE);

                Glide.with(context).load(tweet.mediaLink).transform(new RoundedCorners(MEDIA_RADIUS)).into(ivFirstPhoto);
            } else {
                ivFirstPhoto.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, TweetDetailsActivity.class);
                    i.putExtra("tweet", Parcels.wrap(tweet));
                    context.startActivity(i);
                }
            });
        }

        // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
        public String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            String relativeDate = "";
            try {
                long dateMillis = sf.parse(rawJsonDate).getTime();
                relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return String.format("· %s", relativeDate);
        }
    }
}
