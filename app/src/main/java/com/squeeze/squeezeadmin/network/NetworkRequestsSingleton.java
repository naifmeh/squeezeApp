package com.squeeze.squeezeadmin.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkRequestsSingleton {

    private static NetworkRequestsSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private NetworkRequestsSingleton(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized NetworkRequestsSingleton getInstance(Context context) {
        if(mInstance == null)
            mInstance = new NetworkRequestsSingleton(context);
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null) {
            //Pour eviter de tourner sur le contexte de l'activité
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }
}
