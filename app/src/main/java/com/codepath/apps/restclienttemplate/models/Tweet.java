package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Tweet
{
    public String body;
    public String createdAt;
    public User user;
    public String timestamp;

    // parses through json obj and returns tweet attributes ?
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException
    {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        return tweet;
    }

    // parses through json array and returns list of tweets
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException
    {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++)
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        return tweets;
    }

    // relative timestamp of when tweet was posted
    public String getRelativeTime(String rawJsonDate)
    {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try
        {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            timestamp = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS).toString();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return timestamp;
    }
}
