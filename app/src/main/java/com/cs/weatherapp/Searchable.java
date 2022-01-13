package com.cs.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Searchable extends AppCompatActivity {
    private final GeocodingService gs = new GeocodingService(Searchable.this);
    private final BackendService bs = new BackendService(Searchable.this);
    private  String address;
    private  String latLng;
    private View contentView;
    private View loadingView;
    private JSONObject weatherData;
    private Toolbar myactionbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        contentView = findViewById(R.id.contentView);
        loadingView = findViewById(R.id.loadingView);
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        setActionBar();
        handleIntent(getIntent());
    }

    private void setActionBar() {
        myactionbar = this.findViewById(R.id.actionbar);
        myactionbar.setBackgroundResource(R.color.cardCol);
        setSupportActionBar(myactionbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Searchable.this.address = getIntent().getStringExtra(SearchManager.QUERY);
        getSupportActionBar().setTitle(this.address);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void getGeoCoding(String query) {
        gs.getAddress(query, new GeocodingService.GeocodingListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(Searchable.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String latLng, String address) {
                Searchable.this.latLng = latLng;
//                Searchable.this.address =address;
                getWeatherData(latLng);
            }
        });
    }

    void getWeatherData(String latLng) {
        Log.i("SEARCHABLE LAT LNG", latLng);
        bs.getBackendData(latLng, new BackendService.BackendListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(Searchable.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject backendObj) {
                Searchable.this.weatherData = backendObj;
//                setView();
                setFragment();
            }
        });
    }

    private void setFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.fragPlaceholder, FavoriteFragment.newInstance(weatherData, address, "#000000"));
        ft.commit();
        contentView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
    }

    private void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
//            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
            getGeoCoding(query);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}