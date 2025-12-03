package kr.salm.closet.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "outfits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Outfit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "temp_min")
    private Integer tempMin;
    
    @Column(name = "temp_max")
    private Integer tempMax;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "weather_type")
    @Builder.Default
    private WeatherType weatherType = WeatherType.CLEAR;
    
    @Column(length = 50)
    private String occasion;
    
    @Column(name = "is_ai_generated")
    @Builder.Default
    private Boolean isAiGenerated = false;
    
    private Integer rating;
    
    @OneToMany(mappedBy = "outfit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OutfitCloth> outfitClothes = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum WeatherType {
        CLEAR, CLOUDY, RAINY, SNOWY, WINDY
    }
    
    // 옷 추가 헬퍼 메서드
    public void addCloth(Cloth cloth, int layerOrder) {
        OutfitCloth outfitCloth = OutfitCloth.builder()
                .outfit(this)
                .cloth(cloth)
                .layerOrder(layerOrder)
                .build();
        this.outfitClothes.add(outfitCloth);
    }
}
