package com.imfine.ngs.community.repository;

import com.imfine.ngs.community.entity.CommunityPost;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
  List<CommunityPost> findCommunityPostsByBoardId(Long boardId);

  @Query(value = """
SELECT p.*
FROM community_post p
JOIN users u ON u.id = p.author_id
WHERE
      (( :type = 'TITLE_ONLY'          AND p.title   LIKE CONCAT('%', :keyword, '%') )
    OR ( :type = 'AUTHOR_ONLY'         AND u.name    LIKE CONCAT('%', :keyword, '%') )
    OR ( :type = 'CONTENT_ONLY'        AND p.content LIKE CONCAT('%', :keyword, '%') )
    OR ( :type = 'TITLE_AND_CONTENT'   AND (p.title LIKE CONCAT('%', :keyword, '%')
                                      OR  p.content LIKE CONCAT('%', :keyword, '%')) ))
AND p.board_id = :board_id
AND (
        :is_manager = TRUE
    OR (:is_manager = FALSE AND p.is_deleted = FALSE)
);
""", nativeQuery = true)
  List<CommunityPost> searchKeywords(
          @Param("board_id") Long boardId,
          @Param("type") String type,
          @Param("keyword") String keyword,
          @Param("is_manager") Boolean isManager
  );

  @Query(value = """
SELECT p.*
FROM community_post p
JOIN (
    SELECT pt.community_post_id
    FROM post_tag pt
    JOIN community_board_tags t ON t.id = pt.community_tag_id
    WHERE t.name IN (:tagNames)
    GROUP BY pt.community_post_id
    HAVING COUNT(DISTINCT t.id) = :tagCount
) tagged ON tagged.community_post_id = p.id
JOIN users u ON u.id = p.author_id
WHERE 
      ((:type = 'TITLE_ONLY'        AND p.title   LIKE CONCAT('%', :keyword, '%')) 
    OR (:type = 'AUTHOR_ONLY'       AND u.name    LIKE CONCAT('%', :keyword, '%'))
    OR (:type = 'CONTENT_ONLY'      AND p.content LIKE CONCAT('%', :keyword, '%'))
    OR (:type = 'TITLE_AND_CONTENT' AND (p.title LIKE CONCAT('%', :keyword, '%')
                                        OR  p.content LIKE CONCAT('%', :keyword, '%')))
      )
AND p.board_id = :boardId
AND (
        :is_manager = TRUE
    OR (:is_manager = FALSE AND p.is_deleted = FALSE)
);
""", nativeQuery = true)
  List<CommunityPost> searchKeywordsWithTags(
          @Param("boardId") Long boardId,
          @Param("type") String type,
          @Param("keyword") String keyword,
          @Param("tagNames") List<String> tagNames,
          @Param("tagCount") long tagCount,
          @Param("is_manager") Boolean isManager
  );
}
