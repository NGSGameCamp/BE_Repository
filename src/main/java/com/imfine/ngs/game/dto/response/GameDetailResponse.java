package com.imfine.ngs.game.dto.response;

import lombok.*;

import javax.print.DocFlavor;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * {@link com.imfine.ngs.game.entity.Game} 상세 조회 응답 dto 클래스.
 *
 * @author chan
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameDetailResponse {

    private Long id; // 아이디
    private String name; // 게임 이름
    private Long price; // 게임 가격
    private List<String> tags; // 게임 태그
    private String description; // 게임 설명
    private String introduction; // 게임 본문
    private String thumbnailUrl; // 게임 썸네일 이미지
    private String spec; // 게임 사양
    private Integer reviewCount; // 리뷰 카운트
    private Double averageScore; // 평균 평점
    private List<String> mediaUrls; // 본문에 표시할 이미지 배열

    private LocalDate releaseDate;// 출시일
    private Integer discountRate;// 할인율
    private Long publisherId; // 배급사 id
    private String publisherName; // 배급사 이름
    private List<String> env; // 게임 환경 (OS)

}
