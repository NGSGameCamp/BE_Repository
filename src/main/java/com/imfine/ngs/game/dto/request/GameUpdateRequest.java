package com.imfine.ngs.game.dto.request;

import com.imfine.ngs.game.enums.EnvType;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.enums.GameTagType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

/**
 * 게임 {@link com.imfine.ngs.game.entity.Game} 수정 요청 dto 클래스.
 * 모든 필드가 선택적이므로 부분 업데이트를 지원합니다.
 *
 * @author chan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameUpdateRequest {

    @Size(min = 1, max = 100, message = "게임 이름은 1-100자 사이여야 합니다")
    private String name;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다")
    @Max(value = 1000000, message = "가격은 1,000,000원 이하여야 합니다")
    private Long price;

    private Set<GameTagType> tags;

    private Set<EnvType> environments;

    private GameStatusType gameStatus;

    @Size(max = 2000, message = "설명은 2000자를 초과할 수 없습니다")
    private String description;

    @Pattern(regexp = "^(https?://)?.+\\.(jpg|jpeg|png|gif|webp)$",
             message = "유효한 이미지 URL을 입력해주세요")
    private String thumbnailUrl;
}
