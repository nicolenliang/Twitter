package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;

public class TweetDetailsActivity extends AppCompatActivity
{
    Tweet tweet;
    TextView tvBody, tvScreenName, tvName, tvTimestamp;
    ImageView ivProfileImage, ivEmbed;
    CardView cvEmbed;
    Button btnReply, btnRetweet, btnLike;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        tvBody = findViewById(R.id.tvBody);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvName = findViewById(R.id.tvName);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivEmbed = findViewById(R.id.ivEmbed);
        cvEmbed = findViewById(R.id.cvEmbed);
        btnReply = findViewById(R.id.btnReply);
        btnRetweet = findViewById(R.id.btnRetweet);
        btnLike = findViewById(R.id.btnLike);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        tvBody.setText(tweet.body);
        tvScreenName.setText("@" + tweet.user.screenName);
        tvName.setText(tweet.user.name);
        Glide.with(this)
                .load(tweet.user.publicImgURL)
                .transform(new CircleCrop())
                .into(ivProfileImage);
        // getting detailed time (includes parsing --> try/catch
        try { tvTimestamp.setText(tweet.getDetailedTime(tweet)); }
        catch (ParseException e) { e.printStackTrace(); }
        if (tweet.embedUrl != "")
        {
            ivEmbed.setVisibility(View.VISIBLE);
            Glide.with(this)
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

        btnLike.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
    }
}