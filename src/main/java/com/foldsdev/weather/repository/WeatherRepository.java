package com.foldsdev.weather.repository;

import com.foldsdev.weather.model.WeatherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherRepository extends JpaRepository< WeatherEntity , String > {

Optional<WeatherEntity>findFirstByRequestedCityNameOrderByUpdatedTimeDesc(String city);
}
