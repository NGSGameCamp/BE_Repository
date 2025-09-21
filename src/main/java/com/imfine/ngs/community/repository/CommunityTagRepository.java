package com.imfine.ngs.community.repository;

import com.imfine.ngs.community.entity.CommunityTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityTagRepository extends JpaRepository<CommunityTag, Long> {
  Optional<CommunityTag> findByName(String name);

  List<CommunityTag> findAllByName(String name);
}
