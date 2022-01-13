package com.cs.weatherapp;

import java.util.HashMap;

public class CodeResources {
    public static HashMap<Integer, Integer> codeToImg;
    public static HashMap<Integer, Integer> codeToStatus;
    static {
        codeToImg = new HashMap<>();
        codeToImg.put(0,R.drawable.clear_day);
        codeToImg.put(1000,R.drawable.clear_day);
        codeToImg.put(1100,R.drawable.mostly_clear_day);
        codeToImg.put(1101,R.drawable.partly_cloudy_day);
        codeToImg.put(1102,R.drawable.mostly_cloudy);
        codeToImg.put(1001,R.drawable.cloudy);
        codeToImg.put(2000,R.drawable.fog);
        codeToImg.put(2100,R.drawable.fog_light);
        codeToImg.put(8000,R.drawable.tstorm);
        codeToImg.put(5001,R.drawable.flurries);
        codeToImg.put(5100,R.drawable.snow_light);
        codeToImg.put(5000,R.drawable.snow);
        codeToImg.put(5101,R.drawable.snow_heavy);
        codeToImg.put(7102,R.drawable.ice_pellets_light);
        codeToImg.put(7000,R.drawable.ice_pellets);
        codeToImg.put(7101,R.drawable.ice_pellets_heavy);
        codeToImg.put(4000,R.drawable.drizzle);
        codeToImg.put(6000,R.drawable.freezing_drizzle);
        codeToImg.put(6200,R.drawable.freezing_rain_light);
        codeToImg.put(6001,R.drawable.freezing_rain);
        codeToImg.put(6201,R.drawable.freezing_rain_heavy);
        codeToImg.put(4200,R.drawable.rain_light);
        codeToImg.put(4001,R.drawable.rain);
        codeToImg.put(4201,R.drawable.rain_heavy);
        codeToImg.put(3000,R.drawable.light_wind);
        codeToImg.put(3001,R.drawable.wind);
        codeToImg.put(3002,R.drawable.strong_wind);

        codeToStatus = new HashMap<>();

        codeToStatus.put(0,R.string.unkown);
        codeToStatus.put(1000,R.string.clear);
        codeToStatus.put(1100,R.string.mostly_clear	    );
        codeToStatus.put(1101,R.string.partly_cloudy	    );
        codeToStatus.put(1102,R.string.mostly_cloudy	    );
        codeToStatus.put(1001,R.string.cloudy			    );
        codeToStatus.put(2000,R.string.fog				);
        codeToStatus.put(2100,R.string.light_fog		    );
        codeToStatus.put(8000,R.string.thunderstorm	    );
        codeToStatus.put(5001,R.string.flurries			);
        codeToStatus.put(5100,R.string.light_snow		    );
        codeToStatus.put(5000,R.string.snow				);
        codeToStatus.put(5101,R.string.heavy_snow		    );
        codeToStatus.put(7102,R.string.light_ice_pellets	);
        codeToStatus.put(7000,R.string.ice_pellets		);
        codeToStatus.put(7101,R.string.heavy_ice_pellets	);
        codeToStatus.put(4000,R.string.drizzle			);
        codeToStatus.put(6000,R.string.freezing_drizzle	);
        codeToStatus.put(6200,R.string.light_freezing_rain);
        codeToStatus.put(6001,R.string.freezing_rain		);
        codeToStatus.put(6201,R.string.heavy_freezing_rain);
        codeToStatus.put(4200,R.string.light_rain		    );
        codeToStatus.put(4001,R.string.rain				);
        codeToStatus.put(4201,R.string.heavy_rain		    );
        codeToStatus.put(3000,R.string.light_wind		    );
        codeToStatus.put(3001,R.string.wind				);
        codeToStatus.put(3002,R.string.strong_wind		);
    }
}
