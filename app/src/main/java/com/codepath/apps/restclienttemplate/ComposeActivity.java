package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity
{
    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET = 280;

    EditText etCompose;
    Button btnTweet;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        // add click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String tweet = etCompose.getText().toString();
                // check if tweet is valid !! 0 < tweet.length() < 140
                if (tweet.isEmpty())
                {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tweet.length() > MAX_TWEET)
                {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Toast.makeText(ComposeActivity.this, tweet, Toast.LENGTH_LONG).show();
                // make an api call to twt publish text
                client.publishTweet(tweet, new JsonHttpResponseHandler()
                {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json)
                    {
                        Log.i(TAG, "onSuccess publishTweet");
                        try
                        {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "published tweet: " + tweet.body);
                            // returning to timeline once tweet is published
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet)); // must parcel bc We defined tweet obj
                            setResult(RESULT_OK, intent);
                            finish(); // returns to previous page
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable)
                    {
                        Log.e(TAG, "onFailure publishTweet", throwable);
                    }
                });
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