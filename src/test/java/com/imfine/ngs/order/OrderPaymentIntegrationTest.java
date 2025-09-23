package com.imfine.ngs.order;

import com.imfine.ngs._global.error.exception.BusinessException;
import com.imfine.ngs._global.error.model.ErrorCode;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderStatus;
import com.imfine.ngs.order.repository.OrderRepository;
import com.imfine.ngs.order.service.OrderService;
import com.imfine.ngs.payment.client.PortOneApiClient;
import com.imfine.ngs.payment.client.PortOneAmount;
import com.imfine.ngs.payment.client.PortOnePaymentData;
import com.imfine.ngs.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

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
    private String testMerchantUid;
    private long testAmount;
    private Long testUserId;
    private Game gameA;
    private Game gameB;

    @BeforeEach
    void setUp() {
        testMerchantUid = "PAY-integration-test-123";
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
        testOrder.setMerchantUid(testMerchantUid);
        orderRepository.save(testOrder);
    }

    private PortOnePaymentData createPortOnePaymentData(String merchantUid, long amount, String status) {
        PortOneAmount amountObject = new PortOneAmount(amount, 0, 0, amount, 0, amount, 0, 0);
        // PortOne 응답에서 id는 imp_uid, merchantUid는 가맹점 주문 ID
        return new PortOnePaymentData("imp_" + merchantUid, status, merchantUid, "테스트 상품", amountObject, null, null, null, null);
    }

    @Test
    @DisplayName("주문 생성 후 결제까지 성공적으로 완료된다.")
    void testSuccessfulOrderAndPayment() {
        // Given
        String paymentId = "imp_" + testMerchantUid;
        given(portOneApiClient.getPayment(paymentId))
                .willReturn(createPortOnePaymentData(testMerchantUid, testAmount, "PAID"));

        // When
        paymentService.completePayment(paymentId);

        // Then
        Order updatedOrder = orderRepository.findByMerchantUid(testMerchantUid).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }

    @Test
    @DisplayName("결제 금액이 불일치하면 예외가 발생하고 주문 상태는 PAYMENT_FAILED가 된다.")
    void testPaymentAmountMismatch() {
        // Given
        long paidAmount = testAmount - 1000L; // Mismatched amount
        String paymentId = "imp_" + testMerchantUid;

        given(portOneApiClient.getPayment(paymentId))
                .willReturn(createPortOnePaymentData(testMerchantUid, paidAmount, "PAID"));

        // When
        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.completePayment(paymentId));

        // Then
        assertEquals(ErrorCode.PAYMENT_AMOUNT_MISMATCH, exception.getErrorCode());
        Order updatedOrder = orderRepository.findByMerchantUid(testMerchantUid).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
    }

    @Test
    @DisplayName("존재하지 않는 주문(merchant_uid)으로 결제 시도 시 예외가 발생한다.")
    void testPaymentForNonExistentOrder() {
        // Given
        String nonExistentMerchantUid = "non-existent-merchant-uid";
        String paymentId = "imp_" + nonExistentMerchantUid;
        given(portOneApiClient.getPayment(paymentId))
                .willReturn(createPortOnePaymentData(nonExistentMerchantUid, 10000L, "PAID")); // PortOne에서는 성공했다고 가정

        // When
        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.completePayment(paymentId));

        // Then
        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("이미 결제 완료된 주문을 다시 결제 시도 시 예외가 발생한다.")
    void testPaymentForAlreadyCompletedOrder() {
        // Given
        testOrder.setStatus(OrderStatus.PAYMENT_COMPLETED);
        orderRepository.save(testOrder);
        String paymentId = "imp_" + testMerchantUid;

        given(portOneApiClient.getPayment(paymentId))
                .willReturn(createPortOnePaymentData(testMerchantUid, testAmount, "PAID"));

        // When
        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.completePayment(paymentId));

        // Then
        assertEquals(ErrorCode.PAYMENT_ALREADY_COMPLETED, exception.getErrorCode());
        Order updatedOrder = orderRepository.findByMerchantUid(testMerchantUid).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED); // 실패로 기록됨
    }

    @Test
    @DisplayName("PortOne API 결제 정보 조회 실패 시 예외가 발생한다")
    void testPortOneApiFailure() {
        // given
        String paymentId = "imp_" + testMerchantUid;
        given(portOneApiClient.getPayment(paymentId))
                .willThrow(new RuntimeException("API 통신 오류"));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.completePayment(paymentId));

        assertEquals(ErrorCode.PORTONE_API_ERROR, exception.getErrorCode());
        // 주문을 찾기 전이므로 주문 상태는 변경되지 않아야 함
        Order updatedOrder = orderRepository.findByMerchantUid(testMerchantUid).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("PortOne 결제 상태가 PAID가 아니면 예외가 발생하고 주문은 실패 처리된다.")
    void testPortOneStatusNotPaid() {
        // given
        String paymentId = "imp_" + testMerchantUid;
        given(portOneApiClient.getPayment(paymentId))
                .willReturn(createPortOnePaymentData(testMerchantUid, testAmount, "READY"));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.completePayment(paymentId));

        assertEquals(ErrorCode.PAYMENT_NOT_COMPLETED, exception.getErrorCode());
        // 주문을 찾은 후 예외가 발생했으므로 주문 상태는 PAYMENT_FAILED가 됨
        Order updatedOrder = orderRepository.findByMerchantUid(testMerchantUid).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
    }
}
