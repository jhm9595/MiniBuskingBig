package com.minibuskingbig.dto;

/**
 * Hello 엔드포인트 응답 DTO.
 * Request/Response 분리 규칙 준수.
 */
public class HelloResponse {

    private String message;

    /**
     * 기본 생성자.
     */
    public HelloResponse() {
    }

    /**
     * 메시지를 포함한 생성자.
     *
     * @param message 응답 메시지
     */
    public HelloResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "HelloResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
