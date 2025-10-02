package com.imfine.ngs.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 사용자 라이브러리 게임 정보 응답 DTO
 *
 * @author chan
 */
@Getter
@AllArgsConstructor
@Builder
public class UserLibraryResponse {
    private Long id;
    private String name;
    private Long price;
    private String thumbnailUrl;
    private List<String> tags;
}
