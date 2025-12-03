package kr.salm.closet.controller.api;

import kr.salm.closet.dto.WeatherResponse;
import kr.salm.closet.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherApiController {
    
    private final WeatherService weatherService;
    
    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(
            @RequestParam double lat,
            @RequestParam double lon) {
        
        WeatherResponse weather = weatherService.getCurrentWeather(lat, lon);
        
        if (!weather.isSuccess()) {
            return ResponseEntity.badRequest().body(weather);
        }
        return ResponseEntity.ok(weather);
    }
    
    @GetMapping("/city")
    public ResponseEntity<WeatherResponse> getWeatherByCity(
            @RequestParam String name) {
        
        WeatherResponse weather = weatherService.getCurrentWeatherByCity(name);
        
        if (!weather.isSuccess()) {
            return ResponseEntity.badRequest().body(weather);
        }
        return ResponseEntity.ok(weather);
    }
}
