package com.minibuskingbig.payment.service;

import com.minibuskingbig.chat.service.EcsContainerService;
import com.minibuskingbig.common.exception.BusinessException;
import com.minibuskingbig.common.exception.ErrorCode;
import com.minibuskingbig.event.entity.Event;
import com.minibuskingbig.event.repository.EventRepository;
import com.minibuskingbig.payment.entity.Payment;
import com.minibuskingbig.payment.entity.PaymentStatus;
import com.minibuskingbig.payment.entity.PaymentType;
import com.minibuskingbig.payment.repository.PaymentRepository;
import com.minibuskingbig.user.entity.User;
import com.minibuskingbig.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final EventRepository eventRepository;
    private final EcsContainerService ecsContainerService;

    /**
     * 채팅 결제 생성
     */
    @Transactional
    public Payment createChatPayment(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));

        if (!event.getChatEnabled()) {
            throw new BusinessException(ErrorCode.CHAT_NOT_ENABLED);
        }

        // 이미 결제했는지 확인
        boolean alreadyPaid = paymentRepository.existsByOrderId(
            generateOrderId(userId, eventId, PaymentType.CHAT)
        );

        if (alreadyPaid) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 결제되었습니다.");
        }

        // 컨테이너 비용 계산
        // 실제로는 ECS 서비스를 통해 계산하지만, 여기서는 기본 1,000원
        int amount = 1000; // 시간당 기본 요금

        String orderId = generateOrderId(userId, eventId, PaymentType.CHAT);

        Payment payment = Payment.builder()
            .user(user)
            .orderId(orderId)
            .paymentType(PaymentType.CHAT)
            .amount(amount)
            .itemName(event.getTitle() + " - 채팅 이용권")
            .itemId(eventId)
            .pgProvider("toss")
            .build();

        Payment savedPayment = paymentRepository.save(payment);

        log.info("Chat payment created: {} for event: {} by user: {}", savedPayment.getPaymentId(), eventId, userId);
        return savedPayment;
    }

    /**
     * VIP 구독 결제 생성
     */
    @Transactional
    public Payment createVipSubscriptionPayment(Long userId) {
        User user = userService.getUserById(userId);

        String orderId = generateOrderId(userId, null, PaymentType.VIP_SUBSCRIPTION);

        Payment payment = Payment.builder()
            .user(user)
            .orderId(orderId)
            .paymentType(PaymentType.VIP_SUBSCRIPTION)
            .amount(9900) // 월 9,900원
            .itemName("VIP 구독 (월)")
            .pgProvider("toss")
            .build();

        Payment savedPayment = paymentRepository.save(payment);

        log.info("VIP subscription payment created: {} for user: {}", savedPayment.getPaymentId(), userId);
        return savedPayment;
    }

    /**
     * 광고 제거 결제 생성
     */
    @Transactional
    public Payment createAdFreePayment(Long userId) {
        User user = userService.getUserById(userId);

        String orderId = generateOrderId(userId, null, PaymentType.AD_FREE);

        Payment payment = Payment.builder()
            .user(user)
            .orderId(orderId)
            .paymentType(PaymentType.AD_FREE)
            .amount(4900) // 4,900원
            .itemName("광고 제거 (평생)")
            .pgProvider("toss")
            .build();

        Payment savedPayment = paymentRepository.save(payment);

        log.info("Ad-free payment created: {} for user: {}", savedPayment.getPaymentId(), userId);
        return savedPayment;
    }

    /**
     * 결제 완료 처리
     */
    @Transactional
    public void completePayment(String orderId, String pgTransactionId, String pgResponse) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));

        payment.complete(pgTransactionId, pgResponse);

        // 결제 타입별 후처리
        switch (payment.getPaymentType()) {
            case CHAT -> handleChatPaymentComplete(payment);
            case VIP_SUBSCRIPTION -> handleVipSubscriptionComplete(payment);
            case AD_FREE -> handleAdFreeComplete(payment);
        }

        log.info("Payment completed: {} - order: {}", payment.getPaymentId(), orderId);
    }

    /**
     * 결제 취소
     */
    @Transactional
    public void cancelPayment(String orderId, String reason) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "완료된 결제만 취소할 수 있습니다.");
        }

        payment.cancel(reason);

        log.info("Payment cancelled: {} - reason: {}", payment.getPaymentId(), reason);
    }

    private void handleChatPaymentComplete(Payment payment) {
        // Event의 chatPaymentStatus 업데이트
        Event event = eventRepository.findById(payment.getItemId())
            .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));

        event.completeChatPayment();

        log.info("Chat payment completed for event: {}", payment.getItemId());
    }

    private void handleVipSubscriptionComplete(Payment payment) {
        // User를 VIP로 업그레이드
        userService.upgradeToVip(payment.getUser().getUserId());

        log.info("VIP subscription activated for user: {}", payment.getUser().getUserId());
    }

    private void handleAdFreeComplete(Payment payment) {
        // User의 광고 제거 활성화
        userService.purchaseAdFree(payment.getUser().getUserId());

        log.info("Ad-free activated for user: {}", payment.getUser().getUserId());
    }

    private String generateOrderId(Long userId, Long itemId, PaymentType paymentType) {
        String prefix = switch (paymentType) {
            case CHAT -> "CHAT";
            case VIP_SUBSCRIPTION -> "VIP";
            case AD_FREE -> "ADFREE";
        };

        return String.format("%s_%d_%s_%s",
            prefix,
            userId,
            itemId != null ? itemId : "0",
            UUID.randomUUID().toString().substring(0, 8)
        );
    }

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));
    }
}
