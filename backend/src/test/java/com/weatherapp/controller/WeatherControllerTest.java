package com.weatherapp.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherapp.model.Address;
import com.weatherapp.model.WeatherResponseDTO;
import com.weatherapp.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class WeatherControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(weatherController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetWeatherByAddress_ShouldReturnWeather() throws Exception {
        Address testAddress = new Address("1600 Amphitheatre Parkway", "Mountain View", "CA", "94043");
        WeatherResponseDTO mockWeather = new WeatherResponseDTO("72°F", "Sunny", "Clear skies", false);

        when(weatherService.getWeatherByAddress(any(Address.class))).thenReturn(mockWeather);

        mockMvc.perform(post("/api/weather")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAddress)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temperature").value("72°F"))
                .andExpect(jsonPath("$.shortForecast").value("Sunny"))
                .andExpect(jsonPath("$.detailedForecast").value("Clear skies"))
                .andExpect(jsonPath("$.fromCache").value(false));

        verify(weatherService, times(1)).getWeatherByAddress(any(Address.class));
    }

    @Test
    void testGetWeatherByAddress_ShouldHandleServerError() throws Exception {
        Address testAddress = new Address("1600 Amphitheatre Parkway", "Mountain View", "CA", "94043");

        when(weatherService.getWeatherByAddress(any(Address.class)))
                .thenThrow(new RuntimeException("Weather service failed"));

        mockMvc.perform(post("/api/weather")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAddress)))
                .andExpect(status().isInternalServerError());

        verify(weatherService, times(1)).getWeatherByAddress(any(Address.class));
    }
}