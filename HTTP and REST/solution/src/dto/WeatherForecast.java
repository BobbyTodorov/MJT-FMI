package dto;


import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class WeatherForecast {
    @SerializedName("weather")
    private final WeatherCondition[] weatherConditions;
    @SerializedName("main")
    private final WeatherData weatherData;

    public WeatherForecast(WeatherCondition[] weatherConditions, WeatherData weatherData) {
        this.weatherConditions = weatherConditions;
        this.weatherData = weatherData;
    }

    public WeatherCondition[] getWeatherConditions() {
        return weatherConditions;
    }

    public WeatherData getWeatherData() {
        return weatherData;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WeatherForecast)) {
            return false;
        }
        WeatherForecast other = (WeatherForecast) o;
        return Arrays.equals(weatherConditions, other.weatherConditions) && weatherData.equals(other.weatherData);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (WeatherCondition weatherCondition : weatherConditions) {
            string.append(weatherCondition.toString()).append(System.lineSeparator());
        }

        string.append(weatherData.toString());
        return string.toString();
    }
}
