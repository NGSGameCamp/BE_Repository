package com.imfine.ngs.user.repository;

import com.imfine.ngs.user.entity.Follow;
import com.imfine.ngs.user.entity.TargetType;
import com.imfine.ngs.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    Optional<Follow> findByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    List<Follow> findAllByUserId(Long userId);

    List<Follow> findAllByTargetTypeAndTargetId(TargetType targetType, Long targetId);

}
