package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

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
    Button btnReply;
    ToggleButton btnRetweet, btnLike;

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
        btnLike.setText(tweet.favCount);
        btnLike.setButtonDrawable(R.drawable.ic_vector_heart_stroke);
        btnLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                int favs;
                tweet.isFav = isChecked;
                if (!btnLike.isChecked()) // tweet is UNliked, like it
                {
                    favs = Integer.parseInt(tweet.favCount);
                    favs += 1;
                    tweet.favCount = String.valueOf(favs); // reassign favCount value after like is added
                    tweet.isFav = true;
                    btnLike.setTextOff(tweet.favCount);
                    btnLike.setButtonDrawable(R.drawable.ic_vector_heart);
                    btnLike.setTextColor(getResources().getColor(R.color.inline_action_like));
                }
                else // tweet is LIKED, unlike it
                {
                    favs = Integer.parseInt(tweet.favCount);
                    favs -= 1;
                    tweet.favCount = String.valueOf(favs);
                    tweet.isFav = false;
                    btnLike.setTextOn(tweet.favCount);
                    btnLike.setButtonDrawable(R.drawable.ic_vector_heart_stroke);
                    btnLike.setTextColor(getResources().getColor(R.color.medium_gray_50));
                }
            }
        });

        btnRetweet.setText(tweet.rtCount);
        btnRetweet.setButtonDrawable(R.drawable.ic_vector_retweet_stroke);
        btnRetweet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                int retweets;
                if (!btnRetweet.isChecked()) // tweet is UNretweeted, retweet it
                {
                    retweets = Integer.parseInt(tweet.rtCount);
                    retweets += 1;
                    tweet.rtCount = String.valueOf(retweets); // reassign favCount value after like is added
                    tweet.isRt = true;
                    btnRetweet.setTextOff(tweet.rtCount);
                    btnRetweet.setButtonDrawable(R.drawable.ic_vector_retweet);
                    btnRetweet.setTextColor(getResources().getColor(R.color.inline_action_retweet_pressed));
                }
                else // tweet is RETWEETED, unretweet
                {
                    retweets = Integer.parseInt(tweet.rtCount);
                    retweets -= 1;
                    tweet.rtCount = String.valueOf(retweets);
                    tweet.isRt = false;
                    btnRetweet.setTextOn(tweet.rtCount);
                    btnRetweet.setButtonDrawable(R.drawable.ic_vector_retweet_stroke);
                    btnRetweet.setTextColor(getResources().getColor(R.color.medium_gray_50));
                }
            }
        });

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  Twitter");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}