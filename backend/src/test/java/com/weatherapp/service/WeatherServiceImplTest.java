package com.weatherapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.weatherapp.client.WeatherClient;
import com.weatherapp.model.Address;
import com.weatherapp.model.WeatherResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class WeatherServiceImplTest {

    private final WeatherClient weatherServiceClient = Mockito.mock(WeatherClient.class);

    @InjectMocks
    private WeatherServiceImpl weatherService;

    private Address testAddress;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testAddress = new Address("1600 Amphitheatre Parkway", "Mountain View", "CA", "94043");

        WeatherResponseDTO mockWeatherResponse = new WeatherResponseDTO();
        mockWeatherResponse.setTemperature("72°F");
        mockWeatherResponse.setShortForecast("Sunny");
        mockWeatherResponse.setDetailedForecast("Clear skies with mild temperatures.");
        mockWeatherResponse.setFromCache(false);
    }

    @Test
    void testGetWeatherByAddress_ShouldReturnWeatherData() throws Exception {
        // Mock Geo response (Mocking the lat and lon values)
        JsonNode mockGeoResponse = mock(JsonNode.class);
        JsonNode mockGeoNode = mock(JsonNode.class);
        when(mockGeoResponse.isEmpty()).thenReturn(false);
        when(mockGeoResponse.get(0)).thenReturn(mockGeoNode);

        // Mocking lat and lon values (make sure get("lat") and get("lon") return valid JsonNode objects)
        JsonNode mockLatNode = mock(JsonNode.class);
        JsonNode mockLonNode = mock(JsonNode.class);

        when(mockGeoNode.get("lat")).thenReturn(mockLatNode);
        when(mockGeoNode.get("lon")).thenReturn(mockLonNode);

        // Mock the values returned by asText() on lat and lon nodes
        when(mockLatNode.asText()).thenReturn("37.422");
        when(mockLonNode.asText()).thenReturn("-122.084");

        // Mock NWS response
        JsonNode mockNwsResponse = mock(JsonNode.class);
        when(mockNwsResponse.has("properties")).thenReturn(true);
        JsonNode propertiesNode = mock(JsonNode.class);
        when(mockNwsResponse.get("properties")).thenReturn(propertiesNode);

        // Mock the "forecast" property to return a valid string
        JsonNode mockForecastUrlNode = mock(JsonNode.class);
        when(propertiesNode.get("forecast")).thenReturn(mockForecastUrlNode);
        when(mockForecastUrlNode.asText()).thenReturn("https://api.weather.gov/gridpoints/MTR/84,105/forecast");

        // Mock Forecast response
        JsonNode mockForecastResponse = mock(JsonNode.class);
        when(mockForecastResponse.has("properties")).thenReturn(true);
        JsonNode forecastPropertiesNode = mock(JsonNode.class);
        when(mockForecastResponse.get("properties")).thenReturn(forecastPropertiesNode);
        JsonNode periodsNode = mock(JsonNode.class);
        when(forecastPropertiesNode.get("periods")).thenReturn(periodsNode);

        // Mock period
        JsonNode periodNode = mock(JsonNode.class);
        when(periodsNode.get(0)).thenReturn(periodNode);

        // Mock temperature, short forecast, and detailed forecast
        JsonNode temperatureNode = mock(JsonNode.class);
        when(periodNode.get("temperature")).thenReturn(temperatureNode);
        when(temperatureNode.asInt()).thenReturn(72);  // Mock temperature value to 72

        // Mock short forecast
        JsonNode shortForecastNode = mock(JsonNode.class);
        when(periodNode.get("shortForecast")).thenReturn(shortForecastNode);
        when(shortForecastNode.asText()).thenReturn("Sunny");

        // Mock detailed forecast
        JsonNode detailedForecastNode = mock(JsonNode.class);
        when(periodNode.get("detailedForecast")).thenReturn(detailedForecastNode);
        when(periodNode.get("detailedForecast").asText()).thenReturn("Clear skies with mild temperatures.");

        WeatherResponseDTO response = weatherService.getWeatherByAddress(testAddress);

        // Assert
        assertNotNull(response);
        assertFalse(response.getFromCache());
    }
}