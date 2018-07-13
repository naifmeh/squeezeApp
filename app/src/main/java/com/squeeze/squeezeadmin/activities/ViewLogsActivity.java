package com.squeeze.squeezeadmin.activities;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.squeeze.squeezeadmin.R;
import com.squeeze.squeezeadmin.network.NetworkRequestsSingleton;
import com.squeeze.squeezeadmin.network.RequestScheme;
import com.squeeze.squeezeadmin.utils.NetworkUtils;
import com.squeeze.squeezeadmin.utils.RecyclerLogAdapter;

import java.util.HashMap;
import java.util.Map;

public class ViewLogsActivity extends AppCompatActivity {
    private static final String TAG = ViewLogsActivity.class.getSimpleName();

    /* UI */
    private Toolbar mToolbar;
    private RecyclerView mRecycler;
    private RecyclerView.LayoutManager mLayoutManager;

    /* Data */
    private RecyclerLogAdapter mAdapter;

    private SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_logs);

        /* Init UI */
        mToolbar = (Toolbar) findViewById(R.id.viewLogsToolbar);
        mRecycler = (RecyclerView) findViewById(R.id.logsRecycler);

        /* Setting up status bar color and toolbar*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Business logic */
        mSharedPrefs = getSharedPreferences(getString(R.string.shared_prefs_file_key), MODE_PRIVATE);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);

        requestLogs();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestLogs() {
        RequestQueue queue = NetworkRequestsSingleton.getInstance(this)
                .getRequestQueue();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(RequestScheme.HTTP_SCHEME)
                .encodedAuthority(RequestScheme.AUTHORITY)
                .appendPath(RequestScheme.EMPLOYEES_PATH)
                .appendPath(RequestScheme.EMPLOYEES_LOGS);

        StringRequest logsRequest = new StringRequest(Request.Method.GET,
                builder.build().toString(),
                (response) -> {
                    Gson gson = new Gson();
                    Log.d(TAG, response);
                    if (!response.equals("null")) {
                        JsonArray jsonArray = gson.fromJson(response, JsonArray.class);
                        mAdapter = new RecyclerLogAdapter(ViewLogsActivity.this, jsonArray, null);
                        mRecycler.setAdapter(mAdapter);

                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ViewLogsActivity.this, "Log file is empty for now.", Toast.LENGTH_LONG).show();
                    }
                }, (error) -> {
            Toast.makeText(ViewLogsActivity.this, "Cannot perfom network request", Toast.LENGTH_LONG).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + NetworkUtils.getJwtFromSharedPrefs(ViewLogsActivity.this, mSharedPrefs));

                return headers;
            }
        };

        queue.add(logsRequest);
    }
}
