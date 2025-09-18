package com.imfine.ngs.community.repository;

import com.imfine.ngs.community.entity.CommunityPost;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
  Optional<CommunityPost> findCommunityPostsByBoardId(Long boardId, Limit limit);

  List<CommunityPost> findCommunityPostsByTitleContains(String title);
}
