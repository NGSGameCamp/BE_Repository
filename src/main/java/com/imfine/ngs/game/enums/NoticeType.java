package com.imfine.ngs.game.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 게임 공지사항 타입 열거형
 *
 * @author chan
 */
@Getter
@RequiredArgsConstructor
public enum NoticeType {

    // 주요 업데이트
    MAJOR_UPDATE("주요 업데이트", "게임의 대규모 업데이트 및 주요 기능 추가"),

    // 소규모업데이트/패치노트
    PATCH_NOTE("패치 노트", "버그 수정 및 소규모 개선 사항"),

    // 뉴스
    NEWS("뉴스", "게임 관련 뉴스 및 이벤트 공지"),

    // 추가 타입들
    MAINTENANCE("점검 안내", "서버 점검 및 유지보수 안내"),
    EVENT("이벤트", "게임 내 이벤트 및 프로모션"),
    NOTICE("일반 공지", "일반적인 공지사항");

    private final String displayName;
    private final String description;
}
