package com.imfine.ngs.support.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Support {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    long userId;
    long orderId;
    long categoryId;
    String title;
    String content;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    LocalDateTime createdAt;

    @Builder
    public Support(Long userId, long orderId, long categoryId, String title, String content, LocalDateTime createdAt) {
        this.userId = userId;
        this.orderId = orderId;
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }
}
