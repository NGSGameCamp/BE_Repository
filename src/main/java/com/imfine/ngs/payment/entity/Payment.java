package com.imfine.ngs.payment.entity;

import com.imfine.ngs.order.entity.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private long amount; // 결제 금액

    @Column(unique = true)
    private String impUid; // 포트원 거래 ID

    public Payment(Order order, long amount, String impUid) {
        this.order = order;
        this.amount = amount;
        this.impUid = impUid;
    }
}
