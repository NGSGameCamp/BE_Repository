package com.imfine.ngs.user.dto.request;

import com.imfine.ngs.user.entity.TargetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UnfollowRequest {
    private Long userId;
    private TargetType targetType;
    private Long targetId;
}

