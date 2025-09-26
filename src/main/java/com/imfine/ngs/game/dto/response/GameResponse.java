package com.imfine.ngs.game.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 게임 {@link com.imfine.ngs.game.entity.Game} 기본 정보 응답 dto 클래스.
 * BaseGameResponse를 상속받아 공통 필드를 재사용합니다.
 *
 * @author chan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GameResponse extends BaseGameResponse {
    // BaseGameResponse의 모든 필드를 상속받음
    // 추가 필드가 필요한 경우 여기에 정의
}
