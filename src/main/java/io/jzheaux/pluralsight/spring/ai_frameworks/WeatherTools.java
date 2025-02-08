package io.jzheaux.pluralsight.spring.ai_frameworks;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WeatherTools {
    private static final String WEATHER_URI = 
        "https://api.weather.gov/gridpoints/MTR/85,106/forecast/hourly";
    private final RestClient rest = RestClient.create(WEATHER_URI);

    public record Response(Properties properties) {}
    public record Properties(List<WeatherResponse> periods) {}
    public record WeatherResponse(ZonedDateTime startTime, ZonedDateTime endTime, Integer temperature, String windSpeed,
        Precipitation probabilityOfPrecipitation, String shortForecast) {}
    public record Precipitation(Integer value) {}
}
