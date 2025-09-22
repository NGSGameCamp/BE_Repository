package com.imfine.ngs.payment.controller;

import com.imfine.ngs.payment.dto.PaymentCompleteRequest;
import com.imfine.ngs.payment.dto.PaymentCompleteResponse;
import com.imfine.ngs.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/complete")
    public ResponseEntity<PaymentCompleteResponse> completePayment(@RequestBody PaymentCompleteRequest request) {
        try {
            PaymentCompleteResponse response = paymentService.completePayment(request.getPaymentId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 서비스 레이어에서 던져진 예외를 클라이언트에게 전달
            return ResponseEntity.badRequest().body(new PaymentCompleteResponse("FAILED", e.getMessage()));
        }
    }
}
