package com.minibuskingbig.payment.repository;

import com.minibuskingbig.payment.entity.Payment;
import com.minibuskingbig.payment.entity.PaymentStatus;
import com.minibuskingbig.payment.entity.PaymentType;
import com.minibuskingbig.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(String orderId);

    List<Payment> findByUserOrderByCreatedAtDesc(User user);

    List<Payment> findByUserAndPaymentType(User user, PaymentType paymentType);

    Optional<Payment> findByUserAndPaymentTypeAndItemIdAndStatus(
        User user,
        PaymentType paymentType,
        Long itemId,
        PaymentStatus status
    );

    boolean existsByOrderId(String orderId);
}
