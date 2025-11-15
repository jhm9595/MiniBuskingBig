package com.minibuskingbig.payment.dto;

import com.minibuskingbig.payment.entity.Payment;
import com.minibuskingbig.payment.entity.PaymentStatus;
import com.minibuskingbig.payment.entity.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long paymentId;
    private String orderId;
    private PaymentType paymentType;
    private Integer amount;
    private PaymentStatus status;
    private String itemName;
    private Long itemId;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
            .paymentId(payment.getPaymentId())
            .orderId(payment.getOrderId())
            .paymentType(payment.getPaymentType())
            .amount(payment.getAmount())
            .status(payment.getStatus())
            .itemName(payment.getItemName())
            .itemId(payment.getItemId())
            .paidAt(payment.getPaidAt())
            .createdAt(payment.getCreatedAt())
            .build();
    }
}
