package kr.salm.closet.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(nullable = false, length = 50)
    private String nickname;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal height;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal weight;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "body_type")
    @Builder.Default
    private BodyType bodyType = BodyType.NORMAL;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    @Column(name = "login_fail_count")
    @Builder.Default
    private Integer loginFailCount = 0;
    
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Avatar avatar;
    
    public enum Gender {
        MALE, FEMALE, OTHER
    }
    
    public enum BodyType {
        SLIM, NORMAL, ATHLETIC, CURVY, PLUS
    }
    
    public enum Role {
        USER, ADMIN
    }
    
    // 계정 잠금 확인
    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }
    
    // 로그인 실패 처리
    public void incrementLoginFailCount() {
        this.loginFailCount++;
        if (this.loginFailCount >= 5) {
            this.lockedUntil = LocalDateTime.now().plusMinutes(30);
        }
    }
    
    // 로그인 성공 처리
    public void resetLoginFailCount() {
        this.loginFailCount = 0;
        this.lockedUntil = null;
        this.lastLoginAt = LocalDateTime.now();
    }
}
