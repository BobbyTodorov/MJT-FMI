package exceptions;

public class LocationNotFoundException extends WeatherForecastClientException {
    public LocationNotFoundException(String msg) {
        super(msg);
    }
}
