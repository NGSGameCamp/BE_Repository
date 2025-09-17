package com.imfine.ngs.payment;

import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.service.OrderService;
import com.imfine.ngs.payment.client.PortOneClient;
import com.imfine.ngs.payment.dto.PaymentRequest;
import com.imfine.ngs.payment.dto.PaymentResponse;
import com.imfine.ngs.payment.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private OrderService orderService;

    @Mock
    private PortOneClient portOneClient;

    @Test
    @DisplayName("실제 결제 금액과 주문 금액이 일치하면, 주문 상태를 COMPLETED로 변경한다.")
    void testPaymentSuccess() {
        //given
        long orderId = 12345L;
        String impUid = "imp-456";
        long correctAmount = 50000L;

        given(orderService.findByOrderId(orderId))
                .willReturn(new Order(orderId, correctAmount));
        given(portOneClient.getPaymentData(impUid))
                .willReturn(new PaymentResponse(impUid, correctAmount));

        //when
        PaymentRequest request = new PaymentRequest(orderId, impUid);
        paymentService.verifyPayment(request);

        //then
        verify(orderService).updateOrderStatus(orderId, com.imfine.ngs.order.entity.OrderStatus.PAYMENT_COMPLETED);
    }

    @Test
    @DisplayName("실제 결제 금액과 주문 금액이 다르면, 예외가 발생하고 결제 취소를 요청한다")
    void testPaymentFailed() {
        // given
        long orderId = 12345L;
        String impUid = "imp-456";
        long orderAmount = 50000L; // 주문 금액
        long paidAmount = 40000L;  // 실제 결제된 금액 (위변조)

        given(orderService.findByOrderId(orderId))
                .willReturn(new Order(orderId, orderAmount));
        given(portOneClient.getPaymentData(impUid))
                .willReturn(new PaymentResponse(impUid, paidAmount));

        // when & then
        PaymentRequest request = new PaymentRequest(orderId, impUid);
        assertThrows(RuntimeException.class, () -> {
            paymentService.verifyPayment(request);
        });

        verify(portOneClient).cancelPayment(impUid, "결제 금액 위변조 의심");
        verify(orderService).updateOrderStatus(orderId, com.imfine.ngs.order.entity.OrderStatus.PAYMENT_FAILED);
    }

    @Test
    @DisplayName("이미 결제 완료된 주문을 다시 결제 시도하면 예외가 발생한다.")
    void testPaymentAlreadyCompleted() {
        //given
        long orderId = 12345L;
        String impUid = "imp-456";
        long correctAmount = 50000L;

        Order completedOrder = new Order(orderId, correctAmount);
        completedOrder.setOrderStatus(com.imfine.ngs.order.entity.OrderStatus.PAYMENT_COMPLETED);

        given(orderService.findByOrderId(orderId))
                .willReturn(completedOrder);

        //when & then
        PaymentRequest request = new PaymentRequest(orderId, impUid);
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.verifyPayment(request);
        });
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID로 결제 시도하면 예외가 발생하고 결제 조회는 호출되지 않는다")
    void testPaymentOrderNotFound() {
        // given
        long orderId = 99999L;
        String impUid = "imp-999";

        given(orderService.findByOrderId(orderId))
                .willThrow(new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));
        

        // when & then
        PaymentRequest request = new PaymentRequest(orderId, impUid);
        assertThrows(IllegalArgumentException.class, () -> paymentService.verifyPayment(request));

        // 결제 서비스(외부) 호출되지 않아야 함
        verify(portOneClient, never()).getPaymentData(impUid);
    }

    @Test
    @DisplayName("결제 정보 조회 실패 시 예외가 발생하고 주문 상태는 변경되지 않는다")
    void testPaymentDataNotFound() {
        // given
        long orderId = 12345L;
        String impUid = "imp-404";
        long orderAmount = 50000L;

        Order order = new Order(orderId, orderAmount);
        given(orderService.findByOrderId(orderId))
                .willReturn(order);
        given(portOneClient.getPaymentData(impUid))
                .willReturn(null); // 결제 데이터 조회 실패

        // when & then
        PaymentRequest request = new PaymentRequest(orderId, impUid);
        assertThrows(RuntimeException.class, () -> paymentService.verifyPayment(request));

        // 외부 취소 호출도, 상태 변경도 없어야 함
        verify(portOneClient, never()).cancelPayment(impUid, "결제 금액 위변조 의심");
        verify(orderService, never()).updateOrderStatus(orderId, com.imfine.ngs.order.entity.OrderStatus.PAYMENT_FAILED);
        verify(orderService, never()).updateOrderStatus(orderId, com.imfine.ngs.order.entity.OrderStatus.PAYMENT_COMPLETED);
    }

    @Test
    @DisplayName("PortOne API 네트워크 오류 발생 시, 주문 상태는 변경되지 않고 예외가 발생한다")
    void testNetworkErrorDuringPayment() {
        // given
        long orderId = 12345L;
        String impUid = "imp-999";
        long orderAmount = 50000L;

        Order pendingOrder = new Order(orderId, orderAmount);
        pendingOrder.setOrderStatus(com.imfine.ngs.order.entity.OrderStatus.PENDING);

        given(orderService.findByOrderId(orderId))
                .willReturn(pendingOrder);
        given(portOneClient.getPaymentData(impUid))
                .willThrow(new RuntimeException("API 연결 실패"));

        // when & then
        PaymentRequest request = new PaymentRequest(orderId, impUid);
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentService.verifyPayment(request));

        assertThat(exception.getMessage()).isEqualTo("API 연결 실패");

        // 주문 상태는 그대로 PENDING
        assertThat(pendingOrder.getOrderStatus()).isEqualTo(com.imfine.ngs.order.entity.OrderStatus.PENDING);
    }
}