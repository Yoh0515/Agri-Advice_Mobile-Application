package com.example.finalthesis.Fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.Marketplace.HomeAdapterMarket;
import com.example.finalthesis.Marketplace.MarketData;
import com.example.finalthesis.R;
import com.example.finalthesis.WeatherUpdate.ApiClient;
import com.example.finalthesis.WeatherUpdate.ApiService;
import com.example.finalthesis.WeatherUpdate.CurrentWeatherResponse;
import com.example.finalthesis.WeatherUpdate.DailyForecastResponse;
import com.example.finalthesis.WeatherUpdate.ForecastAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Fragment {

    private static final String API_KEY = "e6703e9a69e64c118c7165416240106";
    private static final String VIDEO_PATH = "dashboardVideo/dashboard.mp4";

    private View view;
    private TextView message, userName;
    private RelativeLayout weatherContainer;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private String userId;
    private VideoView videoView;
    private MediaPlayer mediaPlayer;
    private boolean isMuted = true;

    private DatabaseReference databaseReference;

    private TextView cityInfo, currentWeatherDescription, currentTemperature, bacoor;
    private ImageView currentIcon;
    private RecyclerView dailyForecastRecyclerView;
    private ApiService apiService;
    MediaController mediaController;
    private ArrayList<MarketData> marketList;
    private HomeAdapterMarket homeAdapterMarket;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        weatherContainer = view.findViewById(R.id.weatherContainer);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        userName = view.findViewById(R.id.userName);
        message = view.findViewById(R.id.message);
        databaseReference = FirebaseDatabase.getInstance().getReference("Marketplace");

        bacoor = view.findViewById(R.id.bacoor);
        cityInfo = view.findViewById(R.id.cityInfo);
        currentWeatherDescription = view.findViewById(R.id.currentWeatherDescription);
        currentTemperature = view.findViewById(R.id.currentTemperature);
        currentIcon = view.findViewById(R.id.currentIcon);
        dailyForecastRecyclerView = view.findViewById(R.id.dailyForecastRecyclerView);
        videoView = view.findViewById(R.id.videoView);


        videoView.requestFocus();
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer = mp;
                mediaPlayer.setVolume(0.0f, 0.0f); // Initially mute the video
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mediaPlayer != null) {
                        if (isMuted) {
                            mediaPlayer.setVolume(1.0f, 1.0f); // Unmute the video
                            isMuted = false;
                        } else {
                            mediaPlayer.setVolume(0.0f, 0.0f); // Mute the video
                            isMuted = true;
                        }
                    }
                }
                return true;
            }
        });

        marketList = new ArrayList<>();
        homeAdapterMarket = new HomeAdapterMarket(marketList, requireContext());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(homeAdapterMarket);

        dailyForecastRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        DocumentReference documentReference = fStore.collection("Users").document(userId);

        documentReference.addSnapshotListener(executorService, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error fetching document: " + error.getMessage(), Toast.LENGTH_LONG).show());
                    return;
                }

                if (value != null && value.exists()) {
                    String username = value.getString("Username");
                    if (username != null) {
                        requireActivity().runOnUiThread(() -> userName.setText(username));
                    } else {
                        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Username field is missing in the document", Toast.LENGTH_LONG).show());
                    }
                } else {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Document does not exist", Toast.LENGTH_LONG).show());
                }
            }
        });

        setGreetingMessage();

        apiService = ApiClient.getClient().create(ApiService.class);
        fetchWeatherData(bacoor.getText().toString().trim());

        setupViews();
        loadVideoFromFirebaseStorage();

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

        ForecastAdapter adapter = new ForecastAdapter(dailyForecast, getContext());
        dailyForecastRecyclerView.setAdapter(adapter);
    }

    private void showToast(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show());
        }
    }

    private void handleNetworkFailure(Throwable t) {
        showToast("Network failure: " + t.getMessage());
        t.printStackTrace();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupViews() {
        marketList.clear();

        databaseReference.orderByChild("timestamp").limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MarketData homeData = dataSnapshot.getValue(MarketData.class);
                        if (homeData != null) {
                            homeData.setKey(dataSnapshot.getKey());
                            marketList.add(homeData);
                        }
                    }
                    homeAdapterMarket.notifyDataSetChanged();
                } else {
                    Log.d("FirebaseData", "No data available in Marketplace node");
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                // Handle possible errors.
                Log.e("FirebaseData", "Error: " + databaseError.getMessage());
            }
        });
    }

    private void setGreetingMessage() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            message.setText("Good Morning");
        } else if (hour >= 12 && hour < 17) {
            message.setText("Good Afternoon");
        } else {
            message.setText("Good Evening");
        }
    }

    private void loadVideoFromFirebaseStorage() {
        StorageReference storageReference = fStorage.getReference().child(VIDEO_PATH);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            videoView.setVideoURI(uri);
            videoView.start();
        }).addOnFailureListener(e -> {
            showToast("Failed to load video");
            Log.e("FirebaseStorage", "Error getting video URL", e);
        });
    }
}
