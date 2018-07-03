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
import android.view.WindowManager;

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
import com.squeeze.squeezeadmin.utils.RecyclerAdapter;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ViewEmployeesActivity extends AppCompatActivity {
    private static final String TAG = ViewEmployeesActivity.class.getSimpleName();

    /* UI */
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    /* Data */
    private JsonArray mJsonArray;
    private RecyclerAdapter mAdapter;

    private SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_employees);

        /* Init UI */
        mToolbar = (Toolbar) findViewById(R.id.viewEmployeesToolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.listEmployeesRecycler);

        /* Setting up status bar color and toolbar*/
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        /* Business logic */
        mSharedPrefs = getSharedPreferences(getString(R.string.shared_prefs_file_key),MODE_PRIVATE);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    private void requestListEmployees() {
        RequestQueue queue = NetworkRequestsSingleton.getInstance(this)
                .getRequestQueue();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(RequestScheme.HTTP_SCHEME)
                .encodedAuthority(RequestScheme.AUTHORITY)
                .encodedPath(RequestScheme.EMPLOYEES_ALL);

        StringRequest listEmplRequest = new StringRequest(Request.Method.GET,
                builder.build().toString(),
                (response) -> {
                    Gson gson = new Gson();
                    JsonArray jsonArray = gson.fromJson(response,JsonArray.class);
                    mAdapter = new RecyclerAdapter(ViewEmployeesActivity.this,jsonArray);
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.notifyDataSetChanged();
                }, (error) -> {
                    try {
                        Log.e(TAG,new String(error.networkResponse.data,"utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Bearer "+ NetworkUtils.getJwtFromSharedPrefs(ViewEmployeesActivity.this,mSharedPrefs));
                return headers;
            }
        };
    }
}
