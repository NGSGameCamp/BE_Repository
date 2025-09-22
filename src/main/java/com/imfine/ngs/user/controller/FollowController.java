package com.imfine.ngs.user.controller;

import com.imfine.ngs.user.dto.response.FollowResponse;
import com.imfine.ngs.user.entity.Follow;
import com.imfine.ngs.user.entity.TargetType;
import com.imfine.ngs.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{targetType}/{targetId}")
    public ResponseEntity<FollowResponse> follow(@PathVariable TargetType targetType,
                                                 @PathVariable Long targetId,
                                                 @RequestParam Long userId) {
        Follow follow = followService.follow(userId, targetType, targetId);
        return ResponseEntity.ok(FollowResponse.from(follow));
    }

    @DeleteMapping("/{targetType}/{targetId}")
    public ResponseEntity<Void> unfollow(@PathVariable TargetType targetType,
                                         @PathVariable Long targetId,
                                         @RequestParam Long userId) {
        followService.unfollow(userId, targetType, targetId);
        return ResponseEntity.ok().build();
    }

    // 프로필 화면에서 팔로잉 리스트 출력 (옵션: targetType 필터)
    @GetMapping("/following/{userId}")
    public ResponseEntity<List<FollowResponse>> getFollowing(@PathVariable Long userId,
                                                             @RequestParam(required = false) TargetType targetType) {
        var stream = followService.getFollowing(userId).stream();
        if (targetType != null) {
            stream = stream.filter(f -> f.getTargetType() == targetType);
        }
        var result = stream.map(FollowResponse::from).toList();
        return ResponseEntity.ok(result);
    }
}
