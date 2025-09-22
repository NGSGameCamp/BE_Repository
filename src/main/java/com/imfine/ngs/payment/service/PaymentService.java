package com.imfine.ngs.payment.service;

import com.imfine.ngs._global.error.exception.BusinessException;
import com.imfine.ngs._global.error.model.ErrorCode;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderHistory;
import com.imfine.ngs.order.repository.OrderHistoryRepository;
import com.imfine.ngs.order.repository.OrderRepository;
import com.imfine.ngs.payment.client.PortOneApiClient;
import com.imfine.ngs.payment.client.PortOnePaymentData;
import com.imfine.ngs.payment.dto.PaymentCompleteResponse;
import com.imfine.ngs.payment.entity.Payment;
import com.imfine.ngs.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final OrderRepository orderRepository;
    private final PortOneApiClient portOneApiClient;
    private final PaymentRepository paymentRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    @Transactional
    public PaymentCompleteResponse completePayment(String paymentId) {
        // 1. PortOne API를 통해 결제 정보 조회
        PortOnePaymentData portOnePayment;
        try {
            portOnePayment = portOneApiClient.getPayment(paymentId);
            if (portOnePayment == null) {
                // PortOne에서 데이터를 못받아오는 경우.
                throw new BusinessException(ErrorCode.PORTONE_API_ERROR);
            }
        } catch (Exception e) {
            logger.error("PortOne API 호출 중 오류 발생 (paymentId: {})", paymentId, e);
            throw new BusinessException(ErrorCode.PORTONE_API_ERROR);
        }
        logger.info("PortOne 결제 정보 조회 성공: {}", portOnePayment);

        // 2. 우리 DB에서 주문 정보 조회
        final Order order = orderRepository.findByMerchantUid(portOnePayment.getMerchantUid())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        try {
            // 3. 결제 상태 확인 (PortOne)
            if (!"PAID".equalsIgnoreCase(portOnePayment.getStatus())) {
                throw new BusinessException(ErrorCode.PAYMENT_NOT_COMPLETED);
            }

            // 4. 이미 처리된 주문인지 확인 (우리 DB)
            if (order.isPaid()) {
                // 예외를 발생시켜 클라이언트에게 일관된 에러 메시지를 전달
                throw new BusinessException(ErrorCode.PAYMENT_ALREADY_COMPLETED);
            }

            // 5. 결제 금액 검증
            final long paidAmount = portOnePayment.getAmount().getTotal();
            final long expectedAmount = order.getTotalPrice();

            if (paidAmount != expectedAmount) {
                // 금액이 일치하지 않으면 PortOne 결제를 취소해야 함
                portOneApiClient.cancelPayment(paymentId);
                throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
            }

            // 6. 모든 검증 통과 -> 주문 상태를 '결제 완료'로 변경
            order.paymentCompleted();
            orderHistoryRepository.save(new OrderHistory(order, order.getStatus()));

            // 7. Payment 객체 생성 및 저장
            Payment payment = new Payment(order, paidAmount, paymentId);
            paymentRepository.save(payment);

            return new PaymentCompleteResponse("PAID", "결제가 성공적으로 완료되었습니다.");

        } catch (BusinessException e) {
            // 검증 과정에서 비즈니스 예외 발생 시 주문 상태를 '결제 실패'로 변경
            order.paymentFailed();
            orderHistoryRepository.save(new OrderHistory(order, order.getStatus()));
            // GlobalExceptionHandler가 처리하도록 예외를 다시 던짐
            throw e;
        }
    }
}
