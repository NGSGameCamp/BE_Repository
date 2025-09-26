package com.imfine.ngs.game.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 게임 요약 정보 응답 DTO 클래스.
 * 환경 정보를 제외한 간단한 정보만 포함하여 N+1 문제를 방지합니다.
 * 목록 조회 시 주로 사용됩니다.
 *
 * @author chan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSummaryResponse {

    private Long id;
    private String name;
    private Long price;
    private String tag;
    private String thumbnailUrl;
    private boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 환경 정보 개수 (실제 환경 목록은 포함하지 않음)
     * 필요시 별도 API로 조회
     */
    private int environmentCount;

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
     * 무료 게임 여부를 확인합니다.
     *
     * @return 무료 게임인 경우 true
     */
    public boolean isFree() {
        return price == null || price == 0;
    }

    /**
     * 간단한 게임 정보 표시용 문자열
     *
     * @return 게임 요약 정보
     */
    public String getSummary() {
        return String.format("[%d] %s - %s (%d개 플랫폼)",
                id, name, getFormattedPrice(), environmentCount);
    }
}