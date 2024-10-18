package kopo.poly.service;

import kopo.poly.dto.WeatherDTO;

public interface IWeatherService {

    WeatherDTO getWeather(String city, String apiKey) throws Exception;

}