package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet
{
    public String body;
    public String createdAt;
    public User user;
    public String timestampR;
    public String embedUrl;
    public String favCount, rtCount;
    public Boolean isFav, isRt;
    public long id;

    // empty constructor needed by Parceler lib
    public Tweet() {}

    // parses through json obj and returns tweet attributes
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException
    {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        // check if entities obj even exists for this tweet
        if (!jsonObject.isNull("extended_entities"))
        {
            tweet.embedUrl = jsonObject
                    .getJSONObject("extended_entities")
                    .getJSONArray("media")
                    .getJSONObject(0)
                    .getString("media_url_https");
        }
        else { tweet.embedUrl = ""; }
        tweet.favCount = jsonObject.getString("favorite_count");
        // formatting 1000+ likes/retweets to i.e. 1.3k
        if (Double.parseDouble(tweet.favCount) > 1000)
        {
            Double favs = Double.parseDouble(tweet.favCount) / 1000.0;
            String favsS = favs.toString();
            tweet.favCount = favsS.substring(0, favsS.indexOf(".") + 2) + "k";
        }
        tweet.rtCount = jsonObject.getString("retweet_count");
        if (Double.parseDouble(tweet.rtCount) > 1000)
        {
            Double retweets = Double.parseDouble(tweet.rtCount) / 1000.0;
            String retweetsS = retweets.toString();
            tweet.rtCount = retweetsS.substring(0, retweetsS.indexOf(".") + 2) + "k";
        }
        tweet.isFav = jsonObject.getBoolean("favorited");
        tweet.isRt = jsonObject.getBoolean("retweeted");
        tweet.id = jsonObject.getLong("id");

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
        // day of week, month, day, hours:minutes:seconds, timezone, year
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true); // leniency when parsing thru date/time info
        try
        {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            // relative time span string: old time, current time, unit of time to compare against
            timestampR = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS).toString();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return timestampR;
    }

    // detailed timestamp of when tweet was posted
    public String getDetailedTime(Tweet tweet) throws ParseException
    {
        String tweetCreated = tweet.createdAt;
        // DateFormat: interprets strings representing dates in the given format
        DateFormat dfOriginal = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
        // convert from string to date
        Date tweetDate = dfOriginal.parse(tweetCreated);
        // check if date was read in with correct formatting
        Log.i("TweetDate", tweetDate.toString());

        // as date object, we can format however we want
        DateFormat dfDetailed = new SimpleDateFormat("HH:mm \u00B7 M/dd/yy"); // setting desired format
        String tweetDetails = dfDetailed.format(tweetDate);
        Log.i("TweetDate", tweetDetails);

        return tweetDetails;
    }
}
