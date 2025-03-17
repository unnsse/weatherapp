package com.weatherapp.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class WeatherClientTest {

    private static MockWebServer mockWebServer;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testFetchJson_Success() throws IOException {
        // Given: Mock server returns JSON response
        String mockJson = "{ \"temp\": 72 }";
        mockWebServer.enqueue(new MockResponse().setBody(mockJson).setResponseCode(200));

        // When: Calling fetchJson
        String url = mockWebServer.url("/weather").toString();
        JsonNode response = WeatherClient.fetchJson(url);

        // Then: Validate response
        assertNotNull(response);
        assertEquals(72, response.get("temp").asInt());
    }

    @Test
    void testFetchJson_Failure() {
        // Given: Mock server returns a failure response
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        // When & Then: Expect IOException
        String url = mockWebServer.url("/weather").toString();
        IOException thrown = assertThrows(IOException.class, () -> WeatherClient.fetchJson(url));
        assertTrue(thrown.getMessage().contains("Unexpected response"));
    }
}