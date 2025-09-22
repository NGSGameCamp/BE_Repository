package com.imfine.ngs.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompleteResponse {
    private String status;
    private String message;

    public PaymentCompleteResponse(String status) {
        this.status = status;
    }
}
