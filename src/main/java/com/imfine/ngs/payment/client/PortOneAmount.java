package com.imfine.ngs.payment.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortOneAmount {
    private long total;
    private long taxFree;
    private long vat;
    private long supply;
    private long discount;
    private long paid;
    private long cancelled;
    private long cancelledTaxFree;
}