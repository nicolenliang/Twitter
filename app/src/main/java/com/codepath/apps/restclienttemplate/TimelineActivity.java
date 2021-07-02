package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity
{
    public static final String TAG = "TimelineActivity";
    private final int REQ_CODE = 20;
    public long maxID;
    private EndlessRecyclerViewScrollListener scrollListener;

    TwitterClient client;
    RecyclerView rvTweets;
    Button btnLogout;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout scTweets;
    FloatingActionButton composeFab;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        // initialize the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        // recycler view setup: 1. layout manager, 2. adapter
        // SIKE WE GOT INFINITE SCROLLING !!!
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        // creating scrollListener w same layout manager
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager)
        {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view)
            {
                loadMoreData(page);
            }
        };
        // adding scrollListener to rvTweets
        rvTweets.addOnScrollListener(scrollListener);
        rvTweets.setAdapter(adapter);

        // initializing timeline
        client = TwitterApp.getRestClient(this);
        populateHomeTimeline();

        // find swipe container view
        scTweets = findViewById(R.id.scTweets);
        // set up refresh listener: triggers new data loading
        scTweets.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                fetchTimelineAsync(0);
            }
        });

        // floating action button to compose tweet
        composeFab = findViewById(R.id.composeFab);
        composeFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                startActivityForResult(intent, REQ_CODE);
            }
        });

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  Twitter");
    }

    public void loadMoreData(int page)
    {
        client.getInfiniteTimeline(maxID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json)
            {
                Log.i(TAG, "+25 !");
                // add more items
                JSONArray jsonArray = json.jsonArray;
                try
                {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.addAll(tweets);
                    adapter.notifyDataSetChanged();
                    maxID = tweets.get(tweets.size() - 1).id; // update maxID so it keeps loading
                }
                catch (JSONException e) { Log.e(TAG, "Json exception", e); }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable)
            {
                Log.d(TAG, "no load more data", throwable);
            }
        });
    }

    // sends network req to fetch updated data
    public void fetchTimelineAsync(final int page)
    {
        client.getHomeTimeline(new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json)
            {
                // clear old items from list
                adapter.clear();
                // add new items
                JSONArray jsonArray = json.jsonArray;
                try
                {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.addAll(tweets);
                    adapter.notifyDataSetChanged();
                    scrollListener.resetState(); // reset scrollListener when we refresh
                }
                catch (JSONException e) { Log.e(TAG, "Json exception", e); }
                // signal that refresh has finished; setRefreshing to false
                scTweets.setRefreshing(false);
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable)
            {
                Log.d(TAG, "Fetch timeline error: " + response, throwable);
            }
        });
    }

    // inflates menu; adds items to menu (if present)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    // navigates to menubar activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.compose)
        {
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, REQ_CODE);
            return true;
        }
        if (item.getItemId() == R.id.logout)
        {
            client.clearAccessToken(); // forgets who is logged in
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // populates timeline with new tweet
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == REQ_CODE && resultCode == RESULT_OK)
        {
            // get data from the intent (tweet obj)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // update rv with the tweet:
            // 1. modify data source to include new tweet
            tweets.add(0, tweet);
            // 2. update adapter
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0); // takes away need to scroll up to view new tweet
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeline() // API REQUEST
    {
        client.getHomeTimeline(new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json)
            {
                Log.i(TAG, "onSuccess!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try
                {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged(); // ALWAYS NOTIFY ADAPTER !!
                    maxID = tweets.get(tweets.size() - 1).id; // maxID gets oldest tweet ID aka last index tweet's ID
                }
                catch (JSONException e) { Log.e(TAG, "Json exception", e); }

            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable)
            {
                Log.e(TAG, "onFailure!", throwable);
            }
        });
    }
}