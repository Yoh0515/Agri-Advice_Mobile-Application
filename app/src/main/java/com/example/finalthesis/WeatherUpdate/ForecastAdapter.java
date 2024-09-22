package com.example.finalthesis.WeatherUpdate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<DailyForecastResponse.ForecastDay> forecastDays;
    private Context context;

    public ForecastAdapter(List<DailyForecastResponse.ForecastDay> forecastDays, Context context) {
        this.forecastDays = forecastDays;
        this.context = context;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        DailyForecastResponse.ForecastDay day = forecastDays.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(day.date);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            holder.dayOfWeek.setText(dayFormat.format(date));
        } catch (Exception e) {
            e.printStackTrace();
            holder.dayOfWeek.setText(day.date); // Fallback to displaying the raw date string if parsing fails
        }

        Glide.with(context).load("https:" + day.day.condition.icon).into(holder.dayIcon);
        holder.dayTemp.setText(String.format(Locale.getDefault(), "Day - %.1f°C", day.day.maxTempC));
        holder.nightTemp.setText(String.format(Locale.getDefault(), "Night - %.1f°C", day.day.minTempC));
    }

    @Override
    public int getItemCount() {
        return forecastDays.size();
    }

    public static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView dayOfWeek, dayTemp, nightTemp;
        ImageView dayIcon;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfWeek = itemView.findViewById(R.id.dayOfWeekTextView);
            dayIcon = itemView.findViewById(R.id.weatherIconImageView);
            dayTemp = itemView.findViewById(R.id.dayTemperatureTextView);
            nightTemp = itemView.findViewById(R.id.nightTemperatureTextView);
        }
    }
}
