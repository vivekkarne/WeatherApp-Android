package com.cs.weatherapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class AutoCompleteService {

    //    final String BACKEND_URL = "https://weather-app-hw8.wl.r.appspot.com/api/tomorrow/";
    final String BACKEND_URL = "https://weather-app-hw8.wl.r.appspot.com/api/autocomplete/";
    Context context;

    public AutoCompleteService(Context context) {
        this.context = context;
    }

    public interface AutoCompleteListener {
        void onError(String message);
        void onResponse(JSONObject autoCompleteList);
    }

    public void getAutoCompleteRes(String value, AutoCompleteService.AutoCompleteListener autoCompleteListener) {
        JsonObjectRequest autoCompleteRequest = new JsonObjectRequest(Request.Method.GET, BACKEND_URL+value, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                autoCompleteListener.onResponse(response);
                // handle error when we get query limit exceed
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                autoCompleteListener.onError("Backend Not running restart the app!");
            }
        });
        autoCompleteRequest.setShouldCache(false);
        VolleySingleton.getInstance(context).addToRequestQueue(autoCompleteRequest);
    }
}
