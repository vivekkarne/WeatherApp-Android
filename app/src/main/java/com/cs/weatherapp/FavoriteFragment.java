package com.cs.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.ListAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final DecimalFormat df = new DecimalFormat("0.00");
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private JSONObject data;

    private int currentTemperature;
    private Integer currentSummary;
    private String currentLocation = "Los Angeles, California";
    private Integer currentHumidity;
    private Double currentWindSpeed;
    private Double currentVisibility;
    private Double currentpressure;
    private Integer currentIcon;
    private String color = "#2E2E2E";
    private Integer position = 1;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private List<JSONObject> weeklyData;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public static  FavoriteFragment newInstance(JSONObject weatherData, String address, String color) {
        FavoriteFragment fragment = new FavoriteFragment();
        fragment.setData(weatherData, address, color);

        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FavoriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteFragment newInstance(Integer position, List<JSONObject> weatherData, String address) {
        FavoriteFragment fragment = new FavoriteFragment();
        fragment.setData(weatherData.get(position), address, position);
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, par);
//        fragment.setArguments(args);
        return fragment;
    }
    public void setData(JSONObject data, String address, String color) {
        this.data = data;
        this.currentLocation = address;
        this.color = color;
    }
    public void setData(JSONObject data, String address, Integer position) {
        this.data = data;
        this.currentLocation = address;
        this.position = position;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
//            try {
//                data = new JSONObject(getArguments().getString("data1"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mPreferences = getActivity().getSharedPreferences(getString(R.string.secret_key), Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        final View view =inflater.inflate(R.layout.fragment_favorite, container, false);
        TextView t = (TextView) view.findViewById(R.id.mainLocation);
        try {
            populateCurrentDataFields();
            populateWeeklyDataFields();
            setMainCard(view);
            setDetailsCard(view);
            setListView(view);
            setColor(view);
            floatingIconSettings(view);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void floatingIconSettings(View view) {
        FloatingActionButton fab =  view.findViewById(R.id.fab);
        if(position!=0) {
            String city = currentLocation.split(",")[0];
            if(mPreferences.contains("k_"+city)) {
                fab.setImageResource(R.drawable.map_marker_minus);
            }
            else {
                fab.setImageResource(R.drawable.map_marker_plus);
            }
            fab.show();
            fab.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String city = currentLocation.split(",")[0];
                    if(mPreferences.contains("k_"+city)){
                        mEditor.remove("k_"+city).apply();
                        mEditor.remove("full_"+city).apply();
                        Toast.makeText(getActivity(),currentLocation + " was removed from favorites.", Toast.LENGTH_SHORT).show();
                        FloatingActionButton fav = v.findViewById(R.id.fab);
                        fav.setImageResource(R.drawable.map_marker_plus);
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).removeTab(position);
                        }
                    }
                    else{
                        FloatingActionButton fav = view.findViewById(R.id.fab);
                        fav.setImageResource(R.drawable.map_marker_minus);
                        mEditor.putString("k_"+city, data.toString());
                        mEditor.putString("full_"+city, currentLocation);
                        mEditor.apply();
                        Toast.makeText(getActivity(),currentLocation + " was Added to favorites.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            fab.hide();
        }
    }

    private void setColor(View view) {
        ((LinearLayout)view.findViewById(R.id.linearLayout)).setBackgroundColor(Color.parseColor(color));
    }

    void populateCurrentDataFields() throws JSONException{
        JSONObject currData = data.getJSONObject("data").getJSONArray("timelines").getJSONObject(1).getJSONArray("intervals").getJSONObject(0).getJSONObject("values");
        this.currentTemperature = (int) Math.round(currData.getDouble("temperatureApparent"));
        this.currentHumidity = (int) Math.round(currData.getDouble("humidity"));
        this.currentWindSpeed = currData.getDouble("windSpeed");
        this.currentVisibility = currData.getDouble("visibility");
        this.currentpressure = currData.getDouble("pressureSeaLevel");
        this.currentSummary = CodeResources.codeToStatus.get(currData.getInt("weatherCode"));
        this.currentIcon = CodeResources.codeToImg.get(currData.getInt("weatherCode"));
    }

    void populateWeeklyDataFields() throws  JSONException {
        weeklyData = new ArrayList<JSONObject>();
        JSONArray arrData = data.getJSONObject("data").getJSONArray("timelines").getJSONObject(0).getJSONArray("intervals");
        for(int i = 0; i < 8; i++) {
            JSONObject temp = new JSONObject();
            temp.put("startTime", OffsetDateTime.parse(arrData.getJSONObject(i).getString("startTime")).toLocalDate().toString());
            temp.put("tempMin", Math.round(arrData.getJSONObject(i).getJSONObject("values").getDouble("temperatureMin")));
            temp.put("tempMax", Math.round(arrData.getJSONObject(i).getJSONObject("values").getDouble("temperatureMax")));
            temp.put("imgCode", CodeResources.codeToImg.get(arrData.getJSONObject(i).getJSONObject("values").getInt("weatherCode")));
            weeklyData.add(temp);
        }
    }

    private void setMainCard(View view) {
        ((ImageView)view.findViewById(R.id.mainWeatherIcon)).setImageResource(this.currentIcon);
        ((TextView)view.findViewById(R.id.mainTemperature)).setText(((Integer)this.currentTemperature).toString()+"°F");
        ((TextView)view.findViewById(R.id.mainSummary)).setText(this.currentSummary);
        ((TextView)view.findViewById(R.id.mainLocation)).setText(this.currentLocation);
        CardView mainCard = view.findViewById(R.id.mainCard);
        mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), Details.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("responseJson", data.toString());
                mBundle.putString("address", currentLocation);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }
        });
    }

    private void setDetailsCard(View view) {
        setImages(view);
        ((TextView)view.findViewById(R.id.fieldsHumidityText)).setText(((Integer)this.currentHumidity).toString()+"%");
        ((TextView)view.findViewById(R.id.fieldsWindSpeedText)).setText(this.currentWindSpeed.toString()+"mph");
        ((TextView)view.findViewById(R.id.fieldsVisibilityText)).setText(this.currentVisibility+"mi");
        ((TextView)view.findViewById(R.id.fieldsPressureText)).setText(this.currentpressure+"inHg");

    }

    private void setImages(View view) {
        ((ImageView)view.findViewById(R.id.fieldsHumidityIcon)).setImageResource(R.drawable.humidity);
        ((ImageView)view.findViewById(R.id.fieldsWindSpeedIcon)).setImageResource(R.drawable.wind_speed);
        ((ImageView)view.findViewById(R.id.fieldsPressureIcon)).setImageResource(R.drawable.pressure);
        ((ImageView)view.findViewById(R.id.fieldsVisibilityIcon)).setImageResource(R.drawable.visibility);
    }

    public void setListView(View view) {
        ListView listView = view.findViewById(R.id.listView);
        ListTableAdapter adapter= new ListTableAdapter(getContext(), weeklyData);
        listView.setAdapter(adapter);
    }

    class ListTableAdapter extends ArrayAdapter<JSONObject> {
        public ListTableAdapter(Context context, List<JSONObject> wData) {
            super(context, 0, wData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject rowData = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.day_row, parent, false);
            }

            try {
                ((TextView)convertView.findViewById(R.id.date)).setText(rowData.getString("startTime"));
                ((TextView)convertView.findViewById(R.id.maxTemp)).setText(((Long)rowData.getLong("tempMax")).toString()+"°F");
                ((TextView)convertView.findViewById(R.id.minTemp)).setText(((Long)rowData.getLong("tempMin")).toString()+"°F");
                ((ImageView)convertView.findViewById(R.id.weatherIcon)).setImageResource(rowData.getInt("imgCode"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }
}