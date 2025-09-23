package com.imfine.ngs.payment;

import com.imfine.ngs._global.error.exception.BusinessException;
import com.imfine.ngs._global.error.model.ErrorCode;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderHistory;
import com.imfine.ngs.order.repository.OrderHistoryRepository;
import com.imfine.ngs.order.repository.OrderRepository;
import com.imfine.ngs.payment.client.PortOneApiClient;
import com.imfine.ngs.payment.client.PortOneAmount;
import com.imfine.ngs.payment.client.PortOnePaymentData;
import com.imfine.ngs.payment.dto.PaymentCompleteResponse;
import com.imfine.ngs.payment.entity.Payment;
import com.imfine.ngs.payment.repository.PaymentRepository;
import com.imfine.ngs.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderHistoryRepository orderHistoryRepository;

    @Mock
    private PortOneApiClient portOneApiClient;

    @Mock
    private Order testOrder;

    private String testPaymentId;
    private String testMerchantUid;
    private long testAmount;

    @BeforeEach
    void setUp() {
        testPaymentId = "payment-id-123";
        testMerchantUid = "merchant-uid-123";
        testAmount = 50000L;
    }

    private PortOnePaymentData createPortOnePaymentData(String paymentId, String merchantUid, long amount, String status) {
        PortOneAmount amountObject = new PortOneAmount(amount, 0, 0, amount, 0, amount, 0, 0);
        return new PortOnePaymentData(paymentId, status, merchantUid, "테스트 상품", amountObject, null, null, null, null);
    }

    @Test
    @DisplayName("실제 결제 금액과 주문 금액이 일치하면, 주문 상태를 '결제 완료'로 변경한다.")
    void testPaymentSuccess() {
        //given
        PortOnePaymentData portOnePaymentData = createPortOnePaymentData(testPaymentId, testMerchantUid, testAmount, "PAID");
        given(portOneApiClient.getPayment(testPaymentId)).willReturn(portOnePaymentData);
        given(orderRepository.findByMerchantUid(testMerchantUid)).willReturn(Optional.of(testOrder));
        given(testOrder.isPaid()).willReturn(false);
        given(testOrder.getTotalPrice()).willReturn(testAmount);

        //when
        PaymentCompleteResponse response = paymentService.completePayment(testPaymentId);

        //then
        assertThat(response.getStatus()).isEqualTo("PAID");
        assertThat(response.getMessage()).isEqualTo("결제가 성공적으로 완료되었습니다.");

        verify(testOrder).paymentCompleted();
        verify(orderHistoryRepository).save(any(OrderHistory.class));
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("실제 결제 금액과 주문 금액이 다르면, 예외가 발생하고 주문 상태를 '결제 실패'로 변경한다")
    void testPaymentAmountMismatch() {
        // given
        long paidAmount = 40000L;  // 실제 결제된 금액 (불일치)
        PortOnePaymentData portOnePaymentData = createPortOnePaymentData(testPaymentId, testMerchantUid, paidAmount, "PAID");
        given(portOneApiClient.getPayment(testPaymentId)).willReturn(portOnePaymentData);
        given(orderRepository.findByMerchantUid(testMerchantUid)).willReturn(Optional.of(testOrder));
        given(testOrder.getTotalPrice()).willReturn(testAmount);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            paymentService.completePayment(testPaymentId);
        });

        assertEquals(ErrorCode.PAYMENT_AMOUNT_MISMATCH, exception.getErrorCode());
        verify(testOrder).paymentFailed();
        verify(orderHistoryRepository).save(any(OrderHistory.class));
        verify(portOneApiClient).cancelPayment(testPaymentId); // 금액 불일치 시 결제 취소 호출 검증
    }

    @Test
    @DisplayName("이미 결제 완료된 주문을 다시 결제 시도하면 예외가 발생한다.")
    void testPaymentAlreadyCompleted() {
        //given
        PortOnePaymentData portOnePaymentData = createPortOnePaymentData(testPaymentId, testMerchantUid, testAmount, "PAID");
        given(portOneApiClient.getPayment(testPaymentId)).willReturn(portOnePaymentData);
        given(orderRepository.findByMerchantUid(testMerchantUid)).willReturn(Optional.of(testOrder));
        given(testOrder.isPaid()).willReturn(true); // 이미 결제된 상태로 설정

        //when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            paymentService.completePayment(testPaymentId);
        });

        assertEquals(ErrorCode.PAYMENT_ALREADY_COMPLETED, exception.getErrorCode());

        // 추가적인 상태 변경이나 저장이 없어야 함
        verify(testOrder, never()).paymentCompleted();
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID로 결제 시도하면 예외가 발생한다")
    void testPaymentOrderNotFound() {
        // given
        PortOnePaymentData portOnePaymentData = createPortOnePaymentData(testPaymentId, testMerchantUid, testAmount, "PAID");
        given(portOneApiClient.getPayment(testPaymentId)).willReturn(portOnePaymentData);
        given(orderRepository.findByMerchantUid(testMerchantUid)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.completePayment(testPaymentId));

        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("PortOne API 결제 정보 조회 실패 시 예외가 발생한다")
    void testPortOneApiFailure() {
        // given
        given(portOneApiClient.getPayment(testPaymentId)).willThrow(new RuntimeException("API 통신 오류"));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.completePayment(testPaymentId));

        assertEquals(ErrorCode.PORTONE_API_ERROR, exception.getErrorCode());
        verify(orderRepository, never()).findByMerchantUid(anyString());
    }

    @Test
    @DisplayName("PortOne 결제 상태가 PAID가 아니면 예외가 발생한다")
    void testPortOneStatusNotPaid() {
        // given
        PortOnePaymentData portOnePaymentData = createPortOnePaymentData(testPaymentId, testMerchantUid, testAmount, "PENDING");
        given(portOneApiClient.getPayment(testPaymentId)).willReturn(portOnePaymentData);
        given(orderRepository.findByMerchantUid(testMerchantUid)).willReturn(Optional.of(testOrder));


        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> paymentService.completePayment(testPaymentId));

        assertEquals(ErrorCode.PAYMENT_NOT_COMPLETED, exception.getErrorCode());
        verify(orderRepository).findByMerchantUid(testMerchantUid);
    }
}
