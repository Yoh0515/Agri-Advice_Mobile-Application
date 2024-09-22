package com.example.finalthesis.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;
import com.example.finalthesis.WeatherUpdate.ApiClient;
import com.example.finalthesis.WeatherUpdate.ApiService;
import com.example.finalthesis.WeatherUpdate.CurrentWeatherResponse;
import com.example.finalthesis.WeatherUpdate.DailyForecastResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Weather_Updates extends Fragment {

    private View view;
    private static final String API_KEY = "e6703e9a69e64c118c7165416240106";
    private ApiService apiService;
    private EditText cityInput;
    private TextView cityInfo, currentWeatherDescription, currentTemperature, bacoor;
    private ImageView currentIcon;
    private LinearLayout dailyForecastContainer;
    private ImageView searchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weather__updates, container, false);

//        cityInput = view.findViewById(R.id.cityInput);
        bacoor = view.findViewById(R.id.bacoor);
        cityInfo = view.findViewById(R.id.cityInfo);
        currentWeatherDescription = view.findViewById(R.id.currentWeatherDescription);
        currentTemperature = view.findViewById(R.id.currentTemperature);
        currentIcon = view.findViewById(R.id.currentIcon);
        dailyForecastContainer = view.findViewById(R.id.dailyForecast);
        searchButton = view.findViewById(R.id.searchButton);

        apiService = ApiClient.getClient().create(ApiService.class);
        bacoor.setText("Bacoor");

        searchButton.setOnClickListener(v -> fetchWeatherData(cityInput.getText().toString().trim()));
        fetchWeatherData(bacoor.getText().toString().trim());

        return view;
    }

    private void fetchWeatherData(String city) {
        if (city.isEmpty()) {
            showToast("Please enter a city.");
            return;
        }

        apiService.getCurrentWeather(API_KEY, city).enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CurrentWeatherResponse currentWeather = response.body();
                    fetchDailyForecast(currentWeather.location.lat, currentWeather.location.lon, currentWeather);
                } else {
                    if (response.errorBody() != null) {
                        try {
                            Log.e("WeatherAPI", "Error: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    showToast("Failed to fetch weather data.");
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                handleNetworkFailure(t);
            }
        });
    }


    private void fetchDailyForecast(double lat, double lon, CurrentWeatherResponse currentWeather) {
        String location = String.format(Locale.getDefault(), "%.2f,%.2f", lat, lon);
        apiService.getDailyForecast(API_KEY, location, 7).enqueue(new Callback<DailyForecastResponse>() {
            @Override
            public void onResponse(Call<DailyForecastResponse> call, Response<DailyForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayWeather(currentWeather, response.body().forecast.forecastday);
                } else {
                    showToast("Failed to fetch daily forecast data.");
                }
            }

            @Override
            public void onFailure(Call<DailyForecastResponse> call, Throwable t) {
                handleNetworkFailure(t);
            }
        });
    }

    private void displayWeather(CurrentWeatherResponse currentWeather, List<DailyForecastResponse.ForecastDay> dailyForecast) {
        if (!isAdded() || getActivity() == null) {
            return;
        }

        cityInfo.setText(String.format(Locale.getDefault(), "%s, %s", currentWeather.location.name, currentWeather.location.country));
        currentWeatherDescription.setText(currentWeather.current.condition.text);
        currentTemperature.setText(String.format(Locale.getDefault(), " %.2f°C", currentWeather.current.tempC));
        Glide.with(this).load("https:" + currentWeather.current.condition.icon).into(currentIcon);

        dailyForecastContainer.removeAllViews();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (DailyForecastResponse.ForecastDay day : dailyForecast) {
            View view = getLayoutInflater().inflate(R.layout.forecast_item, dailyForecastContainer, false);
            TextView dayOfWeek = view.findViewById(R.id.dayOfWeekTextView);
            ImageView dayIcon = view.findViewById(R.id.weatherIconImageView);
            TextView dayTemp = view.findViewById(R.id.dayTemperatureTextView);
            TextView nightTemp = view.findViewById(R.id.nightTemperatureTextView);

            try {
                Date date = sdf.parse(day.date);
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                dayOfWeek.setText(dayFormat.format(date));
            } catch (Exception e) {
                e.printStackTrace();
                dayOfWeek.setText(day.date); // Fallback to displaying the raw date string if parsing fails
            }

            Glide.with(this).load("https:" + day.day.condition.icon).into(dayIcon);
            dayTemp.setText(String.format(Locale.getDefault(), "Day - %.1f°C", day.day.maxTempC));
            nightTemp.setText(String.format(Locale.getDefault(), "Night - %.1f°C", day.day.minTempC));

            dailyForecastContainer.addView(view);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private void handleNetworkFailure(Throwable t) {
        showToast("Network failure: " + t.getMessage());
        t.printStackTrace();
    }
}
