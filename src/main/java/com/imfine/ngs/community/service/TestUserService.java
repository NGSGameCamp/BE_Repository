package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.TestUser;
import com.imfine.ngs.community.repository.TestUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestUserService {
  private final TestUserRepository userRepo;

  public Long addUser(TestUser user) { return userRepo.save(user).getId(); }

  public TestUser getUserById(Long id) { return userRepo.findById(id).orElse(null); }

  public TestUser getUserByName(String name) { return userRepo.findByName(name).orElse(null); }

  public List<TestUser> getAllUsers() { return userRepo.findAll(); }
}
