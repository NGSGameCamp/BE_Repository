package com.imfine.ngs.support.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Support {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    long userId;
    long orderId;
    long categoryId;
    String content;
    LocalDateTime createdAt;

    @Builder
    public Support(Long userId, long orderId, long categoryId, String content, LocalDateTime createdAt) {
        this.userId = userId;
        this.orderId = orderId;
        this.categoryId = categoryId;
        this.content = content;
        this.createdAt = createdAt;
    }
}
