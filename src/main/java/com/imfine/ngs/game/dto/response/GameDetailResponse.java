package com.imfine.ngs.game.dto.response;

import com.imfine.ngs.game.dto.response.util.BaseGameResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * 게임 {@link com.imfine.ngs.game.entity.Game} 상세 정보 응답 dto 클래스.
 * BaseGameResponse를 상속받아 공통 필드를 재사용하고,
 * 환경 정보를 추가로 포함합니다.
 *
 * @author chan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GameDetailResponse extends BaseGameResponse {

    /**
     * 게임이 지원하는 환경(OS) 목록
     */
    private Set<EnvironmentResponse> environments;

    /**
     * 게임의 평균 평점 (향후 구현 예정)
     */
    private Double averageRating;

    /**
     * 게임의 리뷰 개수 (향후 구현 예정)
     */
    private Long reviewCount;
}
