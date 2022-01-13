package com.cs.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Details extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private DetailsPageAdapter detailsPageAdapter;
    private JSONObject responseJson;
    private String address;
    private Menu menu;
    private Toolbar myactionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        viewPager2 = findViewById(R.id.detailPager);
        tabLayout = findViewById(R.id.tabs);
        viewPager2.setOffscreenPageLimit(3); // Change this if data doesnt refresh
        setActionBar();
        try {
            responseJson = new JSONObject(getIntent().getStringExtra("responseJson"));
            address = getIntent().getStringExtra("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getSupportActionBar().setTitle(Html.fromHtml("<small>"+this.address+"</small>"));
        detailsPageAdapter = new DetailsPageAdapter(getSupportFragmentManager(), getLifecycle(), responseJson);
        viewPager2.setAdapter(detailsPageAdapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if(position == 0) {
                tab.setText("TODAY");
                tab.setIcon(R.drawable.ic_calendar_today);
            }
            else if(position == 1) {
                tab.setText("WEEKLY");
                tab.setIcon(R.drawable.ic_baseline_trending_up_24);
            }
            else if(position == 2) {
                tab.setText("WEATHER DATA");
                tab.setIcon(R.drawable.ic_thermometer_low);
            }
        }).attach();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.twitter_menu, menu);
        MenuItem tweet = menu.findItem(R.id.twitter);
        tweet.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String currTemp = "57";
                try {
                    currTemp = ((Long)Math.round(responseJson.getJSONObject("data").getJSONArray("timelines").getJSONObject(1).getJSONArray("intervals").getJSONObject(0).getJSONObject("values").getDouble("temperatureApparent"))).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url = "https://twitter.com/intent/tweet?text="+ Uri.encode(
                        "Check out "+ Details.this.address +", "
                                +"USA's weather! It is "
                                +currTemp+"°F!")
                        +"&hashtags=CSCI571WeatherForecast";


                url = url.replace("'","%27");
                Log.d("URL",url);
                Uri webpage = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(intent);
                return false;
            }
        });
        return true;
    }

    private void setActionBar() {
        myactionbar = this.findViewById(R.id.actionbar);
        setSupportActionBar(myactionbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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