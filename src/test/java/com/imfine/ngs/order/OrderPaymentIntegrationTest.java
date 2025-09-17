package com.imfine.ngs.order;

import com.imfine.ngs.order.entity.Game;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderStatus;
import com.imfine.ngs.order.service.OrderService;
import com.imfine.ngs.payment.client.PortOneClient;
import com.imfine.ngs.payment.dto.PaymentRequest;
import com.imfine.ngs.payment.dto.PaymentResponse;
import com.imfine.ngs.payment.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("removal")
@SpringBootTest
@Transactional
public class OrderPaymentIntegrationTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;

    @MockBean
    private PortOneClient portOneClient;


    @Test
    @DisplayName("주문 생성 후 결제까지 성공적으로 완료된다.")
    void testSuccessfulOrderAndPayment() {
        // Given
        String userId = "user1";
        List<Game> games = Arrays.asList(new Game("Game A", 10000), new Game("Game B", 20000));
        Order createdOrder = orderService.createOrder(userId, games);

        String impUid = "imp_success_123";
        long paidAmount = createdOrder.getTotalPrice();

        given(portOneClient.getPaymentData(impUid))
                .willReturn(new PaymentResponse(impUid, paidAmount));

        // When
        PaymentRequest request = new PaymentRequest(createdOrder.getOrderId(), impUid);
        paymentService.verifyPayment(request);

        // Then
        Order updatedOrder = orderService.findByOrderId(createdOrder.getOrderId());
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }

    @Test
    @DisplayName("결제 금액이 불일치하면 예외가 발생하고 주문 상태는 PAYMENT_FAILED가 된다.")
    void testPaymentAmountMismatch() {
        // Given
        String userId = "user1";
        List<Game> games = Arrays.asList(new Game("Game C", 15000));
        Order createdOrder = orderService.createOrder(userId, games);

        String impUid = "imp_mismatch_456";
        long paidAmount = createdOrder.getTotalPrice() - 1000; // Mismatched amount

        given(portOneClient.getPaymentData(impUid))
                .willReturn(new PaymentResponse(impUid, paidAmount));

        // When
        PaymentRequest request = new PaymentRequest(createdOrder.getOrderId(), impUid);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.verifyPayment(request));

        // Then
        assertThat(exception.getMessage()).contains("결제 금액이 일치하지 않습니다.");
        Order updatedOrder = orderService.findByOrderId(createdOrder.getOrderId());
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
        verify(portOneClient).cancelPayment(impUid, "결제 금액 위변조 의심");
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID로 결제 시도 시 예외가 발생한다.")
    void testPaymentForNonExistentOrder() {
        // Given
        long nonExistentOrderId = 9999L;
        String impUid = "imp_nonexistent_789";

        // When
        PaymentRequest request = new PaymentRequest(nonExistentOrderId, impUid);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> paymentService.verifyPayment(request));

        // Then
        assertThat(exception.getMessage()).contains("주문을 찾을 수 없습니다:");
    }

    @Test
    @DisplayName("이미 결제 완료된 주문을 다시 결제 시도 시 예외가 발생한다.")
    void testPaymentForAlreadyCompletedOrder() {
        // Given
        String userId = "user1";
        List<Game> games = Arrays.asList(new Game("Game D", 25000));
        Order createdOrder = orderService.createOrder(userId, games);
        orderService.updateOrderStatus(createdOrder.getOrderId(), OrderStatus.PAYMENT_COMPLETED);

        String impUid = "imp_completed_101";
        long paidAmount = createdOrder.getTotalPrice();

        // When
        PaymentRequest request = new PaymentRequest(createdOrder.getOrderId(), impUid);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> paymentService.verifyPayment(request));

        // Then
        assertThat(exception.getMessage()).contains("이미 결제 완료된 주문입니다.");
    }
}
