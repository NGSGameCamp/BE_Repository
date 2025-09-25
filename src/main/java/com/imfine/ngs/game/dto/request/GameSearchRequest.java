package com.imfine.ngs.game.dto.request;

import com.imfine.ngs.game.enums.SortType;
import com.imfine.ngs.game.validation.ValidPriceRange;
import jakarta.validation.constraints.*;
import lombok.*;


/**
 * 게임 {@link com.imfine.ngs.game.entity.Game} 조회 요청 dto 클래스.
 *
 * @author chan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidPriceRange // 커스텀 검증: minPrice <= maxPrice
public class GameSearchRequest {

    /**
     * 게임 이름 검색 키워드
     */
    @Size(max = 100, message = "검색어는 100자를 초과할 수 없습니다")
    private String name;

    /**
     * 게임 태그 (장르)
     */
    @Pattern(regexp = "^[A-Z_]+$", message = "태그는 대문자와 언더스코어만 허용됩니다")
    private String tag;

    /**
     * 최소 가격
     */
    @Min(value = 0, message = "최소 가격은 0원 이상이어야 합니다")
    private Long minPrice;

    /**
     * 최대 가격
     */
    @Max(value = 10000000, message = "최대 가격은 10,000,000원 이하여야 합니다")
    private Long maxPrice;

    /**
     * 게임 환경 (OS)
     */
    @Pattern(regexp = "^(MAC|WINDOWS|LINUX)$", message = "유효한 환경을 선택해주세요")
    private String environment;

    /**
     * 페이지 번호 (0부터 시작)
     */
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
    @Builder.Default
    private int page = 0;

    /**
     * 페이지 크기
     */
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
    @Builder.Default
    private int size = 10;

    /**
     * 정렬 방식
     */
    @NotNull(message = "정렬 방식은 필수입니다")
    @Builder.Default
    private SortType sortType = SortType.NAME_ASC;
}
