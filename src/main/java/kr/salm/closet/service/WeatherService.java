package kr.salm.closet.service;

import kr.salm.closet.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${weather.api.base-url}")
    private String baseUrl;
    
    /**
     * 좌표 기반 현재 날씨 조회 (Open-Meteo API - 무료, 키 불필요)
     */
    @Cacheable(value = "weather", key = "#lat + '_' + #lon", unless = "#result == null")
    public WeatherResponse getCurrentWeather(double lat, double lon) {
        log.info("Fetching weather for lat={}, lon={}", lat, lon);
        
        try {
            WebClient client = webClientBuilder.baseUrl(baseUrl).build();
            
            return client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/forecast")
                            .queryParam("latitude", lat)
                            .queryParam("longitude", lon)
                            .queryParam("current_weather", true)
                            .queryParam("timezone", "Asia/Seoul")
                            .build())
                    .retrieve()
                    .bodyToMono(OpenMeteoResponse.class)
                    .map(this::convertToWeatherResponse)
                    .timeout(Duration.ofSeconds(10))
                    .onErrorResume(e -> {
                        log.error("Weather API error: {}", e.getMessage());
                        return Mono.just(WeatherResponse.error("날씨 정보를 가져올 수 없습니다."));
                    })
                    .block();
        } catch (Exception e) {
            log.error("Weather service error", e);
            return WeatherResponse.error("날씨 서비스 오류");
        }
    }
    
    /**
     * 도시명 기반 현재 날씨 조회
     * Open-Meteo Geocoding API로 좌표 변환 후 조회
     */
    @Cacheable(value = "weather", key = "#cityName", unless = "#result == null")
    public WeatherResponse getCurrentWeatherByCity(String cityName) {
        log.info("Fetching weather for city={}", cityName);
        
        try {
            WebClient client = webClientBuilder.build();
            
            // 1. 도시명 → 좌표 변환 (Open-Meteo Geocoding)
            GeocodingResponse geo = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("geocoding-api.open-meteo.com")
                            .path("/v1/search")
                            .queryParam("name", cityName)
                            .queryParam("count", 1)
                            .queryParam("language", "ko")
                            .build())
                    .retrieve()
                    .bodyToMono(GeocodingResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            
            if (geo == null || geo.results == null || geo.results.isEmpty()) {
                return WeatherResponse.error("도시를 찾을 수 없습니다: " + cityName);
            }
            
            GeoResult location = geo.results.get(0);
            
            // 2. 좌표로 날씨 조회
            WeatherResponse weather = getCurrentWeather(location.latitude, location.longitude);
            weather.setCityName(location.name);
            return weather;
            
        } catch (Exception e) {
            log.error("Weather service error", e);
            return WeatherResponse.error("날씨 서비스 오류");
        }
    }
    
    private WeatherResponse convertToWeatherResponse(OpenMeteoResponse api) {
        if (api == null || api.current_weather == null) {
            return WeatherResponse.error("Invalid API response");
        }
        
        CurrentWeather cw = api.current_weather;
        String weatherMain = mapWeatherCode(cw.weathercode);
        String weatherType = mapWeatherType(cw.weathercode);
        String description = getWeatherDescription(cw.weathercode);
        String icon = getWeatherIcon(cw.weathercode, cw.is_day == 1);
        
        return WeatherResponse.builder()
                .success(true)
                .temperature((int) Math.round(cw.temperature))
                .feelsLike((int) Math.round(cw.temperature))
                .humidity(0)
                .windSpeed(cw.windspeed)
                .weatherMain(weatherMain)
                .weatherType(weatherType)
                .description(description)
                .icon(icon)
                .cityName("현재 위치")
                .build();
    }
    
    // WMO Weather Code → 날씨 상태
    private String mapWeatherCode(int code) {
        return switch (code) {
            case 0 -> "Clear";
            case 1, 2, 3 -> "Clouds";
            case 45, 48 -> "Fog";
            case 51, 53, 55, 56, 57 -> "Drizzle";
            case 61, 63, 65, 66, 67 -> "Rain";
            case 71, 73, 75, 77 -> "Snow";
            case 80, 81, 82 -> "Rain";
            case 85, 86 -> "Snow";
            case 95, 96, 99 -> "Thunderstorm";
            default -> "Clear";
        };
    }
    
    private String mapWeatherType(int code) {
        return switch (code) {
            case 0, 1 -> "CLEAR";
            case 2, 3, 45, 48 -> "CLOUDY";
            case 51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 95, 96, 99 -> "RAINY";
            case 71, 73, 75, 77, 85, 86 -> "SNOWY";
            default -> "CLEAR";
        };
    }
    
    private String getWeatherDescription(int code) {
        return switch (code) {
            case 0 -> "맑음";
            case 1 -> "대체로 맑음";
            case 2 -> "부분적으로 흐림";
            case 3 -> "흐림";
            case 45 -> "안개";
            case 48 -> "짙은 안개";
            case 51 -> "가벼운 이슬비";
            case 53 -> "이슬비";
            case 55 -> "강한 이슬비";
            case 61 -> "약한 비";
            case 63 -> "비";
            case 65 -> "강한 비";
            case 71 -> "약한 눈";
            case 73 -> "눈";
            case 75 -> "강한 눈";
            case 80 -> "약한 소나기";
            case 81 -> "소나기";
            case 82 -> "강한 소나기";
            case 95 -> "뇌우";
            case 96, 99 -> "우박을 동반한 뇌우";
            default -> "맑음";
        };
    }
    
    private String getWeatherIcon(int code, boolean isDay) {
        return switch (code) {
            case 0 -> isDay ? "☀️" : "🌙";
            case 1, 2 -> isDay ? "🌤️" : "☁️";
            case 3 -> "☁️";
            case 45, 48 -> "🌫️";
            case 51, 53, 55, 61, 63, 65, 80, 81, 82 -> "🌧️";
            case 56, 57, 66, 67 -> "🌨️";
            case 71, 73, 75, 77, 85, 86 -> "❄️";
            case 95, 96, 99 -> "⛈️";
            default -> isDay ? "☀️" : "🌙";
        };
    }
    
    // Open-Meteo API 응답 DTO
    private record OpenMeteoResponse(
            CurrentWeather current_weather
    ) {}
    
    private record CurrentWeather(
            double temperature,
            double windspeed,
            int weathercode,
            int is_day
    ) {}
    
    // Geocoding API 응답 DTO
    private record GeocodingResponse(
            java.util.List<GeoResult> results
    ) {}
    
    private record GeoResult(
            String name,
            double latitude,
            double longitude
    ) {}
}
