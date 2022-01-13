package com.cs.weatherapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.HIGradient;
import com.highsoft.highcharts.common.HIStop;
import com.highsoft.highcharts.core.*;
import com.highsoft.highcharts.common.hichartsclasses.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeeklyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeeklyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String ARG_OBJECT = "object";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private JSONObject data;
    private JSONArray intervals;
    private String startTime;

    public WeeklyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeeklyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeeklyFragment newInstance(String param1, String param2) {
        WeeklyFragment fragment = new WeeklyFragment();
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
                JSONObject weekly = (JSONObject) data.getJSONObject("data").getJSONArray("timelines").get(0);
                intervals = weekly.getJSONArray("intervals");
                startTime = weekly.getString("startTime");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weekly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View parent = getView();
        View loading = parent.findViewById(R.id.loadingView);
        loading.setVisibility(View.VISIBLE);
        HIChartView dailyChart = (HIChartView) parent.findViewById(R.id.dailyChart);

        try {
            draw(dailyChart);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        loading.setVisibility(View.GONE);
//        ((TextView) view.findViewById(R.id.weeklyText))
//                .setText(Integer.toString(args.getInt(ARG_OBJECT)));
    }

    public void draw(HIChartView chartView) throws JSONException {

        HIOptions options = new HIOptions();

        HIChart chart = new HIChart();
        chart.setType("arearange");
        chart.setZoomType("x");
        options.setChart(chart);

        HITitle title = new HITitle();
        title.setText("Temperature variation by day");
        HICSSObject titleCSS = new HICSSObject();
        titleCSS.setColor("#716f6f");
        titleCSS.setFontWeight("bold");
        titleCSS.setFontSize("16");
        title.setStyle(titleCSS);
        options.setTitle(title);

        HIXAxis xaxis = new HIXAxis();
        xaxis.setType("datetime");
        xaxis.setTickInterval(24*3600*1000);
        options.setXAxis(new ArrayList<HIXAxis>(){{add(xaxis);}});

        HIYAxis yaxis = new HIYAxis();
        yaxis.setTitle(new HITitle());
        options.setYAxis(new ArrayList<HIYAxis>(){{add(yaxis);}});

        HITooltip tooltip = new HITooltip();
        tooltip.setShadow(true);
        tooltip.setShared(true);
        tooltip.setValueSuffix("°F");
        tooltip.setXDateFormat("%Y-%m-%d");
        options.setTooltip(tooltip);

        HILegend legend = new HILegend();
        legend.setEnabled(false);
        options.setLegend(legend);

        HIArearange series = new HIArearange();
        series.setName("Temperatures");
        HIGradient g = new HIGradient(0,0,0,1);
        LinkedList<HIStop> l = new LinkedList<>();
        l.add(new HIStop(0, HIColor.initWithRGBA(242,144,69, 0.70)));
        l.add(new HIStop(1, HIColor.initWithRGBA(25,171,204,0.5)));
        series.setColor(HIColor.initWithLinearGradient(g, l));
        HIMarker m = new HIMarker();
        m.setEnabled(true);
        series.setMarker(m);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        long epoch = 0;
        try {
            Date date = df.parse(startTime);
            epoch = date.getTime();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        series.setPointStart(epoch);
        series.setPointInterval(24*3600*1000);
//        Object[][] seriesData = new Object[][]{{10,20},{15,20},{10,26},{47,90},{30,60},{40,50},{10,20},{10,20}};
        ArrayList<ArrayList<Double>> dataArr = parseIntervals();
        series.setData(dataArr);
        options.setSeries(new ArrayList<>(Arrays.asList(series)));

        chartView.setOptions(options);
    }

    public ArrayList<ArrayList<Double>> parseIntervals() throws JSONException {
        ArrayList<ArrayList<Double>> temps = new ArrayList<>();
        for(int i=0; i<intervals.length(); i++) {
            ArrayList<Double> temp = new ArrayList<>();
            Double tMax = intervals.getJSONObject(i).getJSONObject("values").getDouble("temperatureMax");
            Double tMin = intervals.getJSONObject(i).getJSONObject("values").getDouble("temperatureMin");
            temp.add(tMin);
            temp.add(tMax);
            temps.add(temp);
        }
        return temps;
    }
}