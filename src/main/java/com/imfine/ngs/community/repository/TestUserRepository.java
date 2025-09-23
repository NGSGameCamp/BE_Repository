package com.imfine.ngs.community.repository;

import com.imfine.ngs.community.entity.TestUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestUserRepository extends JpaRepository<TestUser, Long> {
  Optional<TestUser> findByName(String name);
}
