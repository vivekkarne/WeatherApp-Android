package com.cs.weatherapp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FavoritePageAdapter extends FragmentStatePagerAdapter {
    List<JSONObject> weatherData;
    List<String> addresses;
    int numOfTabs;
    public FavoritePageAdapter(FragmentManager fm, int numOfTabs, List<JSONObject> weatherData, List<String> addresses) {
        super(fm);
        this.weatherData = weatherData;
        this.numOfTabs = numOfTabs;
        this.addresses = addresses;
    }

    @Override
    public Fragment getItem(int position) {
        String address = addresses.get(position);
        return FavoriteFragment.newInstance(position, weatherData, address);
    }


    public void removeTabPage(int position) {
        if (!weatherData.isEmpty() && position<weatherData.size()) {
            weatherData.remove(position);
            addresses.remove(position);
            Log.i("REMOVED", "NOT SHOWN");
            numOfTabs--;
            notifyDataSetChanged();
        }
    }

    public void removeTabEnd() {
            numOfTabs--;
            notifyDataSetChanged();
    }

    public void addTabPage(String fullCity, JSONObject data) {
            weatherData.add(data);
            addresses.add(fullCity);
            numOfTabs++;
            notifyDataSetChanged();
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        return FragmentStatePagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
