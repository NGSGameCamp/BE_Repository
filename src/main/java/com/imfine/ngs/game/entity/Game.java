package com.imfine.ngs.game.entity;

import com.imfine.ngs.game.entity.env.Env;
import com.imfine.ngs.game.entity.env.LinkedEnv;
import com.imfine.ngs.game.entity.env.util.LinkedEnvId;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 게임({@link Game}) 엔티티 클래스.
 * 현재는 테스트코드를 위해 간소화 된 상태이다.
 *
 * @author chan
 */
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게임 식별 아이디

    @Column(nullable = false)
    private String name; // 게임 이름

    @Column(nullable = false)
    private Long price; // 게임 가격

//    private String env;

    private String tag;

    @OneToMany(mappedBy = "game")
    private Set<LinkedEnv> env = new HashSet<>(); // 게임 OS

    private boolean isActive;

    @CreatedDate
    private LocalDateTime createdAt; // 게임 등록 날짜
}

