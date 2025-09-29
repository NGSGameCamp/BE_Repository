package com.imfine.ngs.game.entity.discount;

import com.imfine.ngs.game.entity.Game;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * {@link Game} 게임 개별 할인 테이블
 *
 * @author chan
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class SingleGameDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 할인 ID

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game; // 할인 대상 게임

    private BigDecimal discountRate; // 할인율 (예: 10.50%)

    @CreatedDate
    private LocalDateTime createdAt; // 할인 시작일

    private LocalDateTime expiresAt; // 할인 종료일

}