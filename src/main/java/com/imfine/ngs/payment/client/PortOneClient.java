package com.imfine.ngs.payment.client;

import com.imfine.ngs.payment.dto.PaymentResponse;

public interface PortOneClient {
    PaymentResponse getPaymentData(String impUid);
    void cancelPayment(String impUid, String reason);
}
