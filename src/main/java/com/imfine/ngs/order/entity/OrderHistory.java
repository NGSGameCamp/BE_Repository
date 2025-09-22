package com.imfine.ngs.order.entity;

import com.imfine.ngs._global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public OrderHistory(Order order, OrderStatus orderStatus) {
        this.order = order;
        this.orderStatus = orderStatus;
    }
}
