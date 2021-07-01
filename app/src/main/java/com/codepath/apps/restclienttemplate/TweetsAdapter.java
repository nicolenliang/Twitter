package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

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

    public void clear()
    {
        tweets.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<Tweet> list)
    {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    // define viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView tvBody, tvScreenName, tvName, tvTimestamp;
        ImageView ivProfileImage, ivEmbed;
        CardView cvEmbed;
        Button btnReply, btnRetweet, btnLike;


        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivEmbed = itemView.findViewById(R.id.ivEmbed);
            cvEmbed = itemView.findViewById(R.id.cvEmbed);
            btnReply = itemView.findViewById(R.id.btnReply);
            btnRetweet = itemView.findViewById(R.id.btnRetweet);
            btnLike = itemView.findViewById(R.id.btnLike);

            itemView.setOnClickListener(this);
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
            if (tweet.embedUrl != "")
            {
                ivEmbed.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.embedUrl)
                        .into(ivEmbed);
            }
            else
            {
                ivEmbed.setVisibility(View.GONE);
                cvEmbed.setVisibility(View.GONE);
            }
            btnLike.setText("  " + tweet.favCount);
            btnRetweet.setText("  " + tweet.rtCount);
        }

        @Override
        public void onClick(View v)
        {
            int position = getAdapterPosition();
            // check for valid item position
            if (position != RecyclerView.NO_POSITION)
            {
                // get the tweet at the position
                Tweet tweet = tweets.get(position);
                // create Intent to display TweetDetails
                Intent intent = new Intent(context, TweetDetailsActivity.class);
                // pass tweet as an extra; parcel wrapped bc it is a custom obj
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                // show the activity
                context.startActivity(intent);
            }
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
