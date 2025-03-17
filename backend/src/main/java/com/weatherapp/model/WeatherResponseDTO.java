package com.weatherapp.model;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponseDTO {

    private String temperature;
    private String shortForecast;
    private String detailedForecast;

    @Nullable
    private Boolean fromCache;

}
