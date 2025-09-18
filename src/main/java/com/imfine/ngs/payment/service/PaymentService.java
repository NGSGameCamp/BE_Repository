package com.imfine.ngs.payment.service;

import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.service.OrderService;
import com.imfine.ngs.payment.dto.PaymentResponse;
import com.imfine.ngs.payment.client.PortOneClient;
import com.imfine.ngs.payment.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderService orderService;
    private PortOneClient portOneClient;

    public void verifyPayment(PaymentRequest request) {
        Order order = orderService.findByOrderId(request.getOrderId());

        if (order.getOrderStatus() == com.imfine.ngs.order.entity.OrderStatus.PAYMENT_COMPLETED) {
            throw new IllegalArgumentException("이미 결제 완료된 주문입니다.");
        }

        PaymentResponse paymentResponse;
        try {
            paymentResponse = portOneClient.getPaymentData(request.getImpUid());
            if(paymentResponse == null) {
                throw new RuntimeException("결제 정보 조회 실패");
            }
        } catch (Exception e) {
            System.err.println("PortOne API 오류: " + e.getMessage());
            throw e;
        }

        long paidAmount = paymentResponse.getAmount();
        long expectedAmount = order.getTotalPrice();

        if (paidAmount == expectedAmount) {
            orderService.updateOrderStatus(request.getOrderId(), com.imfine.ngs.order.entity.OrderStatus.PAYMENT_COMPLETED);
        } else {
            orderService.updateOrderStatus(request.getOrderId(), com.imfine.ngs.order.entity.OrderStatus.PAYMENT_FAILED);
            portOneClient.cancelPayment(paymentResponse.getImpUid(), "결제 금액 위변조 의심");
            throw new RuntimeException("결제 금액이 일치하지 않습니다.");
        }
    }
}
