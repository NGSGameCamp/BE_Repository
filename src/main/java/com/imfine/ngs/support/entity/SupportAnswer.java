package com.imfine.ngs.support.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class SupportAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @JoinColumn
    long supportId;

    String content;

    @CreatedDate
    LocalDateTime createDateAt;

    @Builder
    public SupportAnswer(long supportId, String content) {
        this.supportId = supportId;
        this.content = content;
    }

}
