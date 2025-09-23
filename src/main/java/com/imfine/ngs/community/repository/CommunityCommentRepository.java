package com.imfine.ngs.community.repository;

import com.imfine.ngs.community.entity.CommunityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

  Page<CommunityComment> findCommunityCommentsByAuthorId(Long authorId, Pageable pageable);

  @Query(value = """
SELECT c.*
FROM community_comment c
WHERE c.post_id = :post_id
AND (
        :is_manager = TRUE
    OR (:is_manager = FALSE AND c.is_deleted = FALSE)
)
""", nativeQuery = true)
  List<CommunityComment> findCommunityCommentsByPostId(
          @Param("post_id") Long postId,
          @Param("is_manager") Boolean isManager
  );
  List<CommunityComment> findCommunityCommentsByPostId(Long postId);

  long countByPostId(Long postId);
}
