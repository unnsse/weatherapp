package com.weatherapp.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Slf4j
public class WeatherClient {

    private final static OkHttpClient client = new OkHttpClient();
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Value("${geo.api.url}")
    private String weatherApiUrl;

    @Value("${nws.api.url}")
    private String nwsApiUrl;

    public static JsonNode fetchJson(String url) throws IOException {
        log.info("Fetching weather data from {}", url);
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Java Weather Client") // Required for NWS API
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }
            return objectMapper.readTree(response.body().string());
        }
    }
}
