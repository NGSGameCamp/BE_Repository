package com.imfine.ngs.payment.repository;

import com.imfine.ngs.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
