package kr.salm.closet.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "avatars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avatar {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "skin_tone", length = 20)
    @Builder.Default
    private String skinTone = "#F5D0C5";
    
    @Column(name = "hair_style", length = 50)
    @Builder.Default
    private String hairStyle = "default";
    
    @Column(name = "hair_color", length = 20)
    @Builder.Default
    private String hairColor = "#3D2314";
    
    @Column(name = "face_shape", length = 50)
    @Builder.Default
    private String faceShape = "oval";
    
    @Column(name = "avatar_data", columnDefinition = "JSON")
    private String avatarData;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
