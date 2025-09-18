package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.TestUser;
import com.imfine.ngs.community.repository.TestUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestUserService {
  TestUserRepository userRepo;

  @Autowired
  TestUserService(TestUserRepository userRepo) {
    this.userRepo = userRepo;
  }

  void addUser(TestUser user) { userRepo.save(user); }
  TestUser getUserById(Long id) { return userRepo.findById(id).isPresent() ? userRepo.findById(id).get() : null; }

  public TestUser getUserByName(String name) { return userRepo.findByName(name).isPresent() ? userRepo.findByName(name).get() : null ; }
}
