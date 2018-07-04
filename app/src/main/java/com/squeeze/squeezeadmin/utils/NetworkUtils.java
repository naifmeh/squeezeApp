package com.squeeze.squeezeadmin.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.squeeze.squeezeadmin.R;
import com.squeeze.squeezeadmin.network.NetworkRequestsSingleton;
import com.squeeze.squeezeadmin.network.RequestScheme;

import java.util.HashMap;
import java.util.Map;

public class NetworkUtils {

    public static String getJwtFromSharedPrefs(Context context, SharedPreferences sharedPrefs) {
        return sharedPrefs.getString(context.getString(R.string.shared_prefs_jwt_key),"");
    }

    public static void sendTrainingSignal(Context ctx, SharedPreferences sharedPrefs) {
        RequestQueue queue = NetworkRequestsSingleton.getInstance(ctx).getRequestQueue();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(RequestScheme.HTTP_SCHEME)
                .encodedAuthority(RequestScheme.AUTHORITY)
                .encodedPath(RequestScheme.NETWORK_PATH)
                .encodedPath(RequestScheme.NETWORK_TRAIN);

        StringRequest request = new StringRequest(Request.Method.POST,builder.build().toString(),
                (response) -> {
                    Toast.makeText(ctx,"Network effectively trained",Toast.LENGTH_LONG)
                            .show();
                },(error)->{
            Toast.makeText(ctx,"Network training error",Toast.LENGTH_LONG)
                    .show();
        }){
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                JsonObject json = new JsonObject();
                JsonObject jsonBis = new JsonObject();
                jsonBis.addProperty("train",true);
                json.add("data",jsonBis);

                return json.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","Bearer "+getJwtFromSharedPrefs(ctx, sharedPrefs));

                return headers;
            }
        };

        queue.add(request);

    }

 }
