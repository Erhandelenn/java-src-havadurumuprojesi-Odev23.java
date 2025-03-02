package com.foldsdev.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Location(
        String name,
        String country,
        String region,
        Double lat,
        Double lon,
        @JsonProperty("timezone_id")
        String timezoneId,
        String localTime,
        @JsonProperty("localtime_epoch")
        String localtimeEpoch,
        @JsonProperty("utc_offset")
        String utcOffset )
 {
}
