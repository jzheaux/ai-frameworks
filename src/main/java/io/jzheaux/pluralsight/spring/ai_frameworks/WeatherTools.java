package io.jzheaux.pluralsight.spring.ai_frameworks;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WeatherTools {
    @Bean
    @Description("Get Weather Forecast")
    public Supplier<List<WeatherResponse>> getWeatherForecast() {
        return () -> {
            RestTemplate rest = new RestTemplate();
            String uri = "https://api.weather.gov/gridpoints/MTR/85,106/forecast/hourly";
            Response response = rest.getForObject(URI.create(uri), Response.class);
            return response.properties().periods();
        };
    }

    public record Response(Properties properties) {}
    public record Properties(List<WeatherResponse> periods) {}
    public record WeatherResponse(ZonedDateTime startTime, ZonedDateTime endTime, Integer temperature, String windSpeed,
        Precipitation probabilityOfPrecipitation, String shortForecast) {}
    public record Precipitation(Integer value) {}
}
