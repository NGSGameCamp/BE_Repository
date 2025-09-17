package com.imfine.ngs.payment.dto;

import lombok.Getter;

@Getter
public class PaymentRequest {
    private final long orderId;
    private final String impUid;

    public PaymentRequest(long orderId, String impUid) {
        this.orderId = orderId;
        this.impUid = impUid;
    }
}
