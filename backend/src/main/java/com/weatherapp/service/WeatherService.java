package com.weatherapp.service;

import com.weatherapp.model.Address;
import com.weatherapp.model.WeatherResponseDTO;

public interface WeatherService {

    WeatherResponseDTO getWeatherByAddress(Address address);
}
