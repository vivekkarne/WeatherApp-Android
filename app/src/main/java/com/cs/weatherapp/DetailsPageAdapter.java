package com.cs.weatherapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailsPageAdapter extends FragmentStateAdapter {
    private JSONObject responseJson;

    public DetailsPageAdapter(FragmentManager fm, Lifecycle lifecycle, JSONObject responseJson) {
        super(fm, lifecycle);
        this.responseJson = responseJson;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new TodayFragment();
            Bundle args = new Bundle();
            args.putString(TodayFragment.ARG_OBJECT, responseJson.toString());
            fragment.setArguments(args);

        }
        else if (position == 1) {
            fragment = new WeeklyFragment();
            Bundle args = new Bundle();
            args.putString(WeeklyFragment.ARG_OBJECT,responseJson.toString());
            fragment.setArguments(args);

        }
        else if (position == 2) {
            fragment = new WeatherDataFragment();
            Bundle args = new Bundle();
            args.putString(WeatherDataFragment.ARG_OBJECT,responseJson.toString());
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
