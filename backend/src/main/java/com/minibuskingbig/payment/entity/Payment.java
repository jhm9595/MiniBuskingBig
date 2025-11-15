package com.minibuskingbig.payment.entity;

import com.minibuskingbig.common.entity.BaseTimeEntity;
import com.minibuskingbig.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_user", columnList = "user_id"),
    @Index(name = "idx_order_id", columnList = "order_id", unique = true),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "order_id", nullable = false, unique = true, length = 100)
    private String orderId; // 주문 번호

    @Column(name = "payment_type", length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(name = "payment_method", length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    // 결제 상세 정보
    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_id")
    private Long itemId; // Event ID, Chat Room ID 등

    // PG사 정보
    @Column(name = "pg_provider", length = 50)
    private String pgProvider; // 토스페이먼츠

    @Column(name = "pg_transaction_id", length = 100)
    private String pgTransactionId; // PG사 거래 ID

    @Column(name = "pg_response", columnDefinition = "TEXT")
    private String pgResponse; // PG사 응답 전문

    // 결제 시간
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

    // 비즈니스 로직
    public void complete(String pgTransactionId, String pgResponse) {
        this.status = PaymentStatus.COMPLETED;
        this.pgTransactionId = pgTransactionId;
        this.pgResponse = pgResponse;
        this.paidAt = LocalDateTime.now();
    }

    public void fail(String reason) {
        this.status = PaymentStatus.FAILED;
        this.cancelReason = reason;
    }

    public void cancel(String reason) {
        this.status = PaymentStatus.CANCELLED;
        this.cancelReason = reason;
        this.cancelledAt = LocalDateTime.now();
    }
}
