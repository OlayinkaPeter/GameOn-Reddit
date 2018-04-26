package com.olayinkapeter.myreddit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.olayinkapeter.myreddit.helper.AppController;
import com.olayinkapeter.myreddit.helper.DividerItemDecor;
import com.olayinkapeter.myreddit.helper.EndPoints;
import com.olayinkapeter.myreddit.helper.EndlessRecyclerViewScrollListener;
import com.olayinkapeter.myreddit.helper.RecyclerTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    String urlJsonObj = EndPoints.BASE_CLIENT + EndPoints.API_QUERY
            + EndPoints.API_RESPONSE_LIMIT + EndPoints.API_RESPONSE_SORT;
    private static String TAG = MainActivity.class.getSimpleName();

    // Progress dialog
    private ProgressDialog pDialog;
    private String title, score, subreddit, url;

    private LinearLayout mainLayout, errorLayout;
    private Button retry;

    private Item item;
    private List<Item> itemList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ItemAdapter mAdapter;

    private EndlessRecyclerViewScrollListener scrollListener;

    int startValue = 0;
    int endValue = 25;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        errorLayout = (LinearLayout) findViewById(R.id.error_layout);
        retry = (Button) findViewById(R.id.btnRetry);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ItemAdapter(itemList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecor(this, LinearLayoutManager.VERTICAL));

        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (itemList.size() <= 100) {
                    makeFirstRedditRequest(urlJsonObj, startValue, endValue);
                }
            }
        };
        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);

        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        mRecyclerView.setItemAnimator(itemAnimator);

        detectOnlinePresence();

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                openUrlInBrowser(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void makeFirstRedditRequest(String requestParam, final int jsonStartValue, final int jsonEndValue) {
        showpDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                requestParam, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject jsonObject = response.getJSONObject("data");
                    JSONArray jsonArray = jsonObject.getJSONArray("children");

                    for (int i = jsonStartValue; i < jsonEndValue; i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        JSONObject data = jsonObject.getJSONObject("data");

                        title = data.getString("title");
                        score = data.getString("score");
                        subreddit = data.getString("subreddit");
                        url = data.getString("url");

                        itemList.add(new Item(title, score, subreddit, url));

                        startValue++;
                        endValue++;
                    }

                    mAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public boolean detectOnlinePresence() {
        if (isOnline()) {
            makeFirstRedditRequest(urlJsonObj, startValue, endValue);
            mainLayout.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            return true;
        } else {
            showRetry();
            return false;
        }
    }

    public boolean isOnline() {
        Boolean isConnected = false;
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            return isConnected;
        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return isConnected;
    }

    public void showRetry() {
        mainLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectOnlinePresence();
                if (!detectOnlinePresence()) {
                    Toast.makeText(MainActivity.this, "Check your network connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void openUrlInBrowser(int position) {
        item = itemList.get(position);

        url = item.getUrl();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
