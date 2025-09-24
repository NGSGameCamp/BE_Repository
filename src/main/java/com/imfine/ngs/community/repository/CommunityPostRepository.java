package com.imfine.ngs.community.repository;

import com.imfine.ngs.community.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
  Page<CommunityPost> findCommunityPostsByBoardId(Long boardId, Pageable pageable);

  @Query(
          value = """
SELECT p.*
FROM community_post p
JOIN users u ON u.id = p.author_id
WHERE
      (
        :keyword IS NULL OR :keyword = ''
        OR (
          (:type = 'TITLE_ONLY'        AND p.title   LIKE CONCAT('%', :keyword, '%')) OR
          (:type = 'AUTHOR_ONLY'       AND u.name    LIKE CONCAT('%', :keyword, '%')) OR
          (:type = 'CONTENT_ONLY'      AND p.content LIKE CONCAT('%', :keyword, '%')) OR
          (:type = 'TITLE_AND_CONTENT' AND (p.title LIKE CONCAT('%', :keyword, '%')
                                         OR  p.content LIKE CONCAT('%', :keyword, '%')))
        )
      )
  AND p.board_id = :board_id
  AND (
        :is_manager = TRUE
     OR (:is_manager = FALSE AND p.is_deleted = FALSE)
      )
  AND (
      :tag_count = 0
      OR p.id IN (
          SELECT pt.community_post_id
          FROM post_tag pt
          JOIN community_board_tags t ON t.id = pt.community_tag_id
          WHERE t.name IN (:tag_names)
          GROUP BY pt.community_post_id
          HAVING COUNT(DISTINCT t.id) = :tag_count
        )
      )
""",
          countQuery = """
SELECT COUNT(DISTINCT p.id)
FROM community_post p
JOIN users u ON u.id = p.author_id
WHERE
      (
        :keyword IS NULL OR :keyword = ''
        OR (
          (:type = 'TITLE_ONLY'        AND p.title   LIKE CONCAT('%', :keyword, '%')) OR
          (:type = 'AUTHOR_ONLY'       AND u.name    LIKE CONCAT('%', :keyword, '%')) OR
          (:type = 'CONTENT_ONLY'      AND p.content LIKE CONCAT('%', :keyword, '%')) OR
          (:type = 'TITLE_AND_CONTENT' AND (p.title LIKE CONCAT('%', :keyword, '%')
                                         OR  p.content LIKE CONCAT('%', :keyword, '%')))
        )
      )
  AND p.board_id = :board_id
  AND (
        :is_manager = TRUE
     OR (:is_manager = FALSE AND p.is_deleted = FALSE)
      )
  AND (
      :tag_count = 0
      OR p.id IN (
          SELECT pt.community_post_id
          FROM post_tag pt
          JOIN community_board_tags t ON t.id = pt.community_tag_id
          WHERE t.name IN (:tag_names)
          GROUP BY pt.community_post_id
          HAVING COUNT(DISTINCT t.id) = :tag_count
        )
      )
""", nativeQuery = true
  )
  Page<CommunityPost> searchKeywordsWithTags(
          @Param("board_id") Long boardId,
          @Param("type") String type,
          @Param("keyword") String keyword,
          @Param("tag_names") List<String> tagNames,
          @Param("tag_count") long tagCount,
          @Param("is_manager") Boolean isManager,
          Pageable pageable
  );
}
