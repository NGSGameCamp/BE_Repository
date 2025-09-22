package com.imfine.ngs.support.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class OrderDetailEntity {
    @EmbeddedId
    private OrderDetail id;

    public OrderDetailEntity(OrderDetail orderDetail) {
        this.id = orderDetail;
    }
}
