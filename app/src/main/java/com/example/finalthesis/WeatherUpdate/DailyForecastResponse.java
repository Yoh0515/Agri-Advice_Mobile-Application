package com.example.finalthesis.WeatherUpdate;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DailyForecastResponse {
    @SerializedName("forecast")
    public Forecast forecast;

    public static class Forecast {
        @SerializedName("forecastday")
        public List<ForecastDay> forecastday;
    }

    public static class ForecastDay {
        @SerializedName("date")
        public String date; // Date string in "yyyy-MM-dd" format

        @SerializedName("day")
        public Day day;

        public static class Day {
            @SerializedName("maxtemp_c")
            public double maxTempC;

            @SerializedName("mintemp_c")
            public double minTempC;

            @SerializedName("condition")
            public Condition condition;
        }

        public static class Condition {
            @SerializedName("text")
            public String text;

            @SerializedName("icon")
            public String icon;
        }
    }
}
