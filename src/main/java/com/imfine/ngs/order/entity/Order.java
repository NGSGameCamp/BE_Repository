package com.imfine.ngs.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderId;
    private String userId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<Game> orderItems = new ArrayList<>();

    private long totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public Order(long orderId, long totalPrice) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
    }

    public int getOrderItemCount() {
        if (orderItems == null) {
            return 0;
        }
        return orderItems.size();
    }
}
