package kr.salm.closet.controller.web;

import kr.salm.closet.dto.WeatherResponse;
import kr.salm.closet.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MainController {
    
    private final WeatherService weatherService;
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "37.5665") double lat,
            @RequestParam(defaultValue = "126.9780") double lon,
            Model model) {
        
        // 기본값: 서울
        WeatherResponse weather = weatherService.getCurrentWeather(lat, lon);
        model.addAttribute("weather", weather);
        model.addAttribute("userEmail", user.getUsername());
        
        return "dashboard";
    }
}
