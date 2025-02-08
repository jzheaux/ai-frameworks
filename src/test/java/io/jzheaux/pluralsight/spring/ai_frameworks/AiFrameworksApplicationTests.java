package io.jzheaux.pluralsight.spring.ai_frameworks;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class AiFrameworksApplicationTests {

	@Test
	void whenGetWeatherThenReturnsHourly() {
		RestTemplate rest = new RestTemplate();
        String uri = "https://api.weather.gov/gridpoints/MTR/85,106/forecast/hourly";
        Response response = rest.getForObject(URI.create(uri), Response.class);
        assertThat(response.properties().periods()).isNotNull();
	}

	record Response(Properties properties) {}
    record Properties(List<WeatherResponse> periods) {}
    record WeatherResponse(ZonedDateTime startTime, ZonedDateTime endTime, Integer temperature, String windSpeed,
        Precipitation probabilityOfPrecipitation, String shortForecast) {}
    record Precipitation(Integer value) {}
}
