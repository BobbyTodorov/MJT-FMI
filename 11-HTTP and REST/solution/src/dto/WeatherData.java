package dto;

import com.google.gson.annotations.SerializedName;

public class WeatherData {

    @SerializedName("temp")
    private final double temp;
    @SerializedName("feels_like")
    private final double feelsLike;

    public WeatherData(double temp, double feelsLike) {
        this.temp = temp;
        this.feelsLike = feelsLike;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public double getTemp() {
        return temp;
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
        if (!(o instanceof WeatherData)) {
            return false;
        }
        WeatherData other = (WeatherData) o;
        return temp == other.temp && feelsLike == other.feelsLike;
    }

    @Override
    public String toString() {
        return "Temp: " + temp + ", Feels like: " + feelsLike;
    }
}
