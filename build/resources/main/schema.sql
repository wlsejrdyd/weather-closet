-- Weather Closet Database Schema
-- closet.salm.kr

CREATE DATABASE IF NOT EXISTS closet CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE closet;

-- 사용자 테이블
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    height DECIMAL(5,2),  -- cm
    weight DECIMAL(5,2),  -- kg
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    body_type ENUM('SLIM', 'NORMAL', 'ATHLETIC', 'CURVY', 'PLUS') DEFAULT 'NORMAL',
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    last_login_at DATETIME,
    login_fail_count INT DEFAULT 0,
    locked_until DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB;

-- 아바타 테이블
CREATE TABLE avatars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    skin_tone VARCHAR(20) DEFAULT '#F5D0C5',
    hair_style VARCHAR(50) DEFAULT 'default',
    hair_color VARCHAR(20) DEFAULT '#3D2314',
    face_shape VARCHAR(50) DEFAULT 'oval',
    avatar_data JSON,  -- SVG 커스텀 데이터
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 옷 카테고리
CREATE TABLE cloth_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT,
    layer_order INT DEFAULT 0,  -- 레이어링 순서 (속옷=1, 상의=2, 아우터=3 등)
    icon VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES cloth_categories(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 옷 테이블
CREATE TABLE clothes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    brand VARCHAR(100),
    color VARCHAR(50),
    image_path VARCHAR(500),
    product_url VARCHAR(1000),  -- 상품 링크
    temp_min INT,  -- 적정 최저 온도
    temp_max INT,  -- 적정 최고 온도
    weather_tags JSON,  -- ["sunny", "rainy", "snowy", "windy"]
    style_tags JSON,   -- ["casual", "formal", "sporty"]
    is_favorite BOOLEAN DEFAULT FALSE,
    wear_count INT DEFAULT 0,
    last_worn_at DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES cloth_categories(id),
    INDEX idx_user_category (user_id, category_id),
    INDEX idx_temp_range (temp_min, temp_max)
) ENGINE=InnoDB;

-- 코디 세트 테이블
CREATE TABLE outfits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100),
    description TEXT,
    temp_min INT,
    temp_max INT,
    weather_type ENUM('CLEAR', 'CLOUDY', 'RAINY', 'SNOWY', 'WINDY') DEFAULT 'CLEAR',
    occasion VARCHAR(50),  -- daily, work, date, exercise
    is_ai_generated BOOLEAN DEFAULT FALSE,
    rating INT,  -- 사용자 평점 1-5
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_weather (user_id, weather_type),
    INDEX idx_temp (temp_min, temp_max)
) ENGINE=InnoDB;

-- 코디-옷 매핑 테이블
CREATE TABLE outfit_clothes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    outfit_id BIGINT NOT NULL,
    cloth_id BIGINT NOT NULL,
    layer_order INT DEFAULT 0,
    FOREIGN KEY (outfit_id) REFERENCES outfits(id) ON DELETE CASCADE,
    FOREIGN KEY (cloth_id) REFERENCES clothes(id) ON DELETE CASCADE,
    UNIQUE KEY uk_outfit_cloth (outfit_id, cloth_id)
) ENGINE=InnoDB;

-- 날씨 캐시 테이블 (API 호출 최소화)
CREATE TABLE weather_cache (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location_key VARCHAR(100) NOT NULL,  -- "lat_lon" 또는 도시명
    weather_data JSON NOT NULL,
    fetched_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,
    INDEX idx_location (location_key),
    INDEX idx_expires (expires_at)
) ENGINE=InnoDB;

-- 코디 히스토리 (착용 기록)
CREATE TABLE outfit_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    outfit_id BIGINT,
    worn_date DATE NOT NULL,
    weather_temp DECIMAL(4,1),
    weather_condition VARCHAR(50),
    user_feedback ENUM('TOO_HOT', 'HOT', 'PERFECT', 'COLD', 'TOO_COLD'),
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (outfit_id) REFERENCES outfits(id) ON DELETE SET NULL,
    INDEX idx_user_date (user_id, worn_date)
) ENGINE=InnoDB;

-- Refresh Token 테이블 (보안)
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    device_info VARCHAR(255),
    ip_address VARCHAR(45),
    expires_at DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_token (token_hash),
    INDEX idx_expires (expires_at)
) ENGINE=InnoDB;

-- 기본 카테고리 데이터
INSERT INTO cloth_categories (name, parent_id, layer_order, icon) VALUES
('상의', NULL, 2, 'shirt'),
('하의', NULL, 2, 'pants'),
('아우터', NULL, 3, 'jacket'),
('신발', NULL, 1, 'shoe'),
('악세서리', NULL, 4, 'accessory'),
('티셔츠', 1, 2, 'tshirt'),
('셔츠', 1, 2, 'shirt'),
('니트', 1, 2, 'sweater'),
('청바지', 2, 2, 'jeans'),
('슬랙스', 2, 2, 'slacks'),
('반바지', 2, 2, 'shorts'),
('자켓', 3, 3, 'jacket'),
('코트', 3, 3, 'coat'),
('패딩', 3, 3, 'puffer');
