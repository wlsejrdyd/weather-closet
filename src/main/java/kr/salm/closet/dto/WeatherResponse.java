package kr.salm.closet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    
    private boolean success;
    private String errorMessage;
    
    private int temperature;      // 현재 온도 (°C)
    private int feelsLike;        // 체감 온도
    private int humidity;         // 습도 (%)
    private double windSpeed;     // 풍속 (km/h)
    private String weatherMain;   // Clear, Clouds, Rain, Snow 등
    private String weatherType;   // CLEAR, CLOUDY, RAINY, SNOWY (내부용)
    private String description;   // 한글 상세 설명
    private String icon;          // 이모지 아이콘
    private String cityName;      // 도시명
    
    public static WeatherResponse error(String message) {
        return WeatherResponse.builder()
                .success(false)
                .errorMessage(message)
                .build();
    }
    
    /**
     * 온도 기반 옷차림 추천 등급 반환
     */
    public String getTemperatureLevel() {
        if (temperature >= 28) return "VERY_HOT";      // 민소매, 반팔, 반바지
        if (temperature >= 23) return "HOT";           // 반팔, 얇은 셔츠
        if (temperature >= 20) return "WARM";          // 얇은 가디건, 긴팔
        if (temperature >= 17) return "MILD";          // 얇은 니트, 가디건
        if (temperature >= 12) return "COOL";          // 자켓, 가디건, 야상
        if (temperature >= 9) return "CHILLY";         // 트렌치코트, 야상
        if (temperature >= 5) return "COLD";           // 코트, 가죽자켓
        return "VERY_COLD";                            // 패딩, 두꺼운 코트
    }
}
