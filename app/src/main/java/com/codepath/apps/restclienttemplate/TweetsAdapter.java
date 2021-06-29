package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>
{
    // pass in the context and list of tweets
    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets)
    {
        this.context = context;
        this.tweets = tweets;
    }

    // define viewholder
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvTimestamp;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        public void bind(Tweet tweet)
        {
            tvBody.setText(tweet.body);
            tvScreenName.setText("@" + tweet.user.screenName);
            tvName.setText(tweet.user.name);
            // load profile img with Glide
            Glide.with(context)
                    .load(tweet.user.publicImgURL)
                    .transform(new CircleCrop())
                    .into(ivProfileImage);
            // formatting timestamp
            String time = tweet.getRelativeTime(tweet.createdAt);
            time = time.substring(0, time.indexOf(" ") + 2).replaceAll("\\s", "");
            tvTimestamp.setText("\u00B7 " + time);
        }
    }

    // for each row, inflate the layout for a tweet
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // bind values based on position of the element
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position)
    {
        // get the data at position
        Tweet tweet = tweets.get(position);
        // bind the tweet with the viewholder holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount()
    {
        return tweets.size();
    }

}
