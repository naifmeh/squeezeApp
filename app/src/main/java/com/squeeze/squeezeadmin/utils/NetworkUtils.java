package com.squeeze.squeezeadmin.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squeeze.squeezeadmin.R;
import com.squeeze.squeezeadmin.activities.MainActivity;
import com.squeeze.squeezeadmin.beans.AuthDeviceBean;
import com.squeeze.squeezeadmin.beans.DeviceBean;
import com.squeeze.squeezeadmin.beans.EmployeeBean;
import com.squeeze.squeezeadmin.network.NetworkRequestsSingleton;
import com.squeeze.squeezeadmin.network.RequestScheme;

import java.io.UnsupportedEncodingException;
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
                .appendPath(RequestScheme.NETWORK_PATH)
                .appendPath(RequestScheme.NETWORK_TRAIN);
        final Intent intent = new Intent(ctx, MainActivity.class);
        StringRequest request = new StringRequest(Request.Method.POST,builder.build().toString(),
                (response) -> {
                    Toast.makeText(ctx,"Network effectively trained",Toast.LENGTH_LONG)
                            .show();
                    ctx.startActivity(intent);
                },(error)->{
            Toast.makeText(ctx,"Network training error",Toast.LENGTH_LONG)
                    .show();
            ctx.startActivity(intent);
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

    public static void sendRemovePicsSignal(Context ctx, SharedPreferences sharedPrefs) {
        RequestQueue queue = NetworkRequestsSingleton.getInstance(ctx).getRequestQueue();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(RequestScheme.HTTP_SCHEME)
                .encodedAuthority(RequestScheme.AUTHORITY)
                .appendPath(RequestScheme.NETWORK_PATH)
                .appendPath(RequestScheme.NETWORK_DELETE);

        StringRequest request = new StringRequest(Request.Method.DELETE, builder.build().toString(),
                (response) -> {
                    Toast.makeText(ctx, "Pictures deleted", Toast.LENGTH_LONG)
                            .show();
                }, (error) -> {
            Toast.makeText(ctx, "Error while deleting pictures", Toast.LENGTH_LONG)
                    .show();
        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getJwtFromSharedPrefs(ctx, sharedPrefs));

                return headers;
            }
        };

        queue.add(request);
    }

    public static void sendRemoveLogsSignal(Context ctx, SharedPreferences sharedPrefs) {
        RequestQueue queue = NetworkRequestsSingleton.getInstance(ctx).getRequestQueue();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(RequestScheme.HTTP_SCHEME)
                .encodedAuthority(RequestScheme.AUTHORITY)
                .appendPath(RequestScheme.EMPLOYEES_PATH)
                .appendPath(RequestScheme.EMPLOYEES_LOGS);

        StringRequest request = new StringRequest(Request.Method.DELETE, builder.build().toString(),
                (response) -> Toast.makeText(ctx, "Logs vacated", Toast.LENGTH_LONG)
                        .show()
                , (error) ->
                Toast.makeText(ctx, "Error while emptying logs", Toast.LENGTH_LONG)
                        .show()
        ) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getJwtFromSharedPrefs(ctx, sharedPrefs));

                return headers;
            }
        };

        queue.add(request);
    }

    public static void getJwt(Context ctx, SharedPreferences sharedPrefs, @Nullable NetworkActionsListener listener) {
        RequestQueue queue = NetworkRequestsSingleton.getInstance(ctx).getRequestQueue();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(RequestScheme.HTTP_SCHEME)
                .encodedAuthority(RequestScheme.AUTHORITY)
                .appendPath(RequestScheme.DEVICE_PATH)
                .appendPath(RequestScheme.DEVICE_AUTHENTICATE);
        Uri uri = builder.build();

        final Gson gson = new Gson();
        JsonObject deviceObject = new JsonObject();
        DeviceBean deviceBean = new DeviceBean(ctx.getString(R.string.deviceFakeName), ctx.getString(R.string.deviceFakeMac));
        JsonElement dataDevice = gson.fromJson(gson.toJson(deviceBean), JsonElement.class);

        deviceObject.add("data", dataDevice);

        StringRequest jwtRequest = new StringRequest(Request.Method.POST, uri.toString(), (response) -> {
            AuthDeviceBean data = gson.fromJson(response, AuthDeviceBean.class);
            SharedPreferences.Editor sharedEditor = sharedPrefs.edit();
            sharedEditor.putString(ctx.getString(R.string.shared_prefs_jwt_key), data.getData().getToken());
            sharedEditor.commit();
            listener.onNetworkResponse();
        }, (error) -> Toast.makeText(ctx, "Error while performing network action", Toast.LENGTH_LONG)
                .show()) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                try {
                    if (mStatusCode != 200)
                        return Response.error(new VolleyError(new String(response.data, "utf-8")));
                } catch (UnsupportedEncodingException e) {
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
                Map<String, String> params = new HashMap<>();
                params.put("content-type", "application/json");

                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return deviceObject.toString().getBytes();
            }
        };
        queue.add(jwtRequest);
    }

    public static void deleteUser(Context ctx, SharedPreferences sharedPrefs, EmployeeBean employeeBean, NetworkActionsListener listener) {
        RequestQueue queue = NetworkRequestsSingleton.getInstance(ctx).getRequestQueue();
        Uri.Builder builder = new Uri.Builder();
        final Gson gson = new Gson();
        JsonObject json = new JsonObject();
        JsonObject jsonEmployee = gson.fromJson(gson.toJson(employeeBean), JsonObject.class);
        json.add("data", jsonEmployee);
        System.out.println(json);
        builder.scheme(RequestScheme.HTTP_SCHEME)
                .encodedAuthority(RequestScheme.AUTHORITY)
                .appendPath(RequestScheme.EMPLOYEES_PATH)
                .appendPath(RequestScheme.EMPLOYEES_DELETE);

        StringRequest request = new StringRequest(Request.Method.POST, builder.build().toString(),
                (response) -> {
                    Toast.makeText(ctx, "Deleted employee", Toast.LENGTH_LONG)
                            .show();
                    listener.onNetworkResponse();
                }
                , (error) -> {
            Toast.makeText(ctx, "Error while deleting employee", Toast.LENGTH_LONG)
                    .show();
            if (error.networkResponse != null) {
                try {
                    System.out.println(new String(error.networkResponse.data, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        }

        ) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("content-type", "application/json");
                headers.put("Authorization", "Bearer " + getJwtFromSharedPrefs(ctx, sharedPrefs));

                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {

                JsonObject json = new JsonObject();
                JsonObject jsonEmployee = gson.fromJson(gson.toJson(employeeBean), JsonObject.class);
                json.add("data", jsonEmployee);
                System.out.println(json);
                return json.toString().getBytes();
            }
        };

        queue.add(request);
    }

    public interface NetworkActionsListener {
        void onNetworkResponse();
    }

 }
