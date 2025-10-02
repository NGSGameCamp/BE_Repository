package com.imfine.ngs.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * {@link com.imfine.ngs.game.entity.Game} 목록 조회용 응답 dto 클래스.
 *
 * @author chan
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameCardResponse {

    private Long id; // 게임 아이디
    private String name; // 게임 이름
    private Long price; // 게임 가격
    private Integer discountRate; // 게임 할인율
    private List<String> tags; // 게임 태그 리스트

    private Long publisherId; // 배급사 id
    private String publisherName; // 배급사 이름
    private Integer reviewCount; // 리뷰 숫자
    private Double averageScore; // 평균 평점
    private LocalDate releaseDate; //  등록일
    private String thumbnailUrl; // 썸네일 이미지
}
