package exceptions;

public class WeatherForecastClientException extends Exception {
    public WeatherForecastClientException(String msg) {
        super(msg);
    }

    public WeatherForecastClientException(String msg, Exception e) {
        super(msg, e);
    }
}
