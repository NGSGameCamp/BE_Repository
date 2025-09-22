package com.imfine.ngs.user.dto.response;

import com.imfine.ngs.user.entity.Follow;
import com.imfine.ngs.user.entity.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class FollowResponse {
    private Long id;
    private Long userId;
    private TargetType targetType;
    private Long targetId;
    private LocalDateTime createdAt;

    public static FollowResponse from(Follow follow) {
        return FollowResponse.builder()
                .id(follow.getId())
                .userId(follow.getUser() != null ? follow.getUser().getId() : null)
                .targetType(follow.getTargetType())
                .targetId(follow.getTargetId())
                .createdAt(follow.getCreatedAt())
                .build();
    }
}

