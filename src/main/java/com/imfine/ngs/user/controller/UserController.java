package com.imfine.ngs.user.controller;

import com.imfine.ngs.support.entity.Support;
import com.imfine.ngs.support.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/u")
public class UserController {

    private final SupportService supportService;

    @GetMapping("/support")
    public List<Support> getSupportByUserId(int userId) {
        return supportService.findSupportByUserId(userId);
    }

    @GetMapping("/support/{supportId}")
    public Support getSupportById(@PathVariable long supportId) {
        return supportService.findById(supportId);
    }
}
