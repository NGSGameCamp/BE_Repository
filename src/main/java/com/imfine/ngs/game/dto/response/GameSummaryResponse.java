package com.imfine.ngs.game.dto.response;

import com.imfine.ngs.game.dto.response.util.BaseGameResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
@SuperBuilder
public class GameSummaryResponse extends BaseGameResponse {

    /**
     * 환경 정보 개수 (실제 환경 목록은 포함하지 않음)
     * 필요시 별도 API로 조회
     */
    private int environmentCount;

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