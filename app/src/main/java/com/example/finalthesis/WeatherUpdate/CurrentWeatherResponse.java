package com.example.finalthesis.WeatherUpdate;

import com.google.gson.annotations.SerializedName;

public class CurrentWeatherResponse {
    @SerializedName("location")
    public Location location;

    @SerializedName("current")
    public Current current;

    public static class Location {
        @SerializedName("name")
        public String name;

        @SerializedName("country")
        public String country;

        @SerializedName("lat")
        public double lat;

        @SerializedName("lon")
        public double lon;
    }

    public static class Current {
        @SerializedName("temp_c")
        public double tempC;

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
