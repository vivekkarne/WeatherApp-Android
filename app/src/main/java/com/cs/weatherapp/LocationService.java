package com.cs.weatherapp;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationService {
    final String IP_INFO_URL = "https://www.ipinfo.io/?token=a4216feb6ae492";
    Context context;

    public LocationService(Context context) {
        this.context = context;
    }

    public interface LocationListener {
        void onError(String message);
        void onResponse(String latLng, String address);
    }

    void getLocation(LocationListener listener) {
        JsonObjectRequest locationReq = new JsonObjectRequest(Request.Method.GET, IP_INFO_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String loc = response.getString("loc");
                    String address = response.getString("city") + ", "  + response.getString("region");
                    listener.onResponse(loc, address);
                } catch (JSONException e) {
                    listener.onError("IP Info Failed restart app");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError("IP Info Failed restart app");
            }
        });
        locationReq.setShouldCache(false);
        VolleySingleton.getInstance(context).addToRequestQueue(locationReq);
    }
}
