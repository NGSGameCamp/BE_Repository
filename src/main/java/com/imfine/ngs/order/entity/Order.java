package com.imfine.ngs.order.entity;

import com.imfine.ngs._global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetails> orderDetails = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String merchantUid; // 주문 고유 ID

    public Order(Long userId) {
        this.userId = userId;
        this.status = OrderStatus.PENDING;
        this.merchantUid = UUID.randomUUID().toString();
    }

    public void addOrderDetail(OrderDetails detail) {
        this.orderDetails.add(detail);
    }

    public long getTotalPrice() {
        return this.orderDetails.stream()
                .mapToLong(OrderDetails::getPriceSnapshot)
                .sum();
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public boolean isPaid() {
        return this.status == OrderStatus.PAYMENT_COMPLETED;
    }

    public void paymentCompleted() {
        this.status = OrderStatus.PAYMENT_COMPLETED;
    }

    public void paymentFailed() {
        this.status = OrderStatus.PAYMENT_FAILED;
    }

    public void setMerchantUid(String merchantUid) {
        this.merchantUid = merchantUid;
    }
}
