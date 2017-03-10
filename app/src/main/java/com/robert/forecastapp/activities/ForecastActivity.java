package com.robert.forecastapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.robert.forecastapp.R;
import com.robert.forecastapp.adapters.ForecastAdapter;
import com.robert.forecastapp.models.WeatherData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ForecastActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast";
    final String API_KEY = "&APPID=422ff6b63cc2df82cbc04a95aff1cbf1";
    final String TEMP_UNITS = "&units=metric";

    private GoogleApiClient mGoogleApiClient;
    private final int PERMISSION_LOCATION = 111;
    private ArrayList<WeatherData> weatherDatas = new ArrayList<>();

    private RecyclerView recyclerView;
    private ForecastAdapter forecastAdapter;
    private ProgressBar progressBar;

    private ImageView weatherIconMini;
    private ImageView weatherIcon;
    private TextView forecastDate;
    private TextView currentTemp;
    private TextView minTemp;
    private TextView forecastLocation;
    private TextView weatherType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        Toolbar toolbar = (Toolbar) findViewById(R.id.forecast_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressBar = (ProgressBar) findViewById(R.id.progressBarForcast);

        recyclerView = (RecyclerView) findViewById(R.id.content_forecast_data);
        weatherIconMini = (ImageView) findViewById(R.id.imageViewWeatherIconMini);
        weatherIcon = (ImageView) findViewById(R.id.imageViewWeatherIcon);
        forecastDate = (TextView) findViewById(R.id.textViewDate);
        currentTemp = (TextView) findViewById(R.id.textViewCurTemp);
        minTemp = (TextView) findViewById(R.id.textViewMinTemp);
        forecastLocation = (TextView) findViewById(R.id.textViewLocation);
        weatherType = (TextView) findViewById(R.id.textViewCardWeatherType);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        forecastAdapter = new ForecastAdapter(weatherDatas, getApplicationContext());
        recyclerView.setAdapter(forecastAdapter);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void downloadForecastData(Location location) {
        weatherDatas.clear();
        final String URL_COORDS = "?lat=" + location.getLatitude() + "&lon=" + location.getLongitude();
        final String URL = BASE_URL + URL_COORDS + TEMP_UNITS + API_KEY;
        //final String URL = "http://api.openweathermap.org/data/2.5/forecast?lat=41.7151377&lon=44.827096&units=metric&APPID=422ff6b63cc2df82cbc04a95aff1cbf1";
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject cityObj = response.getJSONObject("city");
                    String cityName = cityObj.getString("name");
                    String countryName = cityObj.getString("country");
                    JSONArray forecastsArr = response.getJSONArray("list");

                    for (int i = 0; i < 25; i++) {
                        JSONObject obj = forecastsArr.getJSONObject(i);
                        JSONObject main = obj.getJSONObject("main");
                        Double currentTemp = main.getDouble("temp");
                        Double maxTemp = main.getDouble("temp_max");
                        Double minTemp = main.getDouble("temp_min");

                        JSONArray weatherArr = obj.getJSONArray("weather");
                        JSONObject weatherObj = weatherArr.getJSONObject(0);
                        String weatherType = weatherObj.getString("main");

                        String rawDate = obj.getString("dt_txt");

                        WeatherData weatherData = new WeatherData(cityName, countryName, currentTemp.intValue(), maxTemp.intValue(), minTemp.intValue(), weatherType, rawDate);
                        weatherDatas.add(weatherData);
                        //Log.i("test", rawDate);
                        //Log.i("test", weatherData.getFormattedDate());
                    }
                    forecastAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("ERROR", e.getLocalizedMessage());
                }
                updateUI();
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("MY_TAG", error.getLocalizedMessage());
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    public void updateUI() {
        if (weatherDatas.size() > 0) {
            WeatherData weatherData = weatherDatas.get(0);

            switch (weatherData.getWeather()) {
                case WeatherData.WEATHER_TYPE_CLOUDS:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_mini));
                    break;
                case WeatherData.WEATHER_TYPE_RAIN:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.rainy_mini));
                    break;
                case WeatherData.WEATHER_TYPE_SNOW:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.snow));
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.snow_mini));
                    break;
                case WeatherData.WEATHER_TYPE_WIND:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.thunder_lightning));
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.thunder_lightning_mini));
                    break;
                default:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.sunny_mini));
                    break;
            }
            forecastDate.setText(weatherData.getFormattedDate());
            currentTemp.setText(Integer.toString(weatherData.getCurrentTemp()) + "°");
            minTemp.setText(Integer.toString(weatherData.getMinTemp()) + "°");
            forecastLocation.setText(weatherData.getCity() + ", " + weatherData.getCountry());
            weatherType.setText(weatherData.getWeather());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
        } else {
            startLocationServices();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        downloadForecastData(location);
    }

    public void startLocationServices() {
        try {
            LocationRequest request = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
        } catch (SecurityException e) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationServices();
                } else {
                    Toast.makeText(this, "You denied permissions!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forecast_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}