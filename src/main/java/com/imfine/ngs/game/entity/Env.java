package com.imfine.ngs.game.entity;

import com.imfine.ngs.game.enums.EnvType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * 게임({@link Game})과 {@link EnvType} 테이블을 연결하는 중간 테이블이다.
 *
 * @author chan
 */
@Getter
@Setter
@Table(name = "linked_envs")
@Entity
public class Env {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private EnvType envType;

//    @ManyToMany(mappedBy =  "supportedEnvs")
//    private Set<Game> games = new HashSet<>();
}
