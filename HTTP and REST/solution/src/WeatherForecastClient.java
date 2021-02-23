
import dto.WeatherForecast;
import exceptions.LocationNotFoundException;
import exceptions.WeatherForecastClientException;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherForecastClient {

    private static final String APP_ID = ""; //put a private app key id here
    private static final String OPENWEATHERMAP_URI = "http://api.openweathermap.org/data/2.5/weather?"
        + "q=%s&units=metric&lang=bg&appid=" + APP_ID;

    private static final int OK_STATUS_CODE = 200;
    private static final int LOCATION_NOT_FOUND_CODE = 404;
    private static final String URL_SPACE_CODE = "%20";
    private static final String CITY_NOT_FOUND_EXCEPTION_MESSAGE = "City %s was not found.";
    private static final String RESPONSE_GENERAL_EXCEPTION_MESSAGE = "Failed receiving response from http client.";
    private static final String ARGUMENT_NULL_EXCEPTION_MESSAGE = "Argument %s must not be null.";

    private final HttpClient weatherHttpClient;

    public WeatherForecastClient(HttpClient weatherHttpClient) {
        if (weatherHttpClient == null) {
            throw new IllegalArgumentException(String.format(ARGUMENT_NULL_EXCEPTION_MESSAGE, "weatherHttpClient"));
        }
        this.weatherHttpClient = weatherHttpClient;
    }

    /**
     * Fetches the weather forecast for the specified city.
     *
     * @return the forecast
     * @throws LocationNotFoundException if the city is not found
     * @throws WeatherForecastClientException if information regarding the weather for this location
     *             could not be retrieved
     */
    public WeatherForecast getForecast(String city) throws WeatherForecastClientException {
        if (city == null) {
            throw new IllegalArgumentException(String.format(ARGUMENT_NULL_EXCEPTION_MESSAGE, "city"));
        }
        city = city.replaceAll(" ", URL_SPACE_CODE);
        HttpResponse<String> response = getResponseFromURI(String.format(OPENWEATHERMAP_URI, city));

        if (response.statusCode() == LOCATION_NOT_FOUND_CODE) {
            throw new LocationNotFoundException(String.format(CITY_NOT_FOUND_EXCEPTION_MESSAGE, city));
        }

        if (response.statusCode() != OK_STATUS_CODE) {
            throw new WeatherForecastClientException(RESPONSE_GENERAL_EXCEPTION_MESSAGE);
        }

        return parseFromJsonToWeatherForecast(response.body());
    }

    private HttpResponse<String> getResponseFromURI(String uri) throws WeatherForecastClientException {
        if (uri == null) {
            throw new IllegalArgumentException(String.format(ARGUMENT_NULL_EXCEPTION_MESSAGE, "uri"));
        }
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();
        HttpResponse<String> response;
        try {
            response = weatherHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new WeatherForecastClientException(RESPONSE_GENERAL_EXCEPTION_MESSAGE, e);
        }
        return response;
    }

    private WeatherForecast parseFromJsonToWeatherForecast(String jsonString) {
        return new Gson().fromJson(jsonString, WeatherForecast.class);
    }
}