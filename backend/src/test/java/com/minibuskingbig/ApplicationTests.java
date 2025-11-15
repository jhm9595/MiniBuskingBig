package com.minibuskingbig;

import com.minibuskingbig.controller.HelloController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 애플리케이션 통합 테스트 클래스.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HelloController helloController;

    /**
     * 애플리케이션 컨텍스트 로드 테스트.
     */
    @Test
    @DisplayName("Spring Boot 애플리케이션이 정상적으로 시작되어야 함")
    void should_startup_normally_when_application_starts() {
        assertThat(helloController).isNotNull();
    }

    /**
     * Hello 엔드포인트 API 테스트.
     */
    @Test
    @DisplayName("GET /api/hello는 200 상태코드와 JSON 응답을 반환해야 함")
    void should_return_hello_message_when_get_hello_endpoint() throws Exception {
        mockMvc.perform(get("/api/hello")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").value("Hello from Spring Boot backend"));
    }
}
