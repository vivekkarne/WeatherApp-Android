package com.cs.weatherapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class GeocodingService {
    final String GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    Context context;

    public GeocodingService(Context context) {
        this.context = context;
    }

    public interface GeocodingListener {
        void onError(String message);
        void onResponse(String latLng, String address);
    }

    void getAddress(String loc, GeocodingListener listener) {
        JsonObjectRequest locationReq = new JsonObjectRequest(Request.Method.GET, GEOCODING_URL + loc +"&key=AIzaSyAG-bpTtCMJ6-9m-LSPIhEAzv4dd3fnAGw", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject latLong = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    listener.onResponse(latLong.getString("lat") +", " + latLong.getString("lng"), response.getJSONArray("results").getJSONObject(0).getString("formatted_address"));
                } catch (JSONException e) {
                    listener.onError("GeoCoding Failed restart app");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError("GeoCoding Failed restart app");
            }
        });
        locationReq.setShouldCache(false);
        VolleySingleton.getInstance(context).addToRequestQueue(locationReq);
    }
}
