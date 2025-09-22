package com.imfine.ngs.community.service;

import com.imfine.ngs.community.entity.TestUser;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {
  public boolean isValidUser(Long authorId, TestUser user) {
    if (authorId == null) return false;
    if (user == null) return false;
    return (authorId.equals(user.getId())) || (user.getRole().equals("MANAGER"));
  }
}
