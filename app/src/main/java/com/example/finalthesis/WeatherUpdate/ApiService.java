package com.example.finalthesis.WeatherUpdate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("current.json")
    Call<CurrentWeatherResponse> getCurrentWeather(@Query("key") String apiKey, @Query("q") String city);

    @GET("forecast.json")
    Call<DailyForecastResponse> getDailyForecast(@Query("key") String apiKey, @Query("q") String city, @Query("days") int days);
}
