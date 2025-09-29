package com.imfine.ngs.game.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 게임 태그(장르) 정의 열거형 클래스.
 * 게임의 분류와 카테고리를 나타냅니다.
 *
 * @author chan
 */
@Getter
@RequiredArgsConstructor
public enum GameTagType {

    ACTION("ACTION", "액션", "빠른 반응과 실시간 전투가 특징인 게임"),
    RPG("RPG", "롤플레잉", "캐릭터 성장과 스토리가 중심인 게임"),
    STRATEGY("STRATEGY", "전략", "전술과 계획이 중요한 게임"),
    SIMULATION("SIMULATION", "시뮬레이션", "현실을 모방하거나 가상 환경을 체험하는 게임"),
    SPORTS("SPORTS", "스포츠", "각종 스포츠를 주제로 한 게임"),
    RACING("RACING", "레이싱", "속도 경쟁이 주요 요소인 게임"),
    PUZZLE("PUZZLE", "퍼즐", "문제 해결과 논리적 사고가 필요한 게임"),
    ADVENTURE("ADVENTURE", "어드벤처", "탐험과 스토리 진행이 중심인 게임"),
    SHOOTER("SHOOTER", "슈터", "사격과 전투가 주요 요소인 게임"),
    FIGHTING("FIGHTING", "격투", "1:1 대전이 중심인 게임"),
    PLATFORMER("PLATFORMER", "플랫포머", "점프와 이동이 핵심인 게임"),
    HORROR("HORROR", "호러", "공포와 스릴을 주제로 한 게임"),
    INDIE("INDIE", "인디", "독립 개발자가 만든 창의적인 게임"),
    CASUAL("CASUAL", "캐주얼", "가볍게 즐길 수 있는 게임"),
    MMORPG("MMORPG", "MMORPG", "대규모 다중 사용자 온라인 롤플레잉 게임"),
    SURVIVAL("SURVIVAL", "생존", "생존이 주요 목표인 게임"),
    SANDBOX("SANDBOX", "샌드박스", "자유로운 플레이가 가능한 게임"),
    EDUCATIONAL("EDUCATIONAL", "교육", "학습과 교육을 목적으로 한 게임");

    private final String code;
    private final String koreanName;
    private final String description;

    /**
     * 코드로 GameTag를 찾습니다.
     *
     * @param code 태그 코드
     * @return 해당하는 GameTag, 없으면 null
     */
    @JsonCreator
    public static GameTagType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (GameTagType tag : GameTagType.values()) {
            if (tag.code.equalsIgnoreCase(code)) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Unknown GameTag code: " + code);
    }

    /**
     * JSON 직렬화 시 사용할 값
     */
    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * 유효한 태그 코드인지 확인
     *
     * @param code 확인할 코드
     * @return 유효하면 true
     */
    public static boolean isValidCode(String code) {
        if (code == null) {
            return false;
        }
        for (GameTagType tag : GameTagType.values()) {
            if (tag.code.equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }
}