package com.imfine.ngs.game.entity.review;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * {@link Game}, {@link User}
 * 유저가 게임에 대해 작성한 게임 리뷰 정보 저장 엔티티 클래스.
 *
 * @author chan
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 리뷰 ID

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 게임 리뷰 작성자

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game; // 리뷰되는 게임

    private String content; // 리뷰 내용

    private Integer score; // 게임에 남긴 평점 (1점부터 5점)

    private boolean isDeleted; // isDeleted가 true면 표시 안함

    @CreatedDate
    private LocalDateTime createdAt; // 리뷰 생성일

    @LastModifiedDate
    private LocalDateTime updatedAt; // 리뷰 수정일
}
