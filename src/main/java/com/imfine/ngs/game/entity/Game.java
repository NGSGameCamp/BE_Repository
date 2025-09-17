package com.imfine.ngs.game.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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

    private String env;
    private String tag;
    //    @ManyToMany
//    @JoinTable(
//            name = "linked_envs",
//            joinColumns = @JoinColumn(name = "game_id"),
//            inverseJoinColumns = @JoinColumn(name = "env_id")
//    )
//    private Set<Env> env = new HashSet<>(); // 게임 OS
    private boolean isActive;

    @CreatedDate
    private LocalDateTime createdAt; // 게임 등록 날짜
}
