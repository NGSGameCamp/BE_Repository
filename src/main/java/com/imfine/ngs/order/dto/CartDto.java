package com.imfine.ngs.order.dto;

import com.imfine.ngs.order.entity.Order;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CartDto {

    private final Long orderId;
    private final List<CartItemDto> orderItems;
    private final int itemCount;
    private final long subtotal;
    private final long discount;
    private final long total;

    public CartDto(Order order) {
        this.orderId = order.getOrderId();
        this.orderItems = order.getOrderDetails().stream()
                .map(CartItemDto::new)
                .collect(Collectors.toList());
        this.itemCount = this.orderItems.size();
        this.subtotal = order.getTotalPrice();
        this.discount = 0; // 할인 로직은 추후 적용
        this.total = this.subtotal - this.discount;
    }
}
