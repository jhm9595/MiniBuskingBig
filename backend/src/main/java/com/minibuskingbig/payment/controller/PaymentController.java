package com.minibuskingbig.payment.controller;

import com.minibuskingbig.common.dto.ApiResponse;
import com.minibuskingbig.payment.dto.PaymentResponse;
import com.minibuskingbig.payment.entity.Payment;
import com.minibuskingbig.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/chat")
    public ApiResponse<PaymentResponse> createChatPayment(
        @AuthenticationPrincipal Long userId,
        @RequestParam Long eventId
    ) {
        Payment payment = paymentService.createChatPayment(userId, eventId);
        return ApiResponse.success(PaymentResponse.from(payment), "결제가 생성되었습니다.");
    }

    @PostMapping("/vip")
    public ApiResponse<PaymentResponse> createVipSubscriptionPayment(
        @AuthenticationPrincipal Long userId
    ) {
        Payment payment = paymentService.createVipSubscriptionPayment(userId);
        return ApiResponse.success(PaymentResponse.from(payment), "VIP 구독 결제가 생성되었습니다.");
    }

    @PostMapping("/ad-free")
    public ApiResponse<PaymentResponse> createAdFreePayment(
        @AuthenticationPrincipal Long userId
    ) {
        Payment payment = paymentService.createAdFreePayment(userId);
        return ApiResponse.success(PaymentResponse.from(payment), "광고 제거 결제가 생성되었습니다.");
    }

    @PostMapping("/complete")
    public ApiResponse<Void> completePayment(@RequestBody PaymentCompleteRequest request) {
        paymentService.completePayment(
            request.orderId(),
            request.pgTransactionId(),
            request.pgResponse()
        );
        return ApiResponse.success(null, "결제가 완료되었습니다.");
    }

    @PostMapping("/cancel")
    public ApiResponse<Void> cancelPayment(@RequestBody PaymentCancelRequest request) {
        paymentService.cancelPayment(request.orderId(), request.reason());
        return ApiResponse.success(null, "결제가 취소되었습니다.");
    }

    @GetMapping("/{orderId}")
    public ApiResponse<PaymentResponse> getPayment(@PathVariable String orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ApiResponse.success(PaymentResponse.from(payment));
    }

    record PaymentCompleteRequest(String orderId, String pgTransactionId, String pgResponse) {}
    record PaymentCancelRequest(String orderId, String reason) {}
}
