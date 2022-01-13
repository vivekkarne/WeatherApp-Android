package com.cs.weatherapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class BackendService {

    final String BACKEND_URL = "https://weather-app-hw8.wl.r.appspot.com/api/tomorrow/";
//    final String BACKEND_URL = "https://mocki.io/v1/";
    Context context;

    public BackendService(Context context) {
        this.context = context;
    }

    public interface BackendListener {
        void onError(String message);
        void onResponse(JSONObject backendObj);
    }

    public void getBackendData(String latLng, BackendListener backendListener) {
        JsonObjectRequest backendRequest = new JsonObjectRequest(Request.Method.GET, BACKEND_URL+latLng, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                backendListener.onResponse(response);
                // handle error when we get query limit exceed
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                backendListener.onError("Backend Not running restart the app!");
            }
        });
        backendRequest.setShouldCache(false);
        VolleySingleton.getInstance(context).addToRequestQueue(backendRequest);
    }
}
