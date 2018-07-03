package com.squeeze.squeezeadmin.activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squeeze.squeezeadmin.R;
import com.squeeze.squeezeadmin.beans.AuthDeviceBean;
import com.squeeze.squeezeadmin.beans.DeviceBean;
import com.squeeze.squeezeadmin.network.NetworkRequestsSingleton;
import com.squeeze.squeezeadmin.network.RequestScheme;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {
    private final static String TAG = SplashAsyncTask.class.getSimpleName();

    private ImageView imageLogo;

    private SharedPreferences mSharedPrefs;

    private boolean shouldPulse = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /* Init UI */
        imageLogo = (ImageView) findViewById(R.id.logoSplashScreen);

        /* Setting up status bar color */
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        startLogoAnimation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SplashAsyncTask splashAsyncTask = new SplashAsyncTask(this);
        splashAsyncTask.execute();
    }

    private void startLogoAnimation() {
        ObjectAnimator scalePulse = ObjectAnimator.ofPropertyValuesHolder(
                imageLogo,
                PropertyValuesHolder.ofFloat("scaleX",1.2f),
                PropertyValuesHolder.ofFloat("scaleY",1.2f)
        );
        scalePulse.setInterpolator(new FastOutSlowInInterpolator());
        scalePulse.setDuration(300);
        scalePulse.setRepeatCount(ObjectAnimator.INFINITE);
        scalePulse.setRepeatMode(ObjectAnimator.REVERSE);

        scalePulse.start();
    }

    private void displayMainActivity() {
        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public class SplashAsyncTask extends AsyncTask<Void,Void,Integer> {

        private RequestQueue mQueue;
        private Uri mUri;
        private Uri.Builder mBuilder;
        private Context mCtx;

        public SplashAsyncTask(Context context) {
            mCtx = context;
            mQueue = NetworkRequestsSingleton.getInstance(mCtx).getRequestQueue();

            mBuilder = new Uri.Builder();
            mBuilder.scheme(RequestScheme.HTTP_SCHEME)
                    .encodedAuthority(RequestScheme.AUTHORITY)
                    .encodedPath(RequestScheme.DEVICE_PATH);

        }

        @Override
        protected Integer doInBackground(Void... voids) {
            mSharedPrefs = mCtx.getSharedPreferences(getString(R.string.shared_prefs_file_key), mCtx.MODE_PRIVATE);

            mBuilder.encodedPath(RequestScheme.DEVICE_AUTHENTICATE);
            mUri = mBuilder.build();
            Gson gson = new Gson();
            JsonObject deviceObject = new JsonObject();
            DeviceBean deviceBean = new DeviceBean(getString(R.string.deviceFakeName),getString(R.string.deviceFakeName));

            deviceObject.addProperty("data",gson.toJson(deviceBean));
            StringRequest jwtRequest = new StringRequest(Request.Method.POST,mUri.toString(), (response) -> {
                AuthDeviceBean data = gson.fromJson(response,AuthDeviceBean.class);
                SharedPreferences.Editor sharedEditor = mSharedPrefs.edit();
                sharedEditor.putString(getString(R.string.shared_prefs_jwt_key), data.getData().getToken());
                sharedEditor.commit();
            }, (error) -> {

            }) {
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    int mStatusCode = response.statusCode;
                    try {
                        if(mStatusCode != 200) return Response.error(new VolleyError(new String(response.data,"utf-8")));
                    } catch(UnsupportedEncodingException e) {
                        return Response.error(new ParseError(e));
                    }

                    return super.parseNetworkResponse(response);
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("content-type","application/json");

                    return params;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return deviceObject.toString().getBytes();
                }
            };
            Log.d(TAG,"Done asynctask");
            mQueue.add(jwtRequest);

            try {
                Thread.sleep(1500);
            } catch(InterruptedException e) {
                Log.e(TAG,e.getMessage());
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

        }
    }
}
