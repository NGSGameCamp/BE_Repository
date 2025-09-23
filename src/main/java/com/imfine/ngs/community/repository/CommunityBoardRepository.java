package com.imfine.ngs.community.repository;

import com.imfine.ngs.community.entity.CommunityBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityBoardRepository extends JpaRepository<CommunityBoard, Long> {
  boolean existsById(Long id);

  Optional<CommunityBoard> findByTitle(String title);

  List<CommunityBoard> findCommunityBoardsByTitleContains(String title);

  List<CommunityBoard> findCommunityBoardsByIsDeletedIsFalse();

  List<CommunityBoard> findCommunityBoardsByIsDeletedAndTitleContains(boolean isDeleted, String keyword);
}
