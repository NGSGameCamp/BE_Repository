package com.imfine.ngs.game.dto.request;

import com.imfine.ngs.game.entity.env.Env;
import com.imfine.ngs.game.entity.env.LinkedEnv;
import com.imfine.ngs.game.entity.notice.GameNotice;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.enums.EnvType;
import com.imfine.ngs.game.enums.GameStatusType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * 게임 {@link com.imfine.ngs.game.entity.Game} 등록 요청 dto 클래스.
 *
 * @author chan
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GameCreateRequest {

    @NotBlank(message = "게임 이름은 필수입니다")
    @Size(min = 1, max = 100, message = "게임 이름은 1-100자 사이여야 합니다")
    private String name;

    @NotNull(message = "게임 가격은 필수입니다")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다")
    @Max(value = 1000000, message = "가격은 1,000,000원 이하여야 합니다")
    private Long price;

    private GameStatusType gameStatusType;

    @Size(max = 2000, message = "설명은 2000자를 초과할 수 없습니다")
    private String description;

    @Pattern(regexp = "^(https?://)?.+\\.(jpg|jpeg|png|gif|webp)$",
            message = "유효한 이미지 URL을 입력해주세요")
    private String thumbnailUrl;

    private String spec;

    private List<GameTagRequest> gameTagRequest;

    private List<EnvRequest> envRequest;

    private String introduction;

    private long publisherId;

}
