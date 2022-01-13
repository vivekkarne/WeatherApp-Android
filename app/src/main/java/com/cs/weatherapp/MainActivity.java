package com.cs.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private View contentView;
    private View loadingView;
    private View hButton;
    private String latLng;
    private Toolbar myactionbar;
    private Menu mainMenu;
    private Handler handler;
    private SearchView searchView;
    private ViewPager viewPager2;
    private TabLayout tabLayout;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private AutoCompleteAdapter autoAdapter;
    private List<String> preds;
    private List<JSONObject> weatherData;
    private List<String> addresses;
    boolean flag = false;
    FavoritePageAdapter mDynamicFragmentAdapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String currentLoc = "Los Angeles, California";

    private final LocationService location = new LocationService(MainActivity.this);
    private final BackendService backendService = new BackendService(MainActivity.this);
    private final AutoCompleteService autoCompleteService = new AutoCompleteService(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        flag = true;
        weatherData = new ArrayList<>();
        addresses = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_WeatherApp);
        setContentView(R.layout.activity_main);
        contentView = findViewById(R.id.contentView);
        loadingView = findViewById(R.id.loadingView);
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        sharedPreferences = getSharedPreferences(getString(R.string.secret_key),Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setupActionBar();
        viewPager2 = findViewById(R.id.fragPager);
        tabLayout = findViewById(R.id.dotsTab);
        viewPager2.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        viewPager2.setAdapter(pagerAdapter);
//        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
//        }).attach();

        location.getLocation(new LocationService.LocationListener() {
            @Override
            public void onError(String message) {
                // Display error toast
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(String latLng, String address) {

                MainActivity.this.latLng = latLng;
                Log.i("MAIN LAT LONG", latLng);
                MainActivity.this.currentLoc = address;
                // Call Backend API to get data
                backendService.getBackendData(latLng,new BackendService.BackendListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        //Display Error page
                    }

                    @Override
                    public void onResponse(JSONObject backendObj) {
                        weatherData.add(backendObj);
                        addresses.add(currentLoc);
                        populateWeatherData(weatherData, addresses);

                        setDynamicFragmentToTabLayout();

                        contentView.setVisibility(View.VISIBLE);
                        loadingView.setVisibility(View.GONE);
                    }
                });
            }
        });

    }

    private void populateWeatherData(List<JSONObject> weatherData1, List<String> addresses1) {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        Set<String> s = new HashSet<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if(entry.getKey().startsWith("k_")) {
                try {
                    weatherData1.add(new JSONObject((String) entry.getValue()));
                    addresses1.add(sharedPreferences.getString("full_" + entry.getKey().split("_")[1], "Los Angeles, California"));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setDynamicFragmentToTabLayout() {
        tabLayout.removeAllTabs();

        for (int i = 0; i < (sharedPreferences.getAll().size()/2) + 1; i++) {
            // set the tab name as "Page: " + i
            tabLayout.addTab(tabLayout.newTab());
        }

        mDynamicFragmentAdapter = new FavoritePageAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), weatherData, addresses);

        // set the adapter
        viewPager2.setAdapter(mDynamicFragmentAdapter);

        // set the current item as 0 (when app opens for first time)
        viewPager2.setCurrentItem(0);
    }

    void setupActionBar() {
        myactionbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(myactionbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mainMenu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.my_search_bar);
        searchView = (SearchView) searchItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, Searchable.class)));
        searchView.clearFocus();

        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);

        searchAutoComplete.setDropDownBackgroundResource(android.R.color.background_light);
        autoAdapter = new AutoCompleteAdapter(this, R.layout.auto_text);
        searchAutoComplete.setAdapter(autoAdapter);
        searchAutoComplete.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String queryString = (String) parent.getItemAtPosition(position);
                        searchAutoComplete.setText(queryString);
                    }
                });
        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        autoCompleteService.getAutoCompleteRes(searchAutoComplete.getText().toString(),
                                new AutoCompleteService.AutoCompleteListener() {
                                    @Override
                                    public void onError(String message) {
                                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(JSONObject autoCompleteList) {
                                        preds = new ArrayList<String>();
                                        try {
                                            if(autoCompleteList.getString("status").equals("OK")) {
                                                JSONArray predictions = autoCompleteList.getJSONArray("predictions");
                                                for(int i = 0; i < predictions.length(); i++) {
                                                    String city = "";
                                                    String state = "";
                                                    JSONArray terms = predictions.getJSONObject(i).getJSONArray("terms");
                                                    for(int j = 0; j < terms.length(); j++) {
                                                        if(terms.getJSONObject(j).getString("value").equals("USA")) {
                                                            if(0 <= terms.length()) {
                                                                city = terms.getJSONObject(0).getString("value");
                                                            }
                                                            if(1 <= terms.length()) {
                                                                state = terms.getJSONObject(1).getString("value");
                                                            }
                                                            preds.add(city+", "+state);
                                                        }
                                                    }
                                                }
                                                autoAdapter.setData(preds);
                                                autoAdapter.notifyDataSetChanged();
                                            }
                                        }
                                        catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }




    public void removeTab(int position) {
        if (tabLayout.getTabCount() >= 1 && position<tabLayout.getTabCount()) {
            tabLayout.removeTabAt(position);
            mDynamicFragmentAdapter.removeTabPage(position);
        }
    }

    public void removeTabEnd() {
            tabLayout.removeTabAt(tabLayout.getTabCount()-1);
            mDynamicFragmentAdapter.removeTabEnd();
    }

    private void addTab(String fullCity, String data) throws JSONException {
        tabLayout.addTab(tabLayout.newTab());
        mDynamicFragmentAdapter.addTabPage(fullCity, new JSONObject(data));
    }

    public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private List<String> mlistData;
        public AutoCompleteAdapter(@NonNull Context context, int resource) {
            super(context, resource);
            mlistData = new ArrayList<>();
        }
        public void setData(List<String> list) {
            mlistData.clear();
            mlistData.addAll(list);
        }
        @Override
        public int getCount() {
            return mlistData.size();
        }
        @Nullable
        @Override
        public String getItem(int position) {
            return mlistData.get(position);
        }
        /**
         * Used to Return the full object directly from adapter.
         *
         * @param position
         * @return
         */
        public String getObject(int position) {
            return mlistData.get(position);
        }
        @NonNull
        @Override
        public Filter getFilter() {
            Filter dataFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        filterResults.values = mlistData;
                        filterResults.count = mlistData.size();
                    }
                    return filterResults;
                }
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && (results.count > 0)) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return dataFilter;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(flag) {
            flag = false;
        }
        else {
            myactionbar.collapseActionView();
            loadingView.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);
//            weatherData.subList(1, weatherData.size()).clear();
//            addresses.subList(1, addresses.size()).clear();
            // Check if fav was added or removed
            Map<String, ?> allEntries = sharedPreferences.getAll();
            Set<String> s = new HashSet<>();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                if(entry.getKey().startsWith("k_")) {
                    String city = entry.getKey().split("_")[1];
                    boolean found = false;
                    for(String currLoc: addresses) {
                        currLoc = currLoc.split(",")[0];
                        if(city.equals(currLoc)) {
                            found = true;
                        }
                    }
                    if(!found) {
                        String fullCity = "" ;
                        String data = "";
                        try {
                            fullCity = sharedPreferences.getString("full_"+city, "Los Angeles, California");
                            data = sharedPreferences.getString("k_"+city, "");
                            addTab(fullCity, data);
                            viewPager2.setCurrentItem(mDynamicFragmentAdapter.getCount()-1);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            allEntries = sharedPreferences.getAll();
            //Check if address was removed
            for(int i =1; i < addresses.size(); i++) {
                boolean found = false;
                String currLoc = addresses.get(i);
                currLoc = currLoc.split(",")[0];
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    if (entry.getKey().startsWith("k_")) {
                        String city = entry.getKey().split("_")[1];
                        if (currLoc.equals(city)) {
                            found = true;
                        }
                    }
                }
                if (!found) {
                    removeTab(i);
                }
            }
//            populateWeatherData(weatherData, addresses);
//            setDynamicFragmentToTabLayout();
            contentView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.VISIBLE);
        }
    }
}