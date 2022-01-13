package com.cs.weatherapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodayFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String ARG_OBJECT = "object";
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final int NUM_CARDS = 9;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private JSONObject data;
    private JSONObject values;

    private GridView gridView;

    public TodayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodayFragment newInstance(String param1, String param2) {
        TodayFragment fragment = new TodayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            Bundle args = getArguments();
            try {
                data = new JSONObject(args.getString(ARG_OBJECT));
                JSONObject weekly = (JSONObject) data.getJSONObject("data").getJSONArray("timelines").get(1);
                values = weekly.getJSONArray("intervals").getJSONObject(0).getJSONObject("values");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today, container, false);

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridView = view.findViewById(R.id.todayGrid);
        GridAdapter gridAdapter = new GridAdapter(values);
        gridView.setAdapter(gridAdapter);
    }

    private void parseData() {
        // WindSpeed
        // Pressure
        // Precipitation
        // Temp
        // WeatherCode
        // Humidity
        // Visibility
        // Cloud Cover
        // Ozone
    }

    public class GridAdapter extends BaseAdapter {
        private JSONObject values;

        public GridAdapter(JSONObject values) {
            this.values = values;
        }

        @Override
        public int getCount() {
            return NUM_CARDS;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.grid,null);
            ImageView imageView = view.findViewById(R.id.grid_image);
            TextView textView1 = view.findViewById(R.id.grid_text);
            TextView textView2 = view.findViewById(R.id.grid_text_2);

            try {
            switch (position) {
                case 0:
                    imageView.setImageResource(R.drawable.wind_speed);
                    textView1.setText(df.format(values.getDouble("windSpeed")) +"mph");
                    textView2.setText(R.string.wind_speed);
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.pressure);
                    textView1.setText(df.format(values.getDouble("pressureSeaLevel")) +"inHg");
                    textView2.setText(R.string.pressure);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.weather_pouring);
                    textView1.setText(values.getInt("precipitationProbability")+"%");
                    textView2.setText(R.string.rain);
                    break;

                case 3:
                    imageView.setImageResource(R.drawable.weather_data_tab);
                    textView1.setText((int) Math.round(values.getDouble("temperatureApparent"))+"°F");
                    textView2.setText(R.string.temperature);
                    break;

                case 4:
                    android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                    layoutParams.width = 300;
                    layoutParams.height = 300;
                    imageView.setLayoutParams(layoutParams);
                    imageView.setImageResource(CodeResources.codeToImg.get(values.getInt("weatherCode")));
                    textView2.setText(CodeResources.codeToStatus.get(values.getInt("weatherCode")));
                    break;

                case 5:
                    imageView.setImageResource(R.drawable.humidity);
                    textView1.setText((int) Math.round(values.getDouble("humidity"))+"%");
                    textView2.setText(R.string.humidity);
                    break;

                case 6:
                    imageView.setImageResource(R.drawable.visibility);
                    textView1.setText(df.format(values.getDouble("visibility"))+"mi");
                    textView2.setText(R.string.visibility);
                    break;

                case 7:
                    imageView.setImageResource(R.drawable.cloud_cover);
                    textView1.setText(Math.round(values.getDouble("cloudCover"))+"%");
                    textView2.setText(R.string.cloud_cover);
                    break;

                case 8:
                    imageView.setImageResource(R.drawable.uv);
                    textView1.setText(Math.round(values.getDouble("uvIndex"))+"");
                    textView2.setText(R.string.ozone);
                    break;
            }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return view;
        }
    }
}