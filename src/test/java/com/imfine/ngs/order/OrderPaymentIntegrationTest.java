package com.imfine.ngs.order;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderStatus;
import com.imfine.ngs.order.repository.OrderRepository;
import com.imfine.ngs.order.service.OrderService;
import com.imfine.ngs.payment.client.PortOneApiClient;
import com.imfine.ngs.payment.client.PortOneAmount;
import com.imfine.ngs.payment.client.PortOnePaymentData;
import com.imfine.ngs.payment.dto.PaymentCompleteRequest;
import com.imfine.ngs.payment.dto.PaymentCompleteResponse;
import com.imfine.ngs.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("removal")
@SpringBootTest
@Transactional
public class OrderPaymentIntegrationTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private GameRepository gameRepository;

    @MockitoBean
    private PortOneApiClient portOneApiClient;

    private Order testOrder;
    private String testPaymentId;
    private long testAmount;
    private Long testUserId;
    private Game gameA;
    private Game gameB;

    @BeforeEach
    void setUp() {
        testPaymentId = "PAY-integration-test-123";
        testAmount = 30000L; // Game A(10000) + Game B(20000)
        testUserId = 1L;

        // 테스트용 게임 저장
        gameA = gameRepository.save(Game.builder().name("Game A").price(10000L).isActive(true).build());
        gameB = gameRepository.save(Game.builder().name("Game B").price(20000L).isActive(true).build());

        // 테스트용 주문 생성 (장바구니 사용)
        testOrder = orderService.getOrCreateCart(testUserId);
        orderService.addGameToCart(testUserId, gameA.getId());
        orderService.addGameToCart(testUserId, gameB.getId());

        // merchantUid 설정 (PortOne 연동을 위해 필요)
        testOrder = orderRepository.findById(testOrder.getOrderId()).orElseThrow(); // 최신 상태 반영
        testOrder.setMerchantUid(testPaymentId); 
        orderRepository.save(testOrder);
    }

    private PortOnePaymentData createPortOnePaymentData(String paymentId, long amount, String status) {
        PortOneAmount amountObject = new PortOneAmount(amount, 0, 0, amount, 0, amount, 0, 0);
        return new PortOnePaymentData(paymentId, status, paymentId, "테스트 상품", amountObject, null, null, null, null);
    }

    @Test
    @DisplayName("주문 생성 후 결제까지 성공적으로 완료된다.")
    void testSuccessfulOrderAndPayment() {
        // Given
        given(portOneApiClient.getPayment(testPaymentId))
                .willReturn(createPortOnePaymentData(testPaymentId, testAmount, "PAID"));

        // When
        PaymentCompleteRequest request = new PaymentCompleteRequest(testPaymentId);
        paymentService.completePayment(request.getPaymentId());

        // Then
        Order updatedOrder = orderRepository.findByMerchantUid(testPaymentId).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }

    @Test
    @DisplayName("결제 금액이 불일치하면 예외가 발생하고 주문 상태는 PAYMENT_FAILED가 된다.")
    void testPaymentAmountMismatch() {
        // Given
        long paidAmount = testAmount - 1000L; // Mismatched amount

        given(portOneApiClient.getPayment(testPaymentId))
                .willReturn(createPortOnePaymentData(testPaymentId, paidAmount, "PAID"));

        // When
        PaymentCompleteRequest request = new PaymentCompleteRequest(testPaymentId);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.completePayment(request.getPaymentId()));

        // Then
        assertThat(exception.getMessage()).contains("결제 금액(" + paidAmount + ")이 주문 금액(" + testAmount + ")과 일치하지 않습니다.");
        Order updatedOrder = orderRepository.findByMerchantUid(testPaymentId).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID로 결제 시도 시 예외가 발생한다.")
    void testPaymentForNonExistentOrder() {
        // Given
        String nonExistentPaymentId = "non-existent-pay-id";
        given(portOneApiClient.getPayment(nonExistentPaymentId))
                .willReturn(createPortOnePaymentData(nonExistentPaymentId, 10000L, "PAID")); // PortOne에서는 성공했다고 가정

        // When
        PaymentCompleteRequest request = new PaymentCompleteRequest(nonExistentPaymentId);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.completePayment(request.getPaymentId()));

        // Then
        assertThat(exception.getMessage()).contains("일치하는 주문을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("이미 결제 완료된 주문을 다시 결제 시도 시 성공 응답을 반환한다.")
    void testPaymentForAlreadyCompletedOrder() {
        // Given
        testOrder.setStatus(OrderStatus.PAYMENT_COMPLETED);
        orderRepository.save(testOrder);

        given(portOneApiClient.getPayment(testPaymentId))
                .willReturn(createPortOnePaymentData(testPaymentId, testAmount, "PAID"));

        // When
        PaymentCompleteRequest request = new PaymentCompleteRequest(testPaymentId);
        PaymentCompleteResponse response = paymentService.completePayment(request.getPaymentId());

        // Then
        assertThat(response.getStatus()).isEqualTo("PAID");
        assertThat(response.getMessage()).contains("이미 처리된 주문입니다.");
        Order updatedOrder = orderRepository.findByMerchantUid(testPaymentId).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }

    @Test
    @DisplayName("PortOne API 결제 정보 조회 실패 시 예외가 발생한다")
    void testPortOneApiFailure() {
        // given
        given(portOneApiClient.getPayment(testPaymentId))
                .willThrow(new RuntimeException("API 통신 오류"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.completePayment(testPaymentId));

        assertThat(exception.getMessage()).contains("API 통신 오류");
        // 주문 상태는 변경되지 않아야 함 (PENDING)
        Order updatedOrder = orderRepository.findByMerchantUid(testPaymentId).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("PortOne 결제 상태가 PAID가 아니면 예외가 발생한다")
    void testPortOneStatusNotPaid() {
        // given
        given(portOneApiClient.getPayment(testPaymentId))
                .willReturn(createPortOnePaymentData(testPaymentId, testAmount, "READY"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.completePayment(testPaymentId));

        assertThat(exception.getMessage()).contains("결제가 완료되지 않았습니다. 상태: READY");
        // 주문 상태는 변경되지 않아야 함 (PENDING)
        Order updatedOrder = orderRepository.findByMerchantUid(testPaymentId).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
    }
}