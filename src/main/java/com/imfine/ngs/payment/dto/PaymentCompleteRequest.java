package com.imfine.ngs.payment.dto;

import lombok.Data;

@Data
public class PaymentCompleteRequest {
    private String paymentId;

    public PaymentCompleteRequest(String paymentId) {
        this.paymentId = paymentId;
    }
}
