
import dto.WeatherCondition;
import dto.WeatherData;
import dto.WeatherForecast;
import exceptions.LocationNotFoundException;
import exceptions.WeatherForecastClientException;
import com.google.gson.Gson;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WeatherForecastClientTest {

    @Mock
    private static HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private static WeatherForecastClient client;

    @BeforeClass
    public static void setUp() {
        client = new WeatherForecastClient(httpClientMock);
    }

    @Test
    public void testGetForecastReturnedWeatherForecastComponents() throws Exception {
        when(httpClientMock.send(Mockito.any(HttpRequest.class),
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
            .thenReturn(httpResponseMock);
        WeatherForecast expected = new WeatherForecast(
            new WeatherCondition[]{new WeatherCondition("cold")},
            new WeatherData(1.20, -1.3));
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(new Gson().toJson(expected));

        WeatherForecast actual = client.getForecast("sofia");
        assertEquals("getForecast must return correct description value",
            expected.getWeatherConditions()[0].getDescription(), actual.getWeatherConditions()[0].getDescription());
        assertEquals("getForecast must return correct temp value",
            expected.getWeatherData().getTemp(), actual.getWeatherData().getTemp(), 0.01);
        assertEquals("getForecast must return correct feels like value",
            expected.getWeatherData().getFeelsLike(), actual.getWeatherData().getFeelsLike(), 0.01);
    }

    @Test
    public void testGetForecastReturnedObject() throws Exception {
        when(httpClientMock.send(Mockito.any(HttpRequest.class),
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
            .thenReturn(httpResponseMock);
        WeatherForecast expected = new WeatherForecast(
            new WeatherCondition[]{new WeatherCondition("hot")},
            new WeatherData(35, 36.87));
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(new Gson().toJson(expected));

        WeatherForecast actual = client.getForecast("pleven");
        assertEquals("getForecast must return correct WeatherForecast object", expected, actual);
    }

    @Test
    public void testGetForecastReturnedObjectWithTwoWordsAsCityName() throws Exception {
        when(httpClientMock.send(Mockito.any(HttpRequest.class),
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
            .thenReturn(httpResponseMock);
        WeatherForecast expected = new WeatherForecast(
            new WeatherCondition[]{new WeatherCondition("hot")},
            new WeatherData(35, 36.87));
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(new Gson().toJson(expected));

        WeatherForecast actual = client.getForecast("стара загора");
        assertEquals("getForecast with two words as city name must return correct WeatherForecast object",
            expected, actual);
    }

    @Test(expected = LocationNotFoundException.class)
    public void testGetForecastWithInvalidLocation() throws Exception {
        when(httpClientMock.send(Mockito.any(HttpRequest.class),
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
            .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(404);

        client.getForecast("InvalidCity");
    }

    @Test(expected = WeatherForecastClientException.class)
    public void testGetForecastWithSendWithIoException() throws Exception {
        when(httpClientMock.send(Mockito.any(HttpRequest.class),
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
            .thenThrow(IOException.class);

        client.getForecast("some city");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetForecastWithInvalidCityArgument() throws Exception {
        client.getForecast("!@#$%");
    }
}