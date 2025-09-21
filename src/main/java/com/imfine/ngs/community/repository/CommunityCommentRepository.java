package com.imfine.ngs.community.repository;

import com.imfine.ngs.community.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
  boolean existsByid(Long id);
  Optional<CommunityComment> findById(Long id);

  List<CommunityComment> getCommunityCommentsByPostId(Long postId);

  List<CommunityComment> getCommunityCommentsByAuthorId(Long authorId);
}
