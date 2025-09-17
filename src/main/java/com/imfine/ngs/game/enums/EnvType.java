package com.imfine.ngs.game.enums;

/**
 * 게임 플레이 환경(OS) 정의 열거형 클래스.
 *
 * @author chan
 */
public enum EnvType {

    MAC("Mac"),
    WINDOWS("Windows"),
    LINUX("Linux");

    private final String description;

    EnvType(String description) {
        this.description = description;
    }
}
