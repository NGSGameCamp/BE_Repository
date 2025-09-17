package com.imfine.ngs.payment.entity;

import com.imfine.ngs.order.entity.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private long amount; // 결제 금액

    private String impUid; // 포트원 거래 ID

    public Payment(Order order, long amount, String impUid) {
        this.order = order;
        this.amount = amount;
        this.impUid = impUid;
    }
}
