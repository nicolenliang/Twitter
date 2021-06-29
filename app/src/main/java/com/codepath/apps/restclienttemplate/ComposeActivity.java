package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ComposeActivity extends AppCompatActivity
{
    public final int MAX_TWEET = 140;
    EditText etCompose;
    Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

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
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweet.length() > MAX_TWEET)
                {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, tweet, Toast.LENGTH_LONG).show();
                // make an api call to twt publish text

            }
        });
    }
}