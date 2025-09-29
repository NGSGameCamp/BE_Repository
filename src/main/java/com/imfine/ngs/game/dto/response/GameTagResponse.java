package com.imfine.ngs.game.dto.response;

import com.imfine.ngs.game.enums.GameTagType;
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

    private GameTagType tagType;
    private String name;
    private String description;
}
