package kr.salm.closet.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "clothes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cloth {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ClothCategory category;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 100)
    private String brand;
    
    @Column(length = 50)
    private String color;
    
    @Column(name = "image_path", length = 500)
    private String imagePath;
    
    @Column(name = "product_url", length = 1000)
    private String productUrl;
    
    @Column(name = "temp_min")
    private Integer tempMin;
    
    @Column(name = "temp_max")
    private Integer tempMax;
    
    @Column(name = "weather_tags", columnDefinition = "JSON")
    private String weatherTags;  // ["sunny", "rainy", "snowy", "windy"]
    
    @Column(name = "style_tags", columnDefinition = "JSON")
    private String styleTags;    // ["casual", "formal", "sporty"]
    
    @Column(name = "is_favorite")
    @Builder.Default
    private Boolean isFavorite = false;
    
    @Column(name = "wear_count")
    @Builder.Default
    private Integer wearCount = 0;
    
    @Column(name = "last_worn_at")
    private LocalDate lastWornAt;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 착용 기록
    public void recordWear() {
        this.wearCount++;
        this.lastWornAt = LocalDate.now();
    }
}
