package com.imfine.ngs.support.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class OrderDetail implements Serializable {
    private long orderId;
    private long gameId;

    @Builder
    public OrderDetail(long orderId, long gameId) {
        this.orderId = orderId;
        this.gameId = gameId;

    }
}
