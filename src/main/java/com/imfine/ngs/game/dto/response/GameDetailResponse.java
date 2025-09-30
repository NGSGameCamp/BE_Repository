package com.imfine.ngs.game.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameDetailResponse {

    private Long id;
    private String name;
    private Long price;
    private Set<String> tagNames;
    private String description;
    private String thumbnailUrl;
    private String spec;
    private String publisher;
    private Integer reviewCount;
    private Double averageScore;
    private Set<String> mediaUrls;
}
