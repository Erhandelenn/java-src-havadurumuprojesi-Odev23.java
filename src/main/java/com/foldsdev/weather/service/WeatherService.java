package com.foldsdev.weather.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foldsdev.weather.dto.WeatherDto;
import com.foldsdev.weather.dto.WeatherResponse;
import com.foldsdev.weather.model.WeatherEntity;
import com.foldsdev.weather.repository.WeatherRepository;
import com.foldsdev.weather.constants.Constants;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class WeatherService {
    private final WeatherRepository weatherRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectsMapper = new ObjectMapper();


    public WeatherService (WeatherRepository weatherRepository , RestTemplate restTemplate, RestTemplate restTemplate1){
        this.weatherRepository = weatherRepository;
        this.restTemplate = restTemplate1;
    }

    public WeatherDto getWeatherByCityName(String city)
    {

        Optional<WeatherEntity>weatherEntityOptional = weatherRepository.findFirstByRequestedCityNameOrderByUpdatedTimeDesc(city);


        return weatherEntityOptional.map(weather ->{
                    if(weather.getUpdatedTime().isBefore(LocalDateTime.now().minusMinutes(30))){
                        return WeatherDto.convert(getWeatherFromWeatherStack(city));
                    }
                    return WeatherDto.convert(weather);
                })
                .orElseGet(()->WeatherDto.convert(getWeatherFromWeatherStack(city)));
    }

    private WeatherEntity getWeatherFromWeatherStack(String city) {
        ResponseEntity<String>responseEntity=restTemplate.getForEntity(getWeatherStackURL(city) , String.class);

        try {
            WeatherResponse weatherResponse = objectsMapper.readValue(responseEntity.getBody(),WeatherResponse.class);
            return saveWeatherEntity(city ,weatherResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
    private String getWeatherStackURL(String city){
    return Constants.API_URL + Constants.ACCESS_KEY_PARAM + Constants.API_KEY + Constants.QUERRY_KEY_PARAM + city;
}

private  WeatherEntity saveWeatherEntity (String city, WeatherResponse weatherResponse){

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    WeatherEntity weatherEntity = new WeatherEntity(city,
            weatherResponse.location().name(),
            weatherResponse.location().country()
            ,weatherResponse.current().temperature(),
            LocalDateTime.now(),
            LocalDateTime.parse(weatherResponse.location().localTime(),dateTimeFormatter));

    return  weatherRepository.save(weatherEntity);
}
}