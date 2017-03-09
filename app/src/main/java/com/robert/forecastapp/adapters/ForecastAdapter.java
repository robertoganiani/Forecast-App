package com.robert.forecastapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.robert.forecastapp.R;
import com.robert.forecastapp.models.WeatherData;

import java.util.List;

/**
 * Created by user on 3/9/2017.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.WeatherViewHolder> {

    private List<WeatherData> weatherDataList;
    private Context context;

    public ForecastAdapter(List<WeatherData> weatherDataList, Context context) {
        this.weatherDataList = weatherDataList;
        this.context = context;
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_weather, parent, false);
        return new WeatherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        WeatherData weatherData = weatherDataList.get(position + 1);

        holder.dayOfTheWeek.setText(weatherData.getFormattedDate());
        holder.weatherType.setText(weatherData.getWeather());
        holder.maxTemp.setText(Integer.toString(weatherData.getMaxTemp()) + "°");
        holder.minTemp.setText(Integer.toString(weatherData.getMinTemp()) + "°");

        switch (weatherData.getWeather()) {
            case WeatherData.WEATHER_TYPE_CLOUDS:
                holder.weatherIconMini.setImageDrawable(context.getResources().getDrawable(R.drawable.cloudy_mini));
                break;
            case WeatherData.WEATHER_TYPE_RAIN:
                holder.weatherIconMini.setImageDrawable(context.getResources().getDrawable(R.drawable.rainy_mini));
                break;
            case WeatherData.WEATHER_TYPE_SNOW:
                holder.weatherIconMini.setImageDrawable(context.getResources().getDrawable(R.drawable.snow_mini));
                break;
            case WeatherData.WEATHER_TYPE_WIND:
                holder.weatherIconMini.setImageDrawable(context.getResources().getDrawable(R.drawable.thunder_lightning_mini));
                break;
            default:
                holder.weatherIconMini.setImageDrawable(context.getResources().getDrawable(R.drawable.sunny_mini));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return weatherDataList.size() - 1;
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {

        public ImageView weatherIconMini;
        public TextView dayOfTheWeek;
        public TextView weatherType;
        public TextView maxTemp;
        public TextView minTemp;

        public WeatherViewHolder(View itemView) {
            super(itemView);

            weatherIconMini = (ImageView) itemView.findViewById(R.id.imageViewCardIconMini);
            dayOfTheWeek = (TextView) itemView.findViewById(R.id.textViewCardDayOfWeek);
            weatherType = (TextView) itemView.findViewById(R.id.textViewCardWeatherType);
            maxTemp = (TextView) itemView.findViewById(R.id.textViewCardMaxTempMini);
            minTemp = (TextView) itemView.findViewById(R.id.textViewCardMinTempMini);
        }
    }
}
