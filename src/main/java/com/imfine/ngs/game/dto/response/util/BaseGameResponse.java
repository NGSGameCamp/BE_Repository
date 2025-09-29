package com.imfine.ngs.game.dto.response.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imfine.ngs.game.dto.response.GameTagResponse;
import com.imfine.ngs.game.enums.GameStatusType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 게임 응답 DTO의 기본 클래스.
 * 공통 필드와 메서드를 정의하여 중복을 제거합니다.
 *
 * @author chan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseGameResponse {

    protected Long id;
    protected String name;
    protected Long price;
    protected Set<GameTagResponse> tags;
    protected GameStatusType gameStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime createdAt;

    protected String description;
    protected String thumbnailUrl;

    /**
     * 포맷된 가격 문자열을 반환합니다.
     *
     * @return 포맷된 가격 (예: "₩1,000" 또는 "무료")
     */
    public String getFormattedPrice() {
        if (price == null || price == 0) {
            return "무료";
        }
        return String.format("₩%,d", price);
    }

    /**
     * 할인 여부를 확인합니다.
     *
     * @return 무료 게임인 경우 true
     */
    public boolean isFree() {
        return price == null || price == 0;
    }
}