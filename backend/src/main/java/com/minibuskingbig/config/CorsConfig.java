package com.minibuskingbig.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 설정 클래스.
 * 보안 규칙 준수 (최소한의 허용 범위).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final String API_PATH_PATTERN = "/api/**";
    private static final String[] ALLOWED_ORIGINS = {"http://localhost:5173", "http://localhost:3000"};
    private static final String[] ALLOWED_METHODS = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};
    private static final long MAX_AGE = 3600L;

    /**
     * CORS 매핑 설정.
     * 개발 환경: localhost:5173, 3000
     * 프로덕션 환경: 환경변수로 동적 설정 권장
     *
     * @param registry CorsRegistry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(API_PATH_PATTERN)
                .allowedOrigins(ALLOWED_ORIGINS)
                .allowedMethods(ALLOWED_METHODS)
                .allowCredentials(true)
                .maxAge(MAX_AGE);
    }
}
