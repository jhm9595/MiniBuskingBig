package com.minibuskingbig.controller;

import com.minibuskingbig.dto.HelloResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello 엔드포인트 컨트롤러.
 * RESTful API 규칙 준수 (경로명확성, 상태코드 명확화).
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class HelloController {

    /**
     * Hello 메시지를 반환하는 엔드포인트.
     *
     * @return HelloResponse DTO
     */
    @GetMapping("/hello")
    public ResponseEntity<HelloResponse> getHello() {
        log.info("GET /api/hello 요청 수신");

        String message = "Hello from Spring Boot backend";
        HelloResponse response = new HelloResponse(message);

        log.debug("응답 생성: {}", response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
