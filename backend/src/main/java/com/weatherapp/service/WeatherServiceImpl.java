package com.weatherapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.weatherapp.client.WeatherClient;
import com.weatherapp.model.Address;
import com.weatherapp.model.WeatherResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WeatherServiceImpl implements WeatherService {

    private final Cache<String, WeatherResponseDTO> weatherCache;

    public WeatherServiceImpl() {
        this.weatherCache = Caffeine.newBuilder()
                .expireAfterWrite(300, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    public WeatherResponseDTO getWeatherByAddress(Address address) {
        log.info("Getting weather for: {}", address.toString());

        String zipCode = address.getZipCode();
        WeatherResponseDTO cachedWeather = this.weatherCache.getIfPresent(zipCode);
        if (cachedWeather != null) {
            cachedWeather.setFromCache(true);
            log.info("Returning cached weather for: {}", zipCode);
            return cachedWeather;
        }

        WeatherResponseDTO weatherResponse = getWeather(zipCode);
        if (weatherResponse != null) {
            weatherResponse.setFromCache(false);  // Mark as fresh data
            weatherCache.put(zipCode, weatherResponse);  // Cache the response
        }

        log.info("Weather response = {}", weatherResponse);
        return weatherResponse;
    }

    private WeatherResponseDTO getWeather(String zipCode) {
        WeatherResponseDTO weatherResponseDTO = new WeatherResponseDTO();
        try {
            // Step 1: Get latitude and longitude from ZIP code
            String geoUrl = "https://nominatim.openstreetmap.org/search?postalcode=" + zipCode + "&country=US&format=json";

            JsonNode geoResponse = WeatherClient.fetchJson(geoUrl);
            if (geoResponse.isEmpty()) {
                return null;
            }

            String lat = geoResponse.get(0).get("lat").asText();
            String lon = geoResponse.get(0).get("lon").asText();

            // Step 2: Get NWS API gridpoint for weather forecast
            String nwsUrl = "https://api.weather.gov/points/" + lat + "," + lon;
            JsonNode nwsResponse = WeatherClient.fetchJson(nwsUrl);

            if (!nwsResponse.has("properties")) {
                return null;
            }

            String forecastUrl = nwsResponse.get("properties").get("forecast").asText();

            // Step 3: Fetch the weather forecast
            JsonNode forecastResponse = WeatherClient.fetchJson(forecastUrl);
            log.info(forecastResponse.toString());
            if (!forecastResponse.has("properties")) {
                return null;
            }

            for (JsonNode period : forecastResponse.get("properties").get("periods")) {
                int temperature = period.get("temperature").asInt();
                weatherResponseDTO.setTemperature(temperature + "°F");
                weatherResponseDTO.setShortForecast(period.get("shortForecast").asText());
                weatherResponseDTO.setDetailedForecast(period.get("detailedForecast").asText());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return weatherResponseDTO;
    }
}