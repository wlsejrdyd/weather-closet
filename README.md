# Weather Closet 👕

날씨 기반 의류 코디 추천 서비스

**도메인**: closet.salm.kr

## 기술 스택

- **Backend**: Spring Boot 3.2.5, Java 17
- **Database**: MariaDB 10.5+
- **Template**: Thymeleaf
- **Security**: Spring Security 6 (Session + JWT)
- **Weather API**: Open-Meteo (무료, 키 불필요)

## 시작하기

### 1. DB 설정

```bash
mysql -u root -p < src/main/resources/schema.sql
```

### 2. 환경변수 설정

```bash
export DB_USERNAME=closet
export DB_PASSWORD=your_password
export JWT_SECRET=your-256-bit-secret-key-min-32-characters
# WEATHER_API_KEY 필요 없음! (Open-Meteo 무료 API 사용)
```

### 3. 실행

```bash
./gradlew bootRun
```

### 4. 접속

- Web: http://localhost:8081
- API: http://localhost:8081/api

## API 엔드포인트

### 인증 (JWT)
```
POST /api/auth/login     # 로그인 → JWT 발급
POST /api/auth/register  # 회원가입
POST /api/auth/refresh   # 토큰 갱신
```

### 날씨
```
GET /api/weather?lat=37.5&lon=127.0   # 좌표 기반
GET /api/weather/city?name=Seoul       # 도시명 기반
```

### 옷장 (인증 필요)
```
GET    /api/clothes          # 내 옷 목록
POST   /api/clothes          # 옷 등록
PUT    /api/clothes/{id}     # 옷 수정
DELETE /api/clothes/{id}     # 옷 삭제
```

### 코디 (인증 필요)
```
GET  /api/outfits              # 내 코디 목록
GET  /api/outfits/recommend    # AI 코디 추천
POST /api/outfits              # 코디 저장
```

## 프로젝트 구조

```
weather-closet/
├── src/main/java/kr/salm/closet/
│   ├── config/           # Security, JWT, Cache
│   ├── controller/
│   │   ├── api/          # REST API (모바일용)
│   │   └── web/          # Thymeleaf (웹용)
│   ├── service/          # 비즈니스 로직
│   ├── repository/       # JPA Repository
│   ├── domain/           # Entity
│   └── dto/              # Request/Response DTO
├── src/main/resources/
│   ├── templates/        # Thymeleaf 템플릿
│   ├── static/           # CSS, JS, Images
│   └── application.yml   # 설정
└── build.gradle
```

## 보안 기능

- ✅ BCrypt 비밀번호 해싱 (강도 12)
- ✅ JWT 기반 API 인증
- ✅ CSRF 보호 (웹)
- ✅ XSS 방지 헤더
- ✅ CSP (Content Security Policy)
- ✅ 로그인 실패 5회 시 30분 잠금
- ✅ Secure Cookie 설정
- ✅ CORS 화이트리스트

## salm.kr 연동

현재 독립 운영, 추후 통합 예정:
- 동일 스택 (Spring Boot 3.x)
- SSO 연동 가능
- DB 분리 유지 권장

## TODO

- [ ] 회원가입 구현
- [ ] 옷 CRUD API
- [ ] 아바타 SVG 생성기
- [ ] AI 코디 추천 알고리즘
- [ ] 모바일 앱 API 완성
- [ ] 이미지 업로드 (S3 or 로컬)
- [ ] OAuth 연동 (Google, Kakao)

## 라이선스

MIT License

---

Part of [SALM Project](https://github.com/wlsejrdyd/salm)
