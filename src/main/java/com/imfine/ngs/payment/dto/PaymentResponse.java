package com.imfine.ngs.payment.dto;

import lombok.Getter;

@Getter
public class PaymentResponse {
    private final String impUid;
    private final long amount;

    public PaymentResponse(String impUid, long amount) {
        this.impUid = impUid;
        this.amount = amount;
    }
}
