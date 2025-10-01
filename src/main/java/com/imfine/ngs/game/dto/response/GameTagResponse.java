package com.imfine.ngs.game.dto.response;

import lombok.*;

/**
 * 게임 태그 정보 응답 DTO 클래스.
 *
 * @author chan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameTagResponse {
    private String gameTagType;

}
