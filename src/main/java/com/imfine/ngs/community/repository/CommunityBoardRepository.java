package com.imfine.ngs.community.repository;

import com.imfine.ngs.community.entity.CommunityBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityBoardRepository extends JpaRepository<CommunityBoard, Long> {
  boolean existsById(Long id);

  Page<CommunityBoard> findCommunityBoardsByTitleContains(String title, Pageable pageable);

  Page<CommunityBoard> findCommunityBoardsByIsDeletedIsFalse(Pageable pageable);

  Page<CommunityBoard> findCommunityBoardsByIsDeletedAndTitleContains(boolean isDeleted, String keyword, Pageable pageable);
}
